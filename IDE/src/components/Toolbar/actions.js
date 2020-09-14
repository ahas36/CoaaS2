import {
    FETCH_ENTITIES_FAILURE,FETCH_ENTITIES_REQUEST,FETCH_ENTITIES_SUCCESS,
    FETCH_SERVICES_FAILURE,FETCH_SERVICES_REQUEST,FETCH_SERVICES_SUCCESS,
    FETCH_SITUATIONS_FAILURE,FETCH_SITUATIONS_REQUEST,FETCH_SITUATIONS_SUCCESS,
    FETCH_SUBS_FAILURE,FETCH_SUBS_REQUEST,FETCH_SUBS_SUCCESS,
    SENDING_REQUEST
} from './constants';


///////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////Services//////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////

// fetch all the registered services
export function fetchServicesRequest(data) {
    return {type: FETCH_SERVICES_REQUEST, data}
}

export function fetchServicesSuccess(services) {
    return {type: FETCH_SERVICES_SUCCESS, services}
}

export function fetchServicesFailure() {
    return {type: FETCH_SERVICES_FAILURE}
}


///////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////Entities//////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////

// fetch all the registered entity types
export function fetchEntitiesRequest(data) {
    return {type: FETCH_ENTITIES_REQUEST, data}
}

export function fetchEntitiesSuccess(entities) {
    return {type: FETCH_ENTITIES_SUCCESS, entities}
}

export function fetchEntitiesFailure() {
    return {type: FETCH_ENTITIES_FAILURE}
}

///////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////SITUATIONS////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////

// fetch all the registered situation types
export function fetchSituationsRequest(data) {
    return {type: FETCH_SITUATIONS_REQUEST, data}
}

export function fetchSituationsSuccess(situations) {
    return {type: FETCH_SITUATIONS_SUCCESS, situations}
}

export function fetchSituationsFailure() {
    return {type: FETCH_SITUATIONS_FAILURE}
}

///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////// //////SUBSCRIPTIONS//////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////


// fetch all the registered subscription types
export function fetchSubscriptionsRequest(data) {
    return {type: FETCH_SUBS_REQUEST, data}
}

export function fetchSubscriptionsSuccess(subscriptions) {
    return {type: FETCH_SUBS_SUCCESS, subscriptions}
}

export function fetchSubscriptionsFailure() {
    return {type: FETCH_SUBS_FAILURE}
}



///////////////////
export function sendingRequest(sending) {
    return {type: SENDING_REQUEST, sending}
}