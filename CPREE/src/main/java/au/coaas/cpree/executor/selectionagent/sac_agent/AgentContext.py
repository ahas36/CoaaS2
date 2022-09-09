import sys, os
import traceback
import numpy as np

from Agent import Agent
sys.path.append(os.path.abspath(os.path.join('.')))

from flask_restful import Resource
from flask import request

class AgentContext(Resource):
    __agent = Agent(max_action=1)
    __score_history = []

    def post(self):
        try:         
            # Retriving the measurement
            req_body = request.get_json()
            new_state = req_body['vector']
            utility = req_body['reward']

            actions = self.__agent.choose_action(new_state)


            if(not self.__agent.isBufferEmpty):
                self.__agent.remember(new_state, utility, actions)
                self.__agent.learn()
                self.__score_history.append(utility)
            else:
                self.__agent.init_remember(new_state, actions)

            avg_score = np.mean(self.__score_history[-100:])
            result = { 
                'actions': actions.numpy().tolist(),
                'avg_reward': avg_score
            }
            
            # Return data and 200 OK code
            return result, 200

        except(Exception):
            print('An error occured : ' + traceback.format_exc())
            return {'message':'An error occured'}, 500  
