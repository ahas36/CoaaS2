import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import './css/codemirror-all-themes.css';
import './css/codemirror-editor.css';
import './css/codemirror-icons.css';
import {AutoCompleteEvent, AutoCompletionHandler} from "antlr4-editor";
import JSONTree from 'react-json-tree'
import { withStyles } from "@material-ui/core/styles/index";

const createEditor = require('../../../editor').createExpressionEditor;

const styles = theme => ({
    root: {
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
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
});

const ruleNames = ["expression", "assignmentExpression", "assignmentExpressionNoIn",
    "leftHandSideExpression", "newExpression", "memberExpression",
    "memberExpressionSuffix", "callExpression", "callExpressionSuffix",
    "arguments", "indexSuffix", "propertyReferenceSuffix",
    "assignmentOperator", "conditionalExpression", "conditionalExpressionNoIn",
    "logicalORExpression", "logicalORExpressionNoIn", "logicalANDExpression",
    "logicalANDExpressionNoIn", "bitwiseORExpression", "bitwiseORExpressionNoIn",
    "bitwiseXORExpression", "bitwiseXORExpressionNoIn", "bitwiseANDExpression",
    "bitwiseANDExpressionNoIn", "equalityExpression", "equalityExpressionNoIn",
    "relationalExpression", "relationalExpressionNoIn", "shiftExpression",
    "additiveExpression", "multiplicativeExpression", "unaryExpression",
    "postfixExpression", "primaryExpression", "arrayLiteral",
    "objectLiteral", "propertyNameAndValue", "propertyName",
    "literal"];

const editor = createEditor();

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



class CodeEditorView extends React.Component{


    state = {expression : '' ,debugRule :{
        matchingRule: '',
        parents: []
    }};

    addTerm = (item) =>
    {

        let cursorPosition = editor.getCursorPosition();
        let text = editor.getText().split('\n');
        text[cursorPosition.line] = text[cursorPosition.line].substr(0, cursorPosition.column) + ` ${item} ` + text[cursorPosition.line].substr(cursorPosition.column+1);

        cursorPosition.column = cursorPosition.column + item.length + 1;
        editor.setCursorPosition(cursorPosition);
        editor.setText(text.join('\n'));
        editor.focus();
    }

    //constructor
    constructor(props) {
        super(props);
        this.editorRef = React.createRef();
    }


    componentDidMount() {
        const app = this;
        let map = {
            "Ctrl-Space": function (cm) {
                app.showAutoComplete();
            }
        };

        editor.addKeyMapping(map);

        editor.defineErrorMessage('*', (error) => {
            return 'Please add a semicolon \';\'';
        });

        for (let item in ruleNames) {
            const matchingRule = ruleNames[item];
            editor.addAutoCompleteListener(matchingRule, (event) => {
                try {
                    let debugRule = {}

                    debugRule.matchingRule = event.rule.rule;
                    let text = event.rule.parser.getRuleText(debugRule.matchingRule);

                    debugRule.mainMatchedRule = event.rule.parser.getRuleName(debugRule.matchingRule);
                    debugRule.ruleText = event.rule.parser.getRuleText(debugRule.matchingRule);

                    const candidates = [];


                    debugRule.childrens = [];
                    switch (matchingRule) {
                        case 'primaryExpression':
                            const parentName = event.rule.parser.getRuleName(event.rule.getParent().rule);
                                if(parentName !== 'postfixExpression')
                                {
                                const terms = Object.keys(app.props.serviceResponse);
                                for (let t in terms) {
                                    candidates.push({displayText: terms[t], text: terms[t]});
                                }
                                break;
                            }
                        case 'leftHandSideExpression':
                            text = text.trim();
                            const parents = text.split('.');
                            if(parents.length>1)
                            {
                                text = parents.pop();
                            }
                            else {
                                text = '';
                            }
                            const parentsString = parents.join('.')+'.';
                            let object = JSON.parse(JSON.stringify(app.props.serviceResponse));
                            for (let p in parents) {
                                if (parents[p].trim() == '')
                                    continue;
                                object = object[parents[p]];
                            }
                            if (object instanceof Object) {
                                const subTerms = Object.keys(object);
                                for (let t in subTerms) {
                                    candidates.push({displayText: parentsString+subTerms[t], text: parentsString+subTerms[t]});
                                }
                            }
                            break;
                    }

                    const finalCandidates = [];
                    for (let item in candidates) {

                        if (candidates[item].displayText.trim().toLocaleLowerCase().startsWith(text.trim().toLocaleLowerCase())) {

                            finalCandidates.push(candidates[item])
                        }
                    }

                    debugRule.text = text;

                    debugRule.candidates = finalCandidates;

                    app.setState({debugRule:debugRule});
                    if (finalCandidates.length > 0)
                        event.showCompletions(finalCandidates);
                }
                catch (e) {

                }

            });
        }

        editor.setEditorPlaceholderText('Enter your code here...');
        editor.setShowLineNumbers(true);
        editor.setDisplayEditorErrors(true);
        editor.focus();
        editor.setTheme('idea');



        editor.addChangeListener(this.expressionChanged);

        const el = editor.getDomElement();
        this.editorRef.current.appendChild(el);
    }

    expressionChanged = (editor) => {
        this.setState({expression : editor.editor.getText()});
    }

    clear = () =>
    {
        this.setState({expression : '' ,debugRule :{
                matchingRule: '',
                parents: []
            }});
        editor.setText('');
    }


    showAutoComplete = () => {
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

    setText = (text) =>
    {
        editor.setText(text);
    }

    render() {
        const { classes} = this.props;
        const {debugRule} = this.state;
        return (
            <div className={classes.root}>
                <div className={classes.editor} ref={this.editorRef}></div>
                <JSONTree data={debugRule} theme={jsonTreeTheme} invertTheme={false}/>
            </div>

        );
    }
}


export default withStyles(styles)(CodeEditorView);
