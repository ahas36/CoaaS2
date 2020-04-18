import React, {useEffect} from 'react';
import {makeStyles, useTheme} from "@material-ui/core/styles/index";
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import TreeItem from '@material-ui/lab/TreeItem';


const useStyles = makeStyles(theme => ({
    root: {
        minHeight: '80vh',
        maxHeight: '80vh',
        overflow: 'hidden'
    },
}));

function RegisterDialog({serviceSampleResponse, treeItemClickHandel, ...props}) {
    let nodeId;
    const parseSampleResponse = (sr) => {
        if (sr == null) return;
        nodeId = 0;
        if (Array.isArray(sr)) {
            if (sr.length > 0) {
                return generateServiceTree(sr[0], '');
            }
        }
        return generateServiceTree(sr, '');
    }

    const parseTreeItem = (key, value, parent) => {
        parent = parent.length === 0 ? key : parent + '.' + key;
        if (value instanceof Object) {
            return <TreeItem key={nodeId} nodeId={nodeId++}
                             label={key}>{generateServiceTree(value, parent)}</TreeItem>
        }

        const id = nodeId;
        return <TreeItem key={nodeId} onClick={() => treeItemClickHandel(parent)} nodeId={nodeId++}
                         label={key + ' : ' + value}/>
    }


    const generateServiceTree = (sr, parent) => {
        const tree = Object.keys(sr).map(key => parseTreeItem(key, sr[key], parent));
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

export default React.memo(RegisterDialog, checkUpdate);

const checkUpdate = (p, n) => {

    return p.key == n.key;
}