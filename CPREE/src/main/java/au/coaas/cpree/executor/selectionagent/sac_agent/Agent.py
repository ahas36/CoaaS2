import numpy as np
import tensorflow as tf
import tensorflow.keras as keras
from tensorflow.keras.optimizers import Adam

from utils.ReplayBuffer import ReplayBuffer
from networks.ValueNetwork import ValueNetwork
from networks.ActorNetwork import ActorNetwork
from networks.CriticNetwork import CriticNetwork

class Agent:
    __input_dims = ['size_in_cache', 'earning', 'retrieval_cost', 'penalties', 'process_cost', 'cache_cost',
            'probability_delay', 'avg_cache_life', 'avg_delay_time', 'avg_hitrate']

    def __init__(self, max_action, input_dims = [len(__input_dims)], alpha=0.001, beta=0.002, gamma=0.9, n_actions=6, max_size=60,
            tau=0.005, batch_size=10, reward_scale=1):
        # Hyperparameters
        self.gamma = gamma
        self.tau = tau
        self.alpha = alpha
        self.beta = beta

        # State and Action Spaces
        self.n_actions = n_actions
        self.max_action = max_action
        self.min_action = 0

        # Replay memory buffer
        self.memory = ReplayBuffer(max_size, input_dims, self.n_actions)
        self.batch_size = batch_size

        # Neural Networks
        self.actor = ActorNetwork(n_actions = self.n_actions, max_action=self.max_action, min_action=self.min_action)
        self.critic_1 = CriticNetwork(n_actions = self.n_actions, name='critic_1')
        self.critic_2 = CriticNetwork(n_actions = self.n_actions, name='critic_2')
        self.value = ValueNetwork()
        self.target_value = ValueNetwork(name='target_value')

        # Compling the Actor-Critic Networks
        self.actor.compile(optimizer=Adam(learning_rate=self.alpha))
        self.critic_1.compile(optimizer=Adam(learning_rate=self.beta))
        self.critic_2.compile(optimizer=Adam(learning_rate=self.beta))
        self.value.compile(optimizer=Adam(learning_rate=self.beta))
        self.target_value.compile(optimizer=Adam(learning_rate=self.beta))

        self.scale = reward_scale

        # Hard copying the paramters for a soft update
        self.update_network_params(tau=1)

    def choose_action(self, observation):
        state = tf.convert_to_tensor([observation])
        actions, _ = self.actor.sample_normal(state, reparameterize=False)

        return actions[0]

    # Checking if the buffer is empty
    def isBufferEmpty(self):
        return self.memory.mem_cntr == 0
    
    # Storing in the Replay Buffer
    def remember(self, new_state, reward, action):
        self.memory.store_transition(new_state, reward, action)
    
    # String the first Replay buffer record
    def init_remember(self, state, actions):
        self.memory.store_first_transition(state, actions)

    # Soft updating the target network parameters
    def update_network_params(self, tau=None):
        if tau is None:
            tau = self.tau
        
        # Hard copying the actor network parameters because tau=1
        weights = []
        targets = self.target_value.weights

        for i, weight in enumerate(self.value.weights):
            weights.append(weight * tau + targets[i] * (1 - tau))

        self.target_value.set_weights(weights)
     
    # Saving parameters
    def save_models(self):
        self.actor.save_weights(self.actor.chpnt_file)
        self.critic_1.save_weights(self.critic_1.chpnt_file)
        self.critic_2.save_weights(self.critic_2.chpnt_file)
        self.value.save_weights(self.value.chpnt_file)
        self.target_value.save_weights(self.target_value.chpnt_file)
    
    # Loading the parameters
    def load_models(self):
        self.actor.load_weights(self.actor.chpnt_file)
        self.critic_1.load_weights(self.critic_1.chpnt_file)
        self.critic_2.load_weights(self.critic_2.chpnt_file)
        self.value.load_weights(self.value.chpnt_file)
        self.target_value.load_weights(self.target_value.chpnt_file)

    def learn(self):
        if self.memory.mem_cntr < self.batch_size:
            return
        s_states, s_actions, s_new_states, s_rewards = self.memory.sample_buffer(self.batch_size)
        
        states = tf.convert_to_tensor(s_states, dtype=tf.float32)
        new_states = tf.convert_to_tensor(s_new_states, dtype=tf.float32)

        # Value loss
        with tf.GradientTape() as tape:
            value = tf.squeeze(self.value(states), 1)
            new_value = tf.squeeze(self.target_value(new_states), 1)

            current_policy_actions, log_probs = self.actor.sample_normal(states, reparameterize=False)
            log_probs = tf.squeeze(log_probs, 1)
            q1_new_policy = self.critic_1(states, current_policy_actions)
            q2_new_policy = self.critic_2(states, current_policy_actions)
            critic_value = tf.squeeze(tf.math.minimum(q1_new_policy, q2_new_policy), 1)

            value_target = critic_value - log_probs
            value_loss = 0.5 * keras.losses.MSE(value, value_target)

            value_network_gradient = tape.gradient(value_loss, self.value.trainable_variables)
            self.value.optimizer.apply_gradients(zip(value_network_gradient, self.value.trainable_variables))

        # Actor Loss
        with tf.GradientTape() as tape:
            new_policy_actions, log_probs = self.actor.sample_normal(states, reparameterize=True)
            log_probs = tf.squeeze(log_probs, 1)
            q1_new_policy = self.critic_1(states, new_policy_actions)
            q2_new_policy = self.critic_2(states, new_policy_actions)
            critic_value = tf.squeeze(tf.math.minimum(q1_new_policy, q2_new_policy), 1)

            actor_loss = log_probs - critic_value
            actor_loss = tf.math.reduce_mean(actor_loss)

            actor_network_gradient = tape.gradient(actor_loss, self.actor.trainable_variables)
            self.actor.optimizer.apply_gradients(zip(actor_network_gradient, self.actor.trainable_variables))

        # Critic Loss
        with tf.GradientTape(persistent=True) as tape:
            q_hat = self.scale * s_rewards + self.gamma * new_value
            q1_old_policy = tf.squeeze(self.critic_1(s_states, s_actions), 1)
            q2_old_policy = tf.squeeze(self.critic_2(s_states, s_actions), 1)
            critic_1_loss = 0.5 * keras.losses.MSE(q1_old_policy, q_hat)
            critic_2_loss = 0.5 * keras.losses.MSE(q2_old_policy, q_hat)

            critic_1_network_gradient = tape.gradient(critic_1_loss, self.critic_1.trainable_variables)
            critic_2_network_gradient = tape.gradient(critic_2_loss, self.critic_2.trainable_variables)
            self.critic_1.optimizer.apply_gradients(zip(critic_1_network_gradient, self.critic_1.trainable_variables))
            self.critic_2.optimizer.apply_gradients(zip(critic_2_network_gradient, self.critic_2.trainable_variables))


        # Soft updating the target networks      
        self.update_network_params()
            



        
