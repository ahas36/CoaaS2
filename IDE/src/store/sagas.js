import {
    all,fork
} from 'redux-saga/effects';

import connectRoot from '../components/connect/saga'
import serviceRoot from '../components/serviceRegisteration/saga'
import toolbarRoot from '../components/Toolbar/saga'

export default function* root() {
    yield all([
        fork(connectRoot),
        fork(serviceRoot),
        fork(toolbarRoot),
    ]);
}