// Generated from /Users/ali/CDQLParserV3/Cdql.g4 by ANTLR 4.6
// jshint ignore: start
var antlr4 = require('antlr4/index');

// This class defines a complete generic visitor for a parse tree produced by CdqlParser.

function CdqlVisitor() {
	antlr4.tree.ParseTreeVisitor.call(this);
	return this;
}

CdqlVisitor.prototype = Object.create(antlr4.tree.ParseTreeVisitor.prototype);
CdqlVisitor.prototype.constructor = CdqlVisitor;

// Visit a parse tree produced by CdqlParser#rule_Cdql.
CdqlVisitor.prototype.visitRule_Cdql = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_ddl_statement.
CdqlVisitor.prototype.visitRule_ddl_statement = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_dml_statement.
CdqlVisitor.prototype.visitRule_dml_statement = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_query.
CdqlVisitor.prototype.visitRule_query = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_create_function.
CdqlVisitor.prototype.visitRule_create_function = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_set_package.
CdqlVisitor.prototype.visitRule_set_package = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_create_package.
CdqlVisitor.prototype.visitRule_create_package = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_alter_package.
CdqlVisitor.prototype.visitRule_alter_package = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_alter_function.
CdqlVisitor.prototype.visitRule_alter_function = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_drop_package.
CdqlVisitor.prototype.visitRule_drop_package = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_drop_function.
CdqlVisitor.prototype.visitRule_drop_function = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_package_title.
CdqlVisitor.prototype.visitRule_package_title = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Set_Config.
CdqlVisitor.prototype.visitRule_Set_Config = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Set_Callback.
CdqlVisitor.prototype.visitRule_Set_Callback = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Output_Config.
CdqlVisitor.prototype.visitRule_Output_Config = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Callback_Config.
CdqlVisitor.prototype.visitRule_Callback_Config = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Prefixs.
CdqlVisitor.prototype.visitRule_Prefixs = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Prefix.
CdqlVisitor.prototype.visitRule_Prefix = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Pull.
CdqlVisitor.prototype.visitRule_Pull = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Select.
CdqlVisitor.prototype.visitRule_Select = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_select_Attribute.
CdqlVisitor.prototype.visitRule_select_Attribute = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_select_FunctionCall.
CdqlVisitor.prototype.visitRule_select_FunctionCall = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Attribute.
CdqlVisitor.prototype.visitRule_Attribute = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_EntityTitle.
CdqlVisitor.prototype.visitRule_EntityTitle = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_AttributeTitle.
CdqlVisitor.prototype.visitRule_AttributeTitle = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_FunctionCall.
CdqlVisitor.prototype.visitRule_FunctionCall = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_function_call_method_chaining.
CdqlVisitor.prototype.visitRule_function_call_method_chaining = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_call_FunctionTitle.
CdqlVisitor.prototype.visitRule_call_FunctionTitle = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_call_Operand.
CdqlVisitor.prototype.visitRule_call_Operand = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Name_Operand.
CdqlVisitor.prototype.visitRule_Name_Operand = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_FunctionTitle.
CdqlVisitor.prototype.visitRule_FunctionTitle = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Operand.
CdqlVisitor.prototype.visitRule_Operand = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_ContextValue.
CdqlVisitor.prototype.visitRule_ContextValue = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_When.
CdqlVisitor.prototype.visitRule_When = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_repeat.
CdqlVisitor.prototype.visitRule_repeat = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Start.
CdqlVisitor.prototype.visitRule_Start = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Date_Time_When.
CdqlVisitor.prototype.visitRule_Date_Time_When = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Occurrence.
CdqlVisitor.prototype.visitRule_Occurrence = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Date_Time.
CdqlVisitor.prototype.visitRule_Date_Time = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Date.
CdqlVisitor.prototype.visitRule_Date = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Time.
CdqlVisitor.prototype.visitRule_Time = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Condition.
CdqlVisitor.prototype.visitRule_Condition = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_expr_op.
CdqlVisitor.prototype.visitRule_expr_op = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Constraint.
CdqlVisitor.prototype.visitRule_Constraint = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_left_element.
CdqlVisitor.prototype.visitRule_left_element = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_right_element.
CdqlVisitor.prototype.visitRule_right_element = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_target_element.
CdqlVisitor.prototype.visitRule_target_element = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_relational_op_func.
CdqlVisitor.prototype.visitRule_relational_op_func = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_relational_op.
CdqlVisitor.prototype.visitRule_relational_op = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_between_op.
CdqlVisitor.prototype.visitRule_between_op = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_is_or_is_not.
CdqlVisitor.prototype.visitRule_is_or_is_not = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#ruel_Push.
CdqlVisitor.prototype.visitRuel_Push = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_callback.
CdqlVisitor.prototype.visitRule_callback = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_http_calback.
CdqlVisitor.prototype.visitRule_http_calback = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_fcm_calback.
CdqlVisitor.prototype.visitRule_fcm_calback = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_fcm_topic.
CdqlVisitor.prototype.visitRule_fcm_topic = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_fcm_token.
CdqlVisitor.prototype.visitRule_fcm_token = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_callback_url.
CdqlVisitor.prototype.visitRule_callback_url = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Define.
CdqlVisitor.prototype.visitRule_Define = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Define_Context_Entity.
CdqlVisitor.prototype.visitRule_Define_Context_Entity = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_context_entity.
CdqlVisitor.prototype.visitRule_context_entity = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_entity_type.
CdqlVisitor.prototype.visitRule_entity_type = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_Define_Context_Function.
CdqlVisitor.prototype.visitRule_Define_Context_Function = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_context_function.
CdqlVisitor.prototype.visitRule_context_function = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_aFunction.
CdqlVisitor.prototype.visitRule_aFunction = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_sFunction.
CdqlVisitor.prototype.visitRule_sFunction = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_is_on.
CdqlVisitor.prototype.visitRule_is_on = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_is_on_entity.
CdqlVisitor.prototype.visitRule_is_on_entity = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#cst_situation_def_rule.
CdqlVisitor.prototype.visitCst_situation_def_rule = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_single_situatuin.
CdqlVisitor.prototype.visitRule_single_situatuin = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_situation_pair.
CdqlVisitor.prototype.visitRule_situation_pair = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_situation_attributes.
CdqlVisitor.prototype.visitRule_situation_attributes = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_situation_attribute_name.
CdqlVisitor.prototype.visitRule_situation_attribute_name = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#situation_pair_values.
CdqlVisitor.prototype.visitSituation_pair_values = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#situation_weight.
CdqlVisitor.prototype.visitSituation_weight = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#situation_range_values.
CdqlVisitor.prototype.visitSituation_range_values = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#situation_pair_values_item.
CdqlVisitor.prototype.visitSituation_pair_values_item = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_situation_belief.
CdqlVisitor.prototype.visitRule_situation_belief = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_situation_value.
CdqlVisitor.prototype.visitRule_situation_value = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_discrete_value.
CdqlVisitor.prototype.visitRule_discrete_value = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#discrete_value.
CdqlVisitor.prototype.visitDiscrete_value = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_region_value.
CdqlVisitor.prototype.visitRule_region_value = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#region_value_inclusive.
CdqlVisitor.prototype.visitRegion_value_inclusive = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#region_value_left_inclusive.
CdqlVisitor.prototype.visitRegion_value_left_inclusive = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#region_value_right_inclusive.
CdqlVisitor.prototype.visitRegion_value_right_inclusive = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#region_value_exclusive.
CdqlVisitor.prototype.visitRegion_value_exclusive = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#region_value_value.
CdqlVisitor.prototype.visitRegion_value_value = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_entity_id.
CdqlVisitor.prototype.visitRule_entity_id = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_function_id.
CdqlVisitor.prototype.visitRule_function_id = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#rule_url.
CdqlVisitor.prototype.visitRule_url = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#authority.
CdqlVisitor.prototype.visitAuthority = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#host.
CdqlVisitor.prototype.visitHost = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#hostname.
CdqlVisitor.prototype.visitHostname = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#hostnumber.
CdqlVisitor.prototype.visitHostnumber = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#search.
CdqlVisitor.prototype.visitSearch = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#searchparameter.
CdqlVisitor.prototype.visitSearchparameter = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#port.
CdqlVisitor.prototype.visitPort = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#path.
CdqlVisitor.prototype.visitPath = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#normal_path.
CdqlVisitor.prototype.visitNormal_path = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#path_param.
CdqlVisitor.prototype.visitPath_param = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#json.
CdqlVisitor.prototype.visitJson = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#obj.
CdqlVisitor.prototype.visitObj = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#pair.
CdqlVisitor.prototype.visitPair = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#array.
CdqlVisitor.prototype.visitArray = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by CdqlParser#value.
CdqlVisitor.prototype.visitValue = function(ctx) {
  return this.visitChildren(ctx);
};



exports.CdqlVisitor = CdqlVisitor;