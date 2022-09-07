import os
import tensorflow as tf
import tensorflow.keras as keras
import tensorflow_probability as tfp
from tensorflow.keras.layers import Dense

class ActorNetwork(keras.Model):
    def __init__(self, max_action, min_action, n_actions = 6, fc1_dims=256, fc2_dims=256, name='actor', chkpnt_dir='tmp') -> None:
        super(ActorNetwork, self).__init__()
        self.fc1_dims = fc1_dims
        self.fc2_dims = fc2_dims
        self.n_actions = n_actions

        self.model_name = name
        self.chkpnt_dir = chkpnt_dir
        self.chpnt_file = os.path.join(self.chkpnt_dir, self.model_name+'.h5')

        self.max_action = max_action
        self.min_action = min_action
        self.noise = 1e-6

        self.fc1 = Dense(self.fc1_dims, activation='relu')
        self.fc2 = Dense(self.fc2_dims, activation='relu')
        self.mu = Dense(self.n_actions, activation=None)
        self.sigma = Dense(self.n_actions, activation=None)

    def call(self, state):
        action_value = self.fc1(state)
        action_value = self.fc2(action_value)

        mu = self.mu(action_value)
        sigma = self.sigma(action_value)
        sigma = tf.clip_by_value(sigma, clip_value_min=0, clip_value_max=1)

        return mu, sigma
    
    def sample_normal(self, state, reparameterize=True):
        mu, sigma = self.call(state)
        probabilities = tfp.distributions.Normal(mu, sigma)
        
        actions = probabilities.sample()
        action = abs(-1 - tf.math.tanh(actions))/2

        log_prob = probabilities.log_prob(actions)
        log_prob -= tf.math.log(1 - tf.math.pow(action, 2) + self.noise)
        log_prob = tf.math.reduce_sum(log_prob, axis=1, keepdims=True)

        return action, log_prob

