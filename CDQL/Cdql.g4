/**
 * @Generated
 */
grammar Cdql;


rule_Cdql : rule_Prefixs? (rule_ddl_statement | rule_dml_statement) ;

rule_ddl_statement :  rule_create_function | rule_create_package |
                rule_alter_function | rule_alter_package|
                rule_drop_function | rule_drop_package | ;

rule_dml_statement : rule_query;

rule_query : (rule_Pull | ruel_Push rule_When rule_repeat?)  rule_Define rule_Set_Config? rule_Set_Callback? rule_Set_Meta?;

rule_create_function : CREATE (rule_sFunction | rule_aFunction) rule_set_package?;

rule_set_package : SET PACKAGE rule_package_title;

rule_create_package : CREATE PACKAGE rule_package_title;

rule_alter_package : ALTER PACKAGE rule_package_title SET TITLE rule_package_title;

rule_alter_function : 'tbd';

rule_drop_package: DROP PACKAGE rule_package_title;

rule_drop_function: DROP FUNCTION rule_function_id;

rule_package_title: ID;

rule_Set_Meta : SET (rule_Meta_Config);

rule_Set_Config : SET (rule_Output_Config);

rule_Set_Callback : SET (rule_Callback_Config);

rule_Output_Config : OUTPUT COLON obj;

rule_Meta_Config : META COLON obj;

rule_Callback_Config : CALLBACK COLON obj;

rule_Prefixs : rule_Prefix (COMMA rule_Prefix)*;

rule_Prefix : PREFIX ID COLON rule_url;

rule_Pull : PULL rule_Select;

rule_Select : LPAREN (rule_select_Attribute | rule_select_FunctionCall) (COMMA (rule_select_Attribute | rule_select_FunctionCall))* RPAREN;

rule_select_Attribute : rule_Attribute | rule_EntityTitle DOT ASTERISK;

rule_select_FunctionCall : rule_FunctionCall;

rule_Attribute : rule_EntityTitle (DOT rule_AttributeTitle)+;

rule_EntityTitle : ID;

rule_AttributeTitle : ID;

rule_FunctionCall : rule_call_FunctionTitle LPAREN rule_call_Operand (COMMA rule_call_Operand)* RPAREN rule_function_call_method_chaining ;

rule_function_call_method_chaining : (DOT ID)*;

rule_call_FunctionTitle : rule_FunctionTitle;

rule_call_Operand : rule_Operand | rule_Name_Operand;

rule_Name_Operand : ID COLON rule_Operand;

rule_FunctionTitle : ID (DOT ID)?;

rule_Operand : rule_EntityTitle | rule_Attribute | rule_FunctionCall | rule_ContextValue;

rule_ContextValue : NUMBER | STRING | json;

rule_When: WHEN rule_Start;

rule_repeat : (EVERY NUMBER UNIT_OF_TIME) (UNTIL rule_Occurrence)? | (UNTIL rule_Occurrence);

rule_Start : rule_Condition | rule_Date_Time_When;

rule_Date_Time_When : 'time' COLON rule_Date_Time;

rule_Occurrence : NUMBER UNIT_OF_TIME | NUMBER OCCURRENCES | rule_Date_Time | LIFETIME;

rule_Date_Time :  rule_Date rule_Time?;

rule_Date : NUMBER FSLASH NUMBER FSLASH NUMBER;

rule_Time : NUMBER COLON NUMBER (COLON NUMBER)? TIME_ZONE?;

rule_Condition : rule_Constraint | rule_Condition rule_expr_op rule_Condition | LPAREN rule_Condition RPAREN | NOT rule_Condition;

rule_expr_op : AND | XOR | OR | NOT;

rule_Constraint : rule_left_element rule_relational_op_func rule_right_element | rule_target_element rule_between_op rule_left_element AND rule_right_element | rule_target_element rule_is_or_is_not NULL;

rule_left_element : rule_Operand;

rule_right_element : rule_Operand;

rule_target_element : rule_Operand;

rule_relational_op_func :  rule_relational_op | OP LPAREN rule_relational_op COMMA NUMBER RPAREN;

rule_relational_op: EQ | LTH | NOT_EQ | GTH | LET | GET | CONTAINS_ANY | CONTAINS_ALL;

rule_between_op : BETWEEN | OP LPAREN BETWEEN COMMA NUMBER RPAREN;

rule_is_or_is_not : IS | IS NOT;

ruel_Push: PUSH rule_Select ;

rule_callback : rule_http_calback | rule_fcm_calback;

rule_http_calback : METHOD EQ HTTPPOST  URL EQ rule_callback_url;

rule_fcm_calback : METHOD EQ FCM (rule_fcm_topic | rule_fcm_token);

rule_fcm_topic: TOPIC EQ STRING;

rule_fcm_token: TOKEN EQ STRING;

rule_callback_url : rule_url;

rule_Define : DEFINE rule_Define_Context_Entity (COMMA rule_Define_Context_Function)?;

rule_Define_Context_Entity:  rule_context_entity (COMMA rule_context_entity)*;

rule_context_entity : ENTITY rule_entity_id IS_FROM rule_entity_type  (WHERE rule_Condition)?;

rule_entity_type : (ID COLON)? ID (DOT ID)?;

rule_Define_Context_Function : rule_context_function (COMMA rule_context_function)*;

rule_context_function : rule_aFunction | rule_sFunction;

rule_aFunction : 'aFunction' rule_function_id rule_url;

