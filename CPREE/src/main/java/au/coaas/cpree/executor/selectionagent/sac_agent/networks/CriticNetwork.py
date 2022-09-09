import os
import numpy as np
import tensorflow as tf
import tensorflow.keras as keras
from tensorflow.keras.layers import Dense

class CriticNetwork(keras.Model):
    def __init__(self, n_actions = 6, fc1_dims=256, fc2_dims=256, name='critic', chkpnt_dir='tmp') -> None:
        super(CriticNetwork, self).__init__()
        self.fc1_dims = fc1_dims
        self.fc2_dims = fc2_dims
        self.n_actions = n_actions

        self.model_name = name
        self.chkpnt_dir = chkpnt_dir
        self.chpnt_file = os.path.join(self.chkpnt_dir, self.model_name+'.h5')

        self.fc1 = Dense(self.fc1_dims, activation='relu')
        self.fc2 = Dense(self.fc2_dims, activation='relu')
        self.q = Dense(1, activation=None)

    def call(self, state, action):
        action_value = self.fc1(tf.concat([state, action], axis = 1))
        action_value = self.fc2(action_value)

        q = self.q(action_value)

        return q
