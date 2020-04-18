import
{
    SENDING_REQUEST,
    FETCH_CLASSES_FAILURE, FETCH_CLASSES_SUCCESS,
    FETCH_GRAPHS_FAILURE, FETCH_GRAPHS_SUCCESS,
    FETCH_TERMS_FAILURE, FETCH_TERMS_SUCCESS,
    FETCH_SERVICE_FAILURE, FETCH_SERVICE_SUCCESS
}
    from './constants';

// The initial application state
const initialState = {
    isLoading: false,
    serviceSampleResponse: null,
    semanticResponse: null,
    terms: [],
    graphs: [],
    ontClasses: []
};


export default function ServiceReducer(state = initialState, action) {
    switch (action.type) {
        case FETCH_SERVICE_SUCCESS:
            return {
                ...state,
                serviceSampleResponse: action.serviceSampleResponse
            };
        case FETCH_SERVICE_FAILURE:
            return {
                ...state,
                serviceSampleResponse: null,
            };
        case FETCH_TERMS_SUCCESS:
            return {
                ...state,
                terms: action.terms
            };
        case FETCH_TERMS_FAILURE:
            return {
                ...state,
                terms: [],
            };
        case FETCH_GRAPHS_SUCCESS:
            return {
                ...state,
                graphs: action.graphs
            };
        case FETCH_GRAPHS_FAILURE:
            return {
                ...state,
                graphs: [],
            };
        case FETCH_CLASSES_SUCCESS:
            return {
                ...state,
                ontClasses: action.ontClasses
            };
        case FETCH_CLASSES_FAILURE:
            return {
                ...state,
                ontClasses: []
            };
        case SENDING_REQUEST:
            return {
                ...state,
                isLoading: action.sending
            };
        default:
            return state;
    }
}
  