// Generated from /Users/ali/CDQLParserV3/Cdql.g4 by ANTLR 4.6
package au.coaas.cqp.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.*;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CdqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, TITLE=20, PACKAGE=21, FUNCTION=22, CREATE=23, SET=24, 
		ALTER=25, DROP=26, DEFINE=27, CONTEXT_ENTITY=28, IS_FROM=29, WHERE=30, 
		WHEN=31, DATE=32, LIFETIME=33, BETWEEN=34, IS=35, PULL=36, ENTITY=37, 
		AS=38, EVERY=39, UNTIL=40, LPAREN=41, COMMA=42, RPAREN=43, DOT=44, NOT=45, 
		AND=46, OR=47, XOR=48, IN=49, CONTAINS_ANY=50, CONTAINS_ALL=51, NULL=52, 
		EQ=53, LTH=54, GTH=55, LET=56, GET=57, NOT_EQ=58, PUSH=59, INTO=60, PREFIX=61, 
		HTTPPOST=62, POST=63, METHOD=64, URL=65, FCM=66, TOPIC=67, TOKEN=68, TYPE=69, 
		COLON=70, ASTERISK=71, UNIT_OF_TIME=72, OCCURRENCES=73, FSLASH=74, OP=75, 
		OUTPUT=76, CALLBACK=77, TIME_ZONE=78, STRING=79, ID=80, COMMENT=81, WS=82, 
		NUMBER=83, INT=84, HEX=85;
	public static final int
		RULE_rule_Cdql = 0, RULE_rule_ddl_statement = 1, RULE_rule_dml_statement = 2, 
		RULE_rule_query = 3, RULE_rule_create_function = 4, RULE_rule_set_package = 5, 
		RULE_rule_create_package = 6, RULE_rule_alter_package = 7, RULE_rule_alter_function = 8, 
		RULE_rule_drop_package = 9, RULE_rule_drop_function = 10, RULE_rule_package_title = 11, 
		RULE_rule_Set_Config = 12, RULE_rule_Set_Callback = 13, RULE_rule_Output_Config = 14, 
		RULE_rule_Callback_Config = 15, RULE_rule_Prefixs = 16, RULE_rule_Prefix = 17, 
		RULE_rule_Pull = 18, RULE_rule_Select = 19, RULE_rule_select_Attribute = 20, 
		RULE_rule_select_FunctionCall = 21, RULE_rule_Attribute = 22, RULE_rule_EntityTitle = 23, 
		RULE_rule_AttributeTitle = 24, RULE_rule_FunctionCall = 25, RULE_rule_function_call_method_chaining = 26, 
		RULE_rule_call_FunctionTitle = 27, RULE_rule_call_Operand = 28, RULE_rule_Name_Operand = 29, 
		RULE_rule_FunctionTitle = 30, RULE_rule_Operand = 31, RULE_rule_ContextValue = 32, 
		RULE_rule_When = 33, RULE_rule_repeat = 34, RULE_rule_Start = 35, RULE_rule_Date_Time_When = 36, 
		RULE_rule_Occurrence = 37, RULE_rule_Date_Time = 38, RULE_rule_Date = 39, 
		RULE_rule_Time = 40, RULE_rule_Condition = 41, RULE_rule_expr_op = 42, 
		RULE_rule_Constraint = 43, RULE_rule_left_element = 44, RULE_rule_right_element = 45, 
		RULE_rule_target_element = 46, RULE_rule_relational_op_func = 47, RULE_rule_relational_op = 48, 
		RULE_rule_between_op = 49, RULE_rule_is_or_is_not = 50, RULE_ruel_Push = 51, 
		RULE_rule_callback = 52, RULE_rule_http_calback = 53, RULE_rule_fcm_calback = 54, 
		RULE_rule_fcm_topic = 55, RULE_rule_fcm_token = 56, RULE_rule_callback_url = 57, 
		RULE_rule_Define = 58, RULE_rule_Define_Context_Entity = 59, RULE_rule_context_entity = 60, 
		RULE_rule_entity_type = 61, RULE_rule_Define_Context_Function = 62, RULE_rule_context_function = 63, 
		RULE_rule_aFunction = 64, RULE_rule_sFunction = 65, RULE_rule_is_on = 66, 
		RULE_rule_is_on_entity = 67, RULE_cst_situation_def_rule = 68, RULE_rule_single_situatuin = 69, 
		RULE_rule_situation_pair = 70, RULE_rule_situation_attributes = 71, RULE_rule_situation_attribute_name = 72, 
		RULE_situation_pair_values = 73, RULE_situation_weight = 74, RULE_situation_range_values = 75, 
		RULE_situation_pair_values_item = 76, RULE_rule_situation_belief = 77, 
		RULE_rule_situation_value = 78, RULE_rule_discrete_value = 79, RULE_discrete_value = 80, 
		RULE_rule_region_value = 81, RULE_region_value_inclusive = 82, RULE_region_value_left_inclusive = 83, 
		RULE_region_value_right_inclusive = 84, RULE_region_value_exclusive = 85, 
		RULE_region_value_value = 86, RULE_rule_entity_id = 87, RULE_rule_function_id = 88, 
		RULE_rule_url = 89, RULE_authority = 90, RULE_host = 91, RULE_hostname = 92, 
		RULE_hostnumber = 93, RULE_search = 94, RULE_searchparameter = 95, RULE_port = 96, 
		RULE_path = 97, RULE_normal_path = 98, RULE_path_param = 99, RULE_json = 100, 
		RULE_obj = 101, RULE_pair = 102, RULE_array = 103, RULE_value = 104;
	public static final String[] ruleNames = {
		"rule_Cdql", "rule_ddl_statement", "rule_dml_statement", "rule_query", 
		"rule_create_function", "rule_set_package", "rule_create_package", "rule_alter_package", 
		"rule_alter_function", "rule_drop_package", "rule_drop_function", "rule_package_title", 
		"rule_Set_Config", "rule_Set_Callback", "rule_Output_Config", "rule_Callback_Config", 
		"rule_Prefixs", "rule_Prefix", "rule_Pull", "rule_Select", "rule_select_Attribute", 
		"rule_select_FunctionCall", "rule_Attribute", "rule_EntityTitle", "rule_AttributeTitle", 
		"rule_FunctionCall", "rule_function_call_method_chaining", "rule_call_FunctionTitle", 
		"rule_call_Operand", "rule_Name_Operand", "rule_FunctionTitle", "rule_Operand", 
		"rule_ContextValue", "rule_When", "rule_repeat", "rule_Start", "rule_Date_Time_When", 
		"rule_Occurrence", "rule_Date_Time", "rule_Date", "rule_Time", "rule_Condition", 
		"rule_expr_op", "rule_Constraint", "rule_left_element", "rule_right_element", 
		"rule_target_element", "rule_relational_op_func", "rule_relational_op", 
		"rule_between_op", "rule_is_or_is_not", "ruel_Push", "rule_callback", 
		"rule_http_calback", "rule_fcm_calback", "rule_fcm_topic", "rule_fcm_token", 
		"rule_callback_url", "rule_Define", "rule_Define_Context_Entity", "rule_context_entity", 
		"rule_entity_type", "rule_Define_Context_Function", "rule_context_function", 
		"rule_aFunction", "rule_sFunction", "rule_is_on", "rule_is_on_entity", 
		"cst_situation_def_rule", "rule_single_situatuin", "rule_situation_pair", 
		"rule_situation_attributes", "rule_situation_attribute_name", "situation_pair_values", 
		"situation_weight", "situation_range_values", "situation_pair_values_item", 
		"rule_situation_belief", "rule_situation_value", "rule_discrete_value", 
		"discrete_value", "rule_region_value", "region_value_inclusive", "region_value_left_inclusive", 
		"region_value_right_inclusive", "region_value_exclusive", "region_value_value", 
		"rule_entity_id", "rule_function_id", "rule_url", "authority", "host", 
		"hostname", "hostnumber", "search", "searchparameter", "port", "path", 
		"normal_path", "path_param", "json", "obj", "pair", "array", "value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'tbd'", "'time'", "'aFunction'", "'sFunction'", "'is on'", "'{'", 
		"'}'", "'['", "']'", "'weight'", "'ranges'", "'belief'", "'value'", "';'", 
		"'://'", "'?'", "'&'", "'true'", "'false'", "'title'", "'package'", "'function'", 
		"'create'", "'set'", "'alter'", "'drop'", "'define'", "'context entity'", 
		"'is from'", "'where'", "'when'", "'date'", "'lifetime'", "'between'", 
		"'is'", "'pull'", "'entity'", "'as'", "'every'", "'until'", "'('", "','", 
		"')'", "'.'", null, null, null, "'xor'", "'in'", "'containsAny'", "'containsAll'", 
		"'null'", "'='", "'<'", "'>'", "'<='", "'>='", "'!='", "'push'", "'into'", 
		"'prefix'", "'http/post'", "'post'", "'method'", "'url'", "'fcm'", "'topic'", 
		"'token'", "'type'", "':'", "'*'", null, "'occurences'", "'/'", "'$op'", 
		"'output'", "'callback'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, "TITLE", "PACKAGE", "FUNCTION", 
		"CREATE", "SET", "ALTER", "DROP", "DEFINE", "CONTEXT_ENTITY", "IS_FROM", 
		"WHERE", "WHEN", "DATE", "LIFETIME", "BETWEEN", "IS", "PULL", "ENTITY", 
		"AS", "EVERY", "UNTIL", "LPAREN", "COMMA", "RPAREN", "DOT", "NOT", "AND", 
		"OR", "XOR", "IN", "CONTAINS_ANY", "CONTAINS_ALL", "NULL", "EQ", "LTH", 
		"GTH", "LET", "GET", "NOT_EQ", "PUSH", "INTO", "PREFIX", "HTTPPOST", "POST", 
		"METHOD", "URL", "FCM", "TOPIC", "TOKEN", "TYPE", "COLON", "ASTERISK", 
		"UNIT_OF_TIME", "OCCURRENCES", "FSLASH", "OP", "OUTPUT", "CALLBACK", "TIME_ZONE", 
		"STRING", "ID", "COMMENT", "WS", "NUMBER", "INT", "HEX"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Cdql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CdqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Rule_CdqlContext extends ParserRuleContext {
		public Rule_ddl_statementContext rule_ddl_statement() {
			return getRuleContext(Rule_ddl_statementContext.class,0);
		}
		public Rule_dml_statementContext rule_dml_statement() {
			return getRuleContext(Rule_dml_statementContext.class,0);
		}
		public Rule_PrefixsContext rule_Prefixs() {
			return getRuleContext(Rule_PrefixsContext.class,0);
		}
		public Rule_CdqlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Cdql; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Cdql(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_CdqlContext rule_Cdql() throws RecognitionException {
		Rule_CdqlContext _localctx = new Rule_CdqlContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_rule_Cdql);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PREFIX) {
				{
				setState(210);
				rule_Prefixs();
				}
			}

			setState(215);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EOF:
			case T__0:
			case CREATE:
			case ALTER:
			case DROP:
				{
				setState(213);
				rule_ddl_statement();
				}
				break;
			case PULL:
			case PUSH:
				{
				setState(214);
				rule_dml_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_ddl_statementContext extends ParserRuleContext {
		public Rule_create_functionContext rule_create_function() {
			return getRuleContext(Rule_create_functionContext.class,0);
		}
		public Rule_create_packageContext rule_create_package() {
			return getRuleContext(Rule_create_packageContext.class,0);
		}
		public Rule_alter_functionContext rule_alter_function() {
			return getRuleContext(Rule_alter_functionContext.class,0);
		}
		public Rule_alter_packageContext rule_alter_package() {
			return getRuleContext(Rule_alter_packageContext.class,0);
		}
		public Rule_drop_functionContext rule_drop_function() {
			return getRuleContext(Rule_drop_functionContext.class,0);
		}
		public Rule_drop_packageContext rule_drop_package() {
			return getRuleContext(Rule_drop_packageContext.class,0);
		}
		public Rule_ddl_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_ddl_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_ddl_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ddl_statementContext rule_ddl_statement() throws RecognitionException {
		Rule_ddl_statementContext _localctx = new Rule_ddl_statementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_rule_ddl_statement);
		try {
			setState(224);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(217);
				rule_create_function();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(218);
				rule_create_package();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(219);
				rule_alter_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(220);
				rule_alter_package();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(221);
				rule_drop_function();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(222);
				rule_drop_package();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_dml_statementContext extends ParserRuleContext {
		public Rule_queryContext rule_query() {
			return getRuleContext(Rule_queryContext.class,0);
		}
		public Rule_dml_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_dml_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_dml_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_dml_statementContext rule_dml_statement() throws RecognitionException {
		Rule_dml_statementContext _localctx = new Rule_dml_statementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rule_dml_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			rule_query();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_queryContext extends ParserRuleContext {
		public Rule_DefineContext rule_Define() {
			return getRuleContext(Rule_DefineContext.class,0);
		}
		public Rule_PullContext rule_Pull() {
			return getRuleContext(Rule_PullContext.class,0);
		}
		public Ruel_PushContext ruel_Push() {
			return getRuleContext(Ruel_PushContext.class,0);
		}
		public Rule_WhenContext rule_When() {
			return getRuleContext(Rule_WhenContext.class,0);
		}
		public Rule_Set_ConfigContext rule_Set_Config() {
			return getRuleContext(Rule_Set_ConfigContext.class,0);
		}
		public Rule_Set_CallbackContext rule_Set_Callback() {
			return getRuleContext(Rule_Set_CallbackContext.class,0);
		}
		public Rule_repeatContext rule_repeat() {
			return getRuleContext(Rule_repeatContext.class,0);
		}
		public Rule_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_query; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_query(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_queryContext rule_query() throws RecognitionException {
		Rule_queryContext _localctx = new Rule_queryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rule_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PULL:
				{
				setState(228);
				rule_Pull();
				}
				break;
			case PUSH:
				{
				setState(229);
				ruel_Push();
				setState(230);
				rule_When();
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EVERY || _la==UNTIL) {
					{
					setState(231);
					rule_repeat();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(236);
			rule_Define();
			setState(238);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(237);
				rule_Set_Config();
				}
				break;
			}
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SET) {
				{
				setState(240);
				rule_Set_Callback();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_create_functionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(CdqlParser.CREATE, 0); }
		public Rule_sFunctionContext rule_sFunction() {
			return getRuleContext(Rule_sFunctionContext.class,0);
		}
		public Rule_aFunctionContext rule_aFunction() {
			return getRuleContext(Rule_aFunctionContext.class,0);
		}
		public Rule_set_packageContext rule_set_package() {
			return getRuleContext(Rule_set_packageContext.class,0);
		}
		public Rule_create_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_create_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_create_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_create_functionContext rule_create_function() throws RecognitionException {
		Rule_create_functionContext _localctx = new Rule_create_functionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_rule_create_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(CREATE);
			setState(246);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				{
				setState(244);
				rule_sFunction();
				}
				break;
			case T__2:
				{
				setState(245);
				rule_aFunction();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SET) {
				{
				setState(248);
				rule_set_package();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_set_packageContext extends ParserRuleContext {
		public TerminalNode SET() { return getToken(CdqlParser.SET, 0); }
		public TerminalNode PACKAGE() { return getToken(CdqlParser.PACKAGE, 0); }
		public Rule_package_titleContext rule_package_title() {
			return getRuleContext(Rule_package_titleContext.class,0);
		}
		public Rule_set_packageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_set_package; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_set_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_set_packageContext rule_set_package() throws RecognitionException {
		Rule_set_packageContext _localctx = new Rule_set_packageContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rule_set_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(SET);
			setState(252);
			match(PACKAGE);
			setState(253);
			rule_package_title();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_create_packageContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(CdqlParser.CREATE, 0); }
		public TerminalNode PACKAGE() { return getToken(CdqlParser.PACKAGE, 0); }
		public Rule_package_titleContext rule_package_title() {
			return getRuleContext(Rule_package_titleContext.class,0);
		}
		public Rule_create_packageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_create_package; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_create_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_create_packageContext rule_create_package() throws RecognitionException {
		Rule_create_packageContext _localctx = new Rule_create_packageContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_rule_create_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(CREATE);
			setState(256);
			match(PACKAGE);
			setState(257);
			rule_package_title();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_alter_packageContext extends ParserRuleContext {
		public TerminalNode ALTER() { return getToken(CdqlParser.ALTER, 0); }
		public TerminalNode PACKAGE() { return getToken(CdqlParser.PACKAGE, 0); }
		public List<Rule_package_titleContext> rule_package_title() {
			return getRuleContexts(Rule_package_titleContext.class);
		}
		public Rule_package_titleContext rule_package_title(int i) {
			return getRuleContext(Rule_package_titleContext.class,i);
		}
		public TerminalNode SET() { return getToken(CdqlParser.SET, 0); }
		public TerminalNode TITLE() { return getToken(CdqlParser.TITLE, 0); }
		public Rule_alter_packageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_alter_package; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_alter_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_alter_packageContext rule_alter_package() throws RecognitionException {
		Rule_alter_packageContext _localctx = new Rule_alter_packageContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_rule_alter_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(259);
			match(ALTER);
			setState(260);
			match(PACKAGE);
			setState(261);
			rule_package_title();
			setState(262);
			match(SET);
			setState(263);
			match(TITLE);
			setState(264);
			rule_package_title();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_alter_functionContext extends ParserRuleContext {
		public Rule_alter_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_alter_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_alter_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_alter_functionContext rule_alter_function() throws RecognitionException {
		Rule_alter_functionContext _localctx = new Rule_alter_functionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_rule_alter_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_drop_packageContext extends ParserRuleContext {
		public TerminalNode DROP() { return getToken(CdqlParser.DROP, 0); }
		public TerminalNode PACKAGE() { return getToken(CdqlParser.PACKAGE, 0); }
		public Rule_package_titleContext rule_package_title() {
			return getRuleContext(Rule_package_titleContext.class,0);
		}
		public Rule_drop_packageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_drop_package; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_drop_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_drop_packageContext rule_drop_package() throws RecognitionException {
		Rule_drop_packageContext _localctx = new Rule_drop_packageContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_rule_drop_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268);
			match(DROP);
			setState(269);
			match(PACKAGE);
			setState(270);
			rule_package_title();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_drop_functionContext extends ParserRuleContext {
		public TerminalNode DROP() { return getToken(CdqlParser.DROP, 0); }
		public TerminalNode FUNCTION() { return getToken(CdqlParser.FUNCTION, 0); }
		public Rule_function_idContext rule_function_id() {
			return getRuleContext(Rule_function_idContext.class,0);
		}
		public Rule_drop_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_drop_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_drop_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_drop_functionContext rule_drop_function() throws RecognitionException {
		Rule_drop_functionContext _localctx = new Rule_drop_functionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_rule_drop_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			match(DROP);
			setState(273);
			match(FUNCTION);
			setState(274);
			rule_function_id();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_package_titleContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Rule_package_titleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_package_title; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_package_title(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_package_titleContext rule_package_title() throws RecognitionException {
		Rule_package_titleContext _localctx = new Rule_package_titleContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_rule_package_title);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(276);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Set_ConfigContext extends ParserRuleContext {
		public TerminalNode SET() { return getToken(CdqlParser.SET, 0); }
		public Rule_Output_ConfigContext rule_Output_Config() {
			return getRuleContext(Rule_Output_ConfigContext.class,0);
		}
		public Rule_Set_ConfigContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Set_Config; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Set_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Set_ConfigContext rule_Set_Config() throws RecognitionException {
		Rule_Set_ConfigContext _localctx = new Rule_Set_ConfigContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_rule_Set_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(SET);
			{
			setState(279);
			rule_Output_Config();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Set_CallbackContext extends ParserRuleContext {
		public TerminalNode SET() { return getToken(CdqlParser.SET, 0); }
		public Rule_Callback_ConfigContext rule_Callback_Config() {
			return getRuleContext(Rule_Callback_ConfigContext.class,0);
		}
		public Rule_Set_CallbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Set_Callback; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Set_Callback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Set_CallbackContext rule_Set_Callback() throws RecognitionException {
		Rule_Set_CallbackContext _localctx = new Rule_Set_CallbackContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_rule_Set_Callback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			match(SET);
			{
			setState(282);
			rule_Callback_Config();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Output_ConfigContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(CdqlParser.OUTPUT, 0); }
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public ObjContext obj() {
			return getRuleContext(ObjContext.class,0);
		}
		public Rule_Output_ConfigContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Output_Config; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Output_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Output_ConfigContext rule_Output_Config() throws RecognitionException {
		Rule_Output_ConfigContext _localctx = new Rule_Output_ConfigContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_rule_Output_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			match(OUTPUT);
			setState(285);
			match(COLON);
			setState(286);
			obj();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Callback_ConfigContext extends ParserRuleContext {
		public TerminalNode CALLBACK() { return getToken(CdqlParser.CALLBACK, 0); }
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public ObjContext obj() {
			return getRuleContext(ObjContext.class,0);
		}
		public Rule_Callback_ConfigContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Callback_Config; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Callback_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Callback_ConfigContext rule_Callback_Config() throws RecognitionException {
		Rule_Callback_ConfigContext _localctx = new Rule_Callback_ConfigContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_rule_Callback_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			match(CALLBACK);
			setState(289);
			match(COLON);
			setState(290);
			obj();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_PrefixsContext extends ParserRuleContext {
		public List<Rule_PrefixContext> rule_Prefix() {
			return getRuleContexts(Rule_PrefixContext.class);
		}
		public Rule_PrefixContext rule_Prefix(int i) {
			return getRuleContext(Rule_PrefixContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_PrefixsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Prefixs; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Prefixs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_PrefixsContext rule_Prefixs() throws RecognitionException {
		Rule_PrefixsContext _localctx = new Rule_PrefixsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_rule_Prefixs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			rule_Prefix();
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(293);
				match(COMMA);
				setState(294);
				rule_Prefix();
				}
				}
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_PrefixContext extends ParserRuleContext {
		public TerminalNode PREFIX() { return getToken(CdqlParser.PREFIX, 0); }
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public Rule_urlContext rule_url() {
			return getRuleContext(Rule_urlContext.class,0);
		}
		public Rule_PrefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Prefix; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Prefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_PrefixContext rule_Prefix() throws RecognitionException {
		Rule_PrefixContext _localctx = new Rule_PrefixContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_rule_Prefix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			match(PREFIX);
			setState(301);
			match(ID);
			setState(302);
			match(COLON);
			setState(303);
			rule_url();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_PullContext extends ParserRuleContext {
		public TerminalNode PULL() { return getToken(CdqlParser.PULL, 0); }
		public Rule_SelectContext rule_Select() {
			return getRuleContext(Rule_SelectContext.class,0);
		}
		public Rule_PullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Pull; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Pull(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_PullContext rule_Pull() throws RecognitionException {
		Rule_PullContext _localctx = new Rule_PullContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_rule_Pull);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			match(PULL);
			setState(306);
			rule_Select();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_SelectContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public List<Rule_select_AttributeContext> rule_select_Attribute() {
			return getRuleContexts(Rule_select_AttributeContext.class);
		}
		public Rule_select_AttributeContext rule_select_Attribute(int i) {
			return getRuleContext(Rule_select_AttributeContext.class,i);
		}
		public List<Rule_select_FunctionCallContext> rule_select_FunctionCall() {
			return getRuleContexts(Rule_select_FunctionCallContext.class);
		}
		public Rule_select_FunctionCallContext rule_select_FunctionCall(int i) {
			return getRuleContext(Rule_select_FunctionCallContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_SelectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Select; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Select(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_SelectContext rule_Select() throws RecognitionException {
		Rule_SelectContext _localctx = new Rule_SelectContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_rule_Select);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			match(LPAREN);
			setState(311);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(309);
				rule_select_Attribute();
				}
				break;
			case 2:
				{
				setState(310);
				rule_select_FunctionCall();
				}
				break;
			}
			setState(320);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(313);
				match(COMMA);
				setState(316);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(314);
					rule_select_Attribute();
					}
					break;
				case 2:
					{
					setState(315);
					rule_select_FunctionCall();
					}
					break;
				}
				}
				}
				setState(322);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(323);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_select_AttributeContext extends ParserRuleContext {
		public Rule_AttributeContext rule_Attribute() {
			return getRuleContext(Rule_AttributeContext.class,0);
		}
		public Rule_EntityTitleContext rule_EntityTitle() {
			return getRuleContext(Rule_EntityTitleContext.class,0);
		}
		public TerminalNode DOT() { return getToken(CdqlParser.DOT, 0); }
		public TerminalNode ASTERISK() { return getToken(CdqlParser.ASTERISK, 0); }
		public Rule_select_AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_select_Attribute; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_select_Attribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_select_AttributeContext rule_select_Attribute() throws RecognitionException {
		Rule_select_AttributeContext _localctx = new Rule_select_AttributeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_rule_select_Attribute);
		try {
			setState(330);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(325);
				rule_Attribute();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(326);
				rule_EntityTitle();
				setState(327);
				match(DOT);
				setState(328);
				match(ASTERISK);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_select_FunctionCallContext extends ParserRuleContext {
		public Rule_FunctionCallContext rule_FunctionCall() {
			return getRuleContext(Rule_FunctionCallContext.class,0);
		}
		public Rule_select_FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_select_FunctionCall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_select_FunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_select_FunctionCallContext rule_select_FunctionCall() throws RecognitionException {
		Rule_select_FunctionCallContext _localctx = new Rule_select_FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_rule_select_FunctionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			rule_FunctionCall();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_AttributeContext extends ParserRuleContext {
		public Rule_EntityTitleContext rule_EntityTitle() {
			return getRuleContext(Rule_EntityTitleContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(CdqlParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CdqlParser.DOT, i);
		}
		public List<Rule_AttributeTitleContext> rule_AttributeTitle() {
			return getRuleContexts(Rule_AttributeTitleContext.class);
		}
		public Rule_AttributeTitleContext rule_AttributeTitle(int i) {
			return getRuleContext(Rule_AttributeTitleContext.class,i);
		}
		public Rule_AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Attribute; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Attribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_AttributeContext rule_Attribute() throws RecognitionException {
		Rule_AttributeContext _localctx = new Rule_AttributeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_rule_Attribute);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			rule_EntityTitle();
			setState(339);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(335);
					match(DOT);
					setState(336);
					rule_AttributeTitle();
					}
					} 
				}
				setState(341);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_EntityTitleContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Rule_EntityTitleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_EntityTitle; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_EntityTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_EntityTitleContext rule_EntityTitle() throws RecognitionException {
		Rule_EntityTitleContext _localctx = new Rule_EntityTitleContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_rule_EntityTitle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_AttributeTitleContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Rule_AttributeTitleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_AttributeTitle; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_AttributeTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_AttributeTitleContext rule_AttributeTitle() throws RecognitionException {
		Rule_AttributeTitleContext _localctx = new Rule_AttributeTitleContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_rule_AttributeTitle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_FunctionCallContext extends ParserRuleContext {
		public Rule_call_FunctionTitleContext rule_call_FunctionTitle() {
			return getRuleContext(Rule_call_FunctionTitleContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public List<Rule_call_OperandContext> rule_call_Operand() {
			return getRuleContexts(Rule_call_OperandContext.class);
		}
		public Rule_call_OperandContext rule_call_Operand(int i) {
			return getRuleContext(Rule_call_OperandContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public Rule_function_call_method_chainingContext rule_function_call_method_chaining() {
			return getRuleContext(Rule_function_call_method_chainingContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_FunctionCall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_FunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_FunctionCallContext rule_FunctionCall() throws RecognitionException {
		Rule_FunctionCallContext _localctx = new Rule_FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_rule_FunctionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			rule_call_FunctionTitle();
			setState(347);
			match(LPAREN);
			setState(348);
			rule_call_Operand();
			setState(353);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(349);
				match(COMMA);
				setState(350);
				rule_call_Operand();
				}
				}
				setState(355);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(356);
			match(RPAREN);
			setState(357);
			rule_function_call_method_chaining();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_function_call_method_chainingContext extends ParserRuleContext {
		public List<TerminalNode> DOT() { return getTokens(CdqlParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CdqlParser.DOT, i);
		}
		public List<TerminalNode> ID() { return getTokens(CdqlParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(CdqlParser.ID, i);
		}
		public Rule_function_call_method_chainingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_function_call_method_chaining; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_function_call_method_chaining(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_function_call_method_chainingContext rule_function_call_method_chaining() throws RecognitionException {
		Rule_function_call_method_chainingContext _localctx = new Rule_function_call_method_chainingContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_rule_function_call_method_chaining);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(363);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(359);
					match(DOT);
					setState(360);
					match(ID);
					}
					} 
				}
				setState(365);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_call_FunctionTitleContext extends ParserRuleContext {
		public Rule_FunctionTitleContext rule_FunctionTitle() {
			return getRuleContext(Rule_FunctionTitleContext.class,0);
		}
		public Rule_call_FunctionTitleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_call_FunctionTitle; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_call_FunctionTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_call_FunctionTitleContext rule_call_FunctionTitle() throws RecognitionException {
		Rule_call_FunctionTitleContext _localctx = new Rule_call_FunctionTitleContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_rule_call_FunctionTitle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366);
			rule_FunctionTitle();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_call_OperandContext extends ParserRuleContext {
		public Rule_OperandContext rule_Operand() {
			return getRuleContext(Rule_OperandContext.class,0);
		}
		public Rule_Name_OperandContext rule_Name_Operand() {
			return getRuleContext(Rule_Name_OperandContext.class,0);
		}
		public Rule_call_OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_call_Operand; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_call_Operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_call_OperandContext rule_call_Operand() throws RecognitionException {
		Rule_call_OperandContext _localctx = new Rule_call_OperandContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_rule_call_Operand);
		try {
			setState(370);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(368);
				rule_Operand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(369);
				rule_Name_Operand();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Name_OperandContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public Rule_OperandContext rule_Operand() {
			return getRuleContext(Rule_OperandContext.class,0);
		}
		public Rule_Name_OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Name_Operand; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Name_Operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Name_OperandContext rule_Name_Operand() throws RecognitionException {
		Rule_Name_OperandContext _localctx = new Rule_Name_OperandContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_rule_Name_Operand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			match(ID);
			setState(373);
			match(COLON);
			setState(374);
			rule_Operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_FunctionTitleContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(CdqlParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(CdqlParser.ID, i);
		}
		public TerminalNode DOT() { return getToken(CdqlParser.DOT, 0); }
		public Rule_FunctionTitleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_FunctionTitle; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_FunctionTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_FunctionTitleContext rule_FunctionTitle() throws RecognitionException {
		Rule_FunctionTitleContext _localctx = new Rule_FunctionTitleContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_rule_FunctionTitle);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			match(ID);
			setState(379);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(377);
				match(DOT);
				setState(378);
				match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_OperandContext extends ParserRuleContext {
		public Rule_EntityTitleContext rule_EntityTitle() {
			return getRuleContext(Rule_EntityTitleContext.class,0);
		}
		public Rule_AttributeContext rule_Attribute() {
			return getRuleContext(Rule_AttributeContext.class,0);
		}
		public Rule_FunctionCallContext rule_FunctionCall() {
			return getRuleContext(Rule_FunctionCallContext.class,0);
		}
		public Rule_ContextValueContext rule_ContextValue() {
			return getRuleContext(Rule_ContextValueContext.class,0);
		}
		public Rule_OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Operand; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_OperandContext rule_Operand() throws RecognitionException {
		Rule_OperandContext _localctx = new Rule_OperandContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_rule_Operand);
		try {
			setState(385);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(381);
				rule_EntityTitle();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(382);
				rule_Attribute();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(383);
				rule_FunctionCall();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(384);
				rule_ContextValue();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_ContextValueContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public TerminalNode STRING() { return getToken(CdqlParser.STRING, 0); }
		public JsonContext json() {
			return getRuleContext(JsonContext.class,0);
		}
		public Rule_ContextValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_ContextValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_ContextValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ContextValueContext rule_ContextValue() throws RecognitionException {
		Rule_ContextValueContext _localctx = new Rule_ContextValueContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_rule_ContextValue);
		try {
			setState(390);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(387);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(388);
				match(STRING);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(389);
				json();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_WhenContext extends ParserRuleContext {
		public TerminalNode WHEN() { return getToken(CdqlParser.WHEN, 0); }
		public Rule_StartContext rule_Start() {
			return getRuleContext(Rule_StartContext.class,0);
		}
		public Rule_WhenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_When; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_When(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_WhenContext rule_When() throws RecognitionException {
		Rule_WhenContext _localctx = new Rule_WhenContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_rule_When);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			match(WHEN);
			setState(393);
			rule_Start();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_repeatContext extends ParserRuleContext {
		public TerminalNode EVERY() { return getToken(CdqlParser.EVERY, 0); }
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public TerminalNode UNIT_OF_TIME() { return getToken(CdqlParser.UNIT_OF_TIME, 0); }
		public TerminalNode UNTIL() { return getToken(CdqlParser.UNTIL, 0); }
		public Rule_OccurrenceContext rule_Occurrence() {
			return getRuleContext(Rule_OccurrenceContext.class,0);
		}
		public Rule_repeatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_repeat; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_repeat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_repeatContext rule_repeat() throws RecognitionException {
		Rule_repeatContext _localctx = new Rule_repeatContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_rule_repeat);
		int _la;
		try {
			setState(405);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EVERY:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(395);
				match(EVERY);
				setState(396);
				match(NUMBER);
				setState(397);
				match(UNIT_OF_TIME);
				}
				setState(401);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==UNTIL) {
					{
					setState(399);
					match(UNTIL);
					setState(400);
					rule_Occurrence();
					}
				}

				}
				break;
			case UNTIL:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(403);
				match(UNTIL);
				setState(404);
				rule_Occurrence();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_StartContext extends ParserRuleContext {
		public Rule_ConditionContext rule_Condition() {
			return getRuleContext(Rule_ConditionContext.class,0);
		}
		public Rule_Date_Time_WhenContext rule_Date_Time_When() {
			return getRuleContext(Rule_Date_Time_WhenContext.class,0);
		}
		public Rule_StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Start; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Start(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_StartContext rule_Start() throws RecognitionException {
		Rule_StartContext _localctx = new Rule_StartContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_rule_Start);
		try {
			setState(409);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
			case T__7:
			case T__17:
			case T__18:
			case LPAREN:
			case NOT:
			case NULL:
			case STRING:
			case ID:
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(407);
				rule_Condition(0);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(408);
				rule_Date_Time_When();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Date_Time_WhenContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public Rule_Date_TimeContext rule_Date_Time() {
			return getRuleContext(Rule_Date_TimeContext.class,0);
		}
		public Rule_Date_Time_WhenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Date_Time_When; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Date_Time_When(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Date_Time_WhenContext rule_Date_Time_When() throws RecognitionException {
		Rule_Date_Time_WhenContext _localctx = new Rule_Date_Time_WhenContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_rule_Date_Time_When);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(411);
			match(T__1);
			setState(412);
			match(COLON);
			setState(413);
			rule_Date_Time();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_OccurrenceContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public TerminalNode UNIT_OF_TIME() { return getToken(CdqlParser.UNIT_OF_TIME, 0); }
		public TerminalNode OCCURRENCES() { return getToken(CdqlParser.OCCURRENCES, 0); }
		public Rule_Date_TimeContext rule_Date_Time() {
			return getRuleContext(Rule_Date_TimeContext.class,0);
		}
		public TerminalNode LIFETIME() { return getToken(CdqlParser.LIFETIME, 0); }
		public Rule_OccurrenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Occurrence; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Occurrence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_OccurrenceContext rule_Occurrence() throws RecognitionException {
		Rule_OccurrenceContext _localctx = new Rule_OccurrenceContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_rule_Occurrence);
		try {
			setState(421);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(415);
				match(NUMBER);
				setState(416);
				match(UNIT_OF_TIME);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(417);
				match(NUMBER);
				setState(418);
				match(OCCURRENCES);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(419);
				rule_Date_Time();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(420);
				match(LIFETIME);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Date_TimeContext extends ParserRuleContext {
		public Rule_DateContext rule_Date() {
			return getRuleContext(Rule_DateContext.class,0);
		}
		public Rule_TimeContext rule_Time() {
			return getRuleContext(Rule_TimeContext.class,0);
		}
		public Rule_Date_TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Date_Time; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Date_Time(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Date_TimeContext rule_Date_Time() throws RecognitionException {
		Rule_Date_TimeContext _localctx = new Rule_Date_TimeContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_rule_Date_Time);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			rule_Date();
			setState(425);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NUMBER) {
				{
				setState(424);
				rule_Time();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_DateContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(CdqlParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(CdqlParser.NUMBER, i);
		}
		public List<TerminalNode> FSLASH() { return getTokens(CdqlParser.FSLASH); }
		public TerminalNode FSLASH(int i) {
			return getToken(CdqlParser.FSLASH, i);
		}
		public Rule_DateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Date; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Date(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_DateContext rule_Date() throws RecognitionException {
		Rule_DateContext _localctx = new Rule_DateContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_rule_Date);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
			match(NUMBER);
			setState(428);
			match(FSLASH);
			setState(429);
			match(NUMBER);
			setState(430);
			match(FSLASH);
			setState(431);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_TimeContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(CdqlParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(CdqlParser.NUMBER, i);
		}
		public List<TerminalNode> COLON() { return getTokens(CdqlParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(CdqlParser.COLON, i);
		}
		public TerminalNode TIME_ZONE() { return getToken(CdqlParser.TIME_ZONE, 0); }
		public Rule_TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Time; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Time(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_TimeContext rule_Time() throws RecognitionException {
		Rule_TimeContext _localctx = new Rule_TimeContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_rule_Time);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			match(NUMBER);
			setState(434);
			match(COLON);
			setState(435);
			match(NUMBER);
			setState(438);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(436);
				match(COLON);
				setState(437);
				match(NUMBER);
				}
			}

			setState(441);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TIME_ZONE) {
				{
				setState(440);
				match(TIME_ZONE);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_ConditionContext extends ParserRuleContext {
		public Rule_ConstraintContext rule_Constraint() {
			return getRuleContext(Rule_ConstraintContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public List<Rule_ConditionContext> rule_Condition() {
			return getRuleContexts(Rule_ConditionContext.class);
		}
		public Rule_ConditionContext rule_Condition(int i) {
			return getRuleContext(Rule_ConditionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public TerminalNode NOT() { return getToken(CdqlParser.NOT, 0); }
		public Rule_expr_opContext rule_expr_op() {
			return getRuleContext(Rule_expr_opContext.class,0);
		}
		public Rule_ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Condition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Condition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ConditionContext rule_Condition() throws RecognitionException {
		return rule_Condition(0);
	}

	private Rule_ConditionContext rule_Condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Rule_ConditionContext _localctx = new Rule_ConditionContext(_ctx, _parentState);
		Rule_ConditionContext _prevctx = _localctx;
		int _startState = 82;
		enterRecursionRule(_localctx, 82, RULE_rule_Condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
			case T__7:
			case T__17:
			case T__18:
			case NULL:
			case STRING:
			case ID:
			case NUMBER:
				{
				setState(444);
				rule_Constraint();
				}
				break;
			case LPAREN:
				{
				setState(445);
				match(LPAREN);
				setState(446);
				rule_Condition(0);
				setState(447);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				setState(449);
				match(NOT);
				setState(450);
				rule_Condition(1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(459);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Rule_ConditionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_rule_Condition);
					setState(453);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(454);
					rule_expr_op();
					setState(455);
					rule_Condition(4);
					}
					} 
				}
				setState(461);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Rule_expr_opContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(CdqlParser.AND, 0); }
		public TerminalNode XOR() { return getToken(CdqlParser.XOR, 0); }
		public TerminalNode OR() { return getToken(CdqlParser.OR, 0); }
		public TerminalNode NOT() { return getToken(CdqlParser.NOT, 0); }
		public Rule_expr_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_expr_op; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_expr_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_expr_opContext rule_expr_op() throws RecognitionException {
		Rule_expr_opContext _localctx = new Rule_expr_opContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_rule_expr_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NOT) | (1L << AND) | (1L << OR) | (1L << XOR))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_ConstraintContext extends ParserRuleContext {
		public Rule_left_elementContext rule_left_element() {
			return getRuleContext(Rule_left_elementContext.class,0);
		}
		public Rule_relational_op_funcContext rule_relational_op_func() {
			return getRuleContext(Rule_relational_op_funcContext.class,0);
		}
		public Rule_right_elementContext rule_right_element() {
			return getRuleContext(Rule_right_elementContext.class,0);
		}
		public Rule_target_elementContext rule_target_element() {
			return getRuleContext(Rule_target_elementContext.class,0);
		}
		public Rule_between_opContext rule_between_op() {
			return getRuleContext(Rule_between_opContext.class,0);
		}
		public TerminalNode AND() { return getToken(CdqlParser.AND, 0); }
		public Rule_is_or_is_notContext rule_is_or_is_not() {
			return getRuleContext(Rule_is_or_is_notContext.class,0);
		}
		public TerminalNode NULL() { return getToken(CdqlParser.NULL, 0); }
		public Rule_ConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Constraint; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Constraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ConstraintContext rule_Constraint() throws RecognitionException {
		Rule_ConstraintContext _localctx = new Rule_ConstraintContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_rule_Constraint);
		try {
			setState(478);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(464);
				rule_left_element();
				setState(465);
				rule_relational_op_func();
				setState(466);
				rule_right_element();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(468);
				rule_target_element();
				setState(469);
				rule_between_op();
				setState(470);
				rule_left_element();
				setState(471);
				match(AND);
				setState(472);
				rule_right_element();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(474);
				rule_target_element();
				setState(475);
				rule_is_or_is_not();
				setState(476);
				match(NULL);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_left_elementContext extends ParserRuleContext {
		public Rule_OperandContext rule_Operand() {
			return getRuleContext(Rule_OperandContext.class,0);
		}
		public Rule_left_elementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_left_element; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_left_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_left_elementContext rule_left_element() throws RecognitionException {
		Rule_left_elementContext _localctx = new Rule_left_elementContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_rule_left_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(480);
			rule_Operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_right_elementContext extends ParserRuleContext {
		public Rule_OperandContext rule_Operand() {
			return getRuleContext(Rule_OperandContext.class,0);
		}
		public Rule_right_elementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_right_element; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_right_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_right_elementContext rule_right_element() throws RecognitionException {
		Rule_right_elementContext _localctx = new Rule_right_elementContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_rule_right_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(482);
			rule_Operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_target_elementContext extends ParserRuleContext {
		public Rule_OperandContext rule_Operand() {
			return getRuleContext(Rule_OperandContext.class,0);
		}
		public Rule_target_elementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_target_element; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_target_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_target_elementContext rule_target_element() throws RecognitionException {
		Rule_target_elementContext _localctx = new Rule_target_elementContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_rule_target_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			rule_Operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_relational_op_funcContext extends ParserRuleContext {
		public Rule_relational_opContext rule_relational_op() {
			return getRuleContext(Rule_relational_opContext.class,0);
		}
		public TerminalNode OP() { return getToken(CdqlParser.OP, 0); }
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public TerminalNode COMMA() { return getToken(CdqlParser.COMMA, 0); }
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public Rule_relational_op_funcContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_relational_op_func; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_relational_op_func(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_relational_op_funcContext rule_relational_op_func() throws RecognitionException {
		Rule_relational_op_funcContext _localctx = new Rule_relational_op_funcContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_rule_relational_op_func);
		try {
			setState(494);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONTAINS_ANY:
			case CONTAINS_ALL:
			case EQ:
			case LTH:
			case GTH:
			case LET:
			case GET:
			case NOT_EQ:
				enterOuterAlt(_localctx, 1);
				{
				setState(486);
				rule_relational_op();
				}
				break;
			case OP:
				enterOuterAlt(_localctx, 2);
				{
				setState(487);
				match(OP);
				setState(488);
				match(LPAREN);
				setState(489);
				rule_relational_op();
				setState(490);
				match(COMMA);
				setState(491);
				match(NUMBER);
				setState(492);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_relational_opContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(CdqlParser.EQ, 0); }
		public TerminalNode LTH() { return getToken(CdqlParser.LTH, 0); }
		public TerminalNode NOT_EQ() { return getToken(CdqlParser.NOT_EQ, 0); }
		public TerminalNode GTH() { return getToken(CdqlParser.GTH, 0); }
		public TerminalNode LET() { return getToken(CdqlParser.LET, 0); }
		public TerminalNode GET() { return getToken(CdqlParser.GET, 0); }
		public TerminalNode CONTAINS_ANY() { return getToken(CdqlParser.CONTAINS_ANY, 0); }
		public TerminalNode CONTAINS_ALL() { return getToken(CdqlParser.CONTAINS_ALL, 0); }
		public Rule_relational_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_relational_op; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_relational_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_relational_opContext rule_relational_op() throws RecognitionException {
		Rule_relational_opContext _localctx = new Rule_relational_opContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_rule_relational_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(496);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CONTAINS_ANY) | (1L << CONTAINS_ALL) | (1L << EQ) | (1L << LTH) | (1L << GTH) | (1L << LET) | (1L << GET) | (1L << NOT_EQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_between_opContext extends ParserRuleContext {
		public TerminalNode BETWEEN() { return getToken(CdqlParser.BETWEEN, 0); }
		public TerminalNode OP() { return getToken(CdqlParser.OP, 0); }
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public TerminalNode COMMA() { return getToken(CdqlParser.COMMA, 0); }
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public Rule_between_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_between_op; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_between_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_between_opContext rule_between_op() throws RecognitionException {
		Rule_between_opContext _localctx = new Rule_between_opContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_rule_between_op);
		try {
			setState(505);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(498);
				match(BETWEEN);
				}
				break;
			case OP:
				enterOuterAlt(_localctx, 2);
				{
				setState(499);
				match(OP);
				setState(500);
				match(LPAREN);
				setState(501);
				match(BETWEEN);
				setState(502);
				match(COMMA);
				setState(503);
				match(NUMBER);
				setState(504);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_is_or_is_notContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(CdqlParser.IS, 0); }
		public TerminalNode NOT() { return getToken(CdqlParser.NOT, 0); }
		public Rule_is_or_is_notContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_is_or_is_not; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_is_or_is_not(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_is_or_is_notContext rule_is_or_is_not() throws RecognitionException {
		Rule_is_or_is_notContext _localctx = new Rule_is_or_is_notContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_rule_is_or_is_not);
		try {
			setState(510);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(507);
				match(IS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(508);
				match(IS);
				setState(509);
				match(NOT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ruel_PushContext extends ParserRuleContext {
		public TerminalNode PUSH() { return getToken(CdqlParser.PUSH, 0); }
		public Rule_SelectContext rule_Select() {
			return getRuleContext(Rule_SelectContext.class,0);
		}
		public Ruel_PushContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruel_Push; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRuel_Push(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ruel_PushContext ruel_Push() throws RecognitionException {
		Ruel_PushContext _localctx = new Ruel_PushContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_ruel_Push);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			match(PUSH);
			setState(513);
			rule_Select();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_callbackContext extends ParserRuleContext {
		public Rule_http_calbackContext rule_http_calback() {
			return getRuleContext(Rule_http_calbackContext.class,0);
		}
		public Rule_fcm_calbackContext rule_fcm_calback() {
			return getRuleContext(Rule_fcm_calbackContext.class,0);
		}
		public Rule_callbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_callback; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_callback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_callbackContext rule_callback() throws RecognitionException {
		Rule_callbackContext _localctx = new Rule_callbackContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_rule_callback);
		try {
			setState(517);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(515);
				rule_http_calback();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(516);
				rule_fcm_calback();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_http_calbackContext extends ParserRuleContext {
		public TerminalNode METHOD() { return getToken(CdqlParser.METHOD, 0); }
		public List<TerminalNode> EQ() { return getTokens(CdqlParser.EQ); }
		public TerminalNode EQ(int i) {
			return getToken(CdqlParser.EQ, i);
		}
		public TerminalNode HTTPPOST() { return getToken(CdqlParser.HTTPPOST, 0); }
		public TerminalNode URL() { return getToken(CdqlParser.URL, 0); }
		public Rule_callback_urlContext rule_callback_url() {
			return getRuleContext(Rule_callback_urlContext.class,0);
		}
		public Rule_http_calbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_http_calback; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_http_calback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_http_calbackContext rule_http_calback() throws RecognitionException {
		Rule_http_calbackContext _localctx = new Rule_http_calbackContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_rule_http_calback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			match(METHOD);
			setState(520);
			match(EQ);
			setState(521);
			match(HTTPPOST);
			setState(522);
			match(URL);
			setState(523);
			match(EQ);
			setState(524);
			rule_callback_url();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_fcm_calbackContext extends ParserRuleContext {
		public TerminalNode METHOD() { return getToken(CdqlParser.METHOD, 0); }
		public TerminalNode EQ() { return getToken(CdqlParser.EQ, 0); }
		public TerminalNode FCM() { return getToken(CdqlParser.FCM, 0); }
		public Rule_fcm_topicContext rule_fcm_topic() {
			return getRuleContext(Rule_fcm_topicContext.class,0);
		}
		public Rule_fcm_tokenContext rule_fcm_token() {
			return getRuleContext(Rule_fcm_tokenContext.class,0);
		}
		public Rule_fcm_calbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_fcm_calback; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_fcm_calback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_fcm_calbackContext rule_fcm_calback() throws RecognitionException {
		Rule_fcm_calbackContext _localctx = new Rule_fcm_calbackContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_rule_fcm_calback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526);
			match(METHOD);
			setState(527);
			match(EQ);
			setState(528);
			match(FCM);
			setState(531);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TOPIC:
				{
				setState(529);
				rule_fcm_topic();
				}
				break;
			case TOKEN:
				{
				setState(530);
				rule_fcm_token();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_fcm_topicContext extends ParserRuleContext {
		public TerminalNode TOPIC() { return getToken(CdqlParser.TOPIC, 0); }
		public TerminalNode EQ() { return getToken(CdqlParser.EQ, 0); }
		public TerminalNode STRING() { return getToken(CdqlParser.STRING, 0); }
		public Rule_fcm_topicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_fcm_topic; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_fcm_topic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_fcm_topicContext rule_fcm_topic() throws RecognitionException {
		Rule_fcm_topicContext _localctx = new Rule_fcm_topicContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_rule_fcm_topic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(533);
			match(TOPIC);
			setState(534);
			match(EQ);
			setState(535);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_fcm_tokenContext extends ParserRuleContext {
		public TerminalNode TOKEN() { return getToken(CdqlParser.TOKEN, 0); }
		public TerminalNode EQ() { return getToken(CdqlParser.EQ, 0); }
		public TerminalNode STRING() { return getToken(CdqlParser.STRING, 0); }
		public Rule_fcm_tokenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_fcm_token; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_fcm_token(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_fcm_tokenContext rule_fcm_token() throws RecognitionException {
		Rule_fcm_tokenContext _localctx = new Rule_fcm_tokenContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_rule_fcm_token);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(537);
			match(TOKEN);
			setState(538);
			match(EQ);
			setState(539);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_callback_urlContext extends ParserRuleContext {
		public Rule_urlContext rule_url() {
			return getRuleContext(Rule_urlContext.class,0);
		}
		public Rule_callback_urlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_callback_url; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_callback_url(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_callback_urlContext rule_callback_url() throws RecognitionException {
		Rule_callback_urlContext _localctx = new Rule_callback_urlContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_rule_callback_url);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			rule_url();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_DefineContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(CdqlParser.DEFINE, 0); }
		public Rule_Define_Context_EntityContext rule_Define_Context_Entity() {
			return getRuleContext(Rule_Define_Context_EntityContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(CdqlParser.COMMA, 0); }
		public Rule_Define_Context_FunctionContext rule_Define_Context_Function() {
			return getRuleContext(Rule_Define_Context_FunctionContext.class,0);
		}
		public Rule_DefineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Define; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Define(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_DefineContext rule_Define() throws RecognitionException {
		Rule_DefineContext _localctx = new Rule_DefineContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_rule_Define);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(543);
			match(DEFINE);
			setState(544);
			rule_Define_Context_Entity();
			setState(547);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(545);
				match(COMMA);
				setState(546);
				rule_Define_Context_Function();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Define_Context_EntityContext extends ParserRuleContext {
		public List<Rule_context_entityContext> rule_context_entity() {
			return getRuleContexts(Rule_context_entityContext.class);
		}
		public Rule_context_entityContext rule_context_entity(int i) {
			return getRuleContext(Rule_context_entityContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_Define_Context_EntityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Define_Context_Entity; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Define_Context_Entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Define_Context_EntityContext rule_Define_Context_Entity() throws RecognitionException {
		Rule_Define_Context_EntityContext _localctx = new Rule_Define_Context_EntityContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_rule_Define_Context_Entity);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(549);
			rule_context_entity();
			setState(554);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(550);
					match(COMMA);
					setState(551);
					rule_context_entity();
					}
					} 
				}
				setState(556);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_context_entityContext extends ParserRuleContext {
		public TerminalNode ENTITY() { return getToken(CdqlParser.ENTITY, 0); }
		public Rule_entity_idContext rule_entity_id() {
			return getRuleContext(Rule_entity_idContext.class,0);
		}
		public TerminalNode IS_FROM() { return getToken(CdqlParser.IS_FROM, 0); }
		public Rule_entity_typeContext rule_entity_type() {
			return getRuleContext(Rule_entity_typeContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(CdqlParser.WHERE, 0); }
		public Rule_ConditionContext rule_Condition() {
			return getRuleContext(Rule_ConditionContext.class,0);
		}
		public Rule_context_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_context_entity; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_context_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_context_entityContext rule_context_entity() throws RecognitionException {
		Rule_context_entityContext _localctx = new Rule_context_entityContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_rule_context_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			match(ENTITY);
			setState(558);
			rule_entity_id();
			setState(559);
			match(IS_FROM);
			setState(560);
			rule_entity_type();
			setState(563);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(561);
				match(WHERE);
				setState(562);
				rule_Condition(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_entity_typeContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(CdqlParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(CdqlParser.ID, i);
		}
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public TerminalNode DOT() { return getToken(CdqlParser.DOT, 0); }
		public Rule_entity_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_entity_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_entity_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_entity_typeContext rule_entity_type() throws RecognitionException {
		Rule_entity_typeContext _localctx = new Rule_entity_typeContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_rule_entity_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(567);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(565);
				match(ID);
				setState(566);
				match(COLON);
				}
				break;
			}
			setState(569);
			match(ID);
			setState(572);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(570);
				match(DOT);
				setState(571);
				match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_Define_Context_FunctionContext extends ParserRuleContext {
		public List<Rule_context_functionContext> rule_context_function() {
			return getRuleContexts(Rule_context_functionContext.class);
		}
		public Rule_context_functionContext rule_context_function(int i) {
			return getRuleContext(Rule_context_functionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_Define_Context_FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Define_Context_Function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_Define_Context_Function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Define_Context_FunctionContext rule_Define_Context_Function() throws RecognitionException {
		Rule_Define_Context_FunctionContext _localctx = new Rule_Define_Context_FunctionContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_rule_Define_Context_Function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			rule_context_function();
			setState(579);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(575);
				match(COMMA);
				setState(576);
				rule_context_function();
				}
				}
				setState(581);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_context_functionContext extends ParserRuleContext {
		public Rule_aFunctionContext rule_aFunction() {
			return getRuleContext(Rule_aFunctionContext.class,0);
		}
		public Rule_sFunctionContext rule_sFunction() {
			return getRuleContext(Rule_sFunctionContext.class,0);
		}
		public Rule_context_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_context_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_context_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_context_functionContext rule_context_function() throws RecognitionException {
		Rule_context_functionContext _localctx = new Rule_context_functionContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_rule_context_function);
		try {
			setState(584);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				setState(582);
				rule_aFunction();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(583);
				rule_sFunction();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_aFunctionContext extends ParserRuleContext {
		public Rule_function_idContext rule_function_id() {
			return getRuleContext(Rule_function_idContext.class,0);
		}
		public Rule_urlContext rule_url() {
			return getRuleContext(Rule_urlContext.class,0);
		}
		public Rule_aFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_aFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_aFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_aFunctionContext rule_aFunction() throws RecognitionException {
		Rule_aFunctionContext _localctx = new Rule_aFunctionContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_rule_aFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			match(T__2);
			setState(587);
			rule_function_id();
			setState(588);
			rule_url();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_sFunctionContext extends ParserRuleContext {
		public Rule_function_idContext rule_function_id() {
			return getRuleContext(Rule_function_idContext.class,0);
		}
		public Rule_is_onContext rule_is_on() {
			return getRuleContext(Rule_is_onContext.class,0);
		}
		public Cst_situation_def_ruleContext cst_situation_def_rule() {
			return getRuleContext(Cst_situation_def_ruleContext.class,0);
		}
		public Rule_sFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_sFunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_sFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_sFunctionContext rule_sFunction() throws RecognitionException {
		Rule_sFunctionContext _localctx = new Rule_sFunctionContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_rule_sFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(590);
			match(T__3);
			setState(591);
			rule_function_id();
			setState(592);
			rule_is_on();
			{
			setState(593);
			cst_situation_def_rule();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_is_onContext extends ParserRuleContext {
		public List<Rule_is_on_entityContext> rule_is_on_entity() {
			return getRuleContexts(Rule_is_on_entityContext.class);
		}
		public Rule_is_on_entityContext rule_is_on_entity(int i) {
			return getRuleContext(Rule_is_on_entityContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_is_onContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_is_on; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_is_on(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_is_onContext rule_is_on() throws RecognitionException {
		Rule_is_onContext _localctx = new Rule_is_onContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_rule_is_on);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(595);
			match(T__4);
			setState(596);
			rule_is_on_entity();
			setState(601);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(597);
				match(COMMA);
				setState(598);
				rule_is_on_entity();
				}
				}
				setState(603);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_is_on_entityContext extends ParserRuleContext {
		public Rule_entity_typeContext rule_entity_type() {
			return getRuleContext(Rule_entity_typeContext.class,0);
		}
		public TerminalNode AS() { return getToken(CdqlParser.AS, 0); }
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Rule_is_on_entityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_is_on_entity; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_is_on_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_is_on_entityContext rule_is_on_entity() throws RecognitionException {
		Rule_is_on_entityContext _localctx = new Rule_is_on_entityContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_rule_is_on_entity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(604);
			rule_entity_type();
			setState(605);
			match(AS);
			setState(606);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cst_situation_def_ruleContext extends ParserRuleContext {
		public List<Rule_single_situatuinContext> rule_single_situatuin() {
			return getRuleContexts(Rule_single_situatuinContext.class);
		}
		public Rule_single_situatuinContext rule_single_situatuin(int i) {
			return getRuleContext(Rule_single_situatuinContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Cst_situation_def_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cst_situation_def_rule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitCst_situation_def_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Cst_situation_def_ruleContext cst_situation_def_rule() throws RecognitionException {
		Cst_situation_def_ruleContext _localctx = new Cst_situation_def_ruleContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_cst_situation_def_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(608);
			match(T__5);
			setState(609);
			rule_single_situatuin();
			setState(614);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(610);
				match(COMMA);
				setState(611);
				rule_single_situatuin();
				}
				}
				setState(616);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(617);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_single_situatuinContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(CdqlParser.STRING, 0); }
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public List<Rule_situation_pairContext> rule_situation_pair() {
			return getRuleContexts(Rule_situation_pairContext.class);
		}
		public Rule_situation_pairContext rule_situation_pair(int i) {
			return getRuleContext(Rule_situation_pairContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_single_situatuinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_single_situatuin; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_single_situatuin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_single_situatuinContext rule_single_situatuin() throws RecognitionException {
		Rule_single_situatuinContext _localctx = new Rule_single_situatuinContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_rule_single_situatuin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(619);
			match(STRING);
			setState(620);
			match(COLON);
			setState(621);
			match(T__5);
			setState(622);
			rule_situation_pair();
			setState(627);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(623);
				match(COMMA);
				setState(624);
				rule_situation_pair();
				}
				}
				setState(629);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(630);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_situation_pairContext extends ParserRuleContext {
		public Rule_situation_attributesContext rule_situation_attributes() {
			return getRuleContext(Rule_situation_attributesContext.class,0);
		}
		public Situation_pair_valuesContext situation_pair_values() {
			return getRuleContext(Situation_pair_valuesContext.class,0);
		}
		public Rule_situation_pairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_situation_pair; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_situation_pair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_pairContext rule_situation_pair() throws RecognitionException {
		Rule_situation_pairContext _localctx = new Rule_situation_pairContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_rule_situation_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			rule_situation_attributes();
			setState(633);
			match(COLON);
			setState(634);
			match(T__5);
			setState(635);
			situation_pair_values();
			setState(636);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_situation_attributesContext extends ParserRuleContext {
		public List<Rule_situation_attribute_nameContext> rule_situation_attribute_name() {
			return getRuleContexts(Rule_situation_attribute_nameContext.class);
		}
		public Rule_situation_attribute_nameContext rule_situation_attribute_name(int i) {
			return getRuleContext(Rule_situation_attribute_nameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Rule_situation_attributesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_situation_attributes; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_situation_attributes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_attributesContext rule_situation_attributes() throws RecognitionException {
		Rule_situation_attributesContext _localctx = new Rule_situation_attributesContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_rule_situation_attributes);
		int _la;
		try {
			setState(649);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(638);
				rule_situation_attribute_name();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(639);
				match(T__7);
				setState(640);
				rule_situation_attribute_name();
				setState(643); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(641);
					match(COMMA);
					setState(642);
					rule_situation_attribute_name();
					}
					}
					setState(645); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==COMMA );
				setState(647);
				match(T__8);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_situation_attribute_nameContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(CdqlParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(CdqlParser.ID, i);
		}
		public List<TerminalNode> DOT() { return getTokens(CdqlParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CdqlParser.DOT, i);
		}
		public Rule_situation_attribute_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_situation_attribute_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_situation_attribute_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_attribute_nameContext rule_situation_attribute_name() throws RecognitionException {
		Rule_situation_attribute_nameContext _localctx = new Rule_situation_attribute_nameContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_rule_situation_attribute_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
			match(ID);
			setState(656);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(652);
				match(DOT);
				setState(653);
				match(ID);
				}
				}
				setState(658);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Situation_pair_valuesContext extends ParserRuleContext {
		public Situation_range_valuesContext situation_range_values() {
			return getRuleContext(Situation_range_valuesContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(CdqlParser.COMMA, 0); }
		public Situation_weightContext situation_weight() {
			return getRuleContext(Situation_weightContext.class,0);
		}
		public Situation_pair_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_situation_pair_values; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitSituation_pair_values(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_pair_valuesContext situation_pair_values() throws RecognitionException {
		Situation_pair_valuesContext _localctx = new Situation_pair_valuesContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_situation_pair_values);
		try {
			setState(667);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(659);
				situation_range_values();
				setState(660);
				match(COMMA);
				setState(661);
				situation_weight();
				}
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(663);
				situation_weight();
				setState(664);
				match(COMMA);
				setState(665);
				situation_range_values();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Situation_weightContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public Situation_weightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_situation_weight; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitSituation_weight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_weightContext situation_weight() throws RecognitionException {
		Situation_weightContext _localctx = new Situation_weightContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_situation_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(669);
			match(T__9);
			setState(670);
			match(COLON);
			setState(671);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Situation_range_valuesContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public List<Situation_pair_values_itemContext> situation_pair_values_item() {
			return getRuleContexts(Situation_pair_values_itemContext.class);
		}
		public Situation_pair_values_itemContext situation_pair_values_item(int i) {
			return getRuleContext(Situation_pair_values_itemContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public Situation_range_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_situation_range_values; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitSituation_range_values(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_range_valuesContext situation_range_values() throws RecognitionException {
		Situation_range_valuesContext _localctx = new Situation_range_valuesContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_situation_range_values);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(673);
			match(T__10);
			setState(674);
			match(COLON);
			setState(675);
			match(T__7);
			setState(676);
			situation_pair_values_item();
			setState(681);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(677);
				match(COMMA);
				setState(678);
				situation_pair_values_item();
				}
				}
				setState(683);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(684);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Situation_pair_values_itemContext extends ParserRuleContext {
		public Rule_situation_beliefContext rule_situation_belief() {
			return getRuleContext(Rule_situation_beliefContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(CdqlParser.COMMA, 0); }
		public Rule_situation_valueContext rule_situation_value() {
			return getRuleContext(Rule_situation_valueContext.class,0);
		}
		public Situation_pair_values_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_situation_pair_values_item; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitSituation_pair_values_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_pair_values_itemContext situation_pair_values_item() throws RecognitionException {
		Situation_pair_values_itemContext _localctx = new Situation_pair_values_itemContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_situation_pair_values_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			match(T__5);
			setState(695);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				{
				{
				setState(687);
				rule_situation_belief();
				setState(688);
				match(COMMA);
				setState(689);
				rule_situation_value();
				}
				}
				break;
			case T__12:
				{
				{
				setState(691);
				rule_situation_value();
				setState(692);
				match(COMMA);
				setState(693);
				rule_situation_belief();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(697);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_situation_beliefContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public Rule_situation_beliefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_situation_belief; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_situation_belief(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_beliefContext rule_situation_belief() throws RecognitionException {
		Rule_situation_beliefContext _localctx = new Rule_situation_beliefContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_rule_situation_belief);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			match(T__11);
			setState(700);
			match(COLON);
			setState(701);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_situation_valueContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public Rule_region_valueContext rule_region_value() {
			return getRuleContext(Rule_region_valueContext.class,0);
		}
		public Rule_discrete_valueContext rule_discrete_value() {
			return getRuleContext(Rule_discrete_valueContext.class,0);
		}
		public Discrete_valueContext discrete_value() {
			return getRuleContext(Discrete_valueContext.class,0);
		}
		public Rule_situation_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_situation_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_situation_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_valueContext rule_situation_value() throws RecognitionException {
		Rule_situation_valueContext _localctx = new Rule_situation_valueContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_rule_situation_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(703);
			match(T__12);
			setState(704);
			match(COLON);
			setState(708);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				{
				setState(705);
				rule_region_value();
				}
				break;
			case 2:
				{
				setState(706);
				rule_discrete_value();
				}
				break;
			case 3:
				{
				setState(707);
				discrete_value();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_discrete_valueContext extends ParserRuleContext {
		public List<Discrete_valueContext> discrete_value() {
			return getRuleContexts(Discrete_valueContext.class);
		}
		public Discrete_valueContext discrete_value(int i) {
			return getRuleContext(Discrete_valueContext.class,i);
		}
		public List<TerminalNode> COLON() { return getTokens(CdqlParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(CdqlParser.COLON, i);
		}
		public Rule_discrete_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_discrete_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_discrete_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_discrete_valueContext rule_discrete_value() throws RecognitionException {
		Rule_discrete_valueContext _localctx = new Rule_discrete_valueContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_rule_discrete_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(710);
			match(T__7);
			setState(711);
			discrete_value();
			setState(716);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COLON) {
				{
				{
				setState(712);
				match(COLON);
				setState(713);
				discrete_value();
				}
				}
				setState(718);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(719);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Discrete_valueContext extends ParserRuleContext {
		public JsonContext json() {
			return getRuleContext(JsonContext.class,0);
		}
		public Discrete_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_discrete_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitDiscrete_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Discrete_valueContext discrete_value() throws RecognitionException {
		Discrete_valueContext _localctx = new Discrete_valueContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_discrete_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(721);
			json();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_region_valueContext extends ParserRuleContext {
		public Region_value_inclusiveContext region_value_inclusive() {
			return getRuleContext(Region_value_inclusiveContext.class,0);
		}
		public Region_value_left_inclusiveContext region_value_left_inclusive() {
			return getRuleContext(Region_value_left_inclusiveContext.class,0);
		}
		public Region_value_right_inclusiveContext region_value_right_inclusive() {
			return getRuleContext(Region_value_right_inclusiveContext.class,0);
		}
		public Region_value_exclusiveContext region_value_exclusive() {
			return getRuleContext(Region_value_exclusiveContext.class,0);
		}
		public Rule_region_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_region_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_region_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_region_valueContext rule_region_value() throws RecognitionException {
		Rule_region_valueContext _localctx = new Rule_region_valueContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_rule_region_value);
		try {
			setState(727);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(723);
				region_value_inclusive();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(724);
				region_value_left_inclusive();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(725);
				region_value_right_inclusive();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(726);
				region_value_exclusive();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Region_value_inclusiveContext extends ParserRuleContext {
		public Region_value_valueContext region_value_value() {
			return getRuleContext(Region_value_valueContext.class,0);
		}
		public Region_value_inclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_inclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRegion_value_inclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_inclusiveContext region_value_inclusive() throws RecognitionException {
		Region_value_inclusiveContext _localctx = new Region_value_inclusiveContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_region_value_inclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(729);
			match(T__7);
			setState(730);
			region_value_value();
			setState(731);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Region_value_left_inclusiveContext extends ParserRuleContext {
		public Region_value_valueContext region_value_value() {
			return getRuleContext(Region_value_valueContext.class,0);
		}
		public Region_value_left_inclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_left_inclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRegion_value_left_inclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_left_inclusiveContext region_value_left_inclusive() throws RecognitionException {
		Region_value_left_inclusiveContext _localctx = new Region_value_left_inclusiveContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_region_value_left_inclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(733);
			match(T__7);
			setState(734);
			region_value_value();
			setState(735);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Region_value_right_inclusiveContext extends ParserRuleContext {
		public Region_value_valueContext region_value_value() {
			return getRuleContext(Region_value_valueContext.class,0);
		}
		public Region_value_right_inclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_right_inclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRegion_value_right_inclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_right_inclusiveContext region_value_right_inclusive() throws RecognitionException {
		Region_value_right_inclusiveContext _localctx = new Region_value_right_inclusiveContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_region_value_right_inclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(737);
			match(LPAREN);
			setState(738);
			region_value_value();
			setState(739);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Region_value_exclusiveContext extends ParserRuleContext {
		public Region_value_valueContext region_value_value() {
			return getRuleContext(Region_value_valueContext.class,0);
		}
		public Region_value_exclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_exclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRegion_value_exclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_exclusiveContext region_value_exclusive() throws RecognitionException {
		Region_value_exclusiveContext _localctx = new Region_value_exclusiveContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_region_value_exclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(741);
			match(LPAREN);
			setState(742);
			region_value_value();
			setState(743);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Region_value_valueContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(CdqlParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(CdqlParser.NUMBER, i);
		}
		public Region_value_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRegion_value_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_valueContext region_value_value() throws RecognitionException {
		Region_value_valueContext _localctx = new Region_value_valueContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_region_value_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(745);
			match(NUMBER);
			setState(746);
			match(T__13);
			setState(747);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_entity_idContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Rule_entity_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_entity_id; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_entity_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_entity_idContext rule_entity_id() throws RecognitionException {
		Rule_entity_idContext _localctx = new Rule_entity_idContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_rule_entity_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(749);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_function_idContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Rule_function_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_function_id; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_function_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_function_idContext rule_function_id() throws RecognitionException {
		Rule_function_idContext _localctx = new Rule_function_idContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_rule_function_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(751);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rule_urlContext extends ParserRuleContext {
		public AuthorityContext authority() {
			return getRuleContext(AuthorityContext.class,0);
		}
		public HostContext host() {
			return getRuleContext(HostContext.class,0);
		}
		public PortContext port() {
			return getRuleContext(PortContext.class,0);
		}
		public PathContext path() {
			return getRuleContext(PathContext.class,0);
		}
		public SearchContext search() {
			return getRuleContext(SearchContext.class,0);
		}
		public Rule_urlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_url; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitRule_url(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_urlContext rule_url() throws RecognitionException {
		Rule_urlContext _localctx = new Rule_urlContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_rule_url);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(753);
			authority();
			setState(754);
			match(T__14);
			setState(755);
			host();
			setState(758);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(756);
				match(COLON);
				setState(757);
				port();
				}
			}

			setState(762);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FSLASH) {
				{
				setState(760);
				match(FSLASH);
				setState(761);
				path();
				}
			}

			setState(766);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(764);
				match(T__15);
				setState(765);
				search();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AuthorityContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public AuthorityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_authority; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitAuthority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AuthorityContext authority() throws RecognitionException {
		AuthorityContext _localctx = new AuthorityContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_authority);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HostContext extends ParserRuleContext {
		public HostnameContext hostname() {
			return getRuleContext(HostnameContext.class,0);
		}
		public HostnumberContext hostnumber() {
			return getRuleContext(HostnumberContext.class,0);
		}
		public HostContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_host; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitHost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostContext host() throws RecognitionException {
		HostContext _localctx = new HostContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_host);
		try {
			setState(772);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(770);
				hostname();
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(771);
				hostnumber();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HostnameContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(CdqlParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(CdqlParser.ID, i);
		}
		public HostnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hostname; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitHostname(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostnameContext hostname() throws RecognitionException {
		HostnameContext _localctx = new HostnameContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_hostname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(774);
			match(ID);
			setState(779);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(775);
				match(DOT);
				setState(776);
				match(ID);
				}
				}
				setState(781);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HostnumberContext extends ParserRuleContext {
		public List<TerminalNode> INT() { return getTokens(CdqlParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(CdqlParser.INT, i);
		}
		public HostnumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hostnumber; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitHostnumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostnumberContext hostnumber() throws RecognitionException {
		HostnumberContext _localctx = new HostnumberContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_hostnumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(782);
			match(INT);
			setState(783);
			match(DOT);
			setState(784);
			match(INT);
			setState(785);
			match(DOT);
			setState(786);
			match(INT);
			setState(787);
			match(DOT);
			setState(788);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SearchContext extends ParserRuleContext {
		public List<SearchparameterContext> searchparameter() {
			return getRuleContexts(SearchparameterContext.class);
		}
		public SearchparameterContext searchparameter(int i) {
			return getRuleContext(SearchparameterContext.class,i);
		}
		public SearchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_search; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitSearch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchContext search() throws RecognitionException {
		SearchContext _localctx = new SearchContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_search);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(790);
			searchparameter();
			setState(795);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(791);
				match(T__16);
				setState(792);
				searchparameter();
				}
				}
				setState(797);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SearchparameterContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(CdqlParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(CdqlParser.ID, i);
		}
		public TerminalNode INT() { return getToken(CdqlParser.INT, 0); }
		public TerminalNode HEX() { return getToken(CdqlParser.HEX, 0); }
		public SearchparameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searchparameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitSearchparameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchparameterContext searchparameter() throws RecognitionException {
		SearchparameterContext _localctx = new SearchparameterContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_searchparameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(798);
			match(ID);
			setState(801);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EQ) {
				{
				setState(799);
				match(EQ);
				setState(800);
				_la = _input.LA(1);
				if ( !(((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (ID - 80)) | (1L << (INT - 80)) | (1L << (HEX - 80)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(CdqlParser.INT, 0); }
		public PortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_port; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitPort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortContext port() throws RecognitionException {
		PortContext _localctx = new PortContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_port);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(803);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PathContext extends ParserRuleContext {
		public List<Normal_pathContext> normal_path() {
			return getRuleContexts(Normal_pathContext.class);
		}
		public Normal_pathContext normal_path(int i) {
			return getRuleContext(Normal_pathContext.class,i);
		}
		public List<Path_paramContext> path_param() {
			return getRuleContexts(Path_paramContext.class);
		}
		public Path_paramContext path_param(int i) {
			return getRuleContext(Path_paramContext.class,i);
		}
		public PathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_path; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitPath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PathContext path() throws RecognitionException {
		PathContext _localctx = new PathContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_path);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(807);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				{
				setState(805);
				normal_path();
				}
				break;
			case T__5:
				{
				setState(806);
				path_param();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(816);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FSLASH) {
				{
				{
				setState(809);
				match(FSLASH);
				setState(812);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ID:
					{
					setState(810);
					normal_path();
					}
					break;
				case T__5:
					{
					setState(811);
					path_param();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(818);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Normal_pathContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Normal_pathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normal_path; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitNormal_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Normal_pathContext normal_path() throws RecognitionException {
		Normal_pathContext _localctx = new Normal_pathContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_normal_path);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(819);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Path_paramContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(CdqlParser.ID, 0); }
		public Path_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_path_param; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitPath_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Path_paramContext path_param() throws RecognitionException {
		Path_paramContext _localctx = new Path_paramContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_path_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(821);
			match(T__5);
			setState(822);
			match(ID);
			setState(823);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JsonContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public JsonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_json; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitJson(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonContext json() throws RecognitionException {
		JsonContext _localctx = new JsonContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_json);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(825);
			value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjContext extends ParserRuleContext {
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public ObjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obj; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitObj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjContext obj() throws RecognitionException {
		ObjContext _localctx = new ObjContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_obj);
		int _la;
		try {
			setState(840);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(827);
				match(T__5);
				setState(828);
				pair();
				setState(833);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(829);
					match(COMMA);
					setState(830);
					pair();
					}
					}
					setState(835);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(836);
				match(T__6);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(838);
				match(T__5);
				setState(839);
				match(T__6);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PairContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(CdqlParser.STRING, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(842);
			match(STRING);
			setState(843);
			match(COLON);
			setState(844);
			value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayContext extends ParserRuleContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_array);
		try {
			int _alt;
			setState(859);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(846);
				match(T__7);
				setState(847);
				value();
				setState(852);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						{
						setState(848);
						match(COMMA);
						setState(849);
						value();
						}
						} 
					}
					setState(854);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
				}
				setState(855);
				match(T__8);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(857);
				match(T__7);
				setState(858);
				match(T__8);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(CdqlParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(CdqlParser.NUMBER, 0); }
		public ObjContext obj() {
			return getRuleContext(ObjContext.class,0);
		}
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ICdqlVisitor ) return ((ICdqlVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_value);
		try {
			setState(868);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(861);
				match(STRING);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(862);
				match(NUMBER);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(863);
				obj();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(864);
				array();
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 5);
				{
				setState(865);
				match(T__17);
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 6);
				{
				setState(866);
				match(T__18);
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 7);
				{
				setState(867);
				match(NULL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 41:
			return rule_Condition_sempred((Rule_ConditionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean rule_Condition_sempred(Rule_ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3W\u0369\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\3\2\5"+
		"\2\u00d6\n\2\3\2\3\2\5\2\u00da\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\u00e3"+
		"\n\3\3\4\3\4\3\5\3\5\3\5\3\5\5\5\u00eb\n\5\5\5\u00ed\n\5\3\5\3\5\5\5\u00f1"+
		"\n\5\3\5\5\5\u00f4\n\5\3\6\3\6\3\6\5\6\u00f9\n\6\3\6\5\6\u00fc\n\6\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\7\22\u012a\n\22"+
		"\f\22\16\22\u012d\13\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3"+
		"\25\3\25\5\25\u013a\n\25\3\25\3\25\3\25\5\25\u013f\n\25\7\25\u0141\n\25"+
		"\f\25\16\25\u0144\13\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\5\26\u014d"+
		"\n\26\3\27\3\27\3\30\3\30\3\30\7\30\u0154\n\30\f\30\16\30\u0157\13\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\33\3\33\7\33\u0162\n\33\f\33\16"+
		"\33\u0165\13\33\3\33\3\33\3\33\3\34\3\34\7\34\u016c\n\34\f\34\16\34\u016f"+
		"\13\34\3\35\3\35\3\36\3\36\5\36\u0175\n\36\3\37\3\37\3\37\3\37\3 \3 \3"+
		" \5 \u017e\n \3!\3!\3!\3!\5!\u0184\n!\3\"\3\"\3\"\5\"\u0189\n\"\3#\3#"+
		"\3#\3$\3$\3$\3$\3$\3$\5$\u0194\n$\3$\3$\5$\u0198\n$\3%\3%\5%\u019c\n%"+
		"\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u01a8\n\'\3(\3(\5(\u01ac\n(\3"+
		")\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\5*\u01b9\n*\3*\5*\u01bc\n*\3+\3+\3+\3"+
		"+\3+\3+\3+\3+\5+\u01c6\n+\3+\3+\3+\3+\7+\u01cc\n+\f+\16+\u01cf\13+\3,"+
		"\3,\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\3-\5-\u01e1\n-\3.\3.\3/\3/"+
		"\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\5\61\u01f1\n\61\3\62"+
		"\3\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u01fc\n\63\3\64\3\64\3\64"+
		"\5\64\u0201\n\64\3\65\3\65\3\65\3\66\3\66\5\66\u0208\n\66\3\67\3\67\3"+
		"\67\3\67\3\67\3\67\3\67\38\38\38\38\38\58\u0216\n8\39\39\39\39\3:\3:\3"+
		":\3:\3;\3;\3<\3<\3<\3<\5<\u0226\n<\3=\3=\3=\7=\u022b\n=\f=\16=\u022e\13"+
		"=\3>\3>\3>\3>\3>\3>\5>\u0236\n>\3?\3?\5?\u023a\n?\3?\3?\3?\5?\u023f\n"+
		"?\3@\3@\3@\7@\u0244\n@\f@\16@\u0247\13@\3A\3A\5A\u024b\nA\3B\3B\3B\3B"+
		"\3C\3C\3C\3C\3C\3D\3D\3D\3D\7D\u025a\nD\fD\16D\u025d\13D\3E\3E\3E\3E\3"+
		"F\3F\3F\3F\7F\u0267\nF\fF\16F\u026a\13F\3F\3F\3G\3G\3G\3G\3G\3G\7G\u0274"+
		"\nG\fG\16G\u0277\13G\3G\3G\3H\3H\3H\3H\3H\3H\3I\3I\3I\3I\3I\6I\u0286\n"+
		"I\rI\16I\u0287\3I\3I\5I\u028c\nI\3J\3J\3J\7J\u0291\nJ\fJ\16J\u0294\13"+
		"J\3K\3K\3K\3K\3K\3K\3K\3K\5K\u029e\nK\3L\3L\3L\3L\3M\3M\3M\3M\3M\3M\7"+
		"M\u02aa\nM\fM\16M\u02ad\13M\3M\3M\3N\3N\3N\3N\3N\3N\3N\3N\3N\5N\u02ba"+
		"\nN\3N\3N\3O\3O\3O\3O\3P\3P\3P\3P\3P\5P\u02c7\nP\3Q\3Q\3Q\3Q\7Q\u02cd"+
		"\nQ\fQ\16Q\u02d0\13Q\3Q\3Q\3R\3R\3S\3S\3S\3S\5S\u02da\nS\3T\3T\3T\3T\3"+
		"U\3U\3U\3U\3V\3V\3V\3V\3W\3W\3W\3W\3X\3X\3X\3X\3Y\3Y\3Z\3Z\3[\3[\3[\3"+
		"[\3[\5[\u02f9\n[\3[\3[\5[\u02fd\n[\3[\3[\5[\u0301\n[\3\\\3\\\3]\3]\5]"+
		"\u0307\n]\3^\3^\3^\7^\u030c\n^\f^\16^\u030f\13^\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3`\3`\3`\7`\u031c\n`\f`\16`\u031f\13`\3a\3a\3a\5a\u0324\na\3b\3b\3c"+
		"\3c\5c\u032a\nc\3c\3c\3c\5c\u032f\nc\7c\u0331\nc\fc\16c\u0334\13c\3d\3"+
		"d\3e\3e\3e\3e\3f\3f\3g\3g\3g\3g\7g\u0342\ng\fg\16g\u0345\13g\3g\3g\3g"+
		"\3g\5g\u034b\ng\3h\3h\3h\3h\3i\3i\3i\3i\7i\u0355\ni\fi\16i\u0358\13i\3"+
		"i\3i\3i\3i\5i\u035e\ni\3j\3j\3j\3j\3j\3j\3j\5j\u0367\nj\3j\3\u0356\3T"+
		"k\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDF"+
		"HJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c"+
		"\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4"+
		"\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc"+
		"\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\2\5"+
		"\3\2/\62\4\2\64\65\67<\4\2RRVW\u0359\2\u00d5\3\2\2\2\4\u00e2\3\2\2\2\6"+
		"\u00e4\3\2\2\2\b\u00ec\3\2\2\2\n\u00f5\3\2\2\2\f\u00fd\3\2\2\2\16\u0101"+
		"\3\2\2\2\20\u0105\3\2\2\2\22\u010c\3\2\2\2\24\u010e\3\2\2\2\26\u0112\3"+
		"\2\2\2\30\u0116\3\2\2\2\32\u0118\3\2\2\2\34\u011b\3\2\2\2\36\u011e\3\2"+
		"\2\2 \u0122\3\2\2\2\"\u0126\3\2\2\2$\u012e\3\2\2\2&\u0133\3\2\2\2(\u0136"+
		"\3\2\2\2*\u014c\3\2\2\2,\u014e\3\2\2\2.\u0150\3\2\2\2\60\u0158\3\2\2\2"+
		"\62\u015a\3\2\2\2\64\u015c\3\2\2\2\66\u016d\3\2\2\28\u0170\3\2\2\2:\u0174"+
		"\3\2\2\2<\u0176\3\2\2\2>\u017a\3\2\2\2@\u0183\3\2\2\2B\u0188\3\2\2\2D"+
		"\u018a\3\2\2\2F\u0197\3\2\2\2H\u019b\3\2\2\2J\u019d\3\2\2\2L\u01a7\3\2"+
		"\2\2N\u01a9\3\2\2\2P\u01ad\3\2\2\2R\u01b3\3\2\2\2T\u01c5\3\2\2\2V\u01d0"+
		"\3\2\2\2X\u01e0\3\2\2\2Z\u01e2\3\2\2\2\\\u01e4\3\2\2\2^\u01e6\3\2\2\2"+
		"`\u01f0\3\2\2\2b\u01f2\3\2\2\2d\u01fb\3\2\2\2f\u0200\3\2\2\2h\u0202\3"+
		"\2\2\2j\u0207\3\2\2\2l\u0209\3\2\2\2n\u0210\3\2\2\2p\u0217\3\2\2\2r\u021b"+
		"\3\2\2\2t\u021f\3\2\2\2v\u0221\3\2\2\2x\u0227\3\2\2\2z\u022f\3\2\2\2|"+
		"\u0239\3\2\2\2~\u0240\3\2\2\2\u0080\u024a\3\2\2\2\u0082\u024c\3\2\2\2"+
		"\u0084\u0250\3\2\2\2\u0086\u0255\3\2\2\2\u0088\u025e\3\2\2\2\u008a\u0262"+
		"\3\2\2\2\u008c\u026d\3\2\2\2\u008e\u027a\3\2\2\2\u0090\u028b\3\2\2\2\u0092"+
		"\u028d\3\2\2\2\u0094\u029d\3\2\2\2\u0096\u029f\3\2\2\2\u0098\u02a3\3\2"+
		"\2\2\u009a\u02b0\3\2\2\2\u009c\u02bd\3\2\2\2\u009e\u02c1\3\2\2\2\u00a0"+
		"\u02c8\3\2\2\2\u00a2\u02d3\3\2\2\2\u00a4\u02d9\3\2\2\2\u00a6\u02db\3\2"+
		"\2\2\u00a8\u02df\3\2\2\2\u00aa\u02e3\3\2\2\2\u00ac\u02e7\3\2\2\2\u00ae"+
		"\u02eb\3\2\2\2\u00b0\u02ef\3\2\2\2\u00b2\u02f1\3\2\2\2\u00b4\u02f3\3\2"+
		"\2\2\u00b6\u0302\3\2\2\2\u00b8\u0306\3\2\2\2\u00ba\u0308\3\2\2\2\u00bc"+
		"\u0310\3\2\2\2\u00be\u0318\3\2\2\2\u00c0\u0320\3\2\2\2\u00c2\u0325\3\2"+
		"\2\2\u00c4\u0329\3\2\2\2\u00c6\u0335\3\2\2\2\u00c8\u0337\3\2\2\2\u00ca"+
		"\u033b\3\2\2\2\u00cc\u034a\3\2\2\2\u00ce\u034c\3\2\2\2\u00d0\u035d\3\2"+
		"\2\2\u00d2\u0366\3\2\2\2\u00d4\u00d6\5\"\22\2\u00d5\u00d4\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\u00d9\3\2\2\2\u00d7\u00da\5\4\3\2\u00d8\u00da\5\6"+
		"\4\2\u00d9\u00d7\3\2\2\2\u00d9\u00d8\3\2\2\2\u00da\3\3\2\2\2\u00db\u00e3"+
		"\5\n\6\2\u00dc\u00e3\5\16\b\2\u00dd\u00e3\5\22\n\2\u00de\u00e3\5\20\t"+
		"\2\u00df\u00e3\5\26\f\2\u00e0\u00e3\5\24\13\2\u00e1\u00e3\3\2\2\2\u00e2"+
		"\u00db\3\2\2\2\u00e2\u00dc\3\2\2\2\u00e2\u00dd\3\2\2\2\u00e2\u00de\3\2"+
		"\2\2\u00e2\u00df\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3"+
		"\5\3\2\2\2\u00e4\u00e5\5\b\5\2\u00e5\7\3\2\2\2\u00e6\u00ed\5&\24\2\u00e7"+
		"\u00e8\5h\65\2\u00e8\u00ea\5D#\2\u00e9\u00eb\5F$\2\u00ea\u00e9\3\2\2\2"+
		"\u00ea\u00eb\3\2\2\2\u00eb\u00ed\3\2\2\2\u00ec\u00e6\3\2\2\2\u00ec\u00e7"+
		"\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00f0\5v<\2\u00ef\u00f1\5\32\16\2\u00f0"+
		"\u00ef\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f3\3\2\2\2\u00f2\u00f4\5\34"+
		"\17\2\u00f3\u00f2\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\t\3\2\2\2\u00f5\u00f8"+
		"\7\31\2\2\u00f6\u00f9\5\u0084C\2\u00f7\u00f9\5\u0082B\2\u00f8\u00f6\3"+
		"\2\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00fb\3\2\2\2\u00fa\u00fc\5\f\7\2\u00fb"+
		"\u00fa\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\13\3\2\2\2\u00fd\u00fe\7\32\2"+
		"\2\u00fe\u00ff\7\27\2\2\u00ff\u0100\5\30\r\2\u0100\r\3\2\2\2\u0101\u0102"+
		"\7\31\2\2\u0102\u0103\7\27\2\2\u0103\u0104\5\30\r\2\u0104\17\3\2\2\2\u0105"+
		"\u0106\7\33\2\2\u0106\u0107\7\27\2\2\u0107\u0108\5\30\r\2\u0108\u0109"+
		"\7\32\2\2\u0109\u010a\7\26\2\2\u010a\u010b\5\30\r\2\u010b\21\3\2\2\2\u010c"+
		"\u010d\7\3\2\2\u010d\23\3\2\2\2\u010e\u010f\7\34\2\2\u010f\u0110\7\27"+
		"\2\2\u0110\u0111\5\30\r\2\u0111\25\3\2\2\2\u0112\u0113\7\34\2\2\u0113"+
		"\u0114\7\30\2\2\u0114\u0115\5\u00b2Z\2\u0115\27\3\2\2\2\u0116\u0117\7"+
		"R\2\2\u0117\31\3\2\2\2\u0118\u0119\7\32\2\2\u0119\u011a\5\36\20\2\u011a"+
		"\33\3\2\2\2\u011b\u011c\7\32\2\2\u011c\u011d\5 \21\2\u011d\35\3\2\2\2"+
		"\u011e\u011f\7N\2\2\u011f\u0120\7H\2\2\u0120\u0121\5\u00ccg\2\u0121\37"+
		"\3\2\2\2\u0122\u0123\7O\2\2\u0123\u0124\7H\2\2\u0124\u0125\5\u00ccg\2"+
		"\u0125!\3\2\2\2\u0126\u012b\5$\23\2\u0127\u0128\7,\2\2\u0128\u012a\5$"+
		"\23\2\u0129\u0127\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c#\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u012f\7?\2\2\u012f"+
		"\u0130\7R\2\2\u0130\u0131\7H\2\2\u0131\u0132\5\u00b4[\2\u0132%\3\2\2\2"+
		"\u0133\u0134\7&\2\2\u0134\u0135\5(\25\2\u0135\'\3\2\2\2\u0136\u0139\7"+
		"+\2\2\u0137\u013a\5*\26\2\u0138\u013a\5,\27\2\u0139\u0137\3\2\2\2\u0139"+
		"\u0138\3\2\2\2\u013a\u0142\3\2\2\2\u013b\u013e\7,\2\2\u013c\u013f\5*\26"+
		"\2\u013d\u013f\5,\27\2\u013e\u013c\3\2\2\2\u013e\u013d\3\2\2\2\u013f\u0141"+
		"\3\2\2\2\u0140\u013b\3\2\2\2\u0141\u0144\3\2\2\2\u0142\u0140\3\2\2\2\u0142"+
		"\u0143\3\2\2\2\u0143\u0145\3\2\2\2\u0144\u0142\3\2\2\2\u0145\u0146\7-"+
		"\2\2\u0146)\3\2\2\2\u0147\u014d\5.\30\2\u0148\u0149\5\60\31\2\u0149\u014a"+
		"\7.\2\2\u014a\u014b\7I\2\2\u014b\u014d\3\2\2\2\u014c\u0147\3\2\2\2\u014c"+
		"\u0148\3\2\2\2\u014d+\3\2\2\2\u014e\u014f\5\64\33\2\u014f-\3\2\2\2\u0150"+
		"\u0155\5\60\31\2\u0151\u0152\7.\2\2\u0152\u0154\5\62\32\2\u0153\u0151"+
		"\3\2\2\2\u0154\u0157\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156"+
		"/\3\2\2\2\u0157\u0155\3\2\2\2\u0158\u0159\7R\2\2\u0159\61\3\2\2\2\u015a"+
		"\u015b\7R\2\2\u015b\63\3\2\2\2\u015c\u015d\58\35\2\u015d\u015e\7+\2\2"+
		"\u015e\u0163\5:\36\2\u015f\u0160\7,\2\2\u0160\u0162\5:\36\2\u0161\u015f"+
		"\3\2\2\2\u0162\u0165\3\2\2\2\u0163\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164"+
		"\u0166\3\2\2\2\u0165\u0163\3\2\2\2\u0166\u0167\7-\2\2\u0167\u0168\5\66"+
		"\34\2\u0168\65\3\2\2\2\u0169\u016a\7.\2\2\u016a\u016c\7R\2\2\u016b\u0169"+
		"\3\2\2\2\u016c\u016f\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e"+
		"\67\3\2\2\2\u016f\u016d\3\2\2\2\u0170\u0171\5> \2\u01719\3\2\2\2\u0172"+
		"\u0175\5@!\2\u0173\u0175\5<\37\2\u0174\u0172\3\2\2\2\u0174\u0173\3\2\2"+
		"\2\u0175;\3\2\2\2\u0176\u0177\7R\2\2\u0177\u0178\7H\2\2\u0178\u0179\5"+
		"@!\2\u0179=\3\2\2\2\u017a\u017d\7R\2\2\u017b\u017c\7.\2\2\u017c\u017e"+
		"\7R\2\2\u017d\u017b\3\2\2\2\u017d\u017e\3\2\2\2\u017e?\3\2\2\2\u017f\u0184"+
		"\5\60\31\2\u0180\u0184\5.\30\2\u0181\u0184\5\64\33\2\u0182\u0184\5B\""+
		"\2\u0183\u017f\3\2\2\2\u0183\u0180\3\2\2\2\u0183\u0181\3\2\2\2\u0183\u0182"+
		"\3\2\2\2\u0184A\3\2\2\2\u0185\u0189\7U\2\2\u0186\u0189\7Q\2\2\u0187\u0189"+
		"\5\u00caf\2\u0188\u0185\3\2\2\2\u0188\u0186\3\2\2\2\u0188\u0187\3\2\2"+
		"\2\u0189C\3\2\2\2\u018a\u018b\7!\2\2\u018b\u018c\5H%\2\u018cE\3\2\2\2"+
		"\u018d\u018e\7)\2\2\u018e\u018f\7U\2\2\u018f\u0190\7J\2\2\u0190\u0193"+
		"\3\2\2\2\u0191\u0192\7*\2\2\u0192\u0194\5L\'\2\u0193\u0191\3\2\2\2\u0193"+
		"\u0194\3\2\2\2\u0194\u0198\3\2\2\2\u0195\u0196\7*\2\2\u0196\u0198\5L\'"+
		"\2\u0197\u018d\3\2\2\2\u0197\u0195\3\2\2\2\u0198G\3\2\2\2\u0199\u019c"+
		"\5T+\2\u019a\u019c\5J&\2\u019b\u0199\3\2\2\2\u019b\u019a\3\2\2\2\u019c"+
		"I\3\2\2\2\u019d\u019e\7\4\2\2\u019e\u019f\7H\2\2\u019f\u01a0\5N(\2\u01a0"+
		"K\3\2\2\2\u01a1\u01a2\7U\2\2\u01a2\u01a8\7J\2\2\u01a3\u01a4\7U\2\2\u01a4"+
		"\u01a8\7K\2\2\u01a5\u01a8\5N(\2\u01a6\u01a8\7#\2\2\u01a7\u01a1\3\2\2\2"+
		"\u01a7\u01a3\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a6\3\2\2\2\u01a8M\3"+
		"\2\2\2\u01a9\u01ab\5P)\2\u01aa\u01ac\5R*\2\u01ab\u01aa\3\2\2\2\u01ab\u01ac"+
		"\3\2\2\2\u01acO\3\2\2\2\u01ad\u01ae\7U\2\2\u01ae\u01af\7L\2\2\u01af\u01b0"+
		"\7U\2\2\u01b0\u01b1\7L\2\2\u01b1\u01b2\7U\2\2\u01b2Q\3\2\2\2\u01b3\u01b4"+
		"\7U\2\2\u01b4\u01b5\7H\2\2\u01b5\u01b8\7U\2\2\u01b6\u01b7\7H\2\2\u01b7"+
		"\u01b9\7U\2\2\u01b8\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01bb\3\2"+
		"\2\2\u01ba\u01bc\7P\2\2\u01bb\u01ba\3\2\2\2\u01bb\u01bc\3\2\2\2\u01bc"+
		"S\3\2\2\2\u01bd\u01be\b+\1\2\u01be\u01c6\5X-\2\u01bf\u01c0\7+\2\2\u01c0"+
		"\u01c1\5T+\2\u01c1\u01c2\7-\2\2\u01c2\u01c6\3\2\2\2\u01c3\u01c4\7/\2\2"+
		"\u01c4\u01c6\5T+\3\u01c5\u01bd\3\2\2\2\u01c5\u01bf\3\2\2\2\u01c5\u01c3"+
		"\3\2\2\2\u01c6\u01cd\3\2\2\2\u01c7\u01c8\f\5\2\2\u01c8\u01c9\5V,\2\u01c9"+
		"\u01ca\5T+\6\u01ca\u01cc\3\2\2\2\u01cb\u01c7\3\2\2\2\u01cc\u01cf\3\2\2"+
		"\2\u01cd\u01cb\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ceU\3\2\2\2\u01cf\u01cd"+
		"\3\2\2\2\u01d0\u01d1\t\2\2\2\u01d1W\3\2\2\2\u01d2\u01d3\5Z.\2\u01d3\u01d4"+
		"\5`\61\2\u01d4\u01d5\5\\/\2\u01d5\u01e1\3\2\2\2\u01d6\u01d7\5^\60\2\u01d7"+
		"\u01d8\5d\63\2\u01d8\u01d9\5Z.\2\u01d9\u01da\7\60\2\2\u01da\u01db\5\\"+
		"/\2\u01db\u01e1\3\2\2\2\u01dc\u01dd\5^\60\2\u01dd\u01de\5f\64\2\u01de"+
		"\u01df\7\66\2\2\u01df\u01e1\3\2\2\2\u01e0\u01d2\3\2\2\2\u01e0\u01d6\3"+
		"\2\2\2\u01e0\u01dc\3\2\2\2\u01e1Y\3\2\2\2\u01e2\u01e3\5@!\2\u01e3[\3\2"+
		"\2\2\u01e4\u01e5\5@!\2\u01e5]\3\2\2\2\u01e6\u01e7\5@!\2\u01e7_\3\2\2\2"+
		"\u01e8\u01f1\5b\62\2\u01e9\u01ea\7M\2\2\u01ea\u01eb\7+\2\2\u01eb\u01ec"+
		"\5b\62\2\u01ec\u01ed\7,\2\2\u01ed\u01ee\7U\2\2\u01ee\u01ef\7-\2\2\u01ef"+
		"\u01f1\3\2\2\2\u01f0\u01e8\3\2\2\2\u01f0\u01e9\3\2\2\2\u01f1a\3\2\2\2"+
		"\u01f2\u01f3\t\3\2\2\u01f3c\3\2\2\2\u01f4\u01fc\7$\2\2\u01f5\u01f6\7M"+
		"\2\2\u01f6\u01f7\7+\2\2\u01f7\u01f8\7$\2\2\u01f8\u01f9\7,\2\2\u01f9\u01fa"+
		"\7U\2\2\u01fa\u01fc\7-\2\2\u01fb\u01f4\3\2\2\2\u01fb\u01f5\3\2\2\2\u01fc"+
		"e\3\2\2\2\u01fd\u0201\7%\2\2\u01fe\u01ff\7%\2\2\u01ff\u0201\7/\2\2\u0200"+
		"\u01fd\3\2\2\2\u0200\u01fe\3\2\2\2\u0201g\3\2\2\2\u0202\u0203\7=\2\2\u0203"+
		"\u0204\5(\25\2\u0204i\3\2\2\2\u0205\u0208\5l\67\2\u0206\u0208\5n8\2\u0207"+
		"\u0205\3\2\2\2\u0207\u0206\3\2\2\2\u0208k\3\2\2\2\u0209\u020a\7B\2\2\u020a"+
		"\u020b\7\67\2\2\u020b\u020c\7@\2\2\u020c\u020d\7C\2\2\u020d\u020e\7\67"+
		"\2\2\u020e\u020f\5t;\2\u020fm\3\2\2\2\u0210\u0211\7B\2\2\u0211\u0212\7"+
		"\67\2\2\u0212\u0215\7D\2\2\u0213\u0216\5p9\2\u0214\u0216\5r:\2\u0215\u0213"+
		"\3\2\2\2\u0215\u0214\3\2\2\2\u0216o\3\2\2\2\u0217\u0218\7E\2\2\u0218\u0219"+
		"\7\67\2\2\u0219\u021a\7Q\2\2\u021aq\3\2\2\2\u021b\u021c\7F\2\2\u021c\u021d"+
		"\7\67\2\2\u021d\u021e\7Q\2\2\u021es\3\2\2\2\u021f\u0220\5\u00b4[\2\u0220"+
		"u\3\2\2\2\u0221\u0222\7\35\2\2\u0222\u0225\5x=\2\u0223\u0224\7,\2\2\u0224"+
		"\u0226\5~@\2\u0225\u0223\3\2\2\2\u0225\u0226\3\2\2\2\u0226w\3\2\2\2\u0227"+
		"\u022c\5z>\2\u0228\u0229\7,\2\2\u0229\u022b\5z>\2\u022a\u0228\3\2\2\2"+
		"\u022b\u022e\3\2\2\2\u022c\u022a\3\2\2\2\u022c\u022d\3\2\2\2\u022dy\3"+
		"\2\2\2\u022e\u022c\3\2\2\2\u022f\u0230\7\'\2\2\u0230\u0231\5\u00b0Y\2"+
		"\u0231\u0232\7\37\2\2\u0232\u0235\5|?\2\u0233\u0234\7 \2\2\u0234\u0236"+
		"\5T+\2\u0235\u0233\3\2\2\2\u0235\u0236\3\2\2\2\u0236{\3\2\2\2\u0237\u0238"+
		"\7R\2\2\u0238\u023a\7H\2\2\u0239\u0237\3\2\2\2\u0239\u023a\3\2\2\2\u023a"+
		"\u023b\3\2\2\2\u023b\u023e\7R\2\2\u023c\u023d\7.\2\2\u023d\u023f\7R\2"+
		"\2\u023e\u023c\3\2\2\2\u023e\u023f\3\2\2\2\u023f}\3\2\2\2\u0240\u0245"+
		"\5\u0080A\2\u0241\u0242\7,\2\2\u0242\u0244\5\u0080A\2\u0243\u0241\3\2"+
		"\2\2\u0244\u0247\3\2\2\2\u0245\u0243\3\2\2\2\u0245\u0246\3\2\2\2\u0246"+
		"\177\3\2\2\2\u0247\u0245\3\2\2\2\u0248\u024b\5\u0082B\2\u0249\u024b\5"+
		"\u0084C\2\u024a\u0248\3\2\2\2\u024a\u0249\3\2\2\2\u024b\u0081\3\2\2\2"+
		"\u024c\u024d\7\5\2\2\u024d\u024e\5\u00b2Z\2\u024e\u024f\5\u00b4[\2\u024f"+
		"\u0083\3\2\2\2\u0250\u0251\7\6\2\2\u0251\u0252\5\u00b2Z\2\u0252\u0253"+
		"\5\u0086D\2\u0253\u0254\5\u008aF\2\u0254\u0085\3\2\2\2\u0255\u0256\7\7"+
		"\2\2\u0256\u025b\5\u0088E\2\u0257\u0258\7,\2\2\u0258\u025a\5\u0088E\2"+
		"\u0259\u0257\3\2\2\2\u025a\u025d\3\2\2\2\u025b\u0259\3\2\2\2\u025b\u025c"+
		"\3\2\2\2\u025c\u0087\3\2\2\2\u025d\u025b\3\2\2\2\u025e\u025f\5|?\2\u025f"+
		"\u0260\7(\2\2\u0260\u0261\7R\2\2\u0261\u0089\3\2\2\2\u0262\u0263\7\b\2"+
		"\2\u0263\u0268\5\u008cG\2\u0264\u0265\7,\2\2\u0265\u0267\5\u008cG\2\u0266"+
		"\u0264\3\2\2\2\u0267\u026a\3\2\2\2\u0268\u0266\3\2\2\2\u0268\u0269\3\2"+
		"\2\2\u0269\u026b\3\2\2\2\u026a\u0268\3\2\2\2\u026b\u026c\7\t\2\2\u026c"+
		"\u008b\3\2\2\2\u026d\u026e\7Q\2\2\u026e\u026f\7H\2\2\u026f\u0270\7\b\2"+
		"\2\u0270\u0275\5\u008eH\2\u0271\u0272\7,\2\2\u0272\u0274\5\u008eH\2\u0273"+
		"\u0271\3\2\2\2\u0274\u0277\3\2\2\2\u0275\u0273\3\2\2\2\u0275\u0276\3\2"+
		"\2\2\u0276\u0278\3\2\2\2\u0277\u0275\3\2\2\2\u0278\u0279\7\t\2\2\u0279"+
		"\u008d\3\2\2\2\u027a\u027b\5\u0090I\2\u027b\u027c\7H\2\2\u027c\u027d\7"+
		"\b\2\2\u027d\u027e\5\u0094K\2\u027e\u027f\7\t\2\2\u027f\u008f\3\2\2\2"+
		"\u0280\u028c\5\u0092J\2\u0281\u0282\7\n\2\2\u0282\u0285\5\u0092J\2\u0283"+
		"\u0284\7,\2\2\u0284\u0286\5\u0092J\2\u0285\u0283\3\2\2\2\u0286\u0287\3"+
		"\2\2\2\u0287\u0285\3\2\2\2\u0287\u0288\3\2\2\2\u0288\u0289\3\2\2\2\u0289"+
		"\u028a\7\13\2\2\u028a\u028c\3\2\2\2\u028b\u0280\3\2\2\2\u028b\u0281\3"+
		"\2\2\2\u028c\u0091\3\2\2\2\u028d\u0292\7R\2\2\u028e\u028f\7.\2\2\u028f"+
		"\u0291\7R\2\2\u0290\u028e\3\2\2\2\u0291\u0294\3\2\2\2\u0292\u0290\3\2"+
		"\2\2\u0292\u0293\3\2\2\2\u0293\u0093\3\2\2\2\u0294\u0292\3\2\2\2\u0295"+
		"\u0296\5\u0098M\2\u0296\u0297\7,\2\2\u0297\u0298\5\u0096L\2\u0298\u029e"+
		"\3\2\2\2\u0299\u029a\5\u0096L\2\u029a\u029b\7,\2\2\u029b\u029c\5\u0098"+
		"M\2\u029c\u029e\3\2\2\2\u029d\u0295\3\2\2\2\u029d\u0299\3\2\2\2\u029e"+
		"\u0095\3\2\2\2\u029f\u02a0\7\f\2\2\u02a0\u02a1\7H\2\2\u02a1\u02a2\7U\2"+
		"\2\u02a2\u0097\3\2\2\2\u02a3\u02a4\7\r\2\2\u02a4\u02a5\7H\2\2\u02a5\u02a6"+
		"\7\n\2\2\u02a6\u02ab\5\u009aN\2\u02a7\u02a8\7,\2\2\u02a8\u02aa\5\u009a"+
		"N\2\u02a9\u02a7\3\2\2\2\u02aa\u02ad\3\2\2\2\u02ab\u02a9\3\2\2\2\u02ab"+
		"\u02ac\3\2\2\2\u02ac\u02ae\3\2\2\2\u02ad\u02ab\3\2\2\2\u02ae\u02af\7\13"+
		"\2\2\u02af\u0099\3\2\2\2\u02b0\u02b9\7\b\2\2\u02b1\u02b2\5\u009cO\2\u02b2"+
		"\u02b3\7,\2\2\u02b3\u02b4\5\u009eP\2\u02b4\u02ba\3\2\2\2\u02b5\u02b6\5"+
		"\u009eP\2\u02b6\u02b7\7,\2\2\u02b7\u02b8\5\u009cO\2\u02b8\u02ba\3\2\2"+
		"\2\u02b9\u02b1\3\2\2\2\u02b9\u02b5\3\2\2\2\u02ba\u02bb\3\2\2\2\u02bb\u02bc"+
		"\7\t\2\2\u02bc\u009b\3\2\2\2\u02bd\u02be\7\16\2\2\u02be\u02bf\7H\2\2\u02bf"+
		"\u02c0\7U\2\2\u02c0\u009d\3\2\2\2\u02c1\u02c2\7\17\2\2\u02c2\u02c6\7H"+
		"\2\2\u02c3\u02c7\5\u00a4S\2\u02c4\u02c7\5\u00a0Q\2\u02c5\u02c7\5\u00a2"+
		"R\2\u02c6\u02c3\3\2\2\2\u02c6\u02c4\3\2\2\2\u02c6\u02c5\3\2\2\2\u02c7"+
		"\u009f\3\2\2\2\u02c8\u02c9\7\n\2\2\u02c9\u02ce\5\u00a2R\2\u02ca\u02cb"+
		"\7H\2\2\u02cb\u02cd\5\u00a2R\2\u02cc\u02ca\3\2\2\2\u02cd\u02d0\3\2\2\2"+
		"\u02ce\u02cc\3\2\2\2\u02ce\u02cf\3\2\2\2\u02cf\u02d1\3\2\2\2\u02d0\u02ce"+
		"\3\2\2\2\u02d1\u02d2\7\13\2\2\u02d2\u00a1\3\2\2\2\u02d3\u02d4\5\u00ca"+
		"f\2\u02d4\u00a3\3\2\2\2\u02d5\u02da\5\u00a6T\2\u02d6\u02da\5\u00a8U\2"+
		"\u02d7\u02da\5\u00aaV\2\u02d8\u02da\5\u00acW\2\u02d9\u02d5\3\2\2\2\u02d9"+
		"\u02d6\3\2\2\2\u02d9\u02d7\3\2\2\2\u02d9\u02d8\3\2\2\2\u02da\u00a5\3\2"+
		"\2\2\u02db\u02dc\7\n\2\2\u02dc\u02dd\5\u00aeX\2\u02dd\u02de\7\13\2\2\u02de"+
		"\u00a7\3\2\2\2\u02df\u02e0\7\n\2\2\u02e0\u02e1\5\u00aeX\2\u02e1\u02e2"+
		"\7-\2\2\u02e2\u00a9\3\2\2\2\u02e3\u02e4\7+\2\2\u02e4\u02e5\5\u00aeX\2"+
		"\u02e5\u02e6\7\13\2\2\u02e6\u00ab\3\2\2\2\u02e7\u02e8\7+\2\2\u02e8\u02e9"+
		"\5\u00aeX\2\u02e9\u02ea\7-\2\2\u02ea\u00ad\3\2\2\2\u02eb\u02ec\7U\2\2"+
		"\u02ec\u02ed\7\20\2\2\u02ed\u02ee\7U\2\2\u02ee\u00af\3\2\2\2\u02ef\u02f0"+
		"\7R\2\2\u02f0\u00b1\3\2\2\2\u02f1\u02f2\7R\2\2\u02f2\u00b3\3\2\2\2\u02f3"+
		"\u02f4\5\u00b6\\\2\u02f4\u02f5\7\21\2\2\u02f5\u02f8\5\u00b8]\2\u02f6\u02f7"+
		"\7H\2\2\u02f7\u02f9\5\u00c2b\2\u02f8\u02f6\3\2\2\2\u02f8\u02f9\3\2\2\2"+
		"\u02f9\u02fc\3\2\2\2\u02fa\u02fb\7L\2\2\u02fb\u02fd\5\u00c4c\2\u02fc\u02fa"+
		"\3\2\2\2\u02fc\u02fd\3\2\2\2\u02fd\u0300\3\2\2\2\u02fe\u02ff\7\22\2\2"+
		"\u02ff\u0301\5\u00be`\2\u0300\u02fe\3\2\2\2\u0300\u0301\3\2\2\2\u0301"+
		"\u00b5\3\2\2\2\u0302\u0303\7R\2\2\u0303\u00b7\3\2\2\2\u0304\u0307\5\u00ba"+
		"^\2\u0305\u0307\5\u00bc_\2\u0306\u0304\3\2\2\2\u0306\u0305\3\2\2\2\u0307"+
		"\u00b9\3\2\2\2\u0308\u030d\7R\2\2\u0309\u030a\7.\2\2\u030a\u030c\7R\2"+
		"\2\u030b\u0309\3\2\2\2\u030c\u030f\3\2\2\2\u030d\u030b\3\2\2\2\u030d\u030e"+
		"\3\2\2\2\u030e\u00bb\3\2\2\2\u030f\u030d\3\2\2\2\u0310\u0311\7V\2\2\u0311"+
		"\u0312\7.\2\2\u0312\u0313\7V\2\2\u0313\u0314\7.\2\2\u0314\u0315\7V\2\2"+
		"\u0315\u0316\7.\2\2\u0316\u0317\7V\2\2\u0317\u00bd\3\2\2\2\u0318\u031d"+
		"\5\u00c0a\2\u0319\u031a\7\23\2\2\u031a\u031c\5\u00c0a\2\u031b\u0319\3"+
		"\2\2\2\u031c\u031f\3\2\2\2\u031d\u031b\3\2\2\2\u031d\u031e\3\2\2\2\u031e"+
		"\u00bf\3\2\2\2\u031f\u031d\3\2\2\2\u0320\u0323\7R\2\2\u0321\u0322\7\67"+
		"\2\2\u0322\u0324\t\4\2\2\u0323\u0321\3\2\2\2\u0323\u0324\3\2\2\2\u0324"+
		"\u00c1\3\2\2\2\u0325\u0326\7V\2\2\u0326\u00c3\3\2\2\2\u0327\u032a\5\u00c6"+
		"d\2\u0328\u032a\5\u00c8e\2\u0329\u0327\3\2\2\2\u0329\u0328\3\2\2\2\u032a"+
		"\u0332\3\2\2\2\u032b\u032e\7L\2\2\u032c\u032f\5\u00c6d\2\u032d\u032f\5"+
		"\u00c8e\2\u032e\u032c\3\2\2\2\u032e\u032d\3\2\2\2\u032f\u0331\3\2\2\2"+
		"\u0330\u032b\3\2\2\2\u0331\u0334\3\2\2\2\u0332\u0330\3\2\2\2\u0332\u0333"+
		"\3\2\2\2\u0333\u00c5\3\2\2\2\u0334\u0332\3\2\2\2\u0335\u0336\7R\2\2\u0336"+
		"\u00c7\3\2\2\2\u0337\u0338\7\b\2\2\u0338\u0339\7R\2\2\u0339\u033a\7\t"+
		"\2\2\u033a\u00c9\3\2\2\2\u033b\u033c\5\u00d2j\2\u033c\u00cb\3\2\2\2\u033d"+
		"\u033e\7\b\2\2\u033e\u0343\5\u00ceh\2\u033f\u0340\7,\2\2\u0340\u0342\5"+
		"\u00ceh\2\u0341\u033f\3\2\2\2\u0342\u0345\3\2\2\2\u0343\u0341\3\2\2\2"+
		"\u0343\u0344\3\2\2\2\u0344\u0346\3\2\2\2\u0345\u0343\3\2\2\2\u0346\u0347"+
		"\7\t\2\2\u0347\u034b\3\2\2\2\u0348\u0349\7\b\2\2\u0349\u034b\7\t\2\2\u034a"+
		"\u033d\3\2\2\2\u034a\u0348\3\2\2\2\u034b\u00cd\3\2\2\2\u034c\u034d\7Q"+
		"\2\2\u034d\u034e\7H\2\2\u034e\u034f\5\u00d2j\2\u034f\u00cf\3\2\2\2\u0350"+
		"\u0351\7\n\2\2\u0351\u0356\5\u00d2j\2\u0352\u0353\7,\2\2\u0353\u0355\5"+
		"\u00d2j\2\u0354\u0352\3\2\2\2\u0355\u0358\3\2\2\2\u0356\u0357\3\2\2\2"+
		"\u0356\u0354\3\2\2\2\u0357\u0359\3\2\2\2\u0358\u0356\3\2\2\2\u0359\u035a"+
		"\7\13\2\2\u035a\u035e\3\2\2\2\u035b\u035c\7\n\2\2\u035c\u035e\7\13\2\2"+
		"\u035d\u0350\3\2\2\2\u035d\u035b\3\2\2\2\u035e\u00d1\3\2\2\2\u035f\u0367"+
		"\7Q\2\2\u0360\u0367\7U\2\2\u0361\u0367\5\u00ccg\2\u0362\u0367\5\u00d0"+
		"i\2\u0363\u0367\7\24\2\2\u0364\u0367\7\25\2\2\u0365\u0367\7\66\2\2\u0366"+
		"\u035f\3\2\2\2\u0366\u0360\3\2\2\2\u0366\u0361\3\2\2\2\u0366\u0362\3\2"+
		"\2\2\u0366\u0363\3\2\2\2\u0366\u0364\3\2\2\2\u0366\u0365\3\2\2\2\u0367"+
		"\u00d3\3\2\2\2H\u00d5\u00d9\u00e2\u00ea\u00ec\u00f0\u00f3\u00f8\u00fb"+
		"\u012b\u0139\u013e\u0142\u014c\u0155\u0163\u016d\u0174\u017d\u0183\u0188"+
		"\u0193\u0197\u019b\u01a7\u01ab\u01b8\u01bb\u01c5\u01cd\u01e0\u01f0\u01fb"+
		"\u0200\u0207\u0215\u0225\u022c\u0235\u0239\u023e\u0245\u024a\u025b\u0268"+
		"\u0275\u0287\u028b\u0292\u029d\u02ab\u02b9\u02c6\u02ce\u02d9\u02f8\u02fc"+
		"\u0300\u0306\u030d\u031d\u0323\u0329\u032e\u0332\u0343\u034a\u0356\u035d"+
		"\u0366";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}