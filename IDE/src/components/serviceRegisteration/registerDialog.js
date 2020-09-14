import React, {useRef} from 'react';
import {makeStyles, useTheme} from "@material-ui/core/styles/index";
import TextField from '@material-ui/core/TextField';
import Button from "@material-ui/core/es/Button/Button";
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle'
import Autocomplete from '@material-ui/lab/Autocomplete';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Typography from '@material-ui/core/Typography';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import CheckBox from '@material-ui/core/Checkbox';
import AttributeSelection from './component/AttributeSelection/AttributeSelectionView';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormControl from '@material-ui/core/FormControl';
import FormLabel from '@material-ui/core/FormLabel';
import DialogActions from '@material-ui/core/DialogActions';
import Chip from '@material-ui/core/Chip';
import InputAdornment from '@material-ui/core/InputAdornment';
import Select from '@material-ui/core/Select';
import FormGroup from '@material-ui/core/FormGroup';
import {JsonEditor as JEditor} from 'jsoneditor-react';
import 'jsoneditor-react/es/editor.min.css';
import Paper from '@material-ui/core/Paper';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import AceEditor from 'react-ace'
import "ace-builds/src-noconflict/mode-json";
import "ace-builds/src-noconflict/theme-github";
import findIndex from 'lodash/findIndex';

