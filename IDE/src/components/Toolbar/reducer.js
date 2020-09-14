import
{
    SENDING_REQUEST,
    FETCH_ENTITIES_SUCCESS,FETCH_ENTITIES_FAILURE,
    FETCH_SUBS_SUCCESS,FETCH_SUBS_FAILURE,
    FETCH_SITUATIONS_SUCCESS,FETCH_SITUATIONS_FAILURE,
    FETCH_SERVICES_SUCCESS,FETCH_SERVICES_FAILURE
}
    from './constants';

// The initial application state
const initialState = {
    isLoading: false,
    entities: [],
    subs: [],
    situations : [],
    services: []
};


export default function ToolbarReducer(state = initialState, action) {
    switch (action.type) {
        case FETCH_ENTITIES_SUCCESS:
            return {
                ...state,
                entities: action.entities
            };
        case FETCH_ENTITIES_FAILURE:
            return {
                ...state,
                entities: [],
            };
        case FETCH_SUBS_SUCCESS:
            return {
                ...state,
                subs: action.subscriptions
            };
        case FETCH_SUBS_FAILURE:
            return {
                ...state,
                subs: [],
            };
        case FETCH_SITUATIONS_SUCCESS:
            return {
                ...state,
                situations: action.situations
            };
        case FETCH_SITUATIONS_FAILURE:
            return {
                ...state,
                situations: [],
            };
        case FETCH_SERVICES_SUCCESS:
            return {
                ...state,
                services: action.services
            };
        case FETCH_SERVICES_FAILURE:
            return {
                ...state,
                services: []
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
  