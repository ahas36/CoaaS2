var antlrEditor = require('.');

var editor = antlrEditor.createEditor();
editor.setEditorPlaceholderText('Enter your code here...');

editor.setShowLineNumbers(true);
editor.setDisplayEditorErrors(true);

editor.setTheme('idea');
editor.focus();

var el = editor.getDomElement();

// Render the editor
document.body.appendChild(el);

//
// Rule Styles
//
var ruleStyles = {};
//ruleStyles['rule_Cdql'] = 'cm-keyword';
//ruleStyles['rule_ddl_statement'] = 'cm-keyword';
//ruleStyles['rule_dml_statement'] = 'cm-keyword';
//ruleStyles['rule_query'] = 'cm-keyword';
//ruleStyles['rule_create_function'] = 'cm-keyword';
//ruleStyles['rule_set_package'] = 'cm-keyword';
//ruleStyles['rule_create_package'] = 'cm-keyword';
//ruleStyles['rule_alter_package'] = 'cm-keyword';
//ruleStyles['rule_alter_function'] = 'cm-keyword';
//ruleStyles['rule_drop_package'] = 'cm-keyword';
//ruleStyles['rule_drop_function'] = 'cm-keyword';
//ruleStyles['rule_package_title'] = 'cm-keyword';
//ruleStyles['rule_Set_Config'] = 'cm-keyword';
//ruleStyles['rule_Set_Callback'] = 'cm-keyword';
//ruleStyles['rule_Output_Config'] = 'cm-keyword';
//ruleStyles['rule_Callback_Config'] = 'cm-keyword';
//ruleStyles['rule_Prefixs'] = 'cm-keyword';
//ruleStyles['rule_Prefix'] = 'cm-keyword';
//ruleStyles['rule_Pull'] = 'cm-keyword';
//ruleStyles['rule_Select'] = 'cm-keyword';
//ruleStyles['rule_select_Attribute'] = 'cm-keyword';
//ruleStyles['rule_select_FunctionCall'] = 'cm-keyword';
//ruleStyles['rule_Attribute'] = 'cm-keyword';
//ruleStyles['rule_EntityTitle'] = 'cm-keyword';
//ruleStyles['rule_AttributeTitle'] = 'cm-keyword';
//ruleStyles['rule_FunctionCall'] = 'cm-keyword';
//ruleStyles['rule_function_call_method_chaining'] = 'cm-keyword';
//ruleStyles['rule_call_FunctionTitle'] = 'cm-keyword';
//ruleStyles['rule_call_Operand'] = 'cm-keyword';
//ruleStyles['rule_Name_Operand'] = 'cm-keyword';
//ruleStyles['rule_FunctionTitle'] = 'cm-keyword';
//ruleStyles['rule_Operand'] = 'cm-keyword';
//ruleStyles['rule_ContextValue'] = 'cm-keyword';
//ruleStyles['rule_When'] = 'cm-keyword';
//ruleStyles['rule_repeat'] = 'cm-keyword';
//ruleStyles['rule_Start'] = 'cm-keyword';
//ruleStyles['rule_Date_Time_When'] = 'cm-keyword';
//ruleStyles['rule_Occurrence'] = 'cm-keyword';
//ruleStyles['rule_Date_Time'] = 'cm-keyword';
//ruleStyles['rule_Date'] = 'cm-keyword';
//ruleStyles['rule_Time'] = 'cm-keyword';
//ruleStyles['rule_Condition'] = 'cm-keyword';
//ruleStyles['rule_expr_op'] = 'cm-keyword';
//ruleStyles['rule_Constraint'] = 'cm-keyword';
//ruleStyles['rule_left_element'] = 'cm-keyword';
//ruleStyles['rule_right_element'] = 'cm-keyword';
//ruleStyles['rule_target_element'] = 'cm-keyword';
//ruleStyles['rule_relational_op_func'] = 'cm-keyword';
//ruleStyles['rule_relational_op'] = 'cm-keyword';
//ruleStyles['rule_between_op'] = 'cm-keyword';
//ruleStyles['rule_is_or_is_not'] = 'cm-keyword';
//ruleStyles['ruel_Push'] = 'cm-keyword';
//ruleStyles['rule_callback'] = 'cm-keyword';
//ruleStyles['rule_http_calback'] = 'cm-keyword';
//ruleStyles['rule_fcm_calback'] = 'cm-keyword';
//ruleStyles['rule_fcm_topic'] = 'cm-keyword';
//ruleStyles['rule_fcm_token'] = 'cm-keyword';
//ruleStyles['rule_callback_url'] = 'cm-keyword';
//ruleStyles['rule_Define'] = 'cm-keyword';
//ruleStyles['rule_Define_Context_Entity'] = 'cm-keyword';
//ruleStyles['rule_context_entity'] = 'cm-keyword';
//ruleStyles['rule_entity_type'] = 'cm-keyword';
//ruleStyles['rule_Define_Context_Function'] = 'cm-keyword';
//ruleStyles['rule_context_function'] = 'cm-keyword';
//ruleStyles['rule_aFunction'] = 'cm-keyword';
//ruleStyles['rule_sFunction'] = 'cm-keyword';
//ruleStyles['rule_is_on'] = 'cm-keyword';
//ruleStyles['rule_is_on_entity'] = 'cm-keyword';
//ruleStyles['cst_situation_def_rule'] = 'cm-keyword';
//ruleStyles['rule_single_situatuin'] = 'cm-keyword';
//ruleStyles['rule_situation_pair'] = 'cm-keyword';
//ruleStyles['rule_situation_attributes'] = 'cm-keyword';
//ruleStyles['rule_situation_attribute_name'] = 'cm-keyword';
//ruleStyles['situation_pair_values'] = 'cm-keyword';
//ruleStyles['situation_weight'] = 'cm-keyword';
//ruleStyles['situation_range_values'] = 'cm-keyword';
//ruleStyles['situation_pair_values_item'] = 'cm-keyword';
//ruleStyles['rule_situation_belief'] = 'cm-keyword';
//ruleStyles['rule_situation_value'] = 'cm-keyword';
//ruleStyles['rule_discrete_value'] = 'cm-keyword';
//ruleStyles['discrete_value'] = 'cm-keyword';
//ruleStyles['rule_region_value'] = 'cm-keyword';
//ruleStyles['region_value_inclusive'] = 'cm-keyword';
//ruleStyles['region_value_left_inclusive'] = 'cm-keyword';
//ruleStyles['region_value_right_inclusive'] = 'cm-keyword';
//ruleStyles['region_value_exclusive'] = 'cm-keyword';
//ruleStyles['region_value_value'] = 'cm-keyword';
//ruleStyles['rule_entity_id'] = 'cm-keyword';
//ruleStyles['rule_function_id'] = 'cm-keyword';
//ruleStyles['rule_url'] = 'cm-keyword';
//ruleStyles['authority'] = 'cm-keyword';
//ruleStyles['host'] = 'cm-keyword';
//ruleStyles['hostname'] = 'cm-keyword';
//ruleStyles['hostnumber'] = 'cm-keyword';
//ruleStyles['search'] = 'cm-keyword';
//ruleStyles['searchparameter'] = 'cm-keyword';
//ruleStyles['port'] = 'cm-keyword';
//ruleStyles['path'] = 'cm-keyword';
//ruleStyles['normal_path'] = 'cm-keyword';
//ruleStyles['path_param'] = 'cm-keyword';
//ruleStyles['json'] = 'cm-keyword';
//ruleStyles['obj'] = 'cm-keyword';
//ruleStyles['pair'] = 'cm-keyword';
//ruleStyles['array'] = 'cm-keyword';
//ruleStyles['value'] = 'cm-keyword';
editor.setDefaultRuleStyles(ruleStyles);

