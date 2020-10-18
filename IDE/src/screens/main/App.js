import React, {useEffect} from 'react';
import {makeStyles, useTheme} from '@material-ui/core/styles';
import SplitPane from 'react-split-pane';
import CodeEditor from '../../components/CodeEditor/CodeEditorContainer.js';
import RegisterDialog from '../../components/serviceRegisteration/RegisterDialogContainer';
import PostAddIcon from '@material-ui/icons/PostAdd';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import clsx from 'clsx';
import Connect from '../../components/connect/ConnectContainer.js';
import AppBar from '@material-ui/core/AppBar';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import ConnectIcon from '@material-ui/icons/Power';
import DisconnectIcon from '@material-ui/icons/PowerOff';
import CodeEditorIcon from '@material-ui/icons/Code';
import {ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Tooltip from '@material-ui/core/Tooltip';
import Toolbar from '@material-ui/core/Toolbar';
import ToolbarContainer from '../../components/Toolbar/ToolbarContainer';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
const createEditor = require('../../components/editor').createCDQLEditor;

const Mousetrap = require('mousetrap');

const useStyles = makeStyles(theme => ({
    menu: {},
    content: {
        height: 'calc(100% - 32px) !important',
    },
    component: {
        overflow: 'hidden',
        width: '100%'
    },
    tabContent: {
        height: "100%",
        display: 'none'
    },
    visible: {
        display: 'block'
    },
    button: {
        margin: theme.spacing(1),
    },
    notification: {
        display: "flex",
        alignItems: "center",
        background: "transparent",
        boxShadow: "none",
        overflow: "visible",
    },
    notificationComponent: {
        paddingRight: theme.spacing(4)
    },
    notificationCloseButton: {
        position: 'absolute',
        right: theme.spacing(2),
    },
    toastsContainer: {
        width: 400,
        marginTop: theme.spacing(6),
        right: 0,
    },
    progress: {
        visibility: "hidden"
    },
    notificationComponent: {
        paddingRight: theme.spacing(4)
    },
    menuButton: {
        margin: theme.spacing(0, 2, 0, 0),
        color: "#33af54"
    },
    tabHeader: {
        padding: 0,
    },
    closeIcon: {
        padding: 0,
    },
    topbar: {
        backgroundColor: '#EEEFEE',
        minHeight: '32px !important'
    },
    mainTab: {
        backgroundColor: '#d3d3d3 !important',
    },
    mainTabSelected: {
        backgroundColor: '#FFFFFF !important',
    }
}));


function a11yProps(index) {
    return {
        id: `scrollable-auto-tab-${index}`,
        'aria-controls': `scrollable-auto-tabpanel-${index}`,
    };
}

const CloseButton = ({closeToast, className}) => (
    <CloseIcon
        className={className}
        onClick={closeToast}
    />
);

let keyCounter = 0;
let tabChanged = 0;

const initialMouse = {
    mouseX: null,
    mouseY: null
};

function AppView({handleNotificationCall, isConnected, openConnectDialog, setOpenConnectDialog, openRegisterDialog, setOpenRegisterDialog, handleDisconnectRequest, ...props}) {

    const classes = useStyles();
    const theme = useTheme();

    const [tabContextMenu, setTabContextMenu] = React.useState(initialMouse);
    const [tabIndex, setTabIndex] = React.useState(0);
    const [tabs, setTabs] = React.useState([
        {
            label: "CDQL Editor",
            key: keyCounter++,
            content: <CodeEditor editor={createEditor()} cdql={""} handleNotificationCall={props.showNotification}/>
        }
    ]);
    useEffect(() => {

    });


    const handleChangeTab = (event, newValue) => {
        setTabIndex(newValue);
    };

    const handleActionBarClick = (index) => {
        switch (index) {
            case 0:
                handleDisconnectRequest();
                handleNotificationCall({
                    type: 'info',
                    message: 'The connection to the CoaaS instance has been terminated'
                })
                break;
            case 1:
                setOpenConnectDialog(true);
                break;
            case 2:
                tabs.push({
                    label: "CDQL Editor",
                    key: keyCounter++,
                    content: <CodeEditor editor={createEditor()} cdql={""}
                                         handleNotificationCall={props.showNotification}/>
                });
                setTabIndex(tabs.length - 1);
                break;
            case 3:
                setOpenRegisterDialog(true);
                break;
        }
    };


    const addTab = (tab) => {
        switch (tab.type) {
            case 'CDQL':
                tabs.push({
                    label: `View all ${tab.item}`,
                    key: keyCounter++,
                    content: <CodeEditor editor={createEditor()} cdql={tab.cdql}
                                         handleNotificationCall={props.showNotification} executeWhenOpen={true}/>
                });
                setTabIndex(tabs.length - 1);
                break;
            default:
        }
    }

    const handleCloseTab = (event, index) => {
        tabs.splice(index, 1);
        if (event != null) {
            event.stopPropagation();
        }
        if (index === tabIndex) {
            if (index == 0 && tabs.length > 0) {
                setTimeout(() => setTabIndex(tabIndex + 1), 1);
            }
            else {
                setTimeout(() => setTabIndex(tabIndex - 1), 1);
            }
        }
        else if (tabIndex > index) {
            setTimeout(() => setTabIndex(Math.max(0, tabIndex - 1)), 1);
        }
        else {
            setTimeout(() => setTabIndex(tabIndex - 1), 0);
            setTimeout(() => setTabIndex(tabIndex), 1);
        }

    };

    const tabContextClick = (event, tab, index) => {
        event.preventDefault();
        setTabContextMenu({
            mouseX: event.clientX - 2,
            mouseY: event.clientY - 4,
            index: index,
        });
    };

    const handleCloseContextMenu = () => {
        setTabContextMenu(initialMouse);
    };

    const closeTab = () => {
        handleCloseTab(null,tabContextMenu.index);
        handleCloseContextMenu();
    };

    const closeOtherTabs = () => {
        setTabs([tabs[tabContextMenu.index]]);
        setTabIndex(0);
        handleCloseContextMenu();
    };

    const closeAllTabs = () => {
        setTabs([]);
        setTabIndex(-1);
        handleCloseContextMenu();
    };

    return (
        <div className={classes.root}>
            <RegisterDialog handleNotificationCall={handleNotificationCall}
                            handleCloseDialog={() => setOpenRegisterDialog(false)}
                            openDialog={openRegisterDialog}/>
            <Connect handleNotificationCall={handleNotificationCall}
                     handleCloseDialog={() => setOpenConnectDialog(false)}
                     openDialog={openConnectDialog}/>

            <ToastContainer className={classes.toastsContainer}
                            closeButton={<CloseButton className={classes.notificationCloseButton}/>}
                            closeOnClick={false} progressClassName={classes.notificationProgress}/>
            <AppBar position={'static'}>
                <Toolbar variant='dense' className={classes.topbar}>
                    {isConnected && <Tooltip enterDelay={100} title="Disconnect" aria-label="Disconnect">
                        <IconButton
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            onClick={() => handleActionBarClick(0)}
                            aria-label="Disconnect"
                            size={'small'}
                        >
                            <DisconnectIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>}
                    {!isConnected && <Tooltip enterDelay={100} title="Connect" aria-label="Connect">
                        <IconButton
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            onClick={() => handleActionBarClick(1)}
                            aria-label="Connect"
                            size={'small'}
                        >
                            <ConnectIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>}

                    <Tooltip enterDelay={100} title="CDQL Editor" aria-label="CDQL Editor">
                        <IconButton
                            onClick={() => handleActionBarClick(2)}
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            aria-label="Open CDQL code editor"
                            size={'small'}
                        >
                            <CodeEditorIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>

                    <Tooltip enterDelay={100} title="Service Registration" aria-label="Service Registration">
                        <IconButton
                            onClick={() => handleActionBarClick(3)}
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            aria-label="Open Service Registration"
                            size={'small'}
                        >
                            <PostAddIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>
                </Toolbar>
            </AppBar>

            <SplitPane className={classes.content} split="vertical" minSize={200} defaultSize={200} maxSize={-100}>
                <div className={classes.component}><ToolbarContainer addTab={addTab}/></div>
                <div className={classes.component}>
                    <AppBar position="static" color="default" style={{minHeight: '24px'}}>
                        <Tabs
                            value={tabIndex}
                            onChange={handleChangeTab}
                            indicatorColor="primary"
                            textColor="primary"
                            variant="scrollable"
                            scrollButtons="auto"
                            style={{minHeight: '24px'}}
                            aria-label="IDE main tabs"
                            classes
                        >
                            {tabs.map((tab, index) => {
                                return <Tab style={{minHeight: '24px', minWidth: 'auto'}}
                                            value={index}
                                            onContextMenu={(e) => tabContextClick(e, tab, index)}
                                            key={`tab-${tab.key}`}
                                            className={clsx({
                                                [classes.mainTab]: index !== tabIndex,
                                                [classes.mainTabSelected]: index === tabIndex
                                            })}
                                            label={
                                                <div>
                                                    {tab.label}
                                                    <IconButton
                                                        className={classes.closeIcon}
                                                        onClick={(e) => {
                                                            handleCloseTab(e, index)
                                                        }}>
                                                        <CloseIcon fontSize="small"/>
                                                    </IconButton>
                                                </div>
                                            } {...a11yProps(index)} />;
                            })}
                        </Tabs>
                    </AppBar>
                    {tabs.map((tab, index) => <div key={`tab-content${tab.key}`} className={clsx({
                        [classes.tabContent]: true,
                        [classes.visible]: index === tabIndex
                    })}> {tab.content} </div>)}
                </div>
            </SplitPane>

            {/*tab context menu*/}
            <Menu
                keepMounted
                open={tabContextMenu.mouseY !== null}
                onClose={handleCloseContextMenu}
                anchorReference="anchorPosition"
                anchorPosition={
                    tabContextMenu.mouseY !== null && tabContextMenu.mouseX !== null
                        ? {top: tabContextMenu.mouseY, left: tabContextMenu.mouseX}
                        : undefined
                }>
                <MenuItem onClick={closeTab}>Close</MenuItem>
                <MenuItem onClick={closeOtherTabs}>Close Others</MenuItem>
                <MenuItem onClick={closeAllTabs}>Close All</MenuItem>
            </Menu>
        </div>
    );
}

export default AppView;