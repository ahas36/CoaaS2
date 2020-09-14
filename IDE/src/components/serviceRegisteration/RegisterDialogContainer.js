import {compose, lifecycle, withHandlers, withState} from 'recompose';
import {connect} from 'react-redux';
import RegisterDialog from './registerDialog';
import {fetchGraphsRequest, fetchClassesRequest, fetchServiceRequest, fetchTermsRequest, registerService} from "./actions";

const defaultDesc= {
    info: {
        serviceURL: 'https://data.melbourne.vic.gov.au/resource/vh2v-4nfs.json',
        ontClass: '',
        graph: '',
        name: '',
        description: '',
        resultTag:null,
        params: []
    },
    sla: {
        cache: false,
        cost: {unit: 'aud', value: 0},
        updateFrequency: {unit: 's', value: 120},
        autoFetch: true,
        freshness: {unit: 's', value: 120},
    },
    attributes: []
};

export default compose(
    connect(
        state => ({
            isConnected: state.connect.isConnected,
            baseURL: state.connect.baseURL,
            isLoading: state.service.isLoading,
            serviceSampleResponse:state.service.serviceSampleResponse,
            graphs:state.service.graphs,
            ontClasses:state.service.ontClasses,
            terms: state.service.terms,
            registeredService: state.service.registeredService
        }),
        {fetchGraphsRequest, fetchServiceRequest, fetchClassesRequest,fetchTermsRequest,registerService},
    ),
    // withState('graph','setGraph',null),
    withState('serviceTermID','setServiceTermID',0),
    // withState('ontClass','setOntClass',null),
    withState('leafTerms', 'setLeafTerms',{}),
    withState('serviceDescription','setServiceDescription',defaultDesc),
    withState('saveSuccess', 'setSaveSuccess',false),
    withState('ontClassDetails', 'setOntClassDetails',{g:null,c:null}),
    withHandlers({
        handleFetchService: props => (info) => {
            if(!props.isConnected)
            {
                return;
            }
            if(info.serviceURL!=null)
            {
                let serviceURL= JSON.parse(JSON.stringify(info.serviceURL));
                for(let i in info.params)
                {
                    const item = info.params[i];
                    serviceURL = serviceURL.replace(item.name,item.value);
                }
                props.fetchServiceRequest(serviceURL);
            }
            let temp= {
                g:info.graph.trim().substring(1,info.graph.length-1),
                c:info.ontClass.trim().substring(info.ontClass.trim().lastIndexOf('/')+1,info.ontClass.length-1)
            }
            props.setOntClassDetails(temp);
            props.fetchTermsRequest({baseURL:props.baseURL,graph:temp.g,ontClass:temp.c})
        },
        handleGraphChange: props => (graph) =>
        {
            if(!props.isConnected)
            {
                return;
            }
            // props.setGraph(graph);
            props.fetchClassesRequest({baseURL:props.baseURL,graph:graph});
        },
        handleRegisterService : props => (serviceDescription) =>
        {
          props.registerService({baseURL:props.baseURL,serviceDescription});
        },
        handleFetchTermsRequest: props => (graph,ontClass,g,oc) =>
        {
            if(!props.isConnected)
            {
                return;
            }
            if(g==null)
            {
                props.fetchTermsRequest({baseURL:props.baseURL,graph:graph.trim().substring(1,graph.length-1),ontClass:ontClass.trim().substring(ontClass.trim().lastIndexOf('/')+1,ontClass.length-1)})
                return;
            }
            props.fetchTermsRequest({baseURL:props.baseURL,graph:g,ontClass:oc})
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
            if(nextProps.registeredService != this.props.registeredService){
                this.props.handleCloseDialog();
                this.props.setServiceDescription(defaultDesc);
            }
        }
    })
)(RegisterDialog);