const useStyles = makeStyles(theme => ({
    root: {
        minHeight: '80vh',
        maxHeight: '80vh',
        overflow: 'hidden'
    },
    content: {
        height: 'calc(80vh - 205px)',
        overflow: 'hidden',
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
    slaFormItem: {
        marginBottom: theme.spacing(1),
    },
    stepTab: {
        backgroundColor: '#EEEFEE',
        marginBottom: theme.spacing(1),
    },
    tabContent: {
        overflowY: 'auto',
        overflowX: 'hidden',
        height: 'calc(100% - 80px)',
    },
    editor: {
        width: '100% !important',
        height: 'calc(100% - 75px) !important',
    },
    editorReview: {
        width: '100% !important',
        height: '100% !important',
    }
}));

function getSteps() {
    return ['Enter Service Information', 'Map to Semantic Vocabulary', 'Meta data', 'Review'];
}

export default function RegisterDialog({serviceDescription,setServiceDescription,handleRegisterService, baseURL, ontClassDetails, serviceSampleResponse, terms, ontClasses, handleGraphChange, graphs, handleFetchService, handleFetchSemantic, openDialog, handleCloseDialog, ...props}) {

    const classes = useStyles();
    const theme = useTheme();

    const aceEditorInfo = useRef(null);
    const aceEditorAttrs = useRef(null);
    const aceEditorSLA = useRef(null);


    const [semantic, setSemantic] = React.useState('');
    const [expanded, setExpanded] = React.useState([]);
    const [graphClasses, setGraphClasses] = React.useState([]);
    const [tab, setTab] = React.useState([0, 0, 0]);
    const [selectedLeaf, setSelectedLeaf] = React.useState(null);


    const [rangeSelectDialog, setRangeSelectDialog] = React.useState({
        isOpen: false,
        tempParams: null,
        index: -1,
        ranges: []
    });
    const [selectedRange, setSelectedRange] = React.useState(null);
    const [classTerms, setClassTerms] = React.useState([]);
    const [selectedLeafMappedValue, setSelectedLeafMappedValue] = React.useState('');
    let reviewEditor = null;
    const [activeStep, setActiveStep] = React.useState(0);
    const [isTreeParsed, setIsTreeParsed] = React.useState(false);
    const [skipped, setSkipped] = React.useState(new Set());
    const steps = getSteps();


    const getTerms = () => {
        return terms != null ? Object.values(terms) : [];
    }


    const updateSLA = (key, value) => {
        const temp = JSON.parse(JSON.stringify(serviceDescription.sla));
        const keys = key.split('.');

        let item = temp;
        for (let i = 0; i < keys.length - 1; i++) {
            item = item[keys[i]];
        }
        item[keys[keys.length - 1]] = value;

        // serviceDescription.sla = temp;
        setServiceDescription({...serviceDescription, sla: temp});
    }


    const serviceURLChanged = (e) => {
        debugger;

        const regex = /{(.*?)}/gm;
        let m;
        const newParms = [];
        let index = 0;
        while ((m = regex.exec(e.target.value)) !== null) {
            // This is necessary to avoid infinite loops with zero-width matches
            if (m.index === regex.lastIndex) {
                regex.lastIndex++;
            }

            // The result can be accessed through the `m`-variable.
            m.forEach((match, groupIndex) => {
                if (match.startsWith('{')) {
                    let itemIndex = findIndex(serviceDescription.info.params,(i)=>i.name == match);
                    if(itemIndex!=-1){
                        newParms.push(serviceDescription.info.params[itemIndex]);
                        index++;
                    }else{
                        newParms.push({name: match, g: '', class: '', term: [], value: '', key: index++, termParent: ''});
                    }
                }
            });
        }
        setServiceDescription({
            ...serviceDescription,
            info: {...serviceDescription.info, serviceURL: e.target.value, params: newParms}
        });
        // setParams(newParms);
    }


    const getGraphClasses = (v, index) => {
        if (v != null && v.length > 0 && graphClasses[v] == null) {
            fetch(`${baseURL}/sv/classes/${encodeURIComponent(v.trim().substring(1, v.length - 1))}`, {method: 'GET'})
                .then((response) => {
                    return response.json();
                })
                .then((myJson) => {
                    graphClasses[v] = Object.values(myJson);
                    changeParam(index, 'g', v);
                });
        }
        else {
            changeParam(index, 'g', v);
        }

    }

    const findGraphClasses = (index) => {
        if (graphClasses[index]) {
            return graphClasses[index];
        }
        return []
    }

    const getTerm = (rangeData) => {
        try {
            let newData = rangeData;
            if (!newData.trim().startsWith('<')) {
                newData = '<' + newData + '>';
            }
            const rangeLastIndex = newData.lastIndexOf('/');
            if (rangeLastIndex != -1) {
                const range = newData.trim().substring(rangeLastIndex + 1, newData.length - 1);
                return range;
            }
        }
        catch (e) {
            debugger;
            return "asd";
        }
        return rangeData;
    }

    const getTermAndClass = (rangeData) => {
        try {
            if (!rangeData.trim().startsWith('<')) {
                rangeData = '<' + rangeData + '>';
            }
            const rangeLastIndex = rangeData.lastIndexOf('/');
            if (rangeLastIndex != -1) {
                const range = rangeData.trim().substring(rangeLastIndex + 1, rangeData.length - 1);
                const domain = rangeData.trim().substring(1, rangeLastIndex);
                return {range, domain};
            }
        }
        catch (e) {

        }
        return null;
    }


    const onRangeSelect = () => {
        const {tempParams, index} = rangeSelectDialog;
        tempParams[index].termParent = selectedRange;

        if (classTerms[selectedRange]) {
            tempParams[index].key = tempParams[index].key * -1;
            setServiceDescription({...serviceDescription, info: {...serviceDescription.info, params: tempParams}});
            // setParams(tempParams);
        } else {
            fetchClassTerms(selectedRange, () => {
                tempParams[index].key = tempParams[index].key * -1;
                setServiceDescription({...serviceDescription, info: {...serviceDescription.info, params: tempParams}});

                // setParams(tempParams);
            });
        }
        setRangeSelectDialog({
            isOpen: false,
            tempParams: null,
            ranges: [],
            index: -1
        });
    }

    const changeParam = (index, key, value, noUpdate) => {
        debugger;
        const tempParams = JSON.parse(JSON.stringify(serviceDescription.info.params));
        tempParams[index][key] = value;
        if (key == 'class') {
            tempParams[index].termParent = value;
            tempParams[index].term = [];
            if (classTerms[value] == null) {
                fetchClassTerms(value, () => {
                    if (noUpdate != true) {
                        tempParams[index].key = tempParams[index].key * -1;
                    }
                    setServiceDescription({
                        ...serviceDescription,
                        info: {...serviceDescription.info, params: tempParams}
                    });
                    // setParams(tempParams);
                });
                return;
            }
        } else if (key == 'g') {
            tempParams[index].class = '';
            tempParams[index].termParent = '';
            tempParams[index].term = [];
        }
        else if (key == 'term') {
            if (tempParams[index].term.length > 0) {
                if (tempParams[index].term.length < serviceDescription.info.params[index].term.length) {
                    let i = 0;
                    for (; i < serviceDescription.info.params[index].term.length - 1; i++) {
                        if (serviceDescription.info.params[index].term[i].subject != tempParams[index].term[i].subject) {
                            break;
                        }
                    }
                    tempParams[index].term = tempParams[index].term.slice(0, i);
                }
                const newTerm = tempParams[index].term[tempParams[index].term.length - 1];
                if (newTerm instanceof Object) {
                    const ranges = newTerm.range;
                    if (!Array.isArray(ranges)) {
                        if (classTerms[ranges] == null) {
                            fetchClassTerms(ranges, () => {
                                if (noUpdate != true) {
                                    tempParams[index].key = tempParams[index].key * -1;
                                }
                                setServiceDescription({
                                    ...serviceDescription,
                                    info: {...serviceDescription.info, params: tempParams}
                                });
                                // setParams(tempParams);
                            });
                            return;
                        }
                    }
                    else {
                        setRangeSelectDialog({
                            isOpen: true,
                            tempParams: tempParams,
                            ranges: ranges,
                            index: index
                        });
                    }
                }
                else {
                    tempParams[index].term[tempParams[index].term.length - 1] = {subject: newTerm};
                    tempParams[index].termParent = -1;
                }
            } else {
                tempParams[index].termParent = tempParams[index].class;
            }

        }
        if (noUpdate != true) {
            tempParams[index].key = tempParams[index].key * -1;
        }
        setServiceDescription({...serviceDescription, info: {...serviceDescription.info, params: tempParams}});
        // setParams(tempParams);
    }

    const fetchClassTerms = (parent, callback) => {
        try {

            const item = getTermAndClass(parent);
            fetch(`${baseURL}/sv/terms/${encodeURIComponent(item.domain)}/${encodeURIComponent(item.range)}`, {method: 'GET'})
                .then((response) => {
                    return response.json();
                })
                .then((myJson) => {
                    classTerms[parent] = Object.values(myJson);
                    callback();
                });
        }
        catch (e) {
            callback();
        }
    }

    const handleChangeTab = (newTab, step) => {
        const tempTap = JSON.parse(JSON.stringify(tab));
        if(tempTap[step] == 1 && newTab == 0)
        {
            try{
                switch (step) {
                case 0:
                    setServiceDescription({
                        ...serviceDescription,
                        info: JSON.parse(aceEditorInfo.current.editor.getValue())
                    })
                    break;
                case 1:
                    setServiceDescription({
                        ...serviceDescription,
                        attributes: JSON.parse(aceEditorAttrs.current.editor.getValue())
                    })
                    break;
                case 2:
                    setServiceDescription({
                        ...serviceDescription,
                        sla: JSON.parse(aceEditorSLA.current.editor.getValue())
                    })
                    break;
            }
            }catch (e) {
                return;
            }
        }
        tempTap[step] = newTab;
        setTab(tempTap);
    };

    const getCandidateKOptions = (attrs, prefix = '') => {

        let result = [];
        if (attrs == null)
            return result;

        for(let i in attrs)
        {
            const attr = attrs[i];
            if(attr.attrs != null)
            {
                result = result.concat(getCandidateKOptions(attr.attrs,prefix + attr.key.label + '.'));
            }else {
                result.push(prefix+(attr.key.label));
            }
        }

        return result;
    };


    const getStepContent = (step) => {
        switch (step) {
            case 0:
                return <div className={classes.content}>

                    <Paper square className={classes.stepTab}>
                        <Tabs
                            value={tab[step]}
                            indicatorColor="primary"
                            textColor="primary"
                            variant="fullWidth"
                            onChange={(event, newValue) => handleChangeTab(newValue, step)}
                            aria-label="disabled tabs example"
                        >
                            <Tab style={{width: '50%'}} label="Form"/>
                            <Tab style={{width: '50%'}} label="Json"/>
                        </Tabs>
                    </Paper>

                    {tab[step] == 0 && <form className={classes.tabContent} noValidate autoComplete="off">


                        <TextField id="service-name" value={serviceDescription.info.name} label="Service Name"
                                   fullWidth={true}
                                   onChange={(e) => setServiceDescription({
                                       ...serviceDescription,
                                       info: {...serviceDescription.info, name: e.target.value}
                                   })}/>

                        <TextField id="service-desc" value={serviceDescription.info.description} label="Service Description"
                                   fullWidth={true}
                                   multiline
                                   onChange={(e) => setServiceDescription({
                                       ...serviceDescription,
                                       info: {...serviceDescription.info, description: e.target.value}
                                   })}/>

                        <TextField id="service-url" value={serviceDescription.info.serviceURL} label="Service URL"
                                   fullWidth={true}
                                   onChange={serviceURLChanged}/>

                        {serviceDescription.info.params.length > 0 &&
                        serviceDescription.info.params.map((p, index) => <div
                            style={{display: 'flex', marginTop: '5px'}} key={'id' + p.key}>
                            <TextField style={{flex: .07}} value={p.name} label="Path name" disabled={true}/>
                            <Autocomplete
                                id={"ont-graph" + index}
                                options={graphs}
                                value={p.g}
                                onChange={(e, v) => getGraphClasses(v, index)}
                                getOptionLabel={option => option}
                                style={{flex: .25}}
                                renderInput={params => (
                                    <TextField {...params} label="Semantic Vocabulary" fullWidth/>
                                )}
                            />

                            <Autocomplete
                                id={"ont-class" + index}
                                options={findGraphClasses(p.g)}
                                value={p.class}
                                onChange={(e, v) => changeParam(index, 'class', v)}
                                getOptionLabel={option => getTerm(option)}
                                style={{flex: .28}}
                                renderInput={params => (
                                    <TextField {...params} label="Entity Type" fullWidth/>
                                )}
                            />

                            <Autocomplete
                                id={"ont-term" + index}
                                multiple
                                freeSolo
                                filterSelectedOptions
                                options={classTerms[p.termParent] != null ? classTerms[p.termParent] : []}
                                value={p.term}
                                onChange={(e, v) => {

                                    changeParam(index, 'term', v)
                                }}
                                getOptionLabel={option => getTerm(option.subject)}
                                style={{flex: .3}}
                                renderTags={(value, getTagProps) =>
                                    value.map((option, index) => (
                                        <span>{index > 0 && '.'}<Chip variant="outlined"
                                                                      label={getTerm(option.subject)} {...getTagProps({index})} /></span>
                                    ))
                                }
                                renderInput={params => (
                                    <TextField {...params} label="Term" fullWidth/>
                                )}
                            />

                            <TextField style={{flex: .1}} value={p.value}
                                       onChange={(e) => changeParam(index, 'value', e.target.value, true)}
                                       label="Value"/>
                        </div>)
                        }
                        <Autocomplete
                            id="ont-graph"
                            options={graphs}
                            value={serviceDescription.info.graph}
                            onChange={(e, v) => {
                                setServiceDescription({
                                    ...serviceDescription,
                                    info: {...serviceDescription.info, graph: v}
                                });
                                handleGraphChange(v)
                            }}
                            getOptionLabel={option => option}
                            style={{width: '100%'}}
                            renderInput={params => (
                                <TextField {...params} label="Semantic Vocabulary" fullWidth/>
                            )}
                        />
                        <Autocomplete
                            id="ont-class"
                            options={ontClasses}
                            value={serviceDescription.info.ontClass}
                            onChange={(e, v) => setServiceDescription({
                                ...serviceDescription,
                                info: {...serviceDescription.info, ontClass: v}
                            })}
                            getOptionLabel={option => option}
                            style={{width: '100%'}}
                            renderInput={params => (
                                <TextField {...params} label="Entity Type" fullWidth/>
                            )}
                        />

                        <TextField id="service-tag" value={serviceDescription.info.resultTag} label="Result Tag"
                                   fullWidth={true}
                                   onChange={(e) => setServiceDescription({
                                       ...serviceDescription,
                                       info: {...serviceDescription.info, resultTag: e.target.value==''?null:e.target.value}
                                   })}/>

                    </form>
                    }
                    {tab[step] == 1 &&
                    <AceEditor
                        mode="json"
                        className={classes.editor}
                        ref={aceEditorInfo}
                        defaultValue={JSON.stringify(serviceDescription.info, null, 2)}
                        theme="github"
                        name="editor-info"
                        editorProps={{$blockScrolling: true}}
                    />
                    }
                </div>;
            case 1:
                return <div className={classes.content}>
                    <Paper square className={classes.stepTab}>
                        <Tabs
                            value={tab[step]}
                            indicatorColor="primary"
                            textColor="primary"
                            variant="fullWidth"
                            onChange={(event, newValue) => handleChangeTab(newValue, step)}
                            aria-label="disabled tabs example"
                        >
                            <Tab style={{width: '50%'}} label="Form"/>
                            <Tab style={{width: '50%'}} label="Json"/>
                        </Tabs>
                    </Paper>
                    {tab[step] == 0 && <AttributeSelection attributes={serviceDescription.attributes}
                                                           setAttributes={(attributes) => setServiceDescription({
                                                               ...serviceDescription,
                                                               attributes: attributes
                                                           })}
                                                           serviceSampleResponse={serviceSampleResponse}
                                                           resultTag={serviceDescription.info.resultTag}
                                                           secondary={false}
                                                           baseURL={baseURL} graph={ontClassDetails.g}
                                                           ontClass={ontClassDetails.c}/>}

                    {tab[step] == 1 &&
                    <AceEditor
                        mode="json"
                        className={classes.editor}
                        ref={aceEditorAttrs}
                        defaultValue={JSON.stringify(serviceDescription.attributes, null, 2)}
                        theme="github"
                        name="editor-attributes"
                        editorProps={{$blockScrolling: true}}
                    />
                    }
                </div>
                break;
            case 2:
                return <div className={classes.content}>

                    <Paper square className={classes.stepTab}>
                        <Tabs
                            value={tab[step]}
                            indicatorColor="primary"
                            textColor="primary"
                            variant="fullWidth"
                            onChange={(event, newValue) => handleChangeTab(newValue, step)}
                            aria-label="disabled tabs example"
                        >
                            <Tab style={{width: '50%'}} label="Form"/>
                            <Tab style={{width: '50%'}} label="Json"/>
                        </Tabs>
                    </Paper>


                    {tab[step] == 0 && <form noValidate autoComplete="off">

                        <TextField id="sla-cost" value={serviceDescription.sla.cost.value} label="Cost per 1000 Calls"
                                   fullWidth={true}
                                   type="number"
                                   InputProps={{
                                       startAdornment: <InputAdornment position="start">
                                           <Select
                                               labelId="demo-simple-select-label"
                                               id="demo-simple-select"
                                               value={serviceDescription.sla.cost.unit}
                                               onChange={(e) => updateSLA('cost.unit', e.target.value)}
                                           >
                                               <option style={{cursor: 'pointer'}} value={'aud'}>A $</option>
                                               <option style={{cursor: 'pointer'}} value={'usd'}>US $</option>
                                               <option style={{cursor: 'pointer'}} value={'euro'}>€</option>
                                           </Select>
                                       </InputAdornment>,
                                   }}
                                   className={classes.slaFormItem}
                                   onChange={(e) => updateSLA('cost.value', e.target.value)}/>

                        <Autocomplete
                            id="candidate-key"
                            multiple
                            filterSelectedOptions
                            options={getCandidateKOptions(serviceDescription.attributes)}
                            value={serviceDescription.sla.key}
                            onChange={(e, v) => updateSLA('key', v)}
                            getOptionLabel={option => option}
                            style={{width: '100%'}}
                            renderInput={params => (
                                <TextField {...params} label="Candidate Key" fullWidth/>
                            )}
                        />

                        <FormControlLabel
                            style={{marginLeft: '1px'}}
                            className={classes.slaFormItem}
                            control={<CheckBox checked={serviceDescription.sla.cache}
                                               fullWidth={true}
                                               onChange={() => updateSLA('cache', !serviceDescription.sla.cache)}/>}
                            label="Enable Caching"
                        />

                        {serviceDescription.sla.cache &&
                        <FormGroup>
                            <TextField id="sla-uf" value={serviceDescription.sla.freshness.value}
                                       label="Freshness period"
                                       fullWidth={true}
                                       type="number"
                                       className={classes.slaFormItem}
                                       InputProps={{
                                           startAdornment: <InputAdornment position="start">
                                               <Select
                                                   labelId="demo-simple-select-label"
                                                   id="demo-simple-select"
                                                   value={serviceDescription.sla.freshness.unit}
                                                   onChange={(e) => updateSLA('freshness.unit', e.target.value)}
                                               >
                                                   <option style={{cursor: 'pointer'}} value={'ms'}>Ms</option>
                                                   <option style={{cursor: 'pointer'}} value={'s'}>Sec</option>
                                                   <option style={{cursor: 'pointer'}} value={'min'}>Min</option>
                                                   <option style={{cursor: 'pointer'}} value={'hour'}>Hour</option>
                                                   <option style={{cursor: 'pointer'}} value={'day'}>Day</option>
                                               </Select>
                                           </InputAdornment>,
                                       }}
                                       onChange={(e) => updateSLA('freshness.value', e.target.value)}/>
                            <FormControlLabel
                                style={{marginLeft: '1px'}}
                                className={classes.slaFormItem}
                                control={<CheckBox checked={serviceDescription.sla.autoFetch}
                                                   fullWidth={true}
                                                   onChange={() => updateSLA('autoFetch', !serviceDescription.sla.autoFetch)}/>}
                                label="Auto Fetch"
                            />
                            {serviceDescription.sla.autoFetch &&
                            <TextField id="sla-uf" value={serviceDescription.sla.updateFrequency.value}
                                       label="Update Frequency"
                                       fullWidth={true}
                                       type="number"
                                       className={classes.slaFormItem}
                                       InputProps={{
                                           startAdornment: <InputAdornment position="start">
                                               <Select
                                                   labelId="demo-simple-select-label"
                                                   id="demo-simple-select"
                                                   value={serviceDescription.sla.updateFrequency.unit}
                                                   onChange={(e) => updateSLA('updateFrequency.unit', e.target.value)}
                                               >
                                                   <option style={{cursor: 'pointer'}} value={'ms'}>Ms</option>
                                                   <option style={{cursor: 'pointer'}} value={'s'}>Sec</option>
                                                   <option style={{cursor: 'pointer'}} value={'min'}>Min</option>
                                                   <option style={{cursor: 'pointer'}} value={'hour'}>Hour</option>
                                                   <option style={{cursor: 'pointer'}} value={'day'}>Day</option>
                                               </Select>
                                           </InputAdornment>,
                                       }}
                                       onChange={(e) => updateSLA('updateFrequency.value', e.target.value)}/>}
                        </FormGroup>}


                    </form>}

                    {tab[step] == 1 &&
                    <AceEditor
                        mode="json"
                        ref={aceEditorSLA}
                        className={classes.editor}
                        defaultValue={JSON.stringify(serviceDescription.sla, null, 2)}
                        theme="github"
                        name="editor-sla"
                        editorProps={{$blockScrolling: true}}
                    />
                    }
                </div>;
                break;
            case 3:
                return <div className={classes.content}><AceEditor
                    mode="json"
                    onLoad={(editor, v2, v3, v4) => {

                        reviewEditor = editor
                    }}
                    className={classes.editorReview}
                    defaultValue={JSON.stringify(serviceDescription, null, 2)}
                    theme="github"
                    name="editor-review"
                    editorProps={{$blockScrolling: true}}
                /></div>;
                break;
            default:
                return 'Unknown step';
        }
    }


    const isStepOptional = step => {
        return false;
    };

    const isStepSkipped = step => {
        return false;
    };

    const handleNext = () => {
        let newSkipped = skipped;
        if (isStepSkipped(activeStep)) {
            newSkipped = new Set(newSkipped.values());
            newSkipped.delete(activeStep);
        }
        if (activeStep === 0) {
            handleFetchService(serviceDescription.info);
        }
        if (activeStep === 3) {
            handleRegisterService(reviewEditor.getValue());
            return;
        }

        setActiveStep(prevActiveStep => prevActiveStep + 1);
        setSkipped(newSkipped);
    };

    const handleBack = () => {
        if(activeStep == 3){
            try{
                setServiceDescription(JSON.parse(reviewEditor.getValue()));
            }catch (e) {

            }
        }
        setActiveStep(prevActiveStep => prevActiveStep - 1);
    };

    const handleSkip = () => {
        if (!isStepOptional(activeStep)) {
            // You probably want to guard against something like this,
            // it should never occur unless someone's actively trying to break something.
            throw new Error("You can't skip a step that isn't optional.");
        }

        setActiveStep(prevActiveStep => prevActiveStep + 1);
        setSkipped(prevSkipped => {
            const newSkipped = new Set(prevSkipped.values());
            newSkipped.add(activeStep);
            return newSkipped;
        });
    };

    const handleReset = () => {
        setActiveStep(0);
    };

    return (
        <Dialog classes={{paper: classes.root}} open={openDialog} fullWidth={true} maxWidth={'xl'}
                onClose={handleCloseDialog} aria-labelledby="form-dialog-register">
            <DialogTitle id="form-dialog-title">Register Context Provider</DialogTitle>
            <DialogContent className={classes.content}>


                <Dialog open={rangeSelectDialog.isOpen} fullWidth={true} maxWidth={'sm'}
                        aria-labelledby="form-dialog-select-range">
                    <DialogTitle id="form-dialog-title">Select the correct Range</DialogTitle>
                    <DialogContent className={classes.content}>
                        <FormControl component="fieldset">
                            <FormLabel component="legend">Gender</FormLabel>
                            <RadioGroup aria-label="range" name="range" value={selectedRange}
                                        onChange={(e) => setSelectedRange(e.target.value)}>
                                {rangeSelectDialog.ranges.map((r) => <FormControlLabel value={r} control={<Radio/>}
                                                                                       label={r}/>)}
                            </RadioGroup>
                        </FormControl>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={onRangeSelect} color="primary" autoFocus>
                            Select
                        </Button>
                    </DialogActions>
                </Dialog>


                <Stepper activeStep={activeStep}>
                    {steps.map((label, index) => {
                        const stepProps = {};
                        const labelProps = {};
                        if (isStepOptional(index)) {
                            labelProps.optional = <Typography variant="caption">Optional</Typography>;
                        }
                        if (isStepSkipped(index)) {
                            stepProps.completed = false;
                        }
                        return (
                            <Step key={label} {...stepProps}>
                                <StepLabel {...labelProps}>{label}</StepLabel>
                            </Step>
                        );
                    })}
                </Stepper>
                <div>
                    {activeStep === steps.length ? (
                        <div>
                            <Typography className={classes.instructions}>
                                All steps completed - you&apos;re finished
                            </Typography>
                            <Button onClick={handleReset} className={classes.button}>
                                Reset
                            </Button>
                        </div>
                    ) : (
                        <div>
                            <div
                                className={classes.instructions}>{getStepContent(activeStep, classes)}</div>
                            <div>
                                <Button
                                    variant="contained"
                                    color="secondary"
                                    onClick={handleCloseDialog}
                                    className={classes.button}
                                >
                                    Cancel
                                </Button>
                                <Button variant="contained" color="default" disabled={activeStep === 0}
                                        onClick={handleBack} className={classes.button}>
                                    Back
                                </Button>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleNext}
                                    className={classes.button}
                                >
                                    {activeStep === steps.length - 1 ? 'Register' : 'Next'}
                                </Button>
                                {activeStep !== steps.length - 1 && <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => setActiveStep(steps.length - 1)}
                                    className={classes.button}
                                >
                                    Skip
                                </Button>
                                }

                            </div>
                        </div>
                    )}
                </div>
            </DialogContent>
        </Dialog>
    )
}