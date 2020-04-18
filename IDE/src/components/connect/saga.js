import {showNotification} from '../Notification/NotificationState';

import {
    put,
    call,
    take,
    fork
} from 'redux-saga/effects'

import {
    CONNECT_REQUEST
} from './constants';

import {
    sendingRequest,
    connectSuccess,
    connectFailure
} from './actions';


import request from '../../utils/request';
import showError from '../../utils/ShowError';

export function* connect(data) {
    yield put(sendingRequest(true));
    const requestURL = `${data}/health`;

    try {
        let outcome = yield call(request, requestURL,
            {
                method: 'GET',
                headers:{'Accept':'application/json'}
            });
            if(outcome.status === 'Healthy')
            {
                yield put(connectSuccess(data));
                yield put(showNotification({type:'info',message:'Connected!',category:'connect'}));
            }
            else {
                yield put(showNotification({type:'error',message:'Unable to connect! Please check the provided URL.',category:'connect'}));
            }
    } catch (error) {

        yield put(connectFailure());
        // yield call(showError,error.response,'connect');
        yield put(showNotification({type:'error',message:'Unable to connect! Please check the provided URL.',category:'connect'}));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* connectFlow() {
    while (true) {
        const request = yield take(CONNECT_REQUEST);

        yield call(connect, request.data);
    }
}


export default function* connectRoot() {
    yield fork(connectFlow);
}