rule_sFunction : 'sFunction' rule_function_id rule_is_on ( cst_situation_def_rule);

rule_is_on : 'is on' rule_is_on_entity (COMMA rule_is_on_entity)* ;

rule_is_on_entity : rule_entity_type AS ID;

cst_situation_def_rule : '{' rule_single_situatuin (COMMA rule_single_situatuin)* '}';

rule_single_situatuin : STRING COLON '{' rule_situation_pair (COMMA rule_situation_pair)* '}';

rule_situation_pair :  rule_situation_attributes ':' '{' situation_pair_values '}';

rule_situation_attributes : rule_situation_attribute_name | '[' rule_situation_attribute_name (COMMA rule_situation_attribute_name)+ ']';

rule_situation_attribute_name : ID (DOT ID)*;

situation_pair_values :  (situation_range_values COMMA situation_weight) | (situation_weight COMMA situation_range_values);

situation_weight : 'weight' COLON NUMBER;

situation_range_values: 'ranges' COLON '[' situation_pair_values_item (COMMA situation_pair_values_item)* ']';

situation_pair_values_item : '{' ((rule_situation_belief COMMA rule_situation_value) | (rule_situation_value COMMA rule_situation_belief)) '}';

rule_situation_belief: 'belief' COLON NUMBER;

rule_situation_value : 'value' COLON ( rule_region_value | rule_discrete_value | discrete_value);

rule_discrete_value : '[' discrete_value (COLON discrete_value)* ']';

discrete_value  : json;

rule_region_value : region_value_inclusive | region_value_left_inclusive | region_value_right_inclusive | region_value_exclusive;

region_value_inclusive: '[' region_value_value ']';

region_value_left_inclusive: '[' region_value_value ')';

region_value_right_inclusive: '(' region_value_value ']';

region_value_exclusive: '(' region_value_value ')';

region_value_value: NUMBER ';' NUMBER ;

rule_entity_id : ID;

rule_function_id : ID;

rule_url
   : authority '://'  host (':' port)? ('/' path)? ('?' search)?
   ;

authority
   : ID
   ;

host : hostname| hostnumber;

hostname : ID ('.' ID)*;

hostnumber : INT '.' INT '.' INT '.' INT;

search : searchparameter ('&' searchparameter)*;

searchparameter : ID ('=' (ID |INT | HEX))?;

port
   : INT
   ;

path
   : (normal_path | path_param) ('/' (normal_path | path_param))*
   ;

normal_path : ID;

path_param : '{' ID '}';


TITLE : 'title';

PACKAGE: 'package';

FUNCTION : 'function';

CREATE : 'create';

SET : 'set';

ALTER : 'alter';

DROP : 'drop';

DEFINE : 'define';

CONTEXT_ENTITY : 'context entity';

IS_FROM : 'is from';

WHERE : 'where';

WHEN : 'when';

DATE : 'date';

LIFETIME : 'lifetime';

BETWEEN : 'between';

IS : 'is';

PULL : 'pull';

ENTITY : 'entity';

AS : 'as';

EVERY : 'every';

UNTIL : 'until';

LPAREN : '(';

COMMA : ',';

RPAREN : ')';

DOT : '.';

NOT : '~' | '!' | 'not';

AND : 'and' | '&&' ;

OR : 'or' | '||';

XOR : 'xor';

IN : 'in';

CONTAINS_ANY : 'containsAny';

CONTAINS_ALL : 'containsAll';

NULL : 'null';

EQ  : '=';

LTH : '<';

GTH : '>' ;

LET : '<=';

GET : '>=';

NOT_EQ : '!=';

PUSH : 'push';

INTO : 'into';

PREFIX : 'prefix';

HTTPPOST: 'http/post';

POST : 'post';



METHOD : 'method';

URL: 'url';

FCM : 'fcm';

TOPIC : 'topic';

TOKEN : 'token';

TYPE : 'type';

COLON : ':';

ASTERISK : '*';

UNIT_OF_TIME : 'h' |'s' |'ms' | 'd' | 'm' | 'ns';

OCCURRENCES : 'occurences';

FSLASH: '/';

OP : '$op';

OUTPUT : 'output';

META : 'meta';

CALLBACK : 'callback';

TIME_ZONE :  'UT'
               | 'GMT'
               | 'EST'
               | 'EDT'
               | 'CST'
               | 'CDT'
               | 'MST'
               | 'MDT'
               | 'PST'
               | 'PDT'
               | (('+' | '-') NUMBER);

json
   : value
   ;

obj
   : '{' pair (',' pair)* '}'
   | '{' '}'
   ;

pair
   : STRING ':' value
   ;

array
   : '[' value (',' value)*? ']'
   | '[' ']'
   ;

value
   : STRING
   | NUMBER
   | obj
   | array
   | 'true'
   | 'false'
   | 'null'
   ;



STRING
   : '"' (ESC | ~ ["\\])* '"';



ID : ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')* ;


fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

   fragment UNICODE
      : 'u' HEX HEX HEX HEX
      ;
   fragment HEX
      : [0-9a-fA-F]
      ;

COMMENT : ('/*' .* '*/' | '//' ~('\r' | '\n')*)   -> skip ;

WS:  (' '|'\r'|'\t'|'\u000C'|'\n') -> skip ;



NUMBER
   : '-'? INT '.' [0-9] + EXP? | '-'? INT EXP | '-'? INT
   ;


fragment INT
   : '0' | [1-9] [0-9]*
   ;

   fragment EXP
      : [Ee] [+\-]? INT
      ;