import React, {useEffect, useState, useRef,} from 'react';
import PropTypes from 'prop-types';
import {makeStyles, useTheme} from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import RunIcon from '@material-ui/icons/PlayArrow';
import SaveIcon from '@material-ui/icons/Save';
import LoadIcon from '@material-ui/icons/Folder';
import SplitPane from 'react-split-pane';
import JSONTree from 'react-json-tree'
import ReactJson from 'react-json-view'
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import SaveQuery from './components/SaveQuery/SaveQuery.js';
import clsx from 'clsx';
import ResultMap from '../Map/ResultMap';
import './css/codemirror-all-themes.css';
import './css/codemirror-editor.css';
import './css/codemirror-icons.css';
import LoadQuery from "./components/LoadQuery/LoadQuery";
import {AutoCompleteEvent, AutoCompletionHandler} from "antlr4-editor";


const createEditor = require('../editor').createCDQLEditor;
const fetch = window.require('node-fetch');
const Store = window.require('electron-store');
const store = new Store();
const CdqlLexer = require('../editor/CdqlGrammar/CdqlLexer').CdqlLexer;
const CdqlParser = require('../editor/CdqlGrammar/CdqlParser').CdqlParser;
const jsonTreeTheme = {
    scheme: 'monokai',
    author: 'wimer hazenberg (http://www.monokai.nl)',
    base00: '#272822',
    base01: '#383830',
    base02: '#49483e',
    base03: '#75715e',
    base04: '#a59f85',
    base05: '#f8f8f2',
    base06: '#f5f4f1',
    base07: '#f9f8f5',
    base08: '#f92672',
    base09: '#fd971f',
    base0A: '#f4bf75',
    base0B: '#a6e22e',
    base0C: '#a1efe4',
    base0D: '#66d9ef',
    base0E: '#ae81ff',
    base0F: '#cc6633'
};


const useStyles = makeStyles(theme => ({
    root: {
        height: '100%',
        display: 'flex',
        flexDirection: 'Row'
    },
    content: {
        top: '66px !important'
    },
    toolbar: {
        backgroundColor: '#eeefee',
    },
    topbar: {
        minHeight: '32px !important'
    },
    editor: {
        width: '100%',
    },
    resultMenu: {
        backgroundColor: '#eeefee',
    },
    tabText: {
        color: 'black'
    },
    menuButton: {
        margin: theme.spacing(0, 2, 0, 0),
        color: '#3aad58'
    },
    tabContent: {
        minHeight: "calc(100% - 156px)",
        overflow: 'auto',
        display: 'none',
        height: 'calc(100% - 48px)',
        backgroundColor: '#272823'
    },
    visible: {
        display: 'block'
    },
}));


function a11yProps(index) {
    return {
        id: `scrollable-auto-tab-${index}`,
        'aria-controls': `scrollable-auto-tabpanel-${index}`,
    };
}

