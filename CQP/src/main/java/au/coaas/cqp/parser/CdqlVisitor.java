// Generated from D:/Projects/CoaaS/CDQL\Cdql.g4 by ANTLR 4.9.1
package au.coaas.cqp.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CdqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CdqlVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Cdql}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Cdql(CdqlParser.Rule_CdqlContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_ddl_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_ddl_statement(CdqlParser.Rule_ddl_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_dml_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_dml_statement(CdqlParser.Rule_dml_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_query(CdqlParser.Rule_queryContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_create_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_create_function(CdqlParser.Rule_create_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_set_package}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_set_package(CdqlParser.Rule_set_packageContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_create_package}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_create_package(CdqlParser.Rule_create_packageContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_alter_package}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_alter_package(CdqlParser.Rule_alter_packageContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_alter_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_alter_function(CdqlParser.Rule_alter_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_drop_package}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_drop_package(CdqlParser.Rule_drop_packageContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_drop_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_drop_function(CdqlParser.Rule_drop_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_package_title}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_package_title(CdqlParser.Rule_package_titleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Set_Meta}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Set_Meta(CdqlParser.Rule_Set_MetaContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Set_Config}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Set_Config(CdqlParser.Rule_Set_ConfigContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Set_Callback}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Set_Callback(CdqlParser.Rule_Set_CallbackContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Output_Config}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Output_Config(CdqlParser.Rule_Output_ConfigContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Meta_Config}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Meta_Config(CdqlParser.Rule_Meta_ConfigContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Callback_Config}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Callback_Config(CdqlParser.Rule_Callback_ConfigContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Prefixs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Prefixs(CdqlParser.Rule_PrefixsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Prefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Prefix(CdqlParser.Rule_PrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Pull}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Pull(CdqlParser.Rule_PullContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Select(CdqlParser.Rule_SelectContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_select_Attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_select_Attribute(CdqlParser.Rule_select_AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_select_FunctionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_select_FunctionCall(CdqlParser.Rule_select_FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Attribute(CdqlParser.Rule_AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_EntityTitle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_EntityTitle(CdqlParser.Rule_EntityTitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_AttributeTitle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_AttributeTitle(CdqlParser.Rule_AttributeTitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_FunctionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_FunctionCall(CdqlParser.Rule_FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_function_call_method_chaining}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_function_call_method_chaining(CdqlParser.Rule_function_call_method_chainingContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_call_FunctionTitle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_call_FunctionTitle(CdqlParser.Rule_call_FunctionTitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_call_Operand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_call_Operand(CdqlParser.Rule_call_OperandContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Name_Operand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Name_Operand(CdqlParser.Rule_Name_OperandContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_FunctionTitle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_FunctionTitle(CdqlParser.Rule_FunctionTitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Operand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Operand(CdqlParser.Rule_OperandContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_ContextValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_ContextValue(CdqlParser.Rule_ContextValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_When}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_When(CdqlParser.Rule_WhenContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_repeat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_repeat(CdqlParser.Rule_repeatContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Start(CdqlParser.Rule_StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Date_Time_When}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Date_Time_When(CdqlParser.Rule_Date_Time_WhenContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Occurrence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Occurrence(CdqlParser.Rule_OccurrenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Date_Time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Date_Time(CdqlParser.Rule_Date_TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Date(CdqlParser.Rule_DateContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Time(CdqlParser.Rule_TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Condition(CdqlParser.Rule_ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_expr_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_expr_op(CdqlParser.Rule_expr_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Constraint(CdqlParser.Rule_ConstraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_left_element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_left_element(CdqlParser.Rule_left_elementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_right_element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_right_element(CdqlParser.Rule_right_elementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_target_element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_target_element(CdqlParser.Rule_target_elementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_relational_op_func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_relational_op_func(CdqlParser.Rule_relational_op_funcContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_relational_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_relational_op(CdqlParser.Rule_relational_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_between_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_between_op(CdqlParser.Rule_between_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_is_or_is_not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_is_or_is_not(CdqlParser.Rule_is_or_is_notContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#ruel_Push}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRuel_Push(CdqlParser.Ruel_PushContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_callback}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_callback(CdqlParser.Rule_callbackContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_http_calback}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_http_calback(CdqlParser.Rule_http_calbackContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_fcm_calback}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_fcm_calback(CdqlParser.Rule_fcm_calbackContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_fcm_topic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_fcm_topic(CdqlParser.Rule_fcm_topicContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_fcm_token}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_fcm_token(CdqlParser.Rule_fcm_tokenContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_callback_url}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_callback_url(CdqlParser.Rule_callback_urlContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Define}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Define(CdqlParser.Rule_DefineContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Define_Context_Entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Define_Context_Entity(CdqlParser.Rule_Define_Context_EntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_context_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_context_entity(CdqlParser.Rule_context_entityContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_entity_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_entity_type(CdqlParser.Rule_entity_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_Define_Context_Function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_Define_Context_Function(CdqlParser.Rule_Define_Context_FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_context_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_context_function(CdqlParser.Rule_context_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_aFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_aFunction(CdqlParser.Rule_aFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_sFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_sFunction(CdqlParser.Rule_sFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_is_on}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_is_on(CdqlParser.Rule_is_onContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_is_on_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_is_on_entity(CdqlParser.Rule_is_on_entityContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#cst_situation_def_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCst_situation_def_rule(CdqlParser.Cst_situation_def_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_single_situatuin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_single_situatuin(CdqlParser.Rule_single_situatuinContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_situation_pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_situation_pair(CdqlParser.Rule_situation_pairContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_situation_attributes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_situation_attributes(CdqlParser.Rule_situation_attributesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_situation_attribute_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_situation_attribute_name(CdqlParser.Rule_situation_attribute_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#situation_pair_values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSituation_pair_values(CdqlParser.Situation_pair_valuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#situation_weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSituation_weight(CdqlParser.Situation_weightContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#situation_range_values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSituation_range_values(CdqlParser.Situation_range_valuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#situation_pair_values_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSituation_pair_values_item(CdqlParser.Situation_pair_values_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_situation_belief}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_situation_belief(CdqlParser.Rule_situation_beliefContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_situation_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_situation_value(CdqlParser.Rule_situation_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_discrete_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_discrete_value(CdqlParser.Rule_discrete_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#discrete_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDiscrete_value(CdqlParser.Discrete_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_region_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_region_value(CdqlParser.Rule_region_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#region_value_inclusive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegion_value_inclusive(CdqlParser.Region_value_inclusiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#region_value_left_inclusive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegion_value_left_inclusive(CdqlParser.Region_value_left_inclusiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#region_value_right_inclusive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegion_value_right_inclusive(CdqlParser.Region_value_right_inclusiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#region_value_exclusive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegion_value_exclusive(CdqlParser.Region_value_exclusiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#region_value_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegion_value_value(CdqlParser.Region_value_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_entity_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_entity_id(CdqlParser.Rule_entity_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_function_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_function_id(CdqlParser.Rule_function_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#rule_url}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_url(CdqlParser.Rule_urlContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#authority}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAuthority(CdqlParser.AuthorityContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#host}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHost(CdqlParser.HostContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#hostname}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHostname(CdqlParser.HostnameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#hostnumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHostnumber(CdqlParser.HostnumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#search}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearch(CdqlParser.SearchContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#searchparameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchparameter(CdqlParser.SearchparameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#port}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort(CdqlParser.PortContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath(CdqlParser.PathContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#normal_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormal_path(CdqlParser.Normal_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#path_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath_param(CdqlParser.Path_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#json}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJson(CdqlParser.JsonContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#obj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObj(CdqlParser.ObjContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(CdqlParser.PairContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(CdqlParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link CdqlParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(CdqlParser.ValueContext ctx);
}