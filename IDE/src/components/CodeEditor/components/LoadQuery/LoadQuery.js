import React, {useEffect, useState, useRef,} from 'react';
import {makeStyles, useTheme} from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';
import DeleteIcon from '@material-ui/icons/Delete';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import MUIDataTable from "mui-datatables";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";

const Store = window.require('electron-store');
const store = new Store();


const useStyles = makeStyles(theme => ({
    menuButton: {
        margin: theme.spacing(0, 2, 0, 0)
    },
}));


export default function LoadQuery(props) {

    //get classes and theme
    const classes = useStyles();
    const theme = useTheme();

    const [cdql, setCDQL] = React.useState('');
    const [queries, setQueries] = React.useState(store.get("savedCDQL", []));

    //constructor
    useEffect(() => setQueries(store.get("savedCDQL", [])), [props.open]);


    const columns = [
        {name: "title", label: "Title"},
        {name: "description", label: "Description"},
        {name: "cdql", label: "cdql", options: {display: false}},
    ]

    const options = {
        filter: false,
        responsive: 'scrollMaxHeight',
        expandableRows: true,
        expandableRowsOnClick: true,
        selectableRows: 'single',
        print: false,
        download: false,
        viewColumns: false,
        rowsExpanded: [],
        onRowsSelect: (currentRowsSelected, allRowsSelected) => {
            if (allRowsSelected.length == 0)
            {
                setCDQL('');
            }
            if(queries[allRowsSelected[0].dataIndex])
            {
                setCDQL(queries[allRowsSelected[0].dataIndex].cdql);
            }
        },
        renderExpandableRow: (rowData, rowMeta) => {
            const colSpan = rowData.length + 1;
            return (
                <TableRow>
                    <TableCell colSpan={colSpan}>
                        {queries[rowMeta.dataIndex] && queries[rowMeta.dataIndex].cdql}
                    </TableCell>
                </TableRow>
            );
        },
        onRowsDelete: (rowsDeleted) => {
            deleteQuery(rowsDeleted);
            return true;
        },
    };


    const deleteQuery = (rowsToRemove) => {
        const data = rowsToRemove.data;
        for (let item in data) {
            queries.splice(data[item].dataIndex, 1);
        }
        store.set("savedCDQL", queries);
    }

    const load = () => {
        props.loadQuery(cdql);
        props.handleClose();
    }




    return (
        <Dialog maxWidth="xl" fullWidth={true} open={props.open} onClose={props.handleClose}
                aria-labelledby="form-dialog-connect">
            <DialogTitle id="form-dialog-title">Load Query</DialogTitle>
            <DialogContent>
                <MUIDataTable key={queries.length}
                              title={'Please select a CDQL query from the table below.'}
                              data={queries} columns={columns} options={options}/>
            </DialogContent>
            <DialogActions>
                <Button onClick={props.handleClose} color="primary">
                    Cancel
                </Button>
                <Button disabled={cdql==''} onClick={load} color="primary">
                    Load
                </Button>
            </DialogActions>
        </Dialog>
    );
}
