import
{
    SENDING_REQUEST,
    CONNECT_FAILURE,
    CONNECT_SUCCESS,
    DISCONNECT_REQUEST
}
    from './constants';

// The initial application state
const initialState = {
    isLoading: false,
    isConnected: false,
    baseURL: null,
};


export default function ConnectReducer(state = initialState, action) {
    switch (action.type) {
        case CONNECT_SUCCESS:
            return {
                ...state,
                isConnected: true,
                baseURL : action.baseURL
            };
        case CONNECT_FAILURE:
            return {
                ...state,
                isConnected: false,
                baseURL: null
            };
        case DISCONNECT_REQUEST:

            return {
                ...state,
                isConnected: false,
                baseURL: null
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
  