const ruleNames = ["rule_Cdql", "rule_ddl_statement", "rule_dml_statement",
    "rule_query", "rule_create_function", "rule_set_package",
    "rule_create_package", "rule_alter_package", "rule_alter_function",
    "rule_drop_package", "rule_drop_function", "rule_package_title",
    "rule_Set_Config", "rule_Output_Config", "rule_Prefixs",
    "rule_Prefix", "rule_Pull", "rule_Select", "rule_select_Attribute",
    "rule_select_FunctionCall", "rule_Attribute", "rule_EntityTitle",
    "rule_AttributeTitle", "rule_FunctionCall", "rule_call_FunctionTitle",
    "rule_call_Operand", "rule_Name_Operand", "rule_FunctionTitle",
    "rule_Operand", "rule_ContextValue", "rule_When", "rule_repeat",
    "rule_Start", "rule_Date_Time_When", "rule_Occurrence",
    "rule_Date_Time", "rule_Date", "rule_Time", "rule_Condition",
    "rule_expr_op", "rule_Constraint", "rule_left_element",
    "rule_right_element", "rule_target_element", "rule_relational_op_func",
    "rule_relational_op", "rule_between_op", "rule_is_or_is_not",
    "ruel_Push", "rule_callback", "rule_http_calback", "rule_fcm_calback",
    "rule_fcm_topic", "rule_fcm_token", "rule_callback_url",
    "rule_Define", "rule_Define_Context_Entity", "rule_context_entity",
    "rule_entity_type", "rule_Define_Context_Function", "rule_context_function",
    "rule_aFunction", "rule_sFunction", "rule_is_on", "rule_is_on_entity",
    "cst_situation_def_rule", "rule_single_situatuin", "rule_situation_pair",
    "rule_situation_attributes", "rule_situation_attribute_name",
    "situation_pair_values", "situation_weight", "situation_range_values",
    "situation_pair_values_item", "rule_situation_belief",
    "rule_situation_value", "rule_discrete_value", "discrete_value",
    "rule_region_value", "region_value_inclusive", "region_value_left_inclusive",
    "region_value_right_inclusive", "region_value_exclusive",
    "region_value_value", "rule_entity_id", "rule_function_id",
    "rule_url", "authority", "host", "hostname", "hostnumber",
    "search", "searchparameter", "port", "path", "normal_path",
    "path_param", "json", "obj", "pair", "array", "value"];

const editor = createEditor();


