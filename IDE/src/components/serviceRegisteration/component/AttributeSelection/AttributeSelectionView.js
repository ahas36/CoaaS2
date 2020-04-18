import React, {useEffect, useState} from 'react';
import {makeStyles, useTheme} from "@material-ui/core/styles/index";
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import TreeItem from '@material-ui/lab/TreeItem';
import AddAttributeDialog from './component/AddAttributeDialog/AddAttributeDialog'
import AttributeTree from "../AttributeTree/AttributeTree";
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContentText from '@material-ui/core/DialogContentText';
import Button from '@material-ui/core/Button';
import Select from '@material-ui/core/Select';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Input from '@material-ui/core/Input';

const useStyles = makeStyles(theme => ({
    root: {
        minHeight: '80vh',
        maxHeight: '80vh',
        overflow: 'hidden'
    },
    content: {
        height: 'calc(80vh - 205px)',
        overflow: 'hidden'
    },
    semanticContainer: {
        display: 'flex',
        flexDirection: 'row',
        width: '100%'
    },
    button: {
        marginRight: theme.spacing(1),
    },
    instructions: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(1),
    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
    },
    selectEmpty: {
        marginTop: theme.spacing(2),
    },
    formContainer :{
        display: 'flex',
        flexWrap: 'wrap',
    }

}));

function getSteps() {
    return ['Enter Service Information', 'Map to Semantic Vocabulary', 'Register'];
}

