import React, {useEffect, useState, useRef,} from 'react';
import {makeStyles, useTheme} from '@material-ui/core/styles';
import Box from '@material-ui/core/Box';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ClickAwayListener from '@material-ui/core/ClickAwayListener';
import Grow from '@material-ui/core/Grow';
import Paper from '@material-ui/core/Paper';
import Popper from '@material-ui/core/Popper';
import MenuItem from '@material-ui/core/MenuItem';
import MenuList from '@material-ui/core/MenuList';
import Divider from '@material-ui/core/Divider';
import AddIcon from '@material-ui/icons/Add'
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';


const Store = window.require('electron-store');
const store = new Store();


const useStyles = makeStyles(theme => ({}));


export default function SaveQuery(props) {

    //get classes and theme
    const classes = useStyles();
    const theme = useTheme();

    const [title, setTitle] = React.useState('');
    const [description, setDescription] = React.useState('');


    const save = () => {
        const item = {
            title: title,
            description: description,
            cdql: props.cdql
        };
        let list = store.get('savedCDQL');
        if (list) {
            let flag = true;
            for (let i in list) {
                if (list[i].title === title) {
                    list[i] = item;
                    store.set('savedCDQL', list);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                store.set('savedCDQL', list.concat([item]));
            }
        }
        else {
            store.set('savedCDQL', [item]);
        }
        props.handleClose();
    }


    return (
        <Dialog open={props.open} onClose={props.handleClose} aria-labelledby="form-dialog-connect">
            <DialogTitle id="form-dialog-title">Save Query</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    Please fill the form below to save your CDQL query.
                </DialogContentText>
                <TextField
                    autoFocus
                    margin="dense"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    label="Query name"
                    variant="outlined"
                    fullWidth
                />
                <TextField
                    margin="dense"
                    value={description}
                    multiline
                    rows="4"
                    onChange={(e) => setDescription(e.target.value)}
                    label="Query description (Optional)"
                    variant="outlined"
                    fullWidth
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={props.handleClose} color="primary">
                    Cancel
                </Button>
                <Button onClick={save} color="primary">
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
}
