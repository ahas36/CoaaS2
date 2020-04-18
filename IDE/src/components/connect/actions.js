import
{
    CONNECT_SUCCESS, CONNECT_REQUEST,
    DISCONNECT_REQUEST,
    SENDING_REQUEST, CONNECT_FAILURE
} from './constants';


export function connectRequest(data) {
    return {type: CONNECT_REQUEST, data}
}

export function connectSuccess(baseURL) {
    return {type: CONNECT_SUCCESS, baseURL}
}

export function connectFailure() {
    return {type: CONNECT_FAILURE}
}

export function disconnectRequest() {
    return {type: DISCONNECT_REQUEST}
}

export function sendingRequest(sending) {
    return {type: SENDING_REQUEST, sending}
}