export default function AttributeSelection({attributes, setAttributes,serviceSampleResponse, baseURL, graph, ontClass, ...props}) {

    const classes = useStyles();
    const theme = useTheme();
    const [treeKey, setTreeKey] = useState(0);
    const [selectedType, setSelectedType] = useState("");

    const [typeSelectDialogOpen, setTypeSelectDialogOpen] = useState({
        isOpen: false,
        items: [],
        dialogOpen: null
    });

    const [dialogOpen, setDialogOpen] = useState({
        isOpen: false,
        parent: null,
        ontClass: ontClass,
        graph: graph,
        index: -1,
        item: {
            key: '',
            value: ''
        }
    });


    const closeDialog = () => {
        setDialogOpen({
            isOpen: false,
            parent: null,
            ontClass: ontClass,
            graph: graph,
            index: -1,
            item: {
                key: '',
                value: ''
            }
        });
    }


    const addAttribute = (key, value, index, parent) => {

        if (parent != null) {
            let attr = attributes[parent[0]].attrs;
            let lastParent = attributes[parent[0]];
            for (let i = 1; i < parent.length; i++) {
                lastParent = attr[parent[i]];
                attr = attr[parent[i]].attrs;
            }

            if (attr == null) {
                lastParent.attrs = [];
                attr = lastParent.attrs;
            }

            if (index != -1) {
                attr[index].key = key;
                attr[index].value = value;
            }
            else {
                attr.push({key, value});
            }
        }
        else {
            if (index != -1) {
                attributes[index].key = key;
                attributes[index].value = value;
            }
            else {
                attributes.push({key, value});
            }
        }
        setTreeKey(treeKey + 1);
        closeDialog();
    }


    const onEdit = (item, index, parent) => {
        let semanticData = null;
        let g = graph;
        let oc = ontClass;

        const itemValue = {
            key: '',
            value: ''
        };

        let attr;
        if (parent) {
            attr = attributes[parent[0]];
            for (let i = 1; i < parent.length; i++) {
                attr = attr.attrs[parent[i]];
            }
            if (attr.key.range) {
                semanticData = getSemanticData(attr.key.range);

            }
            attr = attr.attrs[index];
        }
        else {
            attr = attributes[index];
        }

        itemValue.key = attr.key;
        itemValue.value = attr.value;

        if (Array.isArray(semanticData)) {
            setTypeSelectDialogOpen({
                isOpen: true,
                items: semanticData,
                dialogOpen: {
                    isOpen: true,
                    ontClass: oc,
                    graph: g,
                    parent: parent,
                    index: index,
                    item: itemValue
                }
            });
        }
        else {
            if (semanticData != null) {
                oc = semanticData.range;
                graph = semanticData.url;
            }
            setDialogOpen({
                isOpen: true,
                ontClass: oc,
                graph: g,
                parent: parent,
                index: index,
                item: itemValue
            });
        }
    }


    const getSemanticData = (rangeData) => {
        if (Array.isArray(rangeData)) {
            const result = [];
            for (let item in rangeData) {
                const rangeLastIndex = rangeData[item].lastIndexOf('/');
                const range = rangeData[item].trim().substring(rangeLastIndex + 1);
                const url = rangeData[item].trim().substring(0, rangeLastIndex);
                result.push({range, url});
            }
            return result;
        }
        const rangeLastIndex = rangeData.lastIndexOf('/');
        const range = rangeData.trim().substring(rangeLastIndex + 1);
        const url = rangeData.trim().substring(0, rangeLastIndex);
        return {range, url};
    }

    const onAdd = (item, parent) => {
        let g = graph;
        let oc = ontClass;
        let semanticData = null;
        const itemValue = {
            key: '',
            value: ''
        };

        if (parent) {
            let attr = attributes[parent[0]];
            for (let i = 1; i < parent.length; i++) {
                attr = attr.attrs[parent[i]];
            }
            if (attr.key.range) {
                semanticData = getSemanticData(attr.key.range);
            }
        }

        if (Array.isArray(semanticData)) {
            setTypeSelectDialogOpen({
                isOpen: true,
                items: semanticData,
                dialogOpen: {
                    isOpen: true,
                    parent: parent,
                    ontClass: oc,
                    graph: g,
                    index: -1,
                    item: itemValue
                }
            });
        }
        else {
            if (semanticData != null) {
                oc = semanticData.range;
                graph = semanticData.url;
            }
            setDialogOpen({
                isOpen: true,
                parent: parent,
                ontClass: oc,
                graph: g,
                index: -1,
                item: itemValue
            });
        }
    };

    const onDelete = (item, index, parent) => {
        if (parent) {
            let attr = attributes[parent[0]].attrs;
            for (let i = 1; i < parent.length; i++) {
                attr = attr[parent[i]].attrs;
            }
            attr.splice(index, 1);
        }
        else {
            attributes.splice(index, 1);
        }
        setTreeKey(treeKey + 1);
    };

    const handleTypeSelection = () => {
        if (selectedType) {
            const dialogOpenValue = JSON.parse(JSON.stringify(typeSelectDialogOpen.dialogOpen));
            // const semanticData = getSemanticData(selectedType);
            dialogOpenValue.ontClass = selectedType.range;
            dialogOpenValue.graph = selectedType.url;
            setDialogOpen(dialogOpenValue);
            handleCloseTypeSelectDialog();
        }
    }

    const handleCloseTypeSelectDialog = () => {
        setTypeSelectDialogOpen({
            isOpen: false,
            items: [],
            dialogOpen: null
        });
        setSelectedType("");
    }

    const handleChangeType = (e) => {
        setSelectedType(e.target.value);
    }


    let nodeId = 0;

    return (
        <React.Fragment>
            <TreeView
                defaultCollapseIcon={<ExpandMoreIcon/>}
                defaultExpandIcon={<ChevronRightIcon/>}>
                <Button variant="contained" color="primary" onClick={onAdd}>Add</Button>

                <AttributeTree
                    key={treeKey}
                    serviceSampleResponse={attributes}
                    onEdit={onEdit}
                    onAdd={onAdd}
                    onDelete={onDelete}
                />
            </TreeView>

            <AddAttributeDialog addAttribute={addAttribute}
                                serviceSampleResponse={serviceSampleResponse}
                                baseURL={baseURL} openDialog={dialogOpen}
                                handleCloseDialog={closeDialog}/>

            <Dialog open={typeSelectDialogOpen.isOpen} onClose={handleCloseTypeSelectDialog}
                    aria-labelledby="type-select-dialog">
                <DialogTitle id="type-select-dialog">Select Attribute Type</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        The selected attribute have more than one domain. Please select the domain and press the
                        'Select' button
                    </DialogContentText>
                    <form className={classes.formContainer}>
                        <FormControl className={classes.formControl}>
                            <InputLabel id="demo-simple-select-label">Domain</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={selectedType}
                                onChange={handleChangeType}
                                input={<Input />}
                            >
                                {typeSelectDialogOpen.items.map(item => <MenuItem value={item}>{item.range}</MenuItem>)}
                            </Select>
                        </FormControl>
                    </form>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseTypeSelectDialog} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleTypeSelection} color="primary">
                        Select
                    </Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    )
}