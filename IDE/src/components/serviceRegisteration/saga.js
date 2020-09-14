import {showNotification} from '../Notification/NotificationState';

import {
    put,
    call,
    take,
    fork
} from 'redux-saga/effects'

import {
    FETCH_SERVICE_REQUEST,
    FETCH_GRAPHS_REQUEST, FETCH_CLASSES_REQUEST,
    REGISTER_SERVICE_REQUEST,FETCH_TERMS_REQUEST
} from './constants';

import {
    sendingRequest,
    fetchGraphsFailure, fetchGraphsSuccess,
    fetchServiceSuccess, fetchServiceFailure,
    fetchClassesSuccess, fetchClassesFailure,
    fetchTermsSuccess, fetchTermsFailure,
    registerServiceSuccess,registerServiceFailure
} from './actions';

import request from '../../utils/request';
import showError from '../../utils/ShowError';



export function* fetchService(data) {
    yield put(sendingRequest(true));
    try {
        var proxyUrl = 'https://cors-anywhere.herokuapp.com/';
        let outcome = yield call(request, proxyUrl+data,
            {
                redirect: 'follow',
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });
        yield put(fetchServiceSuccess(outcome));
    } catch (error) {
        console.log(error);
        yield put(fetchServiceFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch service! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchServiceFlow() {
    while (true) {
        const request = yield take(FETCH_SERVICE_REQUEST);
        yield call(fetchService, request.data);
    }
}

export function* fetchGraphs(data) {
    yield put(sendingRequest(true));
    try {
        let outcome = yield call(request, data+'/sv/graphs',
            {
                method: 'GET',
            });
        yield put(fetchGraphsSuccess(outcome));
    } catch (error) {
        yield put(fetchGraphsFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch Graphs! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchGraphsFlow() {
    while (true) {
        const request = yield take(FETCH_GRAPHS_REQUEST);
        yield call(fetchGraphs, request.data);
    }
}


export function* fetchClasses(data) {
    yield put(sendingRequest(true));
    try {

        let outcome = yield call(request, `${data.baseURL}/sv/classes/${encodeURIComponent(data.graph.trim().substring(1,data.graph.length-1))}`,
            {
                method: 'GET',
            });
        yield put(fetchClassesSuccess(outcome));
    } catch (error) {
        yield put(fetchClassesFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch Classes! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchClassesFlow() {
    while (true) {
        const request = yield take(FETCH_CLASSES_REQUEST);
        yield call(fetchClasses, request.data);
    }
}

export function* fetchTerms(data) {
    yield put(sendingRequest(true));
    try {

        let outcome = yield call(request, `${data.baseURL}/sv/terms/${encodeURIComponent(data.graph)}/${encodeURIComponent(data.ontClass)}`,
            {
                method: 'GET',
            });
        yield put(fetchTermsSuccess(outcome));
    } catch (error) {
        yield put(fetchTermsFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch Classes! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchTermsFlow() {
    while (true) {
        const request = yield take(FETCH_TERMS_REQUEST);
        yield call(fetchTerms, request.data);
    }
}



export function* registerServiceDescription(data) {
    yield put(sendingRequest(true));
    try {
        let outcome = yield call(request, `${data.baseURL}/service/register`,
            {
                method: 'POST', // *GET, POST, PUT, DELETE, etc.
                mode: 'no-cors', // no-cors, *cors, same-origin
                cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
                credentials: 'omit', // include, *same-origin, omit
                headers: {
                    'Content-Type': 'text/plain'
                },
                redirect: 'follow', // manual, *follow, error
                referrerPolicy: 'no-referrer', // no-referrer, *client
                body: data.serviceDescription
            },'plain');
        yield put(showNotification({
            type: 'shipped',
            message: 'Service Registered',
            category: 'service'
        }));
        yield put(registerServiceSuccess());
    } catch (error) {
        const e = error;
        yield put(registerServiceFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to Register the service',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* registerServiceFlow() {
    while (true) {
        const request = yield take(REGISTER_SERVICE_REQUEST);
        yield call(registerServiceDescription, request.data);
    }
}

export default function* serviceRoot() {
    yield fork(fetchServiceFlow);
    yield fork(fetchGraphsFlow);
    yield fork(fetchClassesFlow);
    yield fork(fetchTermsFlow);
    yield fork(registerServiceFlow);
}