var tokenStyles = {};

//
// Token Styles
//
//tokenStyles['T__0'] = 'cm-keyword';
//tokenStyles['T__1'] = 'cm-keyword';
//tokenStyles['T__2'] = 'cm-keyword';
//tokenStyles['T__3'] = 'cm-keyword';
//tokenStyles['T__4'] = 'cm-keyword';
//tokenStyles['T__5'] = 'cm-keyword';
//tokenStyles['T__6'] = 'cm-keyword';
//tokenStyles['T__7'] = 'cm-keyword';
//tokenStyles['T__8'] = 'cm-keyword';
//tokenStyles['T__9'] = 'cm-keyword';
//tokenStyles['T__10'] = 'cm-keyword';
//tokenStyles['T__11'] = 'cm-keyword';
//tokenStyles['T__12'] = 'cm-keyword';
//tokenStyles['T__13'] = 'cm-keyword';
//tokenStyles['T__14'] = 'cm-keyword';
//tokenStyles['T__15'] = 'cm-keyword';
//tokenStyles['T__16'] = 'cm-keyword';
//tokenStyles['T__17'] = 'cm-keyword';
//tokenStyles['T__18'] = 'cm-keyword';
//tokenStyles['TITLE'] = 'cm-keyword';
//tokenStyles['PACKAGE'] = 'cm-keyword';
//tokenStyles['FUNCTION'] = 'cm-keyword';
//tokenStyles['CREATE'] = 'cm-keyword';
//tokenStyles['SET'] = 'cm-keyword';
//tokenStyles['ALTER'] = 'cm-keyword';
//tokenStyles['DROP'] = 'cm-keyword';
//tokenStyles['DEFINE'] = 'cm-keyword';
//tokenStyles['CONTEXT_ENTITY'] = 'cm-keyword';
//tokenStyles['IS_FROM'] = 'cm-keyword';
//tokenStyles['WHERE'] = 'cm-keyword';
//tokenStyles['WHEN'] = 'cm-keyword';
//tokenStyles['DATE'] = 'cm-keyword';
//tokenStyles['LIFETIME'] = 'cm-keyword';
//tokenStyles['BETWEEN'] = 'cm-keyword';
//tokenStyles['IS'] = 'cm-keyword';
//tokenStyles['PULL'] = 'cm-keyword';
//tokenStyles['ENTITY'] = 'cm-keyword';
//tokenStyles['AS'] = 'cm-keyword';
//tokenStyles['EVERY'] = 'cm-keyword';
//tokenStyles['UNTIL'] = 'cm-keyword';
//tokenStyles['LPAREN'] = 'cm-keyword';
//tokenStyles['COMMA'] = 'cm-keyword';
//tokenStyles['RPAREN'] = 'cm-keyword';
//tokenStyles['DOT'] = 'cm-keyword';
//tokenStyles['NOT'] = 'cm-keyword';
//tokenStyles['AND'] = 'cm-keyword';
//tokenStyles['OR'] = 'cm-keyword';
//tokenStyles['XOR'] = 'cm-keyword';
//tokenStyles['IN'] = 'cm-keyword';
//tokenStyles['CONTAINS_ANY'] = 'cm-keyword';
//tokenStyles['CONTAINS_ALL'] = 'cm-keyword';
//tokenStyles['NULL'] = 'cm-keyword';
//tokenStyles['EQ'] = 'cm-keyword';
//tokenStyles['LTH'] = 'cm-keyword';
//tokenStyles['GTH'] = 'cm-keyword';
//tokenStyles['LET'] = 'cm-keyword';
//tokenStyles['GET'] = 'cm-keyword';
//tokenStyles['NOT_EQ'] = 'cm-keyword';
//tokenStyles['PUSH'] = 'cm-keyword';
//tokenStyles['INTO'] = 'cm-keyword';
//tokenStyles['PREFIX'] = 'cm-keyword';
//tokenStyles['HTTPPOST'] = 'cm-keyword';
//tokenStyles['POST'] = 'cm-keyword';
//tokenStyles['METHOD'] = 'cm-keyword';
//tokenStyles['URL'] = 'cm-keyword';
//tokenStyles['FCM'] = 'cm-keyword';
//tokenStyles['TOPIC'] = 'cm-keyword';
//tokenStyles['TOKEN'] = 'cm-keyword';
//tokenStyles['TYPE'] = 'cm-keyword';
//tokenStyles['COLON'] = 'cm-keyword';
//tokenStyles['ASTERISK'] = 'cm-keyword';
//tokenStyles['UNIT_OF_TIME'] = 'cm-keyword';
//tokenStyles['OCCURRENCES'] = 'cm-keyword';
//tokenStyles['FSLASH'] = 'cm-keyword';
//tokenStyles['OP'] = 'cm-keyword';
//tokenStyles['OUTPUT'] = 'cm-keyword';
//tokenStyles['CALLBACK'] = 'cm-keyword';
//tokenStyles['TIME_ZONE'] = 'cm-keyword';
//tokenStyles['STRING'] = 'cm-keyword';
//tokenStyles['ID'] = 'cm-keyword';
//tokenStyles['ATTRIBUTEID'] = 'cm-keyword';
//tokenStyles['ESC'] = 'cm-keyword';
//tokenStyles['UNICODE'] = 'cm-keyword';
//tokenStyles['HEX'] = 'cm-keyword';
//tokenStyles['COMMENT'] = 'cm-keyword';
//tokenStyles['WS'] = 'cm-keyword';
//tokenStyles['NUMBER'] = 'cm-keyword';
//tokenStyles['INT'] = 'cm-keyword';
//tokenStyles['EXP'] = 'cm-keyword';

editor.setDefaultTokenStyles(tokenStyles);
