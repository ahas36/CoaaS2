import React from "react";
import { Button } from "@material-ui/core";
import {
    NotificationsNone as NotificationsIcon,
    ThumbUp as ThumbUpIcon,
    ShoppingCart as ShoppingCartIcon,
    LocalOffer as TicketIcon,
    BusinessCenter as DeliveredIcon,
    SmsFailed as FeedbackIcon,
    DiscFull as DiscIcon,
    Email as MessageIcon,
    Report as ReportIcon,
    Error as DefenceIcon,
    AccountBox as CustomerIcon,
    Done as ShippedIcon,
    Publish as UploadIcon,
} from "@material-ui/icons";
import classnames from "classnames";
import tinycolor from 'tinycolor2';
import { makeStyles, useTheme  } from '@material-ui/core/styles';
import { Typography } from "../Wrappers/Wrappers.js";

const typesIcons = {
    "e-commerce": <ShoppingCartIcon />,
    notification: <NotificationsIcon />,
    offer: <TicketIcon />,
    info: <ThumbUpIcon />,
    message: <MessageIcon />,
    feedback: <FeedbackIcon />,
    customer: <CustomerIcon />,
    shipped: <ShippedIcon />,
    delivered: <DeliveredIcon />,
    defence: <DefenceIcon />,
    report: <ReportIcon />,
    upload: <UploadIcon />,
    disc: <DiscIcon />,
};

const getIconByType = (type = "offer") => typesIcons[type];

export default function Notification ({variant, ...props }) {
    const classes = useStyles();
    const theme = useTheme();
    const icon = getIconByType(props.type);
    const iconWithStyles = React.cloneElement(icon, {
        classes: {
            root: classes.notificationIcon
        },
        style: {
            color: variant !== "contained" && theme.palette[props.color] && theme.palette[props.color].main
        }
    });

    return (
        <div
            className={classnames(classes.notificationContainer, props.className, {
                [classes.notificationContained]: variant === "contained",
                [classes.notificationContainedShadowless]: props.shadowless,
            })}
            style={{
                backgroundColor:
                variant === "contained" &&
                theme.palette[props.color] &&
                theme.palette[props.color].main
            }}
        >
            <div
                className={classnames(classes.notificationIconContainer, {
                        [classes.notificationIconContainerContained]: variant === "contained",
                        [classes.notificationIconContainerRounded]: variant === "rounded",
                    }
                )}
                style={{
                    backgroundColor: variant === "rounded" &&
                    theme.palette[props.color] &&
                    tinycolor(theme.palette[props.color].main).setAlpha(0.15).toRgbString()
                }}
            >{iconWithStyles}</div>
            <div className={classes.messageContainer}>
                <Typography
                    className={classnames({
                        [classes.containedTypography]: variant === "contained"
                    })}
                    variant={props.typographyVariant}
                    size={variant !== "contained" && !props.typographyVariant && "md"}
                >
                    {props.message}
                </Typography>
                {props.extraButton && props.extraButtonClick && (<Button onClick={props.extraButtonClick} disableRipple className={classes.extraButton}>{props.extraButton}</Button>)}
            </div>
        </div>
    );
};

const useStyles  = makeStyles(theme => ({
    notificationContainer: {
        display: "flex",
        alignItems: "center"
    },
    notificationContained: {
        borderRadius: 45,
        minHeight: 45
    },
    notificationContainedShadowless: {
        boxShadow: 'none',
    },
    notificationIconContainer: {
        minWidth: 45,
        height: 45,
        borderRadius: 45,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: 24,
    },
    notificationIconContainerContained: {
        fontSize: 18,
        color: '#FFFFFF80',
    },
    notificationIconContainerRounded: {
        marginRight: theme.spacing(2),
    },
    containedTypography: {
        color: "white"
    },
    messageContainer: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexGrow: 1,
    },
    extraButton: {
        color: 'white',
        '&:hover, &:focus': {
            background: 'transparent',
        }
    },
}));

