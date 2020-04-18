
var antlrEditor = require('antlr4-editor');
var implementation = require('antlr4-editor/implementations/codemirror');
var antlrHelper = require('antlr4-helper');
var CdqlLexer = require('./CdqlLexer').CdqlLexer;
var CdqlParser = require('./CdqlParser').CdqlParser;

var factory = antlrHelper.createFactoryBuilder()
    .lexer(function(input) { return new CdqlLexer(input); })
    .parser(function(tokenStream) { return new CdqlParser(tokenStream); })
    .rootRule(function(parser) { return parser.compilationUnit(); })
    .build();

var parser = antlrHelper.createParser(factory);


function createEditor() {
    return implementation.createEditor(parser);
}

exports.createEditor = createEditor;

