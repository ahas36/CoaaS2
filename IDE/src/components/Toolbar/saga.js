import {showNotification} from '../Notification/NotificationState';

import {
    put,
    call,
    take,
    fork
} from 'redux-saga/effects'

import {
    FETCH_SERVICES_REQUEST,
    FETCH_SITUATIONS_REQUEST,
    FETCH_SUBS_REQUEST,
    FETCH_ENTITIES_REQUEST
} from './constants';

import {
    sendingRequest,
    fetchEntitiesFailure, fetchEntitiesSuccess,
    fetchServicesFailure, fetchServicesSuccess,
    fetchSituationsFailure, fetchSituationsSuccess,
    fetchSubscriptionsFailure, fetchSubscriptionsSuccess
} from './actions';

import request from '../../utils/request';
import showError from '../../utils/ShowError';

/////////////////////////////////////get all services /////////////////////////////////////
export function* fetchServices(data) {
    yield put(sendingRequest(true));
    try {
        let outcome = yield call(request, data+'/service/all',
            {
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });
        yield put(fetchServicesSuccess(outcome));
    } catch (error) {
        yield put(fetchServicesFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch services! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchServicesFlow() {
    while (true) {
        const request = yield take(FETCH_SERVICES_REQUEST);
        yield call(fetchServices, request.data);
    }
}
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////get all situations /////////////////////////////////////
export function* fetchSituations(data) {
    yield put(sendingRequest(true));
    try {
        let outcome = yield call(request, data+'/situation/all',
            {
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });
        yield put(fetchSituationsSuccess(outcome));
    } catch (error) {
        yield put(fetchSituationsFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch situations! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchSituationsFlow() {
    while (true) {
        const request = yield take(FETCH_SITUATIONS_REQUEST);
        yield call(fetchSituations, request.data);
    }
}
////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////get all subs /////////////////////////////////////
export function* fetchSubscriptions(data) {
    yield put(sendingRequest(true));
    try {
        let outcome = yield call(request, data+'/subscription/all',
            {
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });
        yield put(fetchSubscriptionsSuccess(outcome));
    } catch (error) {
        yield put(fetchSubscriptionsFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch Subscriptions! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchSubscriptionsFlow() {
    while (true) {
        const request = yield take(FETCH_SUBS_REQUEST);
        yield call(fetchSubscriptions, request.data);
    }
}
////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////get all entities /////////////////////////////////////
export function* fetchEntities(data) {
    yield put(sendingRequest(true));
    try {
        let outcome = yield call(request, data+'/entity/all',
            {
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });
        yield put(fetchEntitiesSuccess(outcome));
    } catch (error) {
        yield put(fetchEntitiesFailure());
        yield put(showNotification({
            type: 'error',
            message: 'Unable to fetch Entities! Please check the provided URL.',
            category: 'service'
        }));
    } finally {
        yield put(sendingRequest(false));
    }
}

export function* fetchEntitiesFlow() {
    while (true) {
        const request = yield take(FETCH_ENTITIES_REQUEST);

        yield call(fetchEntities, request.data);
    }
}
////////////////////////////////////////////////////////////////////////////////////////

export default function* toolbarRoot() {
    yield fork(fetchServicesFlow);
    yield fork(fetchSituationsFlow);
    yield fork(fetchSubscriptionsFlow);
    yield fork(fetchEntitiesFlow);
}
