import React, {useEffect, useState, useRef, useCallback} from 'react';
import {makeStyles, useTheme} from "@material-ui/core/styles/index";
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';
import ServiceTree from "../../../ServiceTree/ServiceTree";
import Autocomplete from '@material-ui/lab/Autocomplete';
import ExpressionEditor from '../../../ExpressionCodeEditor/CodeEditor';
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";

const useStyles = makeStyles(theme => ({
    root: {
        minHeight: '80vh',
        maxHeight: '80vh',
        overflow: 'hidden'
    },
}));

function getSteps() {
    return ['Enter Service Information', 'Map to Semantic Vocabulary', 'Register'];
}

export default function AddAttributeDialog({serviceSampleResponse, baseURL, graph, ontClass, handleCloseDialog, openDialog, addAttribute, ...props}) {

    const classes = useStyles();
    const theme = useTheme();
    let [terms, setTerms] = useState([]);
    let [attributeKey, setAttributeKey] = useState('');
    let [hasValue, setHasValue] = useState(false);

    useEffect(() => {
        console.log(`${baseURL}/sv/terms/${encodeURIComponent(openDialog.graph)}/${encodeURIComponent(openDialog.ontClass)}`);
        fetch(`${baseURL}/sv/terms/${encodeURIComponent(openDialog.graph)}/${encodeURIComponent(openDialog.ontClass)}`, {method: 'GET'})
            .then((response) => {
                return response.json();
            })
            .then((myJson) => {

                setTerms(Object.values(myJson));
            });
    }, [openDialog.ontClass,openDialog.graph]);


    const expressionEditor = useCallback(node => {
        if (node !== null) {
            node.setText(openDialog.item.value);
            setAttributeKey(openDialog.item.key);
            expressionEditor.current = node;
        }
    }, [openDialog.item.value, openDialog.item.key]);


    const getTerm = (data) => {
        try {
            const rangeData = data.subject;
            const rangeLastIndex = rangeData.lastIndexOf('/');
            if(rangeLastIndex!=-1)
            {
                const range = rangeData.trim().substring(rangeLastIndex + 1);
                return range;
            }
        }
        catch (e) {

        }
        return data;
    }


    const treeItemClickHandel = (item) => {
        if (expressionEditor.current != null) {
            expressionEditor.current.addTerm(item);
        }
    }

    const add = () => {
        try {
            if (attributeKey != null && expressionEditor.current.state.expression != null) {
                addAttribute(attributeKey, expressionEditor.current.state.expression.trim(), openDialog.index, openDialog.parent);
                expressionEditor.current.clear();
                return;
            }
        }
        catch (e) {

        }


    };

    return (
        <React.Fragment>
            <Dialog classes={{paper: classes.root}} open={openDialog.isOpen} fullWidth={true} maxWidth={'md'}
                    onClose={handleCloseDialog} aria-labelledby="form-dialog-register">
                <DialogTitle id="form-dialog-title">Add Attribute</DialogTitle>
                <DialogContent className={classes.content}>
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Autocomplete
                                id="key"
                                freeSolo
                                onChange={(event, newValue) => {
                                    setAttributeKey(newValue);
                                }}
                                value={attributeKey}
                                options={terms}
                                getOptionLabel={option => getTerm(option)}
                                renderInput={params => (
                                    <TextField {...params} label="Attribute title" margin="normal"
                                               onChange={(event, newValue) => {
                                                   setAttributeKey(newValue);
                                               }}
                                               variant="outlined"
                                               fullWidth/>
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControlLabel
                                control={<Checkbox checked={hasValue} onChange={() => setHasValue(!hasValue)}
                                                   value="hasValue"/>}
                                label="Set Value"
                            />
                        </Grid>
                        {hasValue &&
                        <Grid container xs={12}>
                            <Grid item xs={6}>
                                <ExpressionEditor ref={expressionEditor}
                                                  serviceResponse={(Array.isArray(serviceSampleResponse) && serviceSampleResponse.length > 0) ? serviceSampleResponse[0] : serviceSampleResponse}/>
                            </Grid>
                            <Grid item xs={6}>
                                <ServiceTree
                                    serviceSampleResponse={serviceSampleResponse}
                                    treeItemClickHandel={treeItemClickHandel}/>
                            </Grid>
                        </Grid>
                        }
                    </Grid>
                </DialogContent>
                <DialogActions>
                    <Button variant='contained' color='default' onClick={handleCloseDialog}>close</Button>
                    <Button variant='contained' color='primary' onClick={add}>Add</Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    )
}