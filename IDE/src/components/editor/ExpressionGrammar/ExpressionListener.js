// Generated from /Users/ali/CDQLParserV3/expression/Expression.g4 by ANTLR 4.6
// jshint ignore: start
var antlr4 = require('antlr4/index');

// This class defines a complete listener for a parse tree produced by ExpressionParser.
function ExpressionListener() {
	antlr4.tree.ParseTreeListener.call(this);
	return this;
}

ExpressionListener.prototype = Object.create(antlr4.tree.ParseTreeListener.prototype);
ExpressionListener.prototype.constructor = ExpressionListener;

// Enter a parse tree produced by ExpressionParser#expression.
ExpressionListener.prototype.enterExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#expression.
ExpressionListener.prototype.exitExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#assignmentExpression.
ExpressionListener.prototype.enterAssignmentExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#assignmentExpression.
ExpressionListener.prototype.exitAssignmentExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#assignmentExpressionNoIn.
ExpressionListener.prototype.enterAssignmentExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#assignmentExpressionNoIn.
ExpressionListener.prototype.exitAssignmentExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#leftHandSideExpression.
ExpressionListener.prototype.enterLeftHandSideExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#leftHandSideExpression.
ExpressionListener.prototype.exitLeftHandSideExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#newExpression.
ExpressionListener.prototype.enterNewExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#newExpression.
ExpressionListener.prototype.exitNewExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#memberExpression.
ExpressionListener.prototype.enterMemberExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#memberExpression.
ExpressionListener.prototype.exitMemberExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#memberExpressionSuffix.
ExpressionListener.prototype.enterMemberExpressionSuffix = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#memberExpressionSuffix.
ExpressionListener.prototype.exitMemberExpressionSuffix = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#callExpression.
ExpressionListener.prototype.enterCallExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#callExpression.
ExpressionListener.prototype.exitCallExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#callExpressionSuffix.
ExpressionListener.prototype.enterCallExpressionSuffix = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#callExpressionSuffix.
ExpressionListener.prototype.exitCallExpressionSuffix = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#arguments.
ExpressionListener.prototype.enterArguments = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#arguments.
ExpressionListener.prototype.exitArguments = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#indexSuffix.
ExpressionListener.prototype.enterIndexSuffix = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#indexSuffix.
ExpressionListener.prototype.exitIndexSuffix = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#propertyReferenceSuffix.
ExpressionListener.prototype.enterPropertyReferenceSuffix = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#propertyReferenceSuffix.
ExpressionListener.prototype.exitPropertyReferenceSuffix = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#assignmentOperator.
ExpressionListener.prototype.enterAssignmentOperator = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#assignmentOperator.
ExpressionListener.prototype.exitAssignmentOperator = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#conditionalExpression.
ExpressionListener.prototype.enterConditionalExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#conditionalExpression.
ExpressionListener.prototype.exitConditionalExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#conditionalExpressionNoIn.
ExpressionListener.prototype.enterConditionalExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#conditionalExpressionNoIn.
ExpressionListener.prototype.exitConditionalExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#logicalORExpression.
ExpressionListener.prototype.enterLogicalORExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#logicalORExpression.
ExpressionListener.prototype.exitLogicalORExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#logicalORExpressionNoIn.
ExpressionListener.prototype.enterLogicalORExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#logicalORExpressionNoIn.
ExpressionListener.prototype.exitLogicalORExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#logicalANDExpression.
ExpressionListener.prototype.enterLogicalANDExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#logicalANDExpression.
ExpressionListener.prototype.exitLogicalANDExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#logicalANDExpressionNoIn.
ExpressionListener.prototype.enterLogicalANDExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#logicalANDExpressionNoIn.
ExpressionListener.prototype.exitLogicalANDExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#bitwiseORExpression.
ExpressionListener.prototype.enterBitwiseORExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#bitwiseORExpression.
ExpressionListener.prototype.exitBitwiseORExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#bitwiseORExpressionNoIn.
ExpressionListener.prototype.enterBitwiseORExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#bitwiseORExpressionNoIn.
ExpressionListener.prototype.exitBitwiseORExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#bitwiseXORExpression.
ExpressionListener.prototype.enterBitwiseXORExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#bitwiseXORExpression.
ExpressionListener.prototype.exitBitwiseXORExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#bitwiseXORExpressionNoIn.
ExpressionListener.prototype.enterBitwiseXORExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#bitwiseXORExpressionNoIn.
ExpressionListener.prototype.exitBitwiseXORExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#bitwiseANDExpression.
ExpressionListener.prototype.enterBitwiseANDExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#bitwiseANDExpression.
ExpressionListener.prototype.exitBitwiseANDExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#bitwiseANDExpressionNoIn.
ExpressionListener.prototype.enterBitwiseANDExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#bitwiseANDExpressionNoIn.
ExpressionListener.prototype.exitBitwiseANDExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#equalityExpression.
ExpressionListener.prototype.enterEqualityExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#equalityExpression.
ExpressionListener.prototype.exitEqualityExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#equalityExpressionNoIn.
ExpressionListener.prototype.enterEqualityExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#equalityExpressionNoIn.
ExpressionListener.prototype.exitEqualityExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#relationalExpression.
ExpressionListener.prototype.enterRelationalExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#relationalExpression.
ExpressionListener.prototype.exitRelationalExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#relationalExpressionNoIn.
ExpressionListener.prototype.enterRelationalExpressionNoIn = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#relationalExpressionNoIn.
ExpressionListener.prototype.exitRelationalExpressionNoIn = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#shiftExpression.
ExpressionListener.prototype.enterShiftExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#shiftExpression.
ExpressionListener.prototype.exitShiftExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#additiveExpression.
ExpressionListener.prototype.enterAdditiveExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#additiveExpression.
ExpressionListener.prototype.exitAdditiveExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#multiplicativeExpression.
ExpressionListener.prototype.enterMultiplicativeExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#multiplicativeExpression.
ExpressionListener.prototype.exitMultiplicativeExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#unaryExpression.
ExpressionListener.prototype.enterUnaryExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#unaryExpression.
ExpressionListener.prototype.exitUnaryExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#postfixExpression.
ExpressionListener.prototype.enterPostfixExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#postfixExpression.
ExpressionListener.prototype.exitPostfixExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#primaryExpression.
ExpressionListener.prototype.enterPrimaryExpression = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#primaryExpression.
ExpressionListener.prototype.exitPrimaryExpression = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#arrayLiteral.
ExpressionListener.prototype.enterArrayLiteral = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#arrayLiteral.
ExpressionListener.prototype.exitArrayLiteral = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#objectLiteral.
ExpressionListener.prototype.enterObjectLiteral = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#objectLiteral.
ExpressionListener.prototype.exitObjectLiteral = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#propertyNameAndValue.
ExpressionListener.prototype.enterPropertyNameAndValue = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#propertyNameAndValue.
ExpressionListener.prototype.exitPropertyNameAndValue = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#propertyName.
ExpressionListener.prototype.enterPropertyName = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#propertyName.
ExpressionListener.prototype.exitPropertyName = function(ctx) {
};


// Enter a parse tree produced by ExpressionParser#literal.
ExpressionListener.prototype.enterLiteral = function(ctx) {
};

// Exit a parse tree produced by ExpressionParser#literal.
ExpressionListener.prototype.exitLiteral = function(ctx) {
};



exports.ExpressionListener = ExpressionListener;