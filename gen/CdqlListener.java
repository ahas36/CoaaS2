// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CdqlParser}.
 */
public interface CdqlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Cdql}.
	 * @param ctx the parse tree
	 */
	void enterRule_Cdql(CdqlParser.Rule_CdqlContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Cdql}.
	 * @param ctx the parse tree
	 */
	void exitRule_Cdql(CdqlParser.Rule_CdqlContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_ddl_statement}.
	 * @param ctx the parse tree
	 */
	void enterRule_ddl_statement(CdqlParser.Rule_ddl_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_ddl_statement}.
	 * @param ctx the parse tree
	 */
	void exitRule_ddl_statement(CdqlParser.Rule_ddl_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_dml_statement}.
	 * @param ctx the parse tree
	 */
	void enterRule_dml_statement(CdqlParser.Rule_dml_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_dml_statement}.
	 * @param ctx the parse tree
	 */
	void exitRule_dml_statement(CdqlParser.Rule_dml_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_query}.
	 * @param ctx the parse tree
	 */
	void enterRule_query(CdqlParser.Rule_queryContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_query}.
	 * @param ctx the parse tree
	 */
	void exitRule_query(CdqlParser.Rule_queryContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_create_function}.
	 * @param ctx the parse tree
	 */
	void enterRule_create_function(CdqlParser.Rule_create_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_create_function}.
	 * @param ctx the parse tree
	 */
	void exitRule_create_function(CdqlParser.Rule_create_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_set_package}.
	 * @param ctx the parse tree
	 */
	void enterRule_set_package(CdqlParser.Rule_set_packageContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_set_package}.
	 * @param ctx the parse tree
	 */
	void exitRule_set_package(CdqlParser.Rule_set_packageContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_create_package}.
	 * @param ctx the parse tree
	 */
	void enterRule_create_package(CdqlParser.Rule_create_packageContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_create_package}.
	 * @param ctx the parse tree
	 */
	void exitRule_create_package(CdqlParser.Rule_create_packageContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_alter_package}.
	 * @param ctx the parse tree
	 */
	void enterRule_alter_package(CdqlParser.Rule_alter_packageContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_alter_package}.
	 * @param ctx the parse tree
	 */
	void exitRule_alter_package(CdqlParser.Rule_alter_packageContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_alter_function}.
	 * @param ctx the parse tree
	 */
	void enterRule_alter_function(CdqlParser.Rule_alter_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_alter_function}.
	 * @param ctx the parse tree
	 */
	void exitRule_alter_function(CdqlParser.Rule_alter_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_drop_package}.
	 * @param ctx the parse tree
	 */
	void enterRule_drop_package(CdqlParser.Rule_drop_packageContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_drop_package}.
	 * @param ctx the parse tree
	 */
	void exitRule_drop_package(CdqlParser.Rule_drop_packageContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_drop_function}.
	 * @param ctx the parse tree
	 */
	void enterRule_drop_function(CdqlParser.Rule_drop_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_drop_function}.
	 * @param ctx the parse tree
	 */
	void exitRule_drop_function(CdqlParser.Rule_drop_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_package_title}.
	 * @param ctx the parse tree
	 */
	void enterRule_package_title(CdqlParser.Rule_package_titleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_package_title}.
	 * @param ctx the parse tree
	 */
	void exitRule_package_title(CdqlParser.Rule_package_titleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Set_Meta}.
	 * @param ctx the parse tree
	 */
	void enterRule_Set_Meta(CdqlParser.Rule_Set_MetaContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Set_Meta}.
	 * @param ctx the parse tree
	 */
	void exitRule_Set_Meta(CdqlParser.Rule_Set_MetaContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Set_Config}.
	 * @param ctx the parse tree
	 */
	void enterRule_Set_Config(CdqlParser.Rule_Set_ConfigContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Set_Config}.
	 * @param ctx the parse tree
	 */
	void exitRule_Set_Config(CdqlParser.Rule_Set_ConfigContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Set_Callback}.
	 * @param ctx the parse tree
	 */
	void enterRule_Set_Callback(CdqlParser.Rule_Set_CallbackContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Set_Callback}.
	 * @param ctx the parse tree
	 */
	void exitRule_Set_Callback(CdqlParser.Rule_Set_CallbackContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Output_Config}.
	 * @param ctx the parse tree
	 */
	void enterRule_Output_Config(CdqlParser.Rule_Output_ConfigContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Output_Config}.
	 * @param ctx the parse tree
	 */
	void exitRule_Output_Config(CdqlParser.Rule_Output_ConfigContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Meta_Config}.
	 * @param ctx the parse tree
	 */
	void enterRule_Meta_Config(CdqlParser.Rule_Meta_ConfigContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Meta_Config}.
	 * @param ctx the parse tree
	 */
	void exitRule_Meta_Config(CdqlParser.Rule_Meta_ConfigContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Callback_Config}.
	 * @param ctx the parse tree
	 */
	void enterRule_Callback_Config(CdqlParser.Rule_Callback_ConfigContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Callback_Config}.
	 * @param ctx the parse tree
	 */
	void exitRule_Callback_Config(CdqlParser.Rule_Callback_ConfigContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Prefixs}.
	 * @param ctx the parse tree
	 */
	void enterRule_Prefixs(CdqlParser.Rule_PrefixsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Prefixs}.
	 * @param ctx the parse tree
	 */
	void exitRule_Prefixs(CdqlParser.Rule_PrefixsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Prefix}.
	 * @param ctx the parse tree
	 */
	void enterRule_Prefix(CdqlParser.Rule_PrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Prefix}.
	 * @param ctx the parse tree
	 */
	void exitRule_Prefix(CdqlParser.Rule_PrefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Pull}.
	 * @param ctx the parse tree
	 */
	void enterRule_Pull(CdqlParser.Rule_PullContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Pull}.
	 * @param ctx the parse tree
	 */
	void exitRule_Pull(CdqlParser.Rule_PullContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Select}.
	 * @param ctx the parse tree
	 */
	void enterRule_Select(CdqlParser.Rule_SelectContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Select}.
	 * @param ctx the parse tree
	 */
	void exitRule_Select(CdqlParser.Rule_SelectContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_select_Attribute}.
	 * @param ctx the parse tree
	 */
	void enterRule_select_Attribute(CdqlParser.Rule_select_AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_select_Attribute}.
	 * @param ctx the parse tree
	 */
	void exitRule_select_Attribute(CdqlParser.Rule_select_AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_select_FunctionCall}.
	 * @param ctx the parse tree
	 */
	void enterRule_select_FunctionCall(CdqlParser.Rule_select_FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_select_FunctionCall}.
	 * @param ctx the parse tree
	 */
	void exitRule_select_FunctionCall(CdqlParser.Rule_select_FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Attribute}.
	 * @param ctx the parse tree
	 */
	void enterRule_Attribute(CdqlParser.Rule_AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Attribute}.
	 * @param ctx the parse tree
	 */
	void exitRule_Attribute(CdqlParser.Rule_AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_EntityTitle}.
	 * @param ctx the parse tree
	 */
	void enterRule_EntityTitle(CdqlParser.Rule_EntityTitleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_EntityTitle}.
	 * @param ctx the parse tree
	 */
	void exitRule_EntityTitle(CdqlParser.Rule_EntityTitleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_AttributeTitle}.
	 * @param ctx the parse tree
	 */
	void enterRule_AttributeTitle(CdqlParser.Rule_AttributeTitleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_AttributeTitle}.
	 * @param ctx the parse tree
	 */
	void exitRule_AttributeTitle(CdqlParser.Rule_AttributeTitleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_FunctionCall}.
	 * @param ctx the parse tree
	 */
	void enterRule_FunctionCall(CdqlParser.Rule_FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_FunctionCall}.
	 * @param ctx the parse tree
	 */
	void exitRule_FunctionCall(CdqlParser.Rule_FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_function_call_method_chaining}.
	 * @param ctx the parse tree
	 */
	void enterRule_function_call_method_chaining(CdqlParser.Rule_function_call_method_chainingContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_function_call_method_chaining}.
	 * @param ctx the parse tree
	 */
	void exitRule_function_call_method_chaining(CdqlParser.Rule_function_call_method_chainingContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_call_FunctionTitle}.
	 * @param ctx the parse tree
	 */
	void enterRule_call_FunctionTitle(CdqlParser.Rule_call_FunctionTitleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_call_FunctionTitle}.
	 * @param ctx the parse tree
	 */
	void exitRule_call_FunctionTitle(CdqlParser.Rule_call_FunctionTitleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_call_Operand}.
	 * @param ctx the parse tree
	 */
	void enterRule_call_Operand(CdqlParser.Rule_call_OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_call_Operand}.
	 * @param ctx the parse tree
	 */
	void exitRule_call_Operand(CdqlParser.Rule_call_OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Name_Operand}.
	 * @param ctx the parse tree
	 */
	void enterRule_Name_Operand(CdqlParser.Rule_Name_OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Name_Operand}.
	 * @param ctx the parse tree
	 */
	void exitRule_Name_Operand(CdqlParser.Rule_Name_OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_FunctionTitle}.
	 * @param ctx the parse tree
	 */
	void enterRule_FunctionTitle(CdqlParser.Rule_FunctionTitleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_FunctionTitle}.
	 * @param ctx the parse tree
	 */
	void exitRule_FunctionTitle(CdqlParser.Rule_FunctionTitleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Operand}.
	 * @param ctx the parse tree
	 */
	void enterRule_Operand(CdqlParser.Rule_OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Operand}.
	 * @param ctx the parse tree
	 */
	void exitRule_Operand(CdqlParser.Rule_OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_ContextValue}.
	 * @param ctx the parse tree
	 */
	void enterRule_ContextValue(CdqlParser.Rule_ContextValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_ContextValue}.
	 * @param ctx the parse tree
	 */
	void exitRule_ContextValue(CdqlParser.Rule_ContextValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_When}.
	 * @param ctx the parse tree
	 */
	void enterRule_When(CdqlParser.Rule_WhenContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_When}.
	 * @param ctx the parse tree
	 */
	void exitRule_When(CdqlParser.Rule_WhenContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_repeat}.
	 * @param ctx the parse tree
	 */
	void enterRule_repeat(CdqlParser.Rule_repeatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_repeat}.
	 * @param ctx the parse tree
	 */
	void exitRule_repeat(CdqlParser.Rule_repeatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Start}.
	 * @param ctx the parse tree
	 */
	void enterRule_Start(CdqlParser.Rule_StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Start}.
	 * @param ctx the parse tree
	 */
	void exitRule_Start(CdqlParser.Rule_StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Date_Time_When}.
	 * @param ctx the parse tree
	 */
	void enterRule_Date_Time_When(CdqlParser.Rule_Date_Time_WhenContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Date_Time_When}.
	 * @param ctx the parse tree
	 */
	void exitRule_Date_Time_When(CdqlParser.Rule_Date_Time_WhenContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Occurrence}.
	 * @param ctx the parse tree
	 */
	void enterRule_Occurrence(CdqlParser.Rule_OccurrenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Occurrence}.
	 * @param ctx the parse tree
	 */
	void exitRule_Occurrence(CdqlParser.Rule_OccurrenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Date_Time}.
	 * @param ctx the parse tree
	 */
	void enterRule_Date_Time(CdqlParser.Rule_Date_TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Date_Time}.
	 * @param ctx the parse tree
	 */
	void exitRule_Date_Time(CdqlParser.Rule_Date_TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Date}.
	 * @param ctx the parse tree
	 */
	void enterRule_Date(CdqlParser.Rule_DateContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Date}.
	 * @param ctx the parse tree
	 */
	void exitRule_Date(CdqlParser.Rule_DateContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Time}.
	 * @param ctx the parse tree
	 */
	void enterRule_Time(CdqlParser.Rule_TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Time}.
	 * @param ctx the parse tree
	 */
	void exitRule_Time(CdqlParser.Rule_TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Condition}.
	 * @param ctx the parse tree
	 */
	void enterRule_Condition(CdqlParser.Rule_ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Condition}.
	 * @param ctx the parse tree
	 */
	void exitRule_Condition(CdqlParser.Rule_ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_expr_op}.
	 * @param ctx the parse tree
	 */
	void enterRule_expr_op(CdqlParser.Rule_expr_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_expr_op}.
	 * @param ctx the parse tree
	 */
	void exitRule_expr_op(CdqlParser.Rule_expr_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Constraint}.
	 * @param ctx the parse tree
	 */
	void enterRule_Constraint(CdqlParser.Rule_ConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Constraint}.
	 * @param ctx the parse tree
	 */
	void exitRule_Constraint(CdqlParser.Rule_ConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_left_element}.
	 * @param ctx the parse tree
	 */
	void enterRule_left_element(CdqlParser.Rule_left_elementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_left_element}.
	 * @param ctx the parse tree
	 */
	void exitRule_left_element(CdqlParser.Rule_left_elementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_right_element}.
	 * @param ctx the parse tree
	 */
	void enterRule_right_element(CdqlParser.Rule_right_elementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_right_element}.
	 * @param ctx the parse tree
	 */
	void exitRule_right_element(CdqlParser.Rule_right_elementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_target_element}.
	 * @param ctx the parse tree
	 */
	void enterRule_target_element(CdqlParser.Rule_target_elementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_target_element}.
	 * @param ctx the parse tree
	 */
	void exitRule_target_element(CdqlParser.Rule_target_elementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_relational_op_func}.
	 * @param ctx the parse tree
	 */
	void enterRule_relational_op_func(CdqlParser.Rule_relational_op_funcContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_relational_op_func}.
	 * @param ctx the parse tree
	 */
	void exitRule_relational_op_func(CdqlParser.Rule_relational_op_funcContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_relational_op}.
	 * @param ctx the parse tree
	 */
	void enterRule_relational_op(CdqlParser.Rule_relational_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_relational_op}.
	 * @param ctx the parse tree
	 */
	void exitRule_relational_op(CdqlParser.Rule_relational_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_between_op}.
	 * @param ctx the parse tree
	 */
	void enterRule_between_op(CdqlParser.Rule_between_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_between_op}.
	 * @param ctx the parse tree
	 */
	void exitRule_between_op(CdqlParser.Rule_between_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_is_or_is_not}.
	 * @param ctx the parse tree
	 */
	void enterRule_is_or_is_not(CdqlParser.Rule_is_or_is_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_is_or_is_not}.
	 * @param ctx the parse tree
	 */
	void exitRule_is_or_is_not(CdqlParser.Rule_is_or_is_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#ruel_Push}.
	 * @param ctx the parse tree
	 */
	void enterRuel_Push(CdqlParser.Ruel_PushContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#ruel_Push}.
	 * @param ctx the parse tree
	 */
	void exitRuel_Push(CdqlParser.Ruel_PushContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_callback}.
	 * @param ctx the parse tree
	 */
	void enterRule_callback(CdqlParser.Rule_callbackContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_callback}.
	 * @param ctx the parse tree
	 */
	void exitRule_callback(CdqlParser.Rule_callbackContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_http_calback}.
	 * @param ctx the parse tree
	 */
	void enterRule_http_calback(CdqlParser.Rule_http_calbackContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_http_calback}.
	 * @param ctx the parse tree
	 */
	void exitRule_http_calback(CdqlParser.Rule_http_calbackContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_fcm_calback}.
	 * @param ctx the parse tree
	 */
	void enterRule_fcm_calback(CdqlParser.Rule_fcm_calbackContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_fcm_calback}.
	 * @param ctx the parse tree
	 */
	void exitRule_fcm_calback(CdqlParser.Rule_fcm_calbackContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_fcm_topic}.
	 * @param ctx the parse tree
	 */
	void enterRule_fcm_topic(CdqlParser.Rule_fcm_topicContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_fcm_topic}.
	 * @param ctx the parse tree
	 */
	void exitRule_fcm_topic(CdqlParser.Rule_fcm_topicContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_fcm_token}.
	 * @param ctx the parse tree
	 */
	void enterRule_fcm_token(CdqlParser.Rule_fcm_tokenContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_fcm_token}.
	 * @param ctx the parse tree
	 */
	void exitRule_fcm_token(CdqlParser.Rule_fcm_tokenContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_callback_url}.
	 * @param ctx the parse tree
	 */
	void enterRule_callback_url(CdqlParser.Rule_callback_urlContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_callback_url}.
	 * @param ctx the parse tree
	 */
	void exitRule_callback_url(CdqlParser.Rule_callback_urlContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Define}.
	 * @param ctx the parse tree
	 */
	void enterRule_Define(CdqlParser.Rule_DefineContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Define}.
	 * @param ctx the parse tree
	 */
	void exitRule_Define(CdqlParser.Rule_DefineContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Define_Context_Entity}.
	 * @param ctx the parse tree
	 */
	void enterRule_Define_Context_Entity(CdqlParser.Rule_Define_Context_EntityContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Define_Context_Entity}.
	 * @param ctx the parse tree
	 */
	void exitRule_Define_Context_Entity(CdqlParser.Rule_Define_Context_EntityContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_context_entity}.
	 * @param ctx the parse tree
	 */
	void enterRule_context_entity(CdqlParser.Rule_context_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_context_entity}.
	 * @param ctx the parse tree
	 */
	void exitRule_context_entity(CdqlParser.Rule_context_entityContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_group_by}.
	 * @param ctx the parse tree
	 */
	void enterRule_group_by(CdqlParser.Rule_group_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_group_by}.
	 * @param ctx the parse tree
	 */
	void exitRule_group_by(CdqlParser.Rule_group_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_sort_by}.
	 * @param ctx the parse tree
	 */
	void enterRule_sort_by(CdqlParser.Rule_sort_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_sort_by}.
	 * @param ctx the parse tree
	 */
	void exitRule_sort_by(CdqlParser.Rule_sort_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_entity_type}.
	 * @param ctx the parse tree
	 */
	void enterRule_entity_type(CdqlParser.Rule_entity_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_entity_type}.
	 * @param ctx the parse tree
	 */
	void exitRule_entity_type(CdqlParser.Rule_entity_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_Define_Context_Function}.
	 * @param ctx the parse tree
	 */
	void enterRule_Define_Context_Function(CdqlParser.Rule_Define_Context_FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_Define_Context_Function}.
	 * @param ctx the parse tree
	 */
	void exitRule_Define_Context_Function(CdqlParser.Rule_Define_Context_FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_context_function}.
	 * @param ctx the parse tree
	 */
	void enterRule_context_function(CdqlParser.Rule_context_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_context_function}.
	 * @param ctx the parse tree
	 */
	void exitRule_context_function(CdqlParser.Rule_context_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_aFunction}.
	 * @param ctx the parse tree
	 */
	void enterRule_aFunction(CdqlParser.Rule_aFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_aFunction}.
	 * @param ctx the parse tree
	 */
	void exitRule_aFunction(CdqlParser.Rule_aFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_sFunction}.
	 * @param ctx the parse tree
	 */
	void enterRule_sFunction(CdqlParser.Rule_sFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_sFunction}.
	 * @param ctx the parse tree
	 */
	void exitRule_sFunction(CdqlParser.Rule_sFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_is_on}.
	 * @param ctx the parse tree
	 */
	void enterRule_is_on(CdqlParser.Rule_is_onContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_is_on}.
	 * @param ctx the parse tree
	 */
	void exitRule_is_on(CdqlParser.Rule_is_onContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_is_on_entity}.
	 * @param ctx the parse tree
	 */
	void enterRule_is_on_entity(CdqlParser.Rule_is_on_entityContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_is_on_entity}.
	 * @param ctx the parse tree
	 */
	void exitRule_is_on_entity(CdqlParser.Rule_is_on_entityContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#cst_situation_def_rule}.
	 * @param ctx the parse tree
	 */
	void enterCst_situation_def_rule(CdqlParser.Cst_situation_def_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#cst_situation_def_rule}.
	 * @param ctx the parse tree
	 */
	void exitCst_situation_def_rule(CdqlParser.Cst_situation_def_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_single_situatuin}.
	 * @param ctx the parse tree
	 */
	void enterRule_single_situatuin(CdqlParser.Rule_single_situatuinContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_single_situatuin}.
	 * @param ctx the parse tree
	 */
	void exitRule_single_situatuin(CdqlParser.Rule_single_situatuinContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_situation_pair}.
	 * @param ctx the parse tree
	 */
	void enterRule_situation_pair(CdqlParser.Rule_situation_pairContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_situation_pair}.
	 * @param ctx the parse tree
	 */
	void exitRule_situation_pair(CdqlParser.Rule_situation_pairContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_situation_attributes}.
	 * @param ctx the parse tree
	 */
	void enterRule_situation_attributes(CdqlParser.Rule_situation_attributesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_situation_attributes}.
	 * @param ctx the parse tree
	 */
	void exitRule_situation_attributes(CdqlParser.Rule_situation_attributesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_situation_attribute_name}.
	 * @param ctx the parse tree
	 */
	void enterRule_situation_attribute_name(CdqlParser.Rule_situation_attribute_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_situation_attribute_name}.
	 * @param ctx the parse tree
	 */
	void exitRule_situation_attribute_name(CdqlParser.Rule_situation_attribute_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#situation_pair_values}.
	 * @param ctx the parse tree
	 */
	void enterSituation_pair_values(CdqlParser.Situation_pair_valuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#situation_pair_values}.
	 * @param ctx the parse tree
	 */
	void exitSituation_pair_values(CdqlParser.Situation_pair_valuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#situation_weight}.
	 * @param ctx the parse tree
	 */
	void enterSituation_weight(CdqlParser.Situation_weightContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#situation_weight}.
	 * @param ctx the parse tree
	 */
	void exitSituation_weight(CdqlParser.Situation_weightContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#situation_range_values}.
	 * @param ctx the parse tree
	 */
	void enterSituation_range_values(CdqlParser.Situation_range_valuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#situation_range_values}.
	 * @param ctx the parse tree
	 */
	void exitSituation_range_values(CdqlParser.Situation_range_valuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#situation_pair_values_item}.
	 * @param ctx the parse tree
	 */
	void enterSituation_pair_values_item(CdqlParser.Situation_pair_values_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#situation_pair_values_item}.
	 * @param ctx the parse tree
	 */
	void exitSituation_pair_values_item(CdqlParser.Situation_pair_values_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_situation_belief}.
	 * @param ctx the parse tree
	 */
	void enterRule_situation_belief(CdqlParser.Rule_situation_beliefContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_situation_belief}.
	 * @param ctx the parse tree
	 */
	void exitRule_situation_belief(CdqlParser.Rule_situation_beliefContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_situation_value}.
	 * @param ctx the parse tree
	 */
	void enterRule_situation_value(CdqlParser.Rule_situation_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_situation_value}.
	 * @param ctx the parse tree
	 */
	void exitRule_situation_value(CdqlParser.Rule_situation_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_discrete_value}.
	 * @param ctx the parse tree
	 */
	void enterRule_discrete_value(CdqlParser.Rule_discrete_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_discrete_value}.
	 * @param ctx the parse tree
	 */
	void exitRule_discrete_value(CdqlParser.Rule_discrete_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#discrete_value}.
	 * @param ctx the parse tree
	 */
	void enterDiscrete_value(CdqlParser.Discrete_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#discrete_value}.
	 * @param ctx the parse tree
	 */
	void exitDiscrete_value(CdqlParser.Discrete_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_region_value}.
	 * @param ctx the parse tree
	 */
	void enterRule_region_value(CdqlParser.Rule_region_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_region_value}.
	 * @param ctx the parse tree
	 */
	void exitRule_region_value(CdqlParser.Rule_region_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#region_value_inclusive}.
	 * @param ctx the parse tree
	 */
	void enterRegion_value_inclusive(CdqlParser.Region_value_inclusiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#region_value_inclusive}.
	 * @param ctx the parse tree
	 */
	void exitRegion_value_inclusive(CdqlParser.Region_value_inclusiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#region_value_left_inclusive}.
	 * @param ctx the parse tree
	 */
	void enterRegion_value_left_inclusive(CdqlParser.Region_value_left_inclusiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#region_value_left_inclusive}.
	 * @param ctx the parse tree
	 */
	void exitRegion_value_left_inclusive(CdqlParser.Region_value_left_inclusiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#region_value_right_inclusive}.
	 * @param ctx the parse tree
	 */
	void enterRegion_value_right_inclusive(CdqlParser.Region_value_right_inclusiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#region_value_right_inclusive}.
	 * @param ctx the parse tree
	 */
	void exitRegion_value_right_inclusive(CdqlParser.Region_value_right_inclusiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#region_value_exclusive}.
	 * @param ctx the parse tree
	 */
	void enterRegion_value_exclusive(CdqlParser.Region_value_exclusiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#region_value_exclusive}.
	 * @param ctx the parse tree
	 */
	void exitRegion_value_exclusive(CdqlParser.Region_value_exclusiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#region_value_value}.
	 * @param ctx the parse tree
	 */
	void enterRegion_value_value(CdqlParser.Region_value_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#region_value_value}.
	 * @param ctx the parse tree
	 */
	void exitRegion_value_value(CdqlParser.Region_value_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_entity_id}.
	 * @param ctx the parse tree
	 */
	void enterRule_entity_id(CdqlParser.Rule_entity_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_entity_id}.
	 * @param ctx the parse tree
	 */
	void exitRule_entity_id(CdqlParser.Rule_entity_idContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_function_id}.
	 * @param ctx the parse tree
	 */
	void enterRule_function_id(CdqlParser.Rule_function_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_function_id}.
	 * @param ctx the parse tree
	 */
	void exitRule_function_id(CdqlParser.Rule_function_idContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#rule_url}.
	 * @param ctx the parse tree
	 */
	void enterRule_url(CdqlParser.Rule_urlContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#rule_url}.
	 * @param ctx the parse tree
	 */
	void exitRule_url(CdqlParser.Rule_urlContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#authority}.
	 * @param ctx the parse tree
	 */
	void enterAuthority(CdqlParser.AuthorityContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#authority}.
	 * @param ctx the parse tree
	 */
	void exitAuthority(CdqlParser.AuthorityContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#host}.
	 * @param ctx the parse tree
	 */
	void enterHost(CdqlParser.HostContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#host}.
	 * @param ctx the parse tree
	 */
	void exitHost(CdqlParser.HostContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#hostname}.
	 * @param ctx the parse tree
	 */
	void enterHostname(CdqlParser.HostnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#hostname}.
	 * @param ctx the parse tree
	 */
	void exitHostname(CdqlParser.HostnameContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#hostnumber}.
	 * @param ctx the parse tree
	 */
	void enterHostnumber(CdqlParser.HostnumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#hostnumber}.
	 * @param ctx the parse tree
	 */
	void exitHostnumber(CdqlParser.HostnumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#search}.
	 * @param ctx the parse tree
	 */
	void enterSearch(CdqlParser.SearchContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#search}.
	 * @param ctx the parse tree
	 */
	void exitSearch(CdqlParser.SearchContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#searchparameter}.
	 * @param ctx the parse tree
	 */
	void enterSearchparameter(CdqlParser.SearchparameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#searchparameter}.
	 * @param ctx the parse tree
	 */
	void exitSearchparameter(CdqlParser.SearchparameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#port}.
	 * @param ctx the parse tree
	 */
	void enterPort(CdqlParser.PortContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#port}.
	 * @param ctx the parse tree
	 */
	void exitPort(CdqlParser.PortContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#path}.
	 * @param ctx the parse tree
	 */
	void enterPath(CdqlParser.PathContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#path}.
	 * @param ctx the parse tree
	 */
	void exitPath(CdqlParser.PathContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#normal_path}.
	 * @param ctx the parse tree
	 */
	void enterNormal_path(CdqlParser.Normal_pathContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#normal_path}.
	 * @param ctx the parse tree
	 */
	void exitNormal_path(CdqlParser.Normal_pathContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#path_param}.
	 * @param ctx the parse tree
	 */
	void enterPath_param(CdqlParser.Path_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#path_param}.
	 * @param ctx the parse tree
	 */
	void exitPath_param(CdqlParser.Path_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#json}.
	 * @param ctx the parse tree
	 */
	void enterJson(CdqlParser.JsonContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#json}.
	 * @param ctx the parse tree
	 */
	void exitJson(CdqlParser.JsonContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#obj}.
	 * @param ctx the parse tree
	 */
	void enterObj(CdqlParser.ObjContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#obj}.
	 * @param ctx the parse tree
	 */
	void exitObj(CdqlParser.ObjContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(CdqlParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(CdqlParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(CdqlParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(CdqlParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link CdqlParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(CdqlParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CdqlParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(CdqlParser.ValueContext ctx);
}