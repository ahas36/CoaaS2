import {compose, lifecycle, withHandlers,withState} from 'recompose';
import {connect} from 'react-redux';
import CodeEditor from './CodeEditor';


export default compose(
    connect(
        state => ({
            isConnected: state.connect.isConnected,
            url: state.connect.baseURL,
            isLoading: state.connect.isLoading,
        }),
        {},
    ),
    withHandlers({
        handleExecuteQuery: props => (baseURL) => {
            // props.connectRequest(baseURL);
        }
    }),
    lifecycle({
        componentWillReceiveProps(nextProps) {
        }
    })
)(CodeEditor);