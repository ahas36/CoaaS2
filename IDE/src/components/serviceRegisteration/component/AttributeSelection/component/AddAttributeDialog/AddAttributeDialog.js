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

const defSchemaName = 'http://exm.org#';

const getProperties = (item) =>{
    if(item.properties != null)
    {
        return item.properties;
    }
    if(item.anyOf != null)
    {
        return item.anyOf;
    }
    if(item.oneOf != null)
    {
        return item.oneOf;
    }
    if(item.allOf != null)
    {
        return item.allOf;
    }
    return {};
}
export default function AddAttributeDialog({schema, serviceSampleResponse, baseURL, graph, ontClass, handleCloseDialog, openDialog, addAttribute, ...props}) {

    const classes = useStyles();
    const theme = useTheme();
    let [terms, setTerms] = useState([]);
    let [attributeKey, setAttributeKey] = useState('');
    let [freeAttr, setFreeAttr] = useState('');
    let [hasValue, setHasValue] = useState(false);
    let [isGeoJson, setIsGeoJson] = useState(false);

    useEffect(() => {
        if (schema !== null) {
            let tempSchema = JSON.parse(JSON.stringify(schema));
            let temp = [];
            let parentPath = [];
            let stringPath = '';
            if (openDialog.graph.startsWith(defSchemaName)) {
                parentPath = openDialog.graph.substr(defSchemaName.length).split('/');
                parentPath.push(openDialog.ontClass);
                stringPath = parentPath.join('/');
                for (let i in parentPath) {
                    let path = parentPath[i].trim();
                    if (path === '')
                        continue;
                    let title = null;
                    if (path.includes('.')) {
                        let tempSplit = path.split('.');
                        path = tempSplit[0];
                        title = tempSplit[1]
                    }
                    for (let j in tempSchema) {
                        if (j === path) {
                            debugger;
                            tempSchema = getProperties(tempSchema[j]);
                            break;
                        }
                    }
                    if (title != null) {
                        debugger;
                        for (let j in tempSchema) {
                            if (tempSchema[j].title === title) {
                                tempSchema = getProperties(tempSchema[j]);
                                break;
                            }
                        }
                    }
                }
            }
            for (let i in tempSchema) {
                let range = defSchemaName + stringPath + '/' + i;

                try {
                    if (tempSchema[i].anyOf != null) {
                        range = [];
                        for (let j in tempSchema[i].anyOf) {
                            range.push(defSchemaName + stringPath + '/' + i + '.' + tempSchema[i].anyOf[j].title);
                        }
                    } else if (tempSchema[i].oneOf != null) {
                        range = [];
                        for (let j in tempSchema[i].oneOf) {
                            range.push(defSchemaName + stringPath + '/' + i + '.' + tempSchema[i].oneOf[j].title);
                        }
                    }
                } catch (e) {
                    console.log(e);
                    range = defSchemaName + stringPath + '/' + i;
                }


                temp.push({
                    subject: defSchemaName + stringPath + '/' + i,
                    range: range,
                    label: i
                });
            }
            setTerms(temp);
        } else {
            console.log(`${baseURL}/sv/terms/${encodeURIComponent(openDialog.graph)}/${encodeURIComponent(openDialog.ontClass)}`);
            fetch(`${baseURL}/sv/terms/${encodeURIComponent(openDialog.graph)}/${encodeURIComponent(openDialog.ontClass)}`, {method: 'GET'})
                .then((response) => {
                    return response.json();
                })
                .then((myJson) => {

                    setTerms(Object.values(myJson));
                });
        }

    }, [openDialog.ontClass, openDialog.graph, schema]);

    useEffect(() => {
        const item = JSON.parse(JSON.stringify(openDialog.item));
        setAttributeKey(item.key);
        setIsGeoJson(item.GeoJson == null ? false : item.GeoJson);
        setHasValue(item.hasValue == null ? false : item.hasValue);
    }, [openDialog.isOpen]);

    const expressionEditor = useCallback(node => {
        if (node !== null) {
            const item = JSON.parse(JSON.stringify(openDialog.item));
            node.setText(item.value);
            expressionEditor.current = node;
        }
    }, [openDialog.item.value, openDialog.item.key]);


    const getTerm = (data) => {
        try {
            const rangeData = data.subject;
            const rangeLastIndex = rangeData.lastIndexOf('/');
            if (rangeLastIndex != -1) {
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
            expressionEditor.current.addTerm('$' + item);
        }
    }


    const getValueFromResponse = (key) => {

        let item = JSON.parse(JSON.stringify(serviceSampleResponse));
        const keys = key.trim().split('.');
        for (let i in keys) {
            if(typeof item[keys[i]] === "string"){
                item = "'"+item[keys[i]]+"'";
                break;
            }
            item = item[keys[i]];
        }
        return item;
    }

    const parseExpression = (expr) => {
        try{
            const regex = /\$([a-z]|[A-Z])+(\d|-|_|([a-z]|[A-Z]))*(\.(\d|([a-z]|[A-Z])|-|_)+)*/gm;
            const matches = expr.match(regex);

            for (const match of matches) {
                let val = getValueFromResponse(match.substr(1));
                let re = new RegExp('\\' + match, 'g');
                expr = expr.replace(re, val);
            }
            return eval(expr);
        }catch (e) {
            return "NA";
        }

    }


    const add = () => {
        try {

            let key = {};
            if (attributeKey == null || attributeKey.label != freeAttr) {
                key.label = freeAttr;
                key.subject = 'http://no.schema/' + freeAttr;
                key.range = [];
            } else {
                key = attributeKey;
            }
            if (freeAttr != null) {
                if (!hasValue) {
                    addAttribute(key, '',
                        openDialog.index, openDialog.parent, isGeoJson && !hasValue, hasValue);
                    expressionEditor.current.clear();
                    return;
                } else if (expressionEditor.current != null && expressionEditor.current.state.expression != null) {
                    addAttribute(key, expressionEditor.current.state.expression.trim(),
                        openDialog.index, openDialog.parent, isGeoJson && !hasValue, hasValue, parseExpression(expressionEditor.current.state.expression.trim()));
                    expressionEditor.current.clear();
                    return;
                }

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
                                onInputChange={(event, newValue) => {
                                    setFreeAttr(newValue);
                                }}
                                onChange={(event, newValue) => {
                                    setAttributeKey(newValue);
                                }}
                                value={attributeKey}
                                options={terms}
                                getOptionLabel={option => getTerm(option)}
                                renderInput={params => (
                                    <TextField {...params} label="Attribute title" margin="normal"
                                               variant="outlined"
                                               fullWidth/>
                                )}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <FormControlLabel
                                control={<Checkbox checked={hasValue} onChange={() => {
                                    setHasValue(!hasValue)
                                }}
                                                   value="hasValue"/>}
                                label="Set Value"
                            />
                        </Grid>

                        {!hasValue && <Grid item xs={6}>
                            <FormControlLabel
                                control={<Checkbox checked={isGeoJson} onChange={() => setIsGeoJson(!isGeoJson)}/>}
                                label="Is GeoJson?"
                            />
                        </Grid>}

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