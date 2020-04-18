import {
    put,
    call,
} from 'redux-saga/effects'
import {showNotification} from '../components/Notification/NotificationState';

export default function* showError(response,category){
    let errorMessage =  yield call([response, 'text'])
    yield put(showNotification({type:'error',message:errorMessage,category:category}));
}