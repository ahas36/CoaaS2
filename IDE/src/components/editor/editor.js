
var antlrEditor = require('antlr4-editor');
var implementation = require('antlr4-editor/implementations/codemirror');
var antlrHelper = require('antlr4-helper');

var CdqlLexer = require('./CdqlGrammar/CdqlLexer').CdqlLexer;
var CdqlParser = require('./CdqlGrammar/CdqlParser').CdqlParser;

var ExpressionLexer = require('./ExpressionGrammar/ExpressionLexer').ExpressionLexer;
var ExpressionParser = require('./ExpressionGrammar/ExpressionParser').ExpressionParser;

var cdqlFactory = antlrHelper.createFactoryBuilder()
    .lexer(function(input) { return new CdqlLexer(input); })
    .parser(function(tokenStream) { return new CdqlParser(tokenStream); })
    .rootRule(function(parser) { return parser.rule_Cdql(); })
    .build();

var cdqlParser = antlrHelper.createParser(cdqlFactory);


var expressionFactory = antlrHelper.createFactoryBuilder()
    .lexer(function(input) {  return new ExpressionLexer(input); })
    .parser(function(tokenStream) { return new ExpressionParser(tokenStream); })
    .rootRule(function(parser) { return parser.expression(); })
    .build();

var expressionParser = antlrHelper.createParser(expressionFactory);


function createCDQLEditor() {
    return implementation.createEditor(cdqlParser);
}

function createExpressionEditor() {
    return implementation.createEditor(expressionParser);
}

exports.createCDQLEditor = createCDQLEditor;

exports.createExpressionEditor = createExpressionEditor;