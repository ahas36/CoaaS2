import numpy as np
import tensorflow as tf
import tensorflow.keras as keras
import tensorflow_probability as tfp
from tensorflow.keras.optimizers import Adam

from utils.ReplayBuffer import ReplayBuffer
from networks.ActorNetwork import ActorNetwork
from networks.CriticNetwork import CriticNetwork

class Agent:
    __input_dims = ['size_in_cache', 'earning', 'retrieval_cost', 'penalties', 'process_cost', 'cache_cost',
            'probability_delay', 'avg_cache_life', 'avg_delay_time']

    def __init__(self, max_action = 1, input_dims = [len(__input_dims)], alpha=0.001, beta=0.002, gamma=0.9, n_actions=6, max_size=60,
            tau=0.005, batch_size=10, warmup=10, update_actor_interval=1, noise=0.5):
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

        # Other
        self.time_step = 0
        self.warmup = warmup
        self.learn_step_cntr = 0
        self.update_actor_interval = update_actor_interval

        # Neural Networks
        self.actor = ActorNetwork(n_actions = self.n_actions, max_action=self.max_action, min_action=self.min_action)
        self.critic_1 = CriticNetwork(n_actions = self.n_actions, name='critic_1')
        self.critic_2 = CriticNetwork(n_actions = self.n_actions, name='critic_2')

        self.target_actor = ActorNetwork(n_actions = self.n_actions, max_action=self.max_action, min_action=self.min_action, name='tagert_actor')
        self.target_critic_1 = CriticNetwork(n_actions = self.n_actions, name='target_critic_1')
        self.target_critic_2 = CriticNetwork(n_actions = self.n_actions, name='target_critic_2')

        # Compling the Actor-Critic Networks
        self.actor.compile(optimizer=Adam(learning_rate=self.alpha), loss='mean')
        self.critic_1.compile(optimizer=Adam(learning_rate=self.beta), loss='mean_squared_error')
        self.critic_2.compile(optimizer=Adam(learning_rate=self.beta), loss='mean_squared_error')

        self.target_actor.compile(optimizer=Adam(learning_rate=self.alpha), loss='mean')
        self.target_critic_1.compile(optimizer=Adam(learning_rate=self.beta), loss='mean_squared_error')
        self.target_critic_2.compile(optimizer=Adam(learning_rate=self.beta), loss='mean_squared_error')

        self.noise = noise

        # Hard copying the paramters for a soft update
        self.update_network_params(tau=1)

    def update_exploration(self, diff):
        if(self.time_step > self.warmup):
            if(abs(diff) < 1):
                self.noise = self.noise - 0.05
                if(self.noise <= 0): 
                    self.noise = 0
            else:
                self.noise = self.noise + 0.05
                if(self.noise >= 0.5): 
                    self.noise = 0.5

    def choose_action(self, observation):
        if(self.time_step < self.warmup):
            mu = np.random.normal(scale=self.noise, size=(self.n_actions,))
            mu_prime = mu + np.random.normal(scale=self.noise)
            mu_prime = tf.clip_by_value(mu_prime, self.min_action, self.max_action)
        else:
            state = tf.convert_to_tensor([observation], dtype=tf.float32)
            mu, sigma = self.actor(state) 

            probabilities = tfp.distributions.Normal(mu, sigma)
            exp_noise = probabilities.sample()
            exp_noise = abs(-1 - tf.math.tanh(exp_noise))/2.0 

            mu = abs(-1 - mu[0])/2.0

            mu_prime = mu 
            # mu_prime = mu + np.random.normal(scale=self.noise)
            # mu_prime = (mu + exp_noise)/2
            mu_prime = tf.clip_by_value(mu_prime, self.min_action, self.max_action)
        
        self.time_step += 1

        return mu_prime

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
    def update_network_params(self, tau = None):
        if tau is None:
            tau = self.tau
        
        # Hard copying the actor network parameters because tau=1
        weights = []
        targets = self.target_actor.weights

        for i, weight in enumerate(self.actor.weights):
            weights.append(weight * tau + targets[i] * (1 - tau))

        self.target_actor.set_weights(weights)

        weights = []
        targets = self.target_critic_1.weights
        for i, weight in enumerate(self.critic_1.weights):
            weights.append(weight * tau + targets[i] * (1 - tau))

        self.target_critic_1.set_weights(weights)

        weights = []
        targets = self.target_critic_2.weights
        for i, weight in enumerate(self.critic_2.weights):
            weights.append(weight * tau + targets[i] * (1 - tau))

        self.target_critic_2.set_weights(weights)
     
    # Saving parameters
    def save_models(self):
        self.actor.save_weights(self.actor.chpnt_file)
        self.critic_1.save_weights(self.critic_1.chpnt_file)
        self.critic_2.save_weights(self.critic_2.chpnt_file)

        self.target_actor.save_weights(self.target_actor.chpnt_file)
        self.target_critic_1.save_weights(self.target_critic_1.chpnt_file)
        self.target_critic_2.save_weights(self.target_critic_2.chpnt_file)
    
    # Loading the parameters
    def load_models(self):
        self.actor.load_weights(self.actor.chpnt_file)
        self.critic_1.load_weights(self.critic_1.chpnt_file)
        self.critic_2.load_weights(self.critic_2.chpnt_file)
        
        self.target_actor.load_weights(self.target_actor.chpnt_file)
        self.target_critic_1.load_weights(self.target_critic_1.chpnt_file)
        self.target_critic_2.load_weights(self.target_critic_2.chpnt_file)

    def learn(self):
        try:
            if self.memory.mem_cntr < self.batch_size:
                return
            s_states, s_actions, s_new_states, s_rewards = self.memory.sample_buffer(self.batch_size)
            
            states = tf.convert_to_tensor(s_states, dtype=tf.float32)
            rewards = tf.convert_to_tensor(s_rewards, dtype=tf.float32)
            actions = tf.convert_to_tensor(s_actions, dtype=tf.float32)
            new_states = tf.convert_to_tensor(s_new_states, dtype=tf.float32)

            with tf.GradientTape(persistent=True) as tape:
                target_actions,_ = self.target_actor(new_states)
                target_actions = target_actions + tf.clip_by_value(np.random.normal(scale=0.5), -1.0, 1.0)

                target_actions = tf.clip_by_value(target_actions, self.min_action,
                                                self.max_action)

                q1_ = self.target_critic_1(new_states, target_actions)
                q2_ = self.target_critic_2(new_states, target_actions)

                q1 = tf.squeeze(self.critic_1(states, actions), 1)
                q2 = tf.squeeze(self.critic_2(states, actions), 1)

                q1_ = tf.squeeze(q1_, 1)
                q2_ = tf.squeeze(q2_, 1)

                critic_value_ = tf.math.minimum(q1_, q2_)

                target = rewards + self.gamma * critic_value_
                critic_1_loss = keras.losses.MSE(target, q1)
                critic_2_loss = keras.losses.MSE(target, q2)

            critic_1_gradient = tape.gradient(critic_1_loss,
                                            self.critic_1.trainable_variables)
            critic_2_gradient = tape.gradient(critic_2_loss,
                                            self.critic_2.trainable_variables)

            self.critic_1.optimizer.apply_gradients(
                    zip(critic_1_gradient, self.critic_1.trainable_variables))
            self.critic_2.optimizer.apply_gradients(
                    zip(critic_2_gradient, self.critic_2.trainable_variables))

            self.learn_step_cntr += 1

            if self.learn_step_cntr % self.update_actor_interval != 0:
                return

            with tf.GradientTape() as tape:
                new_actions,_ = self.actor(states)
                critic_1_value = self.critic_1(states, new_actions)
                actor_loss = -tf.math.reduce_mean(critic_1_value)

            actor_gradient = tape.gradient(actor_loss,
                                        self.actor.trainable_variables)
            self.actor.optimizer.apply_gradients(
                            zip(actor_gradient, self.actor.trainable_variables))

            # Soft updating the target networks      
            self.update_network_params()
        
        except(Exception):
            print('An error occured when learning the model')
            



        
