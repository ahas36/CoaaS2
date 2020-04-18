import React, {useEffect} from 'react';
import {makeStyles, useTheme} from "@material-ui/core/styles/index";
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import AddIcon from '@material-ui/icons/Add';
import EditIcon from '@material-ui/icons/Edit';
import TreeItem from '@material-ui/lab/TreeItem';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import DeleteIcon from '@material-ui/icons/Delete';

const useStyles = makeStyles(theme => ({
    root: {
        minHeight: '80vh',
        maxHeight: '80vh',
        overflow: 'hidden'
    },
    treeItem:{
        display: 'flex'
    },
    treeItemText:{
        flexGrow: 1
    },

}));


function AttributeTree({serviceSampleResponse, onDelete,onEdit,onAdd, ...props}) {

    const classes = useStyles();
    const theme = useTheme();

    let nodeId;
    const parseSampleResponse = (sr) => {
        if (sr == null) return;
        nodeId = 0;
        return generateServiceTree(sr, null);
    }

    const parseTreeItem = (item, parent,index) => {
        let newParent;
        if (parent == null)
        {
            newParent = [index];
        }else {
            newParent = JSON.parse(JSON.stringify(parent));
            newParent.push(index);
        }
        if (item.attrs) {
            return <TreeItem key={nodeId} nodeId={nodeId++}
                             label={
                                 <span className={classes.treeItem}>
                                 <Typography className={classes.treeItemText}>{item.key.subject}</Typography>
                                 <ButtonGroup variant="contained" color="primary" aria-label="contained primary button group">
                                     <IconButton aria-label="delete" className={classes.margin} onClick={(e)=>{e.stopPropagation(); onDelete(item,index,parent);}}>
                                         <DeleteIcon fontSize="small"/>
                                     </IconButton>
                                     <IconButton aria-label="edit" className={classes.margin} onClick={(e)=>{e.stopPropagation();onEdit(item,index,parent)}}>
                                         <EditIcon fontSize="small"/>
                                     </IconButton>
                                     <IconButton aria-label="add" className={classes.margin} onClick={(e)=>{e.stopPropagation();onAdd(item,newParent)}}>
                                         <AddIcon fontSize="small" />
                                     </IconButton>
                                    </ButtonGroup>
                             </span>}>{generateServiceTree(item.attrs, newParent)}</TreeItem>
        }

        const id = nodeId;
        return <TreeItem key={nodeId} nodeId={nodeId++}
                         label={
                             <span className={classes.treeItem}>
                                 <Typography className={classes.treeItemText}>{item.key.subject + ' : ' + item.value}</Typography>
                                 <ButtonGroup variant="contained" color="primary" aria-label="contained primary button group">
                                     <IconButton aria-label="delete" className={classes.margin} onClick={(e)=>{e.stopPropagation(); onDelete(item,index,parent);}}>
                                         <DeleteIcon fontSize="small"/>
                                     </IconButton>
                                     <IconButton aria-label="edit" className={classes.margin} onClick={(e)=>{e.stopPropagation();onEdit(item,index,parent)}}>
                                         <EditIcon fontSize="small"/>
                                     </IconButton>
                                     <IconButton aria-label="add" className={classes.margin} onClick={(e)=>{e.stopPropagation();onAdd(item,newParent)}}>
                                         <AddIcon fontSize="small" />
                                     </IconButton>
                                    </ButtonGroup>
                             </span>}/>
    }


    const generateServiceTree = (sr, parent) => {

        const tree = sr.map((item,index) => parseTreeItem(item, parent,index));
        return tree;
    };

    return (
        <TreeView
            defaultCollapseIcon={<ExpandMoreIcon/>}
            defaultExpandIcon={<ChevronRightIcon/>}>
            {parseSampleResponse(serviceSampleResponse)}
        </TreeView>
    )
}

export default React.memo(AttributeTree, checkUpdate);

const checkUpdate = (p, n) => {

    return p.key == n.key;
}