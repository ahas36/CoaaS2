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

export default function ToolbarView({graphs, ...props}) {
    let nodeId = 0;
    const parseGraph = (g) => {
        if (g == null) return;
        return (
            <TreeItem key={nodeId} nodeId={nodeId++}
                      label={'Semantic Vocabs'}>
                {g.map(key => <TreeItem key={nodeId}  nodeId={nodeId++} label={key}/>)}
            </TreeItem>
        )
    };

    return (
        <TreeView
            defaultCollapseIcon={<ExpandMoreIcon/>}
            defaultExpandIcon={<ChevronRightIcon/>}>
            {parseGraph(graphs)}
            <TreeItem nodeId={nodeId++} label={'Entities'}>
                <TreeItem nodeId={nodeId++} label={'Parking Space'}/>
                <TreeItem nodeId={nodeId++} label={'Waste Container'}/>
            </TreeItem>
            <TreeItem nodeId={nodeId++} label={'Subscriptions'}>
                <TreeItem nodeId={nodeId++} label={'Preconditioning'}/>
            </TreeItem>
            <TreeItem nodeId={nodeId++} label={'Situation Functions'}>
                <TreeItem nodeId={nodeId++} label={'Good for walking'}/>
            </TreeItem>
        </TreeView>
    )
}

