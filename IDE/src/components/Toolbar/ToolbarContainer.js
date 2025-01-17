import {compose, lifecycle, withHandlers, withState} from 'recompose';
import {connect} from 'react-redux';
import ToolbarView from './ToolbarView';
import {fetchGraphsRequest} from "../serviceRegisteration/actions";
import {fetchEntitiesRequest,fetchSituationsRequest,fetchSubscriptionsRequest,fetchServicesRequest} from "./actions";


export default compose(
    connect(
        state => ({
            isConnected: state.connect.isConnected,
            baseURL: state.connect.baseURL,
            isLoading: state.service.isLoading,
            graphs:state.service.graphs,
            entities: state.toolbar.entities,
            subs:  state.toolbar.subs,
            situations :  state.toolbar.situations,
            services:  state.toolbar.services
        }),
        {fetchGraphsRequest,fetchEntitiesRequest,fetchSituationsRequest,fetchSubscriptionsRequest,fetchServicesRequest,},
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
                this.props.fetchEntitiesRequest(this.props.baseURL);
                this.props.fetchSituationsRequest(this.props.baseURL);
                this.props.fetchSubscriptionsRequest(this.props.baseURL);
                this.props.fetchServicesRequest(this.props.baseURL);
            }
        },
        componentWillReceiveProps(nextProps) {
            if(nextProps.isConnected && !this.props.isConnected)
            {
                this.props.fetchGraphsRequest(nextProps.baseURL);
                this.props.fetchEntitiesRequest(nextProps.baseURL);
                this.props.fetchSituationsRequest(nextProps.baseURL);
                this.props.fetchSubscriptionsRequest(nextProps.baseURL);
                this.props.fetchServicesRequest(nextProps.baseURL);
            }
        }
    })
)(ToolbarView);
