import React, {useEffect} from 'react';
import {makeStyles, useTheme} from "@material-ui/core/styles/index";
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Collapse from '@material-ui/core/Collapse';
import SituationIcon from '@material-ui/icons/WbIncandescent';
import VocabIcon from '@material-ui/icons/GTranslate';
import ServiceIcon from '@material-ui/icons/Description';
import EntityIcon from '@material-ui/icons/Public';
import SubscriptionIcon from '@material-ui/icons/AllInclusive';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Divider from '@material-ui/core/Divider';
import Tooltip from '@material-ui/core/Tooltip';

const useStyles = makeStyles(theme => ({
    root: {
        minHeight: '80vh',
        maxHeight: '80vh',
        overflow: 'hidden'
    },
    mainItem:{
        backgroundColor: "#f6f6f6",
    },
    nested: {
        paddingLeft: theme.spacing(4),
        cursor: 'context-menu',
        backgroundColor: "#ffffff !important",
    },
}));

const initialState = {
    mouseX: null,
    mouseY: null
};


export default function ToolbarView({entities, subs, situations, services, isConnected, graphs, baseURL,addTab, ...props}) {

    const classes = useStyles();
    const [state, setState] = React.useState(initialState);
    const [open, setOpen] = React.useState({vocab: false, entity: false, situation: false, service: false});

    const menuItems = ["vocab", "service", "situation", /*"subscription",*/ "entity"]

    const handleListOpenClick = (name) => {
        const op = JSON.parse(JSON.stringify(open));
        op[name] = !op[name];
        setOpen(op);
    };


    const handleClose = () => {
        setState(initialState);
    };

    const handleClick = (event, name, item) => {
        event.preventDefault();
        if (!isConnected) {
            setState(initialState);
        }
        else {
            setState({
                mouseX: event.clientX - 2,
                mouseY: event.clientY - 4,
                type: name,
                item: item
            });
        }
    };


    // Vocab menu Items

    const registerVocab = () => {
        alert("registerVocab");
        handleClose();
    };

    const refreshVocab = () => {
        props.fetchGraphsRequest(baseURL);
        handleClose();
    };

    const viewVocab = () => {
        alert("viewVocab");
        handleClose();
    };

    const editVocab = () => {
        alert("editVocab");
        handleClose();
    };

    const deleteVocab = () => {
        alert("deleteVocab");
        handleClose();
    };

    // Service menu Items

    const registerService = () => {
        alert("registerService");
        handleClose();
    };

    const refreshService = () => {
        props.fetchServicesRequest(baseURL);
        handleClose();
    };

    const viewService = () => {
        alert("viewService");
        handleClose();
    };

    const editService = () => {
        alert("editService");
        handleClose();
    };

    const changeActiveService = () => {
        alert("changeActiveService");
        handleClose();
    };

    const deleteService = () => {
        alert("deleteService");
        handleClose();
    };

    // Subscription menu Items

    const registerSubscription = () => {
        alert("registerEntity");
        handleClose();
    };

    const refreshSubscription = () => {
        props.fetchSubscriptionsRequest(baseURL);
        handleClose();
    };

    const viewSubscription = () => {
        alert("viewAllEntities " + state.item);
        handleClose();
    };

    const toggleSubscriptionState = () => {
        alert("toggleSubscriptionState");
        handleClose();
    };

    const editSubscription = () => {
        alert("editSubscription");
        handleClose();
    };

    const deleteSubscription = () => {
        alert("deleteSubscription");
        handleClose();
    };

    // Situation menu Items

    const registerSituation = () => {
        alert("registerSituation");
        handleClose();
    };

    const refreshSituation = () => {
        props.fetchSituationsRequest(baseURL);
        handleClose();
    };

    const viewSituation = () => {
        alert("viewSituation");
        handleClose();
    };

    const editSituation = () => {
        alert("editSituation");
        handleClose();
    };

    const deleteSituation = () => {
        alert("deleteSituation");
        handleClose();
    };

    // entity menu Items


    const refreshEntity = () => {
        props.fetchEntitiesRequest(baseURL);
        handleClose();
    };

    const registerEntity = () => {
        alert("registerEntity");
        handleClose();
    };

    const viewAllEntities = () => {
        addTab({
            type:'CDQL',
            item: state.item,
            cdql: `pull (e.*) define \nentity e is from ${state.item}`
        });
        handleClose();
    };

    const discardEntity = () => {
        alert("discardEntity");
        handleClose();
    };

    const deleteEntity = () => {
        alert("deleteEntity");
        handleClose();
    };

    /////

    const getListIcon = (name) => {
        switch (name) {
            case 'vocab':
                return <VocabIcon/>;
            case 'service' :
                return <ServiceIcon/>;
            case 'situation' :
                return <SituationIcon/>;
            case 'entity' :
                return <EntityIcon/>;
            case 'subscription' :
                return <SubscriptionIcon/>
        }
    };

    const getListData = (name) => {
        switch (name) {
            case 'vocab':
                return graphs.map(item =>
                    <React.Fragment>
                        <Divider variant="middle"/>
                        <ListItem button className={classes.nested}
                                  onContextMenu={(e) => handleClick(e, name + 'Item', item)}
                                  onClick={(e) => handleClick(e, name + 'Item', item)}>
                            <ListItemText primary={item}/>
                        </ListItem>
                    </React.Fragment>
                );
            case 'service' :
                return services.map(item =>
                    <React.Fragment>
                        <Divider variant="middle"/>
                        <Tooltip enterDelay={500} leaveDelay={200} title={item.info.description}><ListItem button className={classes.nested}
                                  onContextMenu={(e) => handleClick(e, name + 'Item', item)}
                                  onClick={(e) => handleClick(e, name + 'Item', item)}>
                            <ListItemText primary={item.info.name}/>
                        </ListItem></Tooltip>
                    </React.Fragment>
                );
            case 'situation' :

                return situations.map(item =>
                    <React.Fragment>
                        <Divider variant="middle"/>
                        <Tooltip enterDelay={500} leaveDelay={200} title={item.raw}><ListItem button className={classes.nested}
                                  onContextMenu={(e) => handleClick(e, name + 'Item', item)}
                                  onClick={(e) => handleClick(e, name + 'Item', item)}>
                            <ListItemText primary={item.title}/>
                        </ListItem></Tooltip>
                    </React.Fragment>
                );
            case 'entity' :
                return entities.map(item =>
                    <React.Fragment>
                        <Divider variant="middle"/>
                        <ListItem button className={classes.nested}
                                  onContextMenu={(e) => handleClick(e, name + 'Item', item)}
                                  onClick={(e) => handleClick(e, name + 'Item', item)}>
                            <ListItemText primary={item}/>
                        </ListItem>
                    </React.Fragment>
                );
            case 'subscription' :

                return subs.map(item =>
                    <React.Fragment>
                        <Divider variant="middle"/>
                        <ListItem button className={classes.nested}
                                  onContextMenu={(e) => handleClick(e, name + 'Item', item)}
                                  onClick={(e) => handleClick(e, name + 'Item', item)}>
                            <ListItemText primary={item}/>
                        </ListItem>
                    </React.Fragment>
                );
        }
    };

    return (
        <React.Fragment>
            <List
                component="nav"
                aria-labelledby="nested-list-subheader"
                className={classes.root}
                dense={true}
            >

                {menuItems.map(name => <React.Fragment>
                        <ListItem button onClick={() => handleListOpenClick(name)} className={classes.mainItem}
                                  onContextMenu={(e) => handleClick(e, name)} style={{cursor: 'context-menu'}}>
                            <ListItemIcon>
                                {getListIcon(name)}
                            </ListItemIcon>
                            <ListItemText primary={name}/>
                            {isConnected && (open[name] ? <ExpandLess/> : <ExpandMore/>)}
                        </ListItem>
                        <Collapse in={open[name] && isConnected} timeout="auto" unmountOnExit>
                            <List component="div" disablePadding dense={true}>
                                {getListData(name)}
                            </List>
                        </Collapse>
                        <Divider/>
                    </React.Fragment>
                )}

            </List>
            <Menu
                keepMounted
                open={state.mouseY !== null && isConnected}
                onClose={handleClose}
                anchorReference="anchorPosition"
                anchorPosition={
                    state.mouseY !== null && state.mouseX !== null
                        ? {top: state.mouseY, left: state.mouseX}
                        : undefined
                }>
                {state.type == 'vocab' && <React.Fragment>
                    <MenuItem onClick={registerVocab}>Register</MenuItem>
                    <MenuItem onClick={refreshVocab}>Refresh</MenuItem>
                </React.Fragment>}

                {state.type == 'vocabItem' && <React.Fragment>
                    <MenuItem onClick={viewVocab}>View</MenuItem>
                    <MenuItem onClick={editVocab}>Edit</MenuItem>
                    <MenuItem onClick={deleteVocab}>Delete</MenuItem>
                </React.Fragment>}

                {state.type == 'entity' && <React.Fragment>
                    <MenuItem onClick={registerEntity}>Register</MenuItem>
                    <MenuItem onClick={refreshEntity}>Refresh</MenuItem>
                </React.Fragment>}

                {state.type == 'entityItem' && <React.Fragment>
                    <MenuItem onClick={viewAllEntities}>View</MenuItem>
                    <MenuItem onClick={discardEntity}>Clear</MenuItem>
                    <MenuItem onClick={deleteEntity}>Delete</MenuItem>
                </React.Fragment>}

                {state.type == 'situation' && <React.Fragment>
                    <MenuItem onClick={registerSituation}>Register</MenuItem>
                    <MenuItem onClick={refreshSituation}>Refresh</MenuItem>
                </React.Fragment>}
                {state.type == 'situationItem' && <React.Fragment>
                    <MenuItem onClick={viewSituation}>View</MenuItem>
                    <MenuItem onClick={editSituation}>Edit</MenuItem>
                    <MenuItem onClick={deleteSituation}>Delete</MenuItem>
                </React.Fragment>}

                {state.type == 'service' && <React.Fragment>
                    <MenuItem onClick={registerService}>Register</MenuItem>
                    <MenuItem onClick={refreshService}>Refresh</MenuItem>
                </React.Fragment>}
                {state.type == 'serviceItem' && <React.Fragment>
                    <MenuItem onClick={viewService}>View</MenuItem>
                    <MenuItem onClick={changeActiveService}>Toggle Active</MenuItem>
                    <MenuItem onClick={editService}>Edit</MenuItem>
                    <MenuItem onClick={deleteService}>Delete</MenuItem>
                </React.Fragment>}

                {state.type == 'subscription' && <React.Fragment>
                    <MenuItem onClick={registerSubscription}>Register</MenuItem>
                    <MenuItem onClick={refreshSubscription}>Refresh</MenuItem>
                </React.Fragment>}
                {state.type == 'subscriptionItem' && <React.Fragment>
                    <MenuItem onClick={viewSubscription}>Evaluate</MenuItem>
                    <MenuItem onClick={toggleSubscriptionState}>Toggle Active</MenuItem>
                    <MenuItem onClick={editSubscription}>Edit</MenuItem>
                    <MenuItem onClick={deleteSubscription}>Delete</MenuItem>
                </React.Fragment>}


            </Menu>


        </React.Fragment>
    )
}

