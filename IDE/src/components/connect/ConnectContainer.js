import {compose, lifecycle, withHandlers} from 'recompose';
import {connect} from 'react-redux';
import ConnectView from './Connect';
import {connectRequest} from "./actions";


export default compose(
    connect(
        state => ({
            isConnected: state.connect.isConnected,
            isLoading: state.connect.isLoading,
        }),
        {connectRequest},
    ),
    withHandlers({
        handleConnect: props => (baseURL) => {
            props.connectRequest(baseURL);
        }
    }),
    lifecycle({
        componentWillReceiveProps(nextProps) {
        }
    })
)(ConnectView);