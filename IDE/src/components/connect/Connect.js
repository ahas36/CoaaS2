import React, { useEffect, useState, useRef, }  from 'react';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';



const fetch = window.require('node-fetch');
const Store = window.require('electron-store');
const store = new Store();


const useStyles = makeStyles(theme => ({

}));


export default function ConnectView({ handleConnect, openDialog, handleCloseDialog, handleNotificationCall, setConnected, ...props}) {

    //get classes and theme
    const classes = useStyles();
    const theme = useTheme();

    const [baseURL, setBaseURL] = React.useState(store.get("url","http://localhost:8070/CASM-2.0.1/webresources/"));

    const connect = () => {
        let url = baseURL;

        if(url.endsWith('/'))
        {
            url = url.substr(0,url.length-1).trim();
        }
        handleConnect(url);
    };


    return (
            <Dialog open={openDialog} fullWidth={true} maxWidth={'sm'} onClose={handleCloseDialog} aria-labelledby="form-dialog-connect">
                <DialogTitle id="form-dialog-title">Connect</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Enter the base url of your CoaaS instance:
                    </DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        value={baseURL}
                        onChange={(e)=>setBaseURL(e.target.value)}
                        label="Base URL"
                        fullWidth
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={connect} color="primary">
                        Connect
                    </Button>
                </DialogActions>
            </Dialog>
    );
}
