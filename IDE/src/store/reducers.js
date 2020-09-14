import { combineReducers } from 'redux';

import connect from '../components/connect/reducer';
import notification from '../components/Notification/NotificationState';
import service from '../components/serviceRegisteration/reducer';
import toolbar from '../components/Toolbar/reducer';

export default combineReducers({
    connect,
    notification,
    service,
    toolbar
});