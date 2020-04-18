import {
    FETCH_GRAPHS_REQUEST,FETCH_GRAPHS_FAILURE,FETCH_GRAPHS_SUCCESS,
    FETCH_SERVICE_REQUEST,FETCH_SERVICE_FAILURE,FETCH_SERVICE_SUCCESS,
    FETCH_CLASSES_REQUEST,FETCH_CLASSES_FAILURE,FETCH_CLASSES_SUCCESS,
    FETCH_TERMS_REQUEST,FETCH_TERMS_FAILURE,FETCH_TERMS_SUCCESS,
    REGISTER_SERVICE_REQUEST,REGISTER_SERVICE_FAILURE,REGISTER_SERVICE_SUCCESS,
    SENDING_REQUEST
} from './constants';


// fetch a sample of service data
export function fetchServiceRequest(data) {
    return {type: FETCH_SERVICE_REQUEST, data}
}

export function fetchServiceSuccess(serviceSampleResponse) {
    return {type: FETCH_SERVICE_SUCCESS, serviceSampleResponse}
}

export function fetchServiceFailure() {
    return {type: FETCH_SERVICE_FAILURE}
}

// get terms of a given ontology class
export function fetchTermsRequest(data) {
    return {type: FETCH_TERMS_REQUEST, data}
}

export function fetchTermsSuccess(terms) {
    return {type: FETCH_TERMS_SUCCESS, terms}
}

export function fetchTermsFailure() {
    return {type: FETCH_TERMS_FAILURE}
}

// get classes of a given semantic vocabulary

export function fetchClassesRequest(data) {
    return {type: FETCH_CLASSES_REQUEST, data}
}

export function fetchClassesSuccess(ontClasses) {
    return {type: FETCH_CLASSES_SUCCESS, ontClasses}
}

export function fetchClassesFailure() {
    return {type: FETCH_CLASSES_FAILURE}
}

// get all the registered semantic vocabularies

export function fetchGraphsRequest(data) {
    return {type: FETCH_GRAPHS_REQUEST, data}
}

export function fetchGraphsSuccess(graphs) {
    return {type: FETCH_GRAPHS_SUCCESS, graphs}
}

export function fetchGraphsFailure() {
    return {type: FETCH_GRAPHS_FAILURE}
}


// Register service description

export function registerService(data) {
    return {type: REGISTER_SERVICE_REQUEST, data}
}

export function registerServiceSuccess() {
    return {type: REGISTER_SERVICE_SUCCESS}
}

export function registerServiceFailure() {
    return {type: REGISTER_SERVICE_FAILURE}
}
// Loading status

export function processServiceRequest(sending) {
    return {type: SENDING_REQUEST, sending}
}

// Save the service info and go to the next step

export function sendingRequest(sending) {
    return {type: SENDING_REQUEST, sending}
}