export default function CodeEditorView({isConnected, handleNotificationCall, ...props}) {

    //get classes and theme
    const classes = useStyles();
    const theme = useTheme();

    const [editorHeight, setEditorHeight] = React.useState(200);
    const [cdql, setCdql] = React.useState("");
    const [result, setResult] = React.useState({});
    const [tabIndex, setTabIndex] = React.useState(0);
    const [openSaveDialog, setOpenSaveDialog] = React.useState(false);
    const [openLoadDialog, setOpenLoadDialog] = React.useState(false);
    const [debugRule, setDebugRule] = React.useState({
        matchingRule: '',
        parents: []
    });

    const [viewport, setViewport] = useState({
        width: '100%',
        height: '100%',
        latitude: 37.7577,
        longitude: -122.4376,
        zoom: 8
    });

    //create a ref to ace instance
    const editorRef = useRef();


    //constructor
    useEffect(() => {


        let map = {
            "Ctrl-Space": function (cm) {
                showAutoComplete();
            }
        };

        editor.addKeyMapping(map);

        editor.defineErrorMessage('*', (error) => {
            return 'Please add a semicolon \';\'';
        });

        const autoCompleteKeywords = ['rule_Prefix'];


        for (let item in ruleNames) {
            const matchingRule = ruleNames[item];
            editor.addAutoCompleteListener(matchingRule, (event) => {

                let debugRule = {}

                debugRule.matchingRule = event.rule.rule;
                let text = event.rule.parser.getRuleText(debugRule.matchingRule);

                debugRule.mainMatchedRule = event.rule.parser.getRuleName(debugRule.matchingRule);
                debugRule.ruleText = event.rule.parser.getRuleText(debugRule.matchingRule);

                const candidates = [];


                debugRule.childrens = [];
                switch (matchingRule) {
                    case 'rule_Cdql':
                        candidates.push({displayText: "prefix namespace:URL", text: "prefix ns:"});
                        candidates.push({
                            displayText: "pull (...) ", text: 'pull ({{ selects }}) ',
                            placeholderVariables: {selects: {defaultValue: ''}}
                        });
                        break;
                    case 'rule_Prefixs':
                        candidates.push({displayText: "prefix namespace:URL", text: ", \nprefix ns:"});
                        break;
                    case 'rule_Prefix':

                        if (text.trim().length == 0) {
                            candidates.push({displayText: "prefix namespace:URL", text: " \nprefix ns:"});
                        }
                        else {
                            candidates.push({displayText: "prefix namespace:URL", text: "prefix ns:"});
                        }
                        break;
                    case 'authority':
                        text = text.substring(text.indexOf(':') + 1);
                        let semanticVocabs = ["http://schema.org", "http://mobivoc.schema.org"];
                        for (let i in semanticVocabs) {
                            let suggestion = semanticVocabs[i];
                            if (text.startsWith('http://')) {
                                suggestion = suggestion.substring('http://'.length);
                            }
                            candidates.push({displayText: semanticVocabs[i], text: suggestion});
                        }
                        break;
                    case 'hostname':
                            semanticVocabs = ["schema.org", "mobivoc.schema.org"];
                            for (let i in semanticVocabs) {
                                let suggestion = semanticVocabs[i];
                                candidates.push({displayText: semanticVocabs[i], text: suggestion});
                            }
                        break;
                    case 'rule_ddl_statement':
                        candidates.push({
                            displayText: "pull (...) ", text: ' \npull ({{ selects }}) ',
                            placeholderVariables: {selects: {defaultValue: ''}}
                        });
                        break;
                    case 'rule_Select':
                        if(text!='(')
                        candidates.push({displayText: "(", text: "("});
                }

                const finalCandidates = [];
                for (let item in candidates) {
                    if (candidates[item].displayText.trim().toLocaleLowerCase().startsWith(text.trim().toLocaleLowerCase())) {

                        finalCandidates.push(candidates[item])
                    }
                }

                debugRule.text = text;

                debugRule.candidates = finalCandidates;

                setDebugRule(debugRule);
                if (finalCandidates.length > 0)
                    event.showCompletions(finalCandidates);

            });
        }


        editor.setEditorPlaceholderText('Enter your code here...');
        editor.setShowLineNumbers(true);
        editor.setDisplayEditorErrors(true);
        editor.focus();
        editor.setTheme('idea');

        //

        var tokenStyles = {};

        //
        // Token Styles
        //
        tokenStyles['PACKAGE'] = 'cm-keyword';
        tokenStyles['CREATE'] = 'cm-keyword';
        tokenStyles['SET'] = 'cm-keyword';
        tokenStyles['ALTER'] = 'cm-keyword';
        tokenStyles['DROP'] = 'cm-keyword';
        tokenStyles['DEFINE'] = 'cm-keyword';
        tokenStyles['IS_FROM'] = 'cm-keyword';
        tokenStyles['WHERE'] = 'cm-keyword';
        tokenStyles['WHEN'] = 'cm-keyword';
        tokenStyles['LIFETIME'] = 'cm-keyword';
        tokenStyles['BETWEEN'] = 'cm-keyword';
        tokenStyles['IS'] = 'cm-keyword';
        tokenStyles['PULL'] = 'cm-keyword';
        tokenStyles['ENTITY'] = 'cm-keyword';
        tokenStyles['AS'] = 'cm-keyword';
        tokenStyles['EVERY'] = 'cm-keyword';
        tokenStyles['UNTIL'] = 'cm-keyword';
        tokenStyles['IN'] = 'cm-keyword';
        tokenStyles['CONTAINS_ANY'] = 'cm-keyword';
        tokenStyles['CONTAINS_ALL'] = 'cm-keyword';
        tokenStyles['PUSH'] = 'cm-keyword';
        tokenStyles['INTO'] = 'cm-keyword';
        tokenStyles['PREFIX'] = 'cm-keyword';
        tokenStyles['POST'] = 'cm-keyword';
        tokenStyles['OCCURRENCES'] = 'cm-keyword';
        tokenStyles['OUTPUT'] = 'cm-keyword';
        tokenStyles['CALLBACK'] = 'cm-keyword';

        editor.setDefaultTokenStyles(tokenStyles);

        editor.addChangeListener(cdqlChanged);

        const el = editor.getDomElement();
        editorRef.current.appendChild(el);
    }, []);

    const cdqlChanged = (editor) => {
        setCdql(editor.editor.getText());
    }

    const loadQuery = (cdql) => {
        editor.setText(cdql);
    }

    const handleChangeTab = (event, newValue) => {
        setTabIndex(newValue);
    };

    const handleResize = (size) => {
        setEditorHeight(size);
    }

    const showAutoComplete = () => {
        try {
            const rule = editor.autoCompletionHandler.getRuleHint()
            if (rule) {
                var stack_1 = [rule];
                while (stack_1.length > 0) {
                    var root = stack_1.pop();
                    var autoCompleteEvent = new AutoCompleteEvent(root, editor);
                    editor.triggerAutoCompletionEvent(autoCompleteEvent);
                    if (autoCompleteEvent.allowChildrenToConsumeEvent) {
                        root.getChildren()
                            .forEach(function (child) {
                                stack_1.push(child);
                            });
                    }
                }
            }
        }
        catch (e) {
        }



    }

    const run = () => {
        fetch(store.get("url"), {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: cdql})
            .then(res => {
                return res.json();
            })
            .then(json => {

                setResult(json);
            })
            .catch(err => {
                handleNotificationCall({type: 'error', message: 'Failed to execute'});
            });
    }

    return (
        <div className={classes.root}>
            <AppBar position="static" className={classes.toolbar}>
                <Toolbar variant='dense' className={classes.topbar}>
                    <Tooltip enterDelay={600} title="Load" aria-label="load">
                        <IconButton
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            onClick={() => setOpenLoadDialog(true)}
                            aria-label="open load dialog"
                            size={'small'}
                        >
                            <LoadIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>
                    <Tooltip enterDelay={600} title="Save" aria-label="save">
                        <IconButton
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            onClick={() => setOpenSaveDialog(true)}
                            aria-label="open save dialog"
                            size={'small'}
                        >
                            <SaveIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>

                    <Tooltip enterDelay={600} title="Run" aria-label="run">
                        <IconButton
                            onClick={run}
                            disabled={!isConnected}
                            edge="start"
                            className={classes.menuButton}
                            color="inherit"
                            aria-label="run query"
                            size={'small'}
                        >
                            <RunIcon fontSize="small"/>
                        </IconButton>
                    </Tooltip>
                </Toolbar>
            </AppBar>
            <SplitPane className={classes.content} split="horizontal" minSize={150} defaultSize={200} maxSize={-150}
                       onChange={handleResize}>
                <div className={classes.editor} ref={editorRef}></div>
                <div className={classes.result}
                     style={{
                         height: `calc(100vh - ${editorHeight + 99}px)`,
                         maxHeight: `calc(100vh - ${editorHeight + 99}px)`,
                         minHeight: `calc(100vh - ${editorHeight + 99}px)`
                     }}>
                    <AppBar position="static" color={"default"} className={classes.resultMenu}>
                        <Tabs
                            value={tabIndex}
                            variant="scrollable"
                            indicatorColor="primary"
                            onChange={handleChangeTab}
                            textColor="primary"
                        >
                            <Tab className={classes.tabText} label="Tree view result" {...a11yProps(0)} />
                            <Tab className={classes.tabText} label="JSON result" {...a11yProps(1)} />
                            <Tab className={classes.tabText} label="Map View" {...a11yProps(2)} />
                        </Tabs>
                    </AppBar>
                    <div className={clsx({
                        [classes.tabContent]: true,
                        [classes.visible]: 0 === tabIndex
                    })}>

                        <JSONTree data={debugRule} theme={theme} invertTheme={false}/>
                    </div>
                    <div className={clsx({
                        [classes.tabContent]: true,
                        [classes.visible]: 1 === tabIndex
                    })}>
                        <ReactJson src={result} theme='monokai' iconStyle='triangle'/>
                    </div>
                    <div className={clsx({
                        [classes.tabContent]: true,
                        [classes.visible]: 2 === tabIndex
                    })}>
                        <div style={{height: '100%', width: '100%'}}>
                            <ResultMap result={result}/>
                        </div>
                    </div>

                </div>
            </SplitPane>
            <SaveQuery open={openSaveDialog} handleClose={() => setOpenSaveDialog(false)} cdql={cdql}/>
            <LoadQuery open={openLoadDialog} handleClose={() => setOpenLoadDialog(false)} loadQuery={loadQuery}/>
        </div>

    );
}


CodeEditorView.propTypes = {
    cdql: PropTypes.string.isRequired
};
