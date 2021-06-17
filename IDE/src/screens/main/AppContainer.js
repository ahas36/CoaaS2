import React from "react";
import { compose, withState, withHandlers, lifecycle } from "recompose";
import { connect } from "react-redux";
import Notification from "../../components/Notification/Notification";
import {showNotification} from "../../components/Notification/NotificationState";
import AppView from "./App";
import { withStyles } from "@material-ui/core";
import { toast } from "react-toastify";
import {disconnectRequest} from "../../components/connect/actions";
const Store = window.require('electron-store');
const store = new Store();

export default compose(
    connect(
        state => ({
            isLoading: state.connect.isLoading,
            isConnected : state.connect.isConnected,
            baseURL : state.connect.baseURL,
            notification : state.notification.notification,
        }),
        {disconnectRequest, showNotification}
    ),
    withState('openConnectDialog', 'setOpenConnectDialog', false),
    withState('openRegisterDialog', 'setOpenRegisterDialog', false),
    withStyles(theme => ({
        progress: {
            visibility: "hidden"
        },
        notification: {
            display: "flex",
            alignItems: "center",
            background: "transparent",
            boxShadow: "none",
            overflow: "visible"
        },
        notificationComponent: {
            paddingRight: theme.spacing(4)
        }
    })),
    withHandlers({
        sendNotification: props => (componentProps, options) => {
            return toast(
                <Notification
                    {...componentProps}
                    className={props.classes.notificationComponent}
                />,
                options
            );
        },
        handleNotificationCall: props => () => {
            let notificationType=props.notification.type;
            let componentProps;
            switch (notificationType) {
                case "info":
                    componentProps = {
                        type: "feedback",
                        message: props.notification.message,
                        variant: "contained",
                        color: "primary"
                    };
                    break;
                case "error":
                    componentProps = {
                        type: "message",
                        message: props.notification.message,
                        variant: "contained",
                        color: "secondary",
                    };
                    break;
                default:
                    componentProps = {
                        type: "shipped",
                        message: props.notification.message,
                        variant: "contained",
                        color: "success"
                    };
            }
            return toast(
                <Notification
                    {...componentProps}
                    className={props.classes.notificationComponent}
                />,
                {
                    type: notificationType,
                    position: toast.POSITION.BOTTOM_LEFT,
                    progressClassName: props.classes.progress,
                    className: props.classes.notification
                }
            );
        },
        handleDisconnectRequest : props => ()=>{
            props.disconnectRequest();
        }
    }),
    lifecycle({
        componentWillReceiveProps(nextProps) {
            if (nextProps.notification !== null && (this.props.notification === null || this.props.notification.notificationID !== nextProps.notification.notificationID )) {
                this.props.handleNotificationCall();
                if(nextProps.notification.category == 'connect')
                {
                    this.props.setOpenConnectDialog(false);
                    if(nextProps.baseURL != null)
                    {
                        store.set("url",nextProps.baseURL);
                    }
                }

            }
        }
    })
)(AppView);
