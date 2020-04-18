import { combineReducers } from 'redux';

import connect from '../components/connect/reducer';
import notification from '../components/Notification/NotificationState';
import service from '../components/serviceRegisteration/reducer';

export default combineReducers({
    connect,
    notification,
    service
});