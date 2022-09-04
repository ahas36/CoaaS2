import sys, os
import traceback

from Agent import Agent
sys.path.append(os.path.abspath(os.path.join('.')))

from flask_restful import Resource
from flask import request

class AgentContext(Resource):
    __agent = Agent(max_action=1)
    score_history = []

    def post(self):
        try:         
            # Retriving the measurement
            req_body = request.get_json()
            new_state = req_body['body']['vector']
            utility = req_body['body']['reward']

            actions = self.__agent.choose_action(new_state)

            if(not self.__agent.isBufferEmpty):
                self.__agent.learn()
                self.__agent.remember(new_state, utility, actions)
            else:
                self.__agent.init_remember(new_state, actions)
        
            result = { 'actions': actions.numpy().tolist() }
            # Return data and 200 OK code
            return result, 200

        except(Exception):
            print('An error occured : ' + traceback.format_exc())
            return {'message':'An error occured'}, 500  
