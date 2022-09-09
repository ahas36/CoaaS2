import sys, os
import numpy as np
from AgentContext import AgentContext

sys.path.append(os.path.abspath(os.path.join('.')))

from flask import Flask
from flask_restful import Api

app = Flask(__name__)
api = Api(app)

api.add_resource(AgentContext, '/selections')

if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0', port=9494)

