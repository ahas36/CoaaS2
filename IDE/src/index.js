import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import ViewManager from './ViewManager';
import registerServiceWorker from './registerServiceWorker';
import { Provider } from 'react-redux'

import store from './store/index';


ReactDOM.render(
    <Provider store={store}>
        <ViewManager/>
    </Provider>
    , document.getElementById('root'));
registerServiceWorker();
