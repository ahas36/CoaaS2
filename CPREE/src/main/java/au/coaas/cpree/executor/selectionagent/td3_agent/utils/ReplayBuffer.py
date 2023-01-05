import numpy as np
from utils.mongoclient import MongoClient 

class ReplayBuffer:
    # Connecting to a monogo DB instance 
    __db = MongoClient('mongodb://localhost:27017', 'coaas_log')

    def __init__(self, max_size, input_shape, n_actions = 6):
        # Initializing 
        self.mem_size = max_size
        self.mem_cntr = 0
        # Buffer of states before the action (s)
        self.state_memory = np.zeros((self.mem_size, *input_shape))
        # Buffer of actions (a)
        self.action_memory = np.zeros((self.mem_size, n_actions))
        # Buffer of new states after the actions (s')
        self.new_state_memory = np.zeros((self.mem_size, *input_shape))
        # Buffer of rewards from taking a action on state to transiton to new state
        self.reward_memory = np.zeros(self.mem_size)
        # Since, Delta Gain is used, recording the last gain.
        self.full_reward = 0

    def store_transition(self, new_state, reward, action):
        # Getting the memory index to rewrite. 
        # This is becuase the buffer memory is limited in size.
        prev_index = self.mem_cntr - 1
        prev_index %= self.mem_size
        self.new_state_memory[prev_index] = new_state

        delta_reward = reward - self.full_reward
        self.reward_memory[prev_index] = delta_reward
        self.full_reward = reward

        if(prev_index > self.mem_size):
            # Persist the trasitions 
            self.__db.insert_one('decisionHistory',
                {
                    'state': self.state_memory[prev_index],
                    'action': self.action_memory[prev_index],
                    'new_state': self.new_state_memory[prev_index],
                    'reward': self.reward_memory[prev_index]
                })

        index = self.mem_cntr % self.mem_size

        # Setting values to the buffer
        self.state_memory[index] = new_state
        self.action_memory[index] = action

        # Updating the point
        self.mem_cntr += 1
    
    def store_first_transition(self, state, action):
        index = self.mem_cntr

        self.state_memory[index] = state
        self.action_memory[index] = action

        # Updating the point
        self.mem_cntr += 1
    
    def sample_buffer(self, batch_size):
        # Sampling without replacement
        max_mem = min(self.mem_cntr, self.mem_size)
        batch = np.random.choice(max_mem, batch_size, replace=False)
        
        curr_pntr = self.mem_cntr % self.mem_size
        if(curr_pntr in batch):
            batch = np.delete(batch, np.where(batch == curr_pntr))

        #Samples
        s_states = self.state_memory[batch]
        s_new_states = self.new_state_memory[batch]
        s_actions = self.action_memory[batch]
        s_rewards = self.reward_memory[batch]

        return s_states, s_actions, s_new_states, s_rewards



