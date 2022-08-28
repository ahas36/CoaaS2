import sys, os
import traceback

from Agent import Agent
sys.path.append(os.path.abspath(os.path.join('.')))

from flask_restful import Resource
from flask import request

class AgentContext(Resource):
    __agent = None
    score_history = []

    def put(self):
        max_action = request.form.get('maxAction')
        self.__agent = Agent(max_action=max_action)

    def post(self):
        try:         
            args = request.args   
            function = args['func']

            if function == 'learn':
                req_body = request.form

                state = req_body.get('state')
                action = req_body.get('action')
                new_state = req_body.get('newAction')
                utility = req_body.get('reward')

                self.__agent.remember(state, action, new_state, utility)
                self.__agent.learn()

                # This is for debugging and plotting 
                # score += reward
                # score_history.append(score)
                # avg_score = np.mean(score_history[-100:])
                # print('episode ', i, ' score %.1f' % score, ' avg score %1f' % avg_score)

                # 204 No Content code
                return 204
            
            elif function == 'act':
                # Retriving the measurement
                observation = request.form.get('vector')
                actions = self.__agent.choose_action(observation)
                result = {'action': actions}
                # Return data and 200 OK code
                return result, 200

            else :
                return {'message':'Wrong function'}, 404  

        
        except(Exception):
            print('An error occured : ' + traceback.format_exc())
            return {'message':'An error occured'}, 500  
