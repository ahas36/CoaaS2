import {compose, lifecycle, withHandlers, withState} from 'recompose';
import {connect} from 'react-redux';
import ToolbarView from './ToolbarView';
import {fetchGraphsRequest} from "../serviceRegisteration/actions";


export default compose(
    connect(
        state => ({
            isConnected: state.connect.isConnected,
            baseURL: state.connect.baseURL,
            isLoading: state.service.isLoading,
            graphs:state.service.graphs,
        }),
        {fetchGraphsRequest},
    ),
    withHandlers({
        handleFetchTermsRequest: props => () =>
        {
        }
    }),
    lifecycle({
        componentDidMount() {
            if (this.props.isConnected)
            {
                this.props.fetchGraphsRequest(this.props.baseURL);
            }
        },
        componentWillReceiveProps(nextProps) {
            if(nextProps.isConnected && !this.props.isConnected)
            {
                this.props.fetchGraphsRequest(nextProps.baseURL);
            }
        }
    })
)(ToolbarView);
