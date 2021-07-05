// Generated from D:/Projects/CoaaS/CDQL\Cdql.g4 by ANTLR 4.9.1
package au.coaas.cqp.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CdqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

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
		OUTPUT=76, META=77, CALLBACK=78, TIME_ZONE=79, STRING=80, ID=81, COMMENT=82, 
		WS=83, NUMBER=84, INT=85, HEX=86;
	public static final int
		RULE_rule_Cdql = 0, RULE_rule_ddl_statement = 1, RULE_rule_dml_statement = 2, 
		RULE_rule_query = 3, RULE_rule_create_function = 4, RULE_rule_set_package = 5, 
		RULE_rule_create_package = 6, RULE_rule_alter_package = 7, RULE_rule_alter_function = 8, 
		RULE_rule_drop_package = 9, RULE_rule_drop_function = 10, RULE_rule_package_title = 11, 
		RULE_rule_Set_Meta = 12, RULE_rule_Set_Config = 13, RULE_rule_Set_Callback = 14, 
		RULE_rule_Output_Config = 15, RULE_rule_Meta_Config = 16, RULE_rule_Callback_Config = 17, 
		RULE_rule_Prefixs = 18, RULE_rule_Prefix = 19, RULE_rule_Pull = 20, RULE_rule_Select = 21, 
		RULE_rule_select_Attribute = 22, RULE_rule_select_FunctionCall = 23, RULE_rule_Attribute = 24, 
		RULE_rule_EntityTitle = 25, RULE_rule_AttributeTitle = 26, RULE_rule_FunctionCall = 27, 
		RULE_rule_function_call_method_chaining = 28, RULE_rule_call_FunctionTitle = 29, 
		RULE_rule_call_Operand = 30, RULE_rule_Name_Operand = 31, RULE_rule_FunctionTitle = 32, 
		RULE_rule_Operand = 33, RULE_rule_ContextValue = 34, RULE_rule_When = 35, 
		RULE_rule_repeat = 36, RULE_rule_Start = 37, RULE_rule_Date_Time_When = 38, 
		RULE_rule_Occurrence = 39, RULE_rule_Date_Time = 40, RULE_rule_Date = 41, 
		RULE_rule_Time = 42, RULE_rule_Condition = 43, RULE_rule_expr_op = 44, 
		RULE_rule_Constraint = 45, RULE_rule_left_element = 46, RULE_rule_right_element = 47, 
		RULE_rule_target_element = 48, RULE_rule_relational_op_func = 49, RULE_rule_relational_op = 50, 
		RULE_rule_between_op = 51, RULE_rule_is_or_is_not = 52, RULE_ruel_Push = 53, 
		RULE_rule_callback = 54, RULE_rule_http_calback = 55, RULE_rule_fcm_calback = 56, 
		RULE_rule_fcm_topic = 57, RULE_rule_fcm_token = 58, RULE_rule_callback_url = 59, 
		RULE_rule_Define = 60, RULE_rule_Define_Context_Entity = 61, RULE_rule_context_entity = 62, 
		RULE_rule_entity_type = 63, RULE_rule_Define_Context_Function = 64, RULE_rule_context_function = 65, 
		RULE_rule_aFunction = 66, RULE_rule_sFunction = 67, RULE_rule_is_on = 68, 
		RULE_rule_is_on_entity = 69, RULE_cst_situation_def_rule = 70, RULE_rule_single_situatuin = 71, 
		RULE_rule_situation_pair = 72, RULE_rule_situation_attributes = 73, RULE_rule_situation_attribute_name = 74, 
		RULE_situation_pair_values = 75, RULE_situation_weight = 76, RULE_situation_range_values = 77, 
		RULE_situation_pair_values_item = 78, RULE_rule_situation_belief = 79, 
		RULE_rule_situation_value = 80, RULE_rule_discrete_value = 81, RULE_discrete_value = 82, 
		RULE_rule_region_value = 83, RULE_region_value_inclusive = 84, RULE_region_value_left_inclusive = 85, 
		RULE_region_value_right_inclusive = 86, RULE_region_value_exclusive = 87, 
		RULE_region_value_value = 88, RULE_rule_entity_id = 89, RULE_rule_function_id = 90, 
		RULE_rule_url = 91, RULE_authority = 92, RULE_host = 93, RULE_hostname = 94, 
		RULE_hostnumber = 95, RULE_search = 96, RULE_searchparameter = 97, RULE_port = 98, 
		RULE_path = 99, RULE_normal_path = 100, RULE_path_param = 101, RULE_json = 102, 
		RULE_obj = 103, RULE_pair = 104, RULE_array = 105, RULE_value = 106;
	private static String[] makeRuleNames() {
		return new String[] {
			"rule_Cdql", "rule_ddl_statement", "rule_dml_statement", "rule_query", 
			"rule_create_function", "rule_set_package", "rule_create_package", "rule_alter_package", 
			"rule_alter_function", "rule_drop_package", "rule_drop_function", "rule_package_title", 
			"rule_Set_Meta", "rule_Set_Config", "rule_Set_Callback", "rule_Output_Config", 
			"rule_Meta_Config", "rule_Callback_Config", "rule_Prefixs", "rule_Prefix", 
			"rule_Pull", "rule_Select", "rule_select_Attribute", "rule_select_FunctionCall", 
			"rule_Attribute", "rule_EntityTitle", "rule_AttributeTitle", "rule_FunctionCall", 
			"rule_function_call_method_chaining", "rule_call_FunctionTitle", "rule_call_Operand", 
			"rule_Name_Operand", "rule_FunctionTitle", "rule_Operand", "rule_ContextValue", 
			"rule_When", "rule_repeat", "rule_Start", "rule_Date_Time_When", "rule_Occurrence", 
			"rule_Date_Time", "rule_Date", "rule_Time", "rule_Condition", "rule_expr_op", 
			"rule_Constraint", "rule_left_element", "rule_right_element", "rule_target_element", 
			"rule_relational_op_func", "rule_relational_op", "rule_between_op", "rule_is_or_is_not", 
			"ruel_Push", "rule_callback", "rule_http_calback", "rule_fcm_calback", 
			"rule_fcm_topic", "rule_fcm_token", "rule_callback_url", "rule_Define", 
			"rule_Define_Context_Entity", "rule_context_entity", "rule_entity_type", 
			"rule_Define_Context_Function", "rule_context_function", "rule_aFunction", 
			"rule_sFunction", "rule_is_on", "rule_is_on_entity", "cst_situation_def_rule", 
			"rule_single_situatuin", "rule_situation_pair", "rule_situation_attributes", 
			"rule_situation_attribute_name", "situation_pair_values", "situation_weight", 
			"situation_range_values", "situation_pair_values_item", "rule_situation_belief", 
			"rule_situation_value", "rule_discrete_value", "discrete_value", "rule_region_value", 
			"region_value_inclusive", "region_value_left_inclusive", "region_value_right_inclusive", 
			"region_value_exclusive", "region_value_value", "rule_entity_id", "rule_function_id", 
			"rule_url", "authority", "host", "hostname", "hostnumber", "search", 
			"searchparameter", "port", "path", "normal_path", "path_param", "json", 
			"obj", "pair", "array", "value"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
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
			"'output'", "'meta'", "'callback'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "TITLE", "PACKAGE", "FUNCTION", 
			"CREATE", "SET", "ALTER", "DROP", "DEFINE", "CONTEXT_ENTITY", "IS_FROM", 
			"WHERE", "WHEN", "DATE", "LIFETIME", "BETWEEN", "IS", "PULL", "ENTITY", 
			"AS", "EVERY", "UNTIL", "LPAREN", "COMMA", "RPAREN", "DOT", "NOT", "AND", 
			"OR", "XOR", "IN", "CONTAINS_ANY", "CONTAINS_ALL", "NULL", "EQ", "LTH", 
			"GTH", "LET", "GET", "NOT_EQ", "PUSH", "INTO", "PREFIX", "HTTPPOST", 
			"POST", "METHOD", "URL", "FCM", "TOPIC", "TOKEN", "TYPE", "COLON", "ASTERISK", 
			"UNIT_OF_TIME", "OCCURRENCES", "FSLASH", "OP", "OUTPUT", "META", "CALLBACK", 
			"TIME_ZONE", "STRING", "ID", "COMMENT", "WS", "NUMBER", "INT", "HEX"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Cdql(this);
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
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PREFIX) {
				{
				setState(214);
				rule_Prefixs();
				}
			}

			setState(219);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EOF:
			case T__0:
			case CREATE:
			case ALTER:
			case DROP:
				{
				setState(217);
				rule_ddl_statement();
				}
				break;
			case PULL:
			case PUSH:
				{
				setState(218);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_ddl_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ddl_statementContext rule_ddl_statement() throws RecognitionException {
		Rule_ddl_statementContext _localctx = new Rule_ddl_statementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_rule_ddl_statement);
		try {
			setState(228);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(221);
				rule_create_function();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(222);
				rule_create_package();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(223);
				rule_alter_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(224);
				rule_alter_package();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(225);
				rule_drop_function();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(226);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_dml_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_dml_statementContext rule_dml_statement() throws RecognitionException {
		Rule_dml_statementContext _localctx = new Rule_dml_statementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rule_dml_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
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
		public Rule_Set_MetaContext rule_Set_Meta() {
			return getRuleContext(Rule_Set_MetaContext.class,0);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_query(this);
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
			setState(238);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PULL:
				{
				setState(232);
				rule_Pull();
				}
				break;
			case PUSH:
				{
				setState(233);
				ruel_Push();
				setState(234);
				rule_When();
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EVERY || _la==UNTIL) {
					{
					setState(235);
					rule_repeat();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(240);
			rule_Define();
			setState(242);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(241);
				rule_Set_Config();
				}
				break;
			}
			setState(245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(244);
				rule_Set_Callback();
				}
				break;
			}
			setState(248);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SET) {
				{
				setState(247);
				rule_Set_Meta();
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_create_function(this);
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
			setState(250);
			match(CREATE);
			setState(253);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				{
				setState(251);
				rule_sFunction();
				}
				break;
			case T__2:
				{
				setState(252);
				rule_aFunction();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SET) {
				{
				setState(255);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_set_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_set_packageContext rule_set_package() throws RecognitionException {
		Rule_set_packageContext _localctx = new Rule_set_packageContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rule_set_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(SET);
			setState(259);
			match(PACKAGE);
			setState(260);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_create_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_create_packageContext rule_create_package() throws RecognitionException {
		Rule_create_packageContext _localctx = new Rule_create_packageContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_rule_create_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			match(CREATE);
			setState(263);
			match(PACKAGE);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_alter_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_alter_packageContext rule_alter_package() throws RecognitionException {
		Rule_alter_packageContext _localctx = new Rule_alter_packageContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_rule_alter_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			match(ALTER);
			setState(267);
			match(PACKAGE);
			setState(268);
			rule_package_title();
			setState(269);
			match(SET);
			setState(270);
			match(TITLE);
			setState(271);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_alter_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_alter_functionContext rule_alter_function() throws RecognitionException {
		Rule_alter_functionContext _localctx = new Rule_alter_functionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_rule_alter_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_drop_package(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_drop_packageContext rule_drop_package() throws RecognitionException {
		Rule_drop_packageContext _localctx = new Rule_drop_packageContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_rule_drop_package);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			match(DROP);
			setState(276);
			match(PACKAGE);
			setState(277);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_drop_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_drop_functionContext rule_drop_function() throws RecognitionException {
		Rule_drop_functionContext _localctx = new Rule_drop_functionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_rule_drop_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(279);
			match(DROP);
			setState(280);
			match(FUNCTION);
			setState(281);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_package_title(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_package_titleContext rule_package_title() throws RecognitionException {
		Rule_package_titleContext _localctx = new Rule_package_titleContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_rule_package_title);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
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

	public static class Rule_Set_MetaContext extends ParserRuleContext {
		public TerminalNode SET() { return getToken(CdqlParser.SET, 0); }
		public Rule_Meta_ConfigContext rule_Meta_Config() {
			return getRuleContext(Rule_Meta_ConfigContext.class,0);
		}
		public Rule_Set_MetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Set_Meta; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Set_Meta(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Set_MetaContext rule_Set_Meta() throws RecognitionException {
		Rule_Set_MetaContext _localctx = new Rule_Set_MetaContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_rule_Set_Meta);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(SET);
			{
			setState(286);
			rule_Meta_Config();
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Set_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Set_ConfigContext rule_Set_Config() throws RecognitionException {
		Rule_Set_ConfigContext _localctx = new Rule_Set_ConfigContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_rule_Set_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			match(SET);
			{
			setState(289);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Set_Callback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Set_CallbackContext rule_Set_Callback() throws RecognitionException {
		Rule_Set_CallbackContext _localctx = new Rule_Set_CallbackContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_rule_Set_Callback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(SET);
			{
			setState(292);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Output_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Output_ConfigContext rule_Output_Config() throws RecognitionException {
		Rule_Output_ConfigContext _localctx = new Rule_Output_ConfigContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_rule_Output_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			match(OUTPUT);
			setState(295);
			match(COLON);
			setState(296);
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

	public static class Rule_Meta_ConfigContext extends ParserRuleContext {
		public TerminalNode META() { return getToken(CdqlParser.META, 0); }
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public ObjContext obj() {
			return getRuleContext(ObjContext.class,0);
		}
		public Rule_Meta_ConfigContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_Meta_Config; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Meta_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Meta_ConfigContext rule_Meta_Config() throws RecognitionException {
		Rule_Meta_ConfigContext _localctx = new Rule_Meta_ConfigContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_rule_Meta_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(META);
			setState(299);
			match(COLON);
			setState(300);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Callback_Config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Callback_ConfigContext rule_Callback_Config() throws RecognitionException {
		Rule_Callback_ConfigContext _localctx = new Rule_Callback_ConfigContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_rule_Callback_Config);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			match(CALLBACK);
			setState(303);
			match(COLON);
			setState(304);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Prefixs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_PrefixsContext rule_Prefixs() throws RecognitionException {
		Rule_PrefixsContext _localctx = new Rule_PrefixsContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_rule_Prefixs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			rule_Prefix();
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(307);
				match(COMMA);
				setState(308);
				rule_Prefix();
				}
				}
				setState(313);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Prefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_PrefixContext rule_Prefix() throws RecognitionException {
		Rule_PrefixContext _localctx = new Rule_PrefixContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_rule_Prefix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(PREFIX);
			setState(315);
			match(ID);
			setState(316);
			match(COLON);
			setState(317);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Pull(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_PullContext rule_Pull() throws RecognitionException {
		Rule_PullContext _localctx = new Rule_PullContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_rule_Pull);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(PULL);
			setState(320);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Select(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_SelectContext rule_Select() throws RecognitionException {
		Rule_SelectContext _localctx = new Rule_SelectContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_rule_Select);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(LPAREN);
			setState(325);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(323);
				rule_select_Attribute();
				}
				break;
			case 2:
				{
				setState(324);
				rule_select_FunctionCall();
				}
				break;
			}
			setState(334);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(327);
				match(COMMA);
				setState(330);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(328);
					rule_select_Attribute();
					}
					break;
				case 2:
					{
					setState(329);
					rule_select_FunctionCall();
					}
					break;
				}
				}
				}
				setState(336);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(337);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_select_Attribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_select_AttributeContext rule_select_Attribute() throws RecognitionException {
		Rule_select_AttributeContext _localctx = new Rule_select_AttributeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_rule_select_Attribute);
		try {
			setState(344);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(339);
				rule_Attribute();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(340);
				rule_EntityTitle();
				setState(341);
				match(DOT);
				setState(342);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_select_FunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_select_FunctionCallContext rule_select_FunctionCall() throws RecognitionException {
		Rule_select_FunctionCallContext _localctx = new Rule_select_FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_rule_select_FunctionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Attribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_AttributeContext rule_Attribute() throws RecognitionException {
		Rule_AttributeContext _localctx = new Rule_AttributeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_rule_Attribute);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			rule_EntityTitle();
			setState(351); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(349);
					match(DOT);
					setState(350);
					rule_AttributeTitle();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(353); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_EntityTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_EntityTitleContext rule_EntityTitle() throws RecognitionException {
		Rule_EntityTitleContext _localctx = new Rule_EntityTitleContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_rule_EntityTitle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_AttributeTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_AttributeTitleContext rule_AttributeTitle() throws RecognitionException {
		Rule_AttributeTitleContext _localctx = new Rule_AttributeTitleContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_rule_AttributeTitle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_FunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_FunctionCallContext rule_FunctionCall() throws RecognitionException {
		Rule_FunctionCallContext _localctx = new Rule_FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_rule_FunctionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(359);
			rule_call_FunctionTitle();
			setState(360);
			match(LPAREN);
			setState(361);
			rule_call_Operand();
			setState(366);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(362);
				match(COMMA);
				setState(363);
				rule_call_Operand();
				}
				}
				setState(368);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(369);
			match(RPAREN);
			setState(370);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_function_call_method_chaining(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_function_call_method_chainingContext rule_function_call_method_chaining() throws RecognitionException {
		Rule_function_call_method_chainingContext _localctx = new Rule_function_call_method_chainingContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_rule_function_call_method_chaining);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(372);
					match(DOT);
					setState(373);
					match(ID);
					}
					} 
				}
				setState(378);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_call_FunctionTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_call_FunctionTitleContext rule_call_FunctionTitle() throws RecognitionException {
		Rule_call_FunctionTitleContext _localctx = new Rule_call_FunctionTitleContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_rule_call_FunctionTitle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_call_Operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_call_OperandContext rule_call_Operand() throws RecognitionException {
		Rule_call_OperandContext _localctx = new Rule_call_OperandContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_rule_call_Operand);
		try {
			setState(383);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(381);
				rule_Operand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(382);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Name_Operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Name_OperandContext rule_Name_Operand() throws RecognitionException {
		Rule_Name_OperandContext _localctx = new Rule_Name_OperandContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_rule_Name_Operand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			match(ID);
			setState(386);
			match(COLON);
			setState(387);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_FunctionTitle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_FunctionTitleContext rule_FunctionTitle() throws RecognitionException {
		Rule_FunctionTitleContext _localctx = new Rule_FunctionTitleContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_rule_FunctionTitle);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			match(ID);
			setState(392);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(390);
				match(DOT);
				setState(391);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Operand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_OperandContext rule_Operand() throws RecognitionException {
		Rule_OperandContext _localctx = new Rule_OperandContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_rule_Operand);
		try {
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(394);
				rule_EntityTitle();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(395);
				rule_Attribute();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(396);
				rule_FunctionCall();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(397);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_ContextValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ContextValueContext rule_ContextValue() throws RecognitionException {
		Rule_ContextValueContext _localctx = new Rule_ContextValueContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_rule_ContextValue);
		try {
			setState(403);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(400);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(401);
				match(STRING);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(402);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_When(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_WhenContext rule_When() throws RecognitionException {
		Rule_WhenContext _localctx = new Rule_WhenContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_rule_When);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(WHEN);
			setState(406);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_repeat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_repeatContext rule_repeat() throws RecognitionException {
		Rule_repeatContext _localctx = new Rule_repeatContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_rule_repeat);
		int _la;
		try {
			setState(418);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EVERY:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(408);
				match(EVERY);
				setState(409);
				match(NUMBER);
				setState(410);
				match(UNIT_OF_TIME);
				}
				setState(414);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==UNTIL) {
					{
					setState(412);
					match(UNTIL);
					setState(413);
					rule_Occurrence();
					}
				}

				}
				break;
			case UNTIL:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(416);
				match(UNTIL);
				setState(417);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Start(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_StartContext rule_Start() throws RecognitionException {
		Rule_StartContext _localctx = new Rule_StartContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_rule_Start);
		try {
			setState(422);
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
				setState(420);
				rule_Condition(0);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(421);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Date_Time_When(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Date_Time_WhenContext rule_Date_Time_When() throws RecognitionException {
		Rule_Date_Time_WhenContext _localctx = new Rule_Date_Time_WhenContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_rule_Date_Time_When);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			match(T__1);
			setState(425);
			match(COLON);
			setState(426);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Occurrence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_OccurrenceContext rule_Occurrence() throws RecognitionException {
		Rule_OccurrenceContext _localctx = new Rule_OccurrenceContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_rule_Occurrence);
		try {
			setState(434);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(428);
				match(NUMBER);
				setState(429);
				match(UNIT_OF_TIME);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(430);
				match(NUMBER);
				setState(431);
				match(OCCURRENCES);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(432);
				rule_Date_Time();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(433);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Date_Time(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Date_TimeContext rule_Date_Time() throws RecognitionException {
		Rule_Date_TimeContext _localctx = new Rule_Date_TimeContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_rule_Date_Time);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			rule_Date();
			setState(438);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NUMBER) {
				{
				setState(437);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Date(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_DateContext rule_Date() throws RecognitionException {
		Rule_DateContext _localctx = new Rule_DateContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_rule_Date);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			match(NUMBER);
			setState(441);
			match(FSLASH);
			setState(442);
			match(NUMBER);
			setState(443);
			match(FSLASH);
			setState(444);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Time(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_TimeContext rule_Time() throws RecognitionException {
		Rule_TimeContext _localctx = new Rule_TimeContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_rule_Time);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(446);
			match(NUMBER);
			setState(447);
			match(COLON);
			setState(448);
			match(NUMBER);
			setState(451);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(449);
				match(COLON);
				setState(450);
				match(NUMBER);
				}
			}

			setState(454);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TIME_ZONE) {
				{
				setState(453);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Condition(this);
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
		int _startState = 86;
		enterRecursionRule(_localctx, 86, RULE_rule_Condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(464);
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
				setState(457);
				rule_Constraint();
				}
				break;
			case LPAREN:
				{
				setState(458);
				match(LPAREN);
				setState(459);
				rule_Condition(0);
				setState(460);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				setState(462);
				match(NOT);
				setState(463);
				rule_Condition(1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(472);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Rule_ConditionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_rule_Condition);
					setState(466);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(467);
					rule_expr_op();
					setState(468);
					rule_Condition(4);
					}
					} 
				}
				setState(474);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_expr_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_expr_opContext rule_expr_op() throws RecognitionException {
		Rule_expr_opContext _localctx = new Rule_expr_opContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_rule_expr_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Constraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_ConstraintContext rule_Constraint() throws RecognitionException {
		Rule_ConstraintContext _localctx = new Rule_ConstraintContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_rule_Constraint);
		try {
			setState(491);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(477);
				rule_left_element();
				setState(478);
				rule_relational_op_func();
				setState(479);
				rule_right_element();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(481);
				rule_target_element();
				setState(482);
				rule_between_op();
				setState(483);
				rule_left_element();
				setState(484);
				match(AND);
				setState(485);
				rule_right_element();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(487);
				rule_target_element();
				setState(488);
				rule_is_or_is_not();
				setState(489);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_left_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_left_elementContext rule_left_element() throws RecognitionException {
		Rule_left_elementContext _localctx = new Rule_left_elementContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_rule_left_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(493);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_right_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_right_elementContext rule_right_element() throws RecognitionException {
		Rule_right_elementContext _localctx = new Rule_right_elementContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_rule_right_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(495);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_target_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_target_elementContext rule_target_element() throws RecognitionException {
		Rule_target_elementContext _localctx = new Rule_target_elementContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_rule_target_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(497);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_relational_op_func(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_relational_op_funcContext rule_relational_op_func() throws RecognitionException {
		Rule_relational_op_funcContext _localctx = new Rule_relational_op_funcContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_rule_relational_op_func);
		try {
			setState(507);
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
				setState(499);
				rule_relational_op();
				}
				break;
			case OP:
				enterOuterAlt(_localctx, 2);
				{
				setState(500);
				match(OP);
				setState(501);
				match(LPAREN);
				setState(502);
				rule_relational_op();
				setState(503);
				match(COMMA);
				setState(504);
				match(NUMBER);
				setState(505);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_relational_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_relational_opContext rule_relational_op() throws RecognitionException {
		Rule_relational_opContext _localctx = new Rule_relational_opContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_rule_relational_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_between_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_between_opContext rule_between_op() throws RecognitionException {
		Rule_between_opContext _localctx = new Rule_between_opContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_rule_between_op);
		try {
			setState(518);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(511);
				match(BETWEEN);
				}
				break;
			case OP:
				enterOuterAlt(_localctx, 2);
				{
				setState(512);
				match(OP);
				setState(513);
				match(LPAREN);
				setState(514);
				match(BETWEEN);
				setState(515);
				match(COMMA);
				setState(516);
				match(NUMBER);
				setState(517);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_is_or_is_not(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_is_or_is_notContext rule_is_or_is_not() throws RecognitionException {
		Rule_is_or_is_notContext _localctx = new Rule_is_or_is_notContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_rule_is_or_is_not);
		try {
			setState(523);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(520);
				match(IS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(521);
				match(IS);
				setState(522);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRuel_Push(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ruel_PushContext ruel_Push() throws RecognitionException {
		Ruel_PushContext _localctx = new Ruel_PushContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_ruel_Push);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(525);
			match(PUSH);
			setState(526);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_callback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_callbackContext rule_callback() throws RecognitionException {
		Rule_callbackContext _localctx = new Rule_callbackContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_rule_callback);
		try {
			setState(530);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(528);
				rule_http_calback();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(529);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_http_calback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_http_calbackContext rule_http_calback() throws RecognitionException {
		Rule_http_calbackContext _localctx = new Rule_http_calbackContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_rule_http_calback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			match(METHOD);
			setState(533);
			match(EQ);
			setState(534);
			match(HTTPPOST);
			setState(535);
			match(URL);
			setState(536);
			match(EQ);
			setState(537);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_fcm_calback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_fcm_calbackContext rule_fcm_calback() throws RecognitionException {
		Rule_fcm_calbackContext _localctx = new Rule_fcm_calbackContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_rule_fcm_calback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(539);
			match(METHOD);
			setState(540);
			match(EQ);
			setState(541);
			match(FCM);
			setState(544);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TOPIC:
				{
				setState(542);
				rule_fcm_topic();
				}
				break;
			case TOKEN:
				{
				setState(543);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_fcm_topic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_fcm_topicContext rule_fcm_topic() throws RecognitionException {
		Rule_fcm_topicContext _localctx = new Rule_fcm_topicContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_rule_fcm_topic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			match(TOPIC);
			setState(547);
			match(EQ);
			setState(548);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_fcm_token(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_fcm_tokenContext rule_fcm_token() throws RecognitionException {
		Rule_fcm_tokenContext _localctx = new Rule_fcm_tokenContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_rule_fcm_token);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(550);
			match(TOKEN);
			setState(551);
			match(EQ);
			setState(552);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_callback_url(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_callback_urlContext rule_callback_url() throws RecognitionException {
		Rule_callback_urlContext _localctx = new Rule_callback_urlContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_rule_callback_url);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Define(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_DefineContext rule_Define() throws RecognitionException {
		Rule_DefineContext _localctx = new Rule_DefineContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_rule_Define);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(556);
			match(DEFINE);
			setState(557);
			rule_Define_Context_Entity();
			setState(560);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(558);
				match(COMMA);
				setState(559);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Define_Context_Entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Define_Context_EntityContext rule_Define_Context_Entity() throws RecognitionException {
		Rule_Define_Context_EntityContext _localctx = new Rule_Define_Context_EntityContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_rule_Define_Context_Entity);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(562);
			rule_context_entity();
			setState(567);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(563);
					match(COMMA);
					setState(564);
					rule_context_entity();
					}
					} 
				}
				setState(569);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_context_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_context_entityContext rule_context_entity() throws RecognitionException {
		Rule_context_entityContext _localctx = new Rule_context_entityContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_rule_context_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(570);
			match(ENTITY);
			setState(571);
			rule_entity_id();
			setState(572);
			match(IS_FROM);
			setState(573);
			rule_entity_type();
			setState(576);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(574);
				match(WHERE);
				setState(575);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_entity_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_entity_typeContext rule_entity_type() throws RecognitionException {
		Rule_entity_typeContext _localctx = new Rule_entity_typeContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_rule_entity_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(580);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(578);
				match(ID);
				setState(579);
				match(COLON);
				}
				break;
			}
			setState(582);
			match(ID);
			setState(585);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(583);
				match(DOT);
				setState(584);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_Define_Context_Function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_Define_Context_FunctionContext rule_Define_Context_Function() throws RecognitionException {
		Rule_Define_Context_FunctionContext _localctx = new Rule_Define_Context_FunctionContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_rule_Define_Context_Function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(587);
			rule_context_function();
			setState(592);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(588);
				match(COMMA);
				setState(589);
				rule_context_function();
				}
				}
				setState(594);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_context_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_context_functionContext rule_context_function() throws RecognitionException {
		Rule_context_functionContext _localctx = new Rule_context_functionContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_rule_context_function);
		try {
			setState(597);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				setState(595);
				rule_aFunction();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(596);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_aFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_aFunctionContext rule_aFunction() throws RecognitionException {
		Rule_aFunctionContext _localctx = new Rule_aFunctionContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_rule_aFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(599);
			match(T__2);
			setState(600);
			rule_function_id();
			setState(601);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_sFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_sFunctionContext rule_sFunction() throws RecognitionException {
		Rule_sFunctionContext _localctx = new Rule_sFunctionContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_rule_sFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			match(T__3);
			setState(604);
			rule_function_id();
			setState(605);
			rule_is_on();
			{
			setState(606);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_is_on(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_is_onContext rule_is_on() throws RecognitionException {
		Rule_is_onContext _localctx = new Rule_is_onContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_rule_is_on);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(608);
			match(T__4);
			setState(609);
			rule_is_on_entity();
			setState(614);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(610);
				match(COMMA);
				setState(611);
				rule_is_on_entity();
				}
				}
				setState(616);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_is_on_entity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_is_on_entityContext rule_is_on_entity() throws RecognitionException {
		Rule_is_on_entityContext _localctx = new Rule_is_on_entityContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_rule_is_on_entity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617);
			rule_entity_type();
			setState(618);
			match(AS);
			setState(619);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitCst_situation_def_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Cst_situation_def_ruleContext cst_situation_def_rule() throws RecognitionException {
		Cst_situation_def_ruleContext _localctx = new Cst_situation_def_ruleContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_cst_situation_def_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			match(T__5);
			setState(622);
			rule_single_situatuin();
			setState(627);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(623);
				match(COMMA);
				setState(624);
				rule_single_situatuin();
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_single_situatuin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_single_situatuinContext rule_single_situatuin() throws RecognitionException {
		Rule_single_situatuinContext _localctx = new Rule_single_situatuinContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_rule_single_situatuin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			match(STRING);
			setState(633);
			match(COLON);
			setState(634);
			match(T__5);
			setState(635);
			rule_situation_pair();
			setState(640);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(636);
				match(COMMA);
				setState(637);
				rule_situation_pair();
				}
				}
				setState(642);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(643);
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
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public Situation_pair_valuesContext situation_pair_values() {
			return getRuleContext(Situation_pair_valuesContext.class,0);
		}
		public Rule_situation_pairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule_situation_pair; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_situation_pair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_pairContext rule_situation_pair() throws RecognitionException {
		Rule_situation_pairContext _localctx = new Rule_situation_pairContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_rule_situation_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(645);
			rule_situation_attributes();
			setState(646);
			match(COLON);
			setState(647);
			match(T__5);
			setState(648);
			situation_pair_values();
			setState(649);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_situation_attributes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_attributesContext rule_situation_attributes() throws RecognitionException {
		Rule_situation_attributesContext _localctx = new Rule_situation_attributesContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_rule_situation_attributes);
		int _la;
		try {
			setState(662);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(651);
				rule_situation_attribute_name();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(652);
				match(T__7);
				setState(653);
				rule_situation_attribute_name();
				setState(656); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(654);
					match(COMMA);
					setState(655);
					rule_situation_attribute_name();
					}
					}
					setState(658); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==COMMA );
				setState(660);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_situation_attribute_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_attribute_nameContext rule_situation_attribute_name() throws RecognitionException {
		Rule_situation_attribute_nameContext _localctx = new Rule_situation_attribute_nameContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_rule_situation_attribute_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(664);
			match(ID);
			setState(669);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(665);
				match(DOT);
				setState(666);
				match(ID);
				}
				}
				setState(671);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitSituation_pair_values(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_pair_valuesContext situation_pair_values() throws RecognitionException {
		Situation_pair_valuesContext _localctx = new Situation_pair_valuesContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_situation_pair_values);
		try {
			setState(680);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(672);
				situation_range_values();
				setState(673);
				match(COMMA);
				setState(674);
				situation_weight();
				}
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(676);
				situation_weight();
				setState(677);
				match(COMMA);
				setState(678);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitSituation_weight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_weightContext situation_weight() throws RecognitionException {
		Situation_weightContext _localctx = new Situation_weightContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_situation_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(682);
			match(T__9);
			setState(683);
			match(COLON);
			setState(684);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitSituation_range_values(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_range_valuesContext situation_range_values() throws RecognitionException {
		Situation_range_valuesContext _localctx = new Situation_range_valuesContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_situation_range_values);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			match(T__10);
			setState(687);
			match(COLON);
			setState(688);
			match(T__7);
			setState(689);
			situation_pair_values_item();
			setState(694);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(690);
				match(COMMA);
				setState(691);
				situation_pair_values_item();
				}
				}
				setState(696);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(697);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitSituation_pair_values_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Situation_pair_values_itemContext situation_pair_values_item() throws RecognitionException {
		Situation_pair_values_itemContext _localctx = new Situation_pair_values_itemContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_situation_pair_values_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			match(T__5);
			setState(708);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				{
				{
				setState(700);
				rule_situation_belief();
				setState(701);
				match(COMMA);
				setState(702);
				rule_situation_value();
				}
				}
				break;
			case T__12:
				{
				{
				setState(704);
				rule_situation_value();
				setState(705);
				match(COMMA);
				setState(706);
				rule_situation_belief();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(710);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_situation_belief(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_beliefContext rule_situation_belief() throws RecognitionException {
		Rule_situation_beliefContext _localctx = new Rule_situation_beliefContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_rule_situation_belief);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(712);
			match(T__11);
			setState(713);
			match(COLON);
			setState(714);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_situation_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_situation_valueContext rule_situation_value() throws RecognitionException {
		Rule_situation_valueContext _localctx = new Rule_situation_valueContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_rule_situation_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			match(T__12);
			setState(717);
			match(COLON);
			setState(721);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(718);
				rule_region_value();
				}
				break;
			case 2:
				{
				setState(719);
				rule_discrete_value();
				}
				break;
			case 3:
				{
				setState(720);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_discrete_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_discrete_valueContext rule_discrete_value() throws RecognitionException {
		Rule_discrete_valueContext _localctx = new Rule_discrete_valueContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_rule_discrete_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(723);
			match(T__7);
			setState(724);
			discrete_value();
			setState(729);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COLON) {
				{
				{
				setState(725);
				match(COLON);
				setState(726);
				discrete_value();
				}
				}
				setState(731);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(732);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitDiscrete_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Discrete_valueContext discrete_value() throws RecognitionException {
		Discrete_valueContext _localctx = new Discrete_valueContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_discrete_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(734);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_region_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_region_valueContext rule_region_value() throws RecognitionException {
		Rule_region_valueContext _localctx = new Rule_region_valueContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_rule_region_value);
		try {
			setState(740);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(736);
				region_value_inclusive();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(737);
				region_value_left_inclusive();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(738);
				region_value_right_inclusive();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(739);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRegion_value_inclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_inclusiveContext region_value_inclusive() throws RecognitionException {
		Region_value_inclusiveContext _localctx = new Region_value_inclusiveContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_region_value_inclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(742);
			match(T__7);
			setState(743);
			region_value_value();
			setState(744);
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
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public Region_value_left_inclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_left_inclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRegion_value_left_inclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_left_inclusiveContext region_value_left_inclusive() throws RecognitionException {
		Region_value_left_inclusiveContext _localctx = new Region_value_left_inclusiveContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_region_value_left_inclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(746);
			match(T__7);
			setState(747);
			region_value_value();
			setState(748);
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
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public Region_value_valueContext region_value_value() {
			return getRuleContext(Region_value_valueContext.class,0);
		}
		public Region_value_right_inclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_right_inclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRegion_value_right_inclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_right_inclusiveContext region_value_right_inclusive() throws RecognitionException {
		Region_value_right_inclusiveContext _localctx = new Region_value_right_inclusiveContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_region_value_right_inclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			match(LPAREN);
			setState(751);
			region_value_value();
			setState(752);
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
		public TerminalNode LPAREN() { return getToken(CdqlParser.LPAREN, 0); }
		public Region_value_valueContext region_value_value() {
			return getRuleContext(Region_value_valueContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(CdqlParser.RPAREN, 0); }
		public Region_value_exclusiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_region_value_exclusive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRegion_value_exclusive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_exclusiveContext region_value_exclusive() throws RecognitionException {
		Region_value_exclusiveContext _localctx = new Region_value_exclusiveContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_region_value_exclusive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(754);
			match(LPAREN);
			setState(755);
			region_value_value();
			setState(756);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRegion_value_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Region_value_valueContext region_value_value() throws RecognitionException {
		Region_value_valueContext _localctx = new Region_value_valueContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_region_value_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(758);
			match(NUMBER);
			setState(759);
			match(T__13);
			setState(760);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_entity_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_entity_idContext rule_entity_id() throws RecognitionException {
		Rule_entity_idContext _localctx = new Rule_entity_idContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_rule_entity_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(762);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_function_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_function_idContext rule_function_id() throws RecognitionException {
		Rule_function_idContext _localctx = new Rule_function_idContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_rule_function_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(764);
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
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public PortContext port() {
			return getRuleContext(PortContext.class,0);
		}
		public TerminalNode FSLASH() { return getToken(CdqlParser.FSLASH, 0); }
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitRule_url(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule_urlContext rule_url() throws RecognitionException {
		Rule_urlContext _localctx = new Rule_urlContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_rule_url);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(766);
			authority();
			setState(767);
			match(T__14);
			setState(768);
			host();
			setState(771);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(769);
				match(COLON);
				setState(770);
				port();
				}
			}

			setState(775);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FSLASH) {
				{
				setState(773);
				match(FSLASH);
				setState(774);
				path();
				}
			}

			setState(779);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(777);
				match(T__15);
				setState(778);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitAuthority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AuthorityContext authority() throws RecognitionException {
		AuthorityContext _localctx = new AuthorityContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_authority);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(781);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitHost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostContext host() throws RecognitionException {
		HostContext _localctx = new HostContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_host);
		try {
			setState(785);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(783);
				hostname();
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(784);
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
		public List<TerminalNode> DOT() { return getTokens(CdqlParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CdqlParser.DOT, i);
		}
		public HostnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hostname; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitHostname(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostnameContext hostname() throws RecognitionException {
		HostnameContext _localctx = new HostnameContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_hostname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(787);
			match(ID);
			setState(792);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(788);
				match(DOT);
				setState(789);
				match(ID);
				}
				}
				setState(794);
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
		public List<TerminalNode> DOT() { return getTokens(CdqlParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CdqlParser.DOT, i);
		}
		public HostnumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hostnumber; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitHostnumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HostnumberContext hostnumber() throws RecognitionException {
		HostnumberContext _localctx = new HostnumberContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_hostnumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(795);
			match(INT);
			setState(796);
			match(DOT);
			setState(797);
			match(INT);
			setState(798);
			match(DOT);
			setState(799);
			match(INT);
			setState(800);
			match(DOT);
			setState(801);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitSearch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchContext search() throws RecognitionException {
		SearchContext _localctx = new SearchContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_search);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(803);
			searchparameter();
			setState(808);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(804);
				match(T__16);
				setState(805);
				searchparameter();
				}
				}
				setState(810);
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
		public TerminalNode EQ() { return getToken(CdqlParser.EQ, 0); }
		public TerminalNode INT() { return getToken(CdqlParser.INT, 0); }
		public TerminalNode HEX() { return getToken(CdqlParser.HEX, 0); }
		public SearchparameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searchparameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitSearchparameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchparameterContext searchparameter() throws RecognitionException {
		SearchparameterContext _localctx = new SearchparameterContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_searchparameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(811);
			match(ID);
			setState(814);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EQ) {
				{
				setState(812);
				match(EQ);
				setState(813);
				_la = _input.LA(1);
				if ( !(((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (ID - 81)) | (1L << (INT - 81)) | (1L << (HEX - 81)))) != 0)) ) {
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitPort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortContext port() throws RecognitionException {
		PortContext _localctx = new PortContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_port);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
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
		public List<TerminalNode> FSLASH() { return getTokens(CdqlParser.FSLASH); }
		public TerminalNode FSLASH(int i) {
			return getToken(CdqlParser.FSLASH, i);
		}
		public PathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_path; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitPath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PathContext path() throws RecognitionException {
		PathContext _localctx = new PathContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_path);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(820);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				{
				setState(818);
				normal_path();
				}
				break;
			case T__5:
				{
				setState(819);
				path_param();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(829);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FSLASH) {
				{
				{
				setState(822);
				match(FSLASH);
				setState(825);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ID:
					{
					setState(823);
					normal_path();
					}
					break;
				case T__5:
					{
					setState(824);
					path_param();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(831);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitNormal_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Normal_pathContext normal_path() throws RecognitionException {
		Normal_pathContext _localctx = new Normal_pathContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_normal_path);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(832);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitPath_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Path_paramContext path_param() throws RecognitionException {
		Path_paramContext _localctx = new Path_paramContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_path_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(834);
			match(T__5);
			setState(835);
			match(ID);
			setState(836);
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
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitJson(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonContext json() throws RecognitionException {
		JsonContext _localctx = new JsonContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_json);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(838);
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
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public ObjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obj; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitObj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjContext obj() throws RecognitionException {
		ObjContext _localctx = new ObjContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_obj);
		int _la;
		try {
			setState(853);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(840);
				match(T__5);
				setState(841);
				pair();
				setState(846);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(842);
					match(COMMA);
					setState(843);
					pair();
					}
					}
					setState(848);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(849);
				match(T__6);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(851);
				match(T__5);
				setState(852);
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
		public TerminalNode COLON() { return getToken(CdqlParser.COLON, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(855);
			match(STRING);
			setState(856);
			match(COLON);
			setState(857);
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
		public List<TerminalNode> COMMA() { return getTokens(CdqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CdqlParser.COMMA, i);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_array);
		try {
			int _alt;
			setState(872);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(859);
				match(T__7);
				setState(860);
				value();
				setState(865);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
				while ( _alt!=1 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						{
						setState(861);
						match(COMMA);
						setState(862);
						value();
						}
						} 
					}
					setState(867);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
				}
				setState(868);
				match(T__8);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(870);
				match(T__7);
				setState(871);
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
		public TerminalNode NULL() { return getToken(CdqlParser.NULL, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CdqlVisitor ) return ((CdqlVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_value);
		try {
			setState(881);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(874);
				match(STRING);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(875);
				match(NUMBER);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(876);
				obj();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(877);
				array();
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 5);
				{
				setState(878);
				match(T__17);
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 6);
				{
				setState(879);
				match(T__18);
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 7);
				{
				setState(880);
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
		case 43:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3X\u0376\4\2\t\2\4"+
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
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\3\2\5\2\u00da\n\2\3\2\3\2\5\2\u00de\n\2\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\5\3\u00e7\n\3\3\4\3\4\3\5\3\5\3\5\3\5\5\5\u00ef\n\5\5\5\u00f1\n"+
		"\5\3\5\3\5\5\5\u00f5\n\5\3\5\5\5\u00f8\n\5\3\5\5\5\u00fb\n\5\3\6\3\6\3"+
		"\6\5\6\u0100\n\6\3\6\5\6\u0103\n\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\7\24\u0138\n\24"+
		"\f\24\16\24\u013b\13\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3"+
		"\27\3\27\5\27\u0148\n\27\3\27\3\27\3\27\5\27\u014d\n\27\7\27\u014f\n\27"+
		"\f\27\16\27\u0152\13\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\5\30\u015b"+
		"\n\30\3\31\3\31\3\32\3\32\3\32\6\32\u0162\n\32\r\32\16\32\u0163\3\33\3"+
		"\33\3\34\3\34\3\35\3\35\3\35\3\35\3\35\7\35\u016f\n\35\f\35\16\35\u0172"+
		"\13\35\3\35\3\35\3\35\3\36\3\36\7\36\u0179\n\36\f\36\16\36\u017c\13\36"+
		"\3\37\3\37\3 \3 \5 \u0182\n \3!\3!\3!\3!\3\"\3\"\3\"\5\"\u018b\n\"\3#"+
		"\3#\3#\3#\5#\u0191\n#\3$\3$\3$\5$\u0196\n$\3%\3%\3%\3&\3&\3&\3&\3&\3&"+
		"\5&\u01a1\n&\3&\3&\5&\u01a5\n&\3\'\3\'\5\'\u01a9\n\'\3(\3(\3(\3(\3)\3"+
		")\3)\3)\3)\3)\5)\u01b5\n)\3*\3*\5*\u01b9\n*\3+\3+\3+\3+\3+\3+\3,\3,\3"+
		",\3,\3,\5,\u01c6\n,\3,\5,\u01c9\n,\3-\3-\3-\3-\3-\3-\3-\3-\5-\u01d3\n"+
		"-\3-\3-\3-\3-\7-\u01d9\n-\f-\16-\u01dc\13-\3.\3.\3/\3/\3/\3/\3/\3/\3/"+
		"\3/\3/\3/\3/\3/\3/\3/\5/\u01ee\n/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3"+
		"\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u01fe\n\63\3\64\3\64\3\65\3\65"+
		"\3\65\3\65\3\65\3\65\3\65\5\65\u0209\n\65\3\66\3\66\3\66\5\66\u020e\n"+
		"\66\3\67\3\67\3\67\38\38\58\u0215\n8\39\39\39\39\39\39\39\3:\3:\3:\3:"+
		"\3:\5:\u0223\n:\3;\3;\3;\3;\3<\3<\3<\3<\3=\3=\3>\3>\3>\3>\5>\u0233\n>"+
		"\3?\3?\3?\7?\u0238\n?\f?\16?\u023b\13?\3@\3@\3@\3@\3@\3@\5@\u0243\n@\3"+
		"A\3A\5A\u0247\nA\3A\3A\3A\5A\u024c\nA\3B\3B\3B\7B\u0251\nB\fB\16B\u0254"+
		"\13B\3C\3C\5C\u0258\nC\3D\3D\3D\3D\3E\3E\3E\3E\3E\3F\3F\3F\3F\7F\u0267"+
		"\nF\fF\16F\u026a\13F\3G\3G\3G\3G\3H\3H\3H\3H\7H\u0274\nH\fH\16H\u0277"+
		"\13H\3H\3H\3I\3I\3I\3I\3I\3I\7I\u0281\nI\fI\16I\u0284\13I\3I\3I\3J\3J"+
		"\3J\3J\3J\3J\3K\3K\3K\3K\3K\6K\u0293\nK\rK\16K\u0294\3K\3K\5K\u0299\n"+
		"K\3L\3L\3L\7L\u029e\nL\fL\16L\u02a1\13L\3M\3M\3M\3M\3M\3M\3M\3M\5M\u02ab"+
		"\nM\3N\3N\3N\3N\3O\3O\3O\3O\3O\3O\7O\u02b7\nO\fO\16O\u02ba\13O\3O\3O\3"+
		"P\3P\3P\3P\3P\3P\3P\3P\3P\5P\u02c7\nP\3P\3P\3Q\3Q\3Q\3Q\3R\3R\3R\3R\3"+
		"R\5R\u02d4\nR\3S\3S\3S\3S\7S\u02da\nS\fS\16S\u02dd\13S\3S\3S\3T\3T\3U"+
		"\3U\3U\3U\5U\u02e7\nU\3V\3V\3V\3V\3W\3W\3W\3W\3X\3X\3X\3X\3Y\3Y\3Y\3Y"+
		"\3Z\3Z\3Z\3Z\3[\3[\3\\\3\\\3]\3]\3]\3]\3]\5]\u0306\n]\3]\3]\5]\u030a\n"+
		"]\3]\3]\5]\u030e\n]\3^\3^\3_\3_\5_\u0314\n_\3`\3`\3`\7`\u0319\n`\f`\16"+
		"`\u031c\13`\3a\3a\3a\3a\3a\3a\3a\3a\3b\3b\3b\7b\u0329\nb\fb\16b\u032c"+
		"\13b\3c\3c\3c\5c\u0331\nc\3d\3d\3e\3e\5e\u0337\ne\3e\3e\3e\5e\u033c\n"+
		"e\7e\u033e\ne\fe\16e\u0341\13e\3f\3f\3g\3g\3g\3g\3h\3h\3i\3i\3i\3i\7i"+
		"\u034f\ni\fi\16i\u0352\13i\3i\3i\3i\3i\5i\u0358\ni\3j\3j\3j\3j\3k\3k\3"+
		"k\3k\7k\u0362\nk\fk\16k\u0365\13k\3k\3k\3k\3k\5k\u036b\nk\3l\3l\3l\3l"+
		"\3l\3l\3l\5l\u0374\nl\3l\3\u0363\3Xm\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080"+
		"\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098"+
		"\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0"+
		"\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8"+
		"\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6\2\5\3\2/\62\4\2\64\65\67<\4"+
		"\2SSWX\2\u0365\2\u00d9\3\2\2\2\4\u00e6\3\2\2\2\6\u00e8\3\2\2\2\b\u00f0"+
		"\3\2\2\2\n\u00fc\3\2\2\2\f\u0104\3\2\2\2\16\u0108\3\2\2\2\20\u010c\3\2"+
		"\2\2\22\u0113\3\2\2\2\24\u0115\3\2\2\2\26\u0119\3\2\2\2\30\u011d\3\2\2"+
		"\2\32\u011f\3\2\2\2\34\u0122\3\2\2\2\36\u0125\3\2\2\2 \u0128\3\2\2\2\""+
		"\u012c\3\2\2\2$\u0130\3\2\2\2&\u0134\3\2\2\2(\u013c\3\2\2\2*\u0141\3\2"+
		"\2\2,\u0144\3\2\2\2.\u015a\3\2\2\2\60\u015c\3\2\2\2\62\u015e\3\2\2\2\64"+
		"\u0165\3\2\2\2\66\u0167\3\2\2\28\u0169\3\2\2\2:\u017a\3\2\2\2<\u017d\3"+
		"\2\2\2>\u0181\3\2\2\2@\u0183\3\2\2\2B\u0187\3\2\2\2D\u0190\3\2\2\2F\u0195"+
		"\3\2\2\2H\u0197\3\2\2\2J\u01a4\3\2\2\2L\u01a8\3\2\2\2N\u01aa\3\2\2\2P"+
		"\u01b4\3\2\2\2R\u01b6\3\2\2\2T\u01ba\3\2\2\2V\u01c0\3\2\2\2X\u01d2\3\2"+
		"\2\2Z\u01dd\3\2\2\2\\\u01ed\3\2\2\2^\u01ef\3\2\2\2`\u01f1\3\2\2\2b\u01f3"+
		"\3\2\2\2d\u01fd\3\2\2\2f\u01ff\3\2\2\2h\u0208\3\2\2\2j\u020d\3\2\2\2l"+
		"\u020f\3\2\2\2n\u0214\3\2\2\2p\u0216\3\2\2\2r\u021d\3\2\2\2t\u0224\3\2"+
		"\2\2v\u0228\3\2\2\2x\u022c\3\2\2\2z\u022e\3\2\2\2|\u0234\3\2\2\2~\u023c"+
		"\3\2\2\2\u0080\u0246\3\2\2\2\u0082\u024d\3\2\2\2\u0084\u0257\3\2\2\2\u0086"+
		"\u0259\3\2\2\2\u0088\u025d\3\2\2\2\u008a\u0262\3\2\2\2\u008c\u026b\3\2"+
		"\2\2\u008e\u026f\3\2\2\2\u0090\u027a\3\2\2\2\u0092\u0287\3\2\2\2\u0094"+
		"\u0298\3\2\2\2\u0096\u029a\3\2\2\2\u0098\u02aa\3\2\2\2\u009a\u02ac\3\2"+
		"\2\2\u009c\u02b0\3\2\2\2\u009e\u02bd\3\2\2\2\u00a0\u02ca\3\2\2\2\u00a2"+
		"\u02ce\3\2\2\2\u00a4\u02d5\3\2\2\2\u00a6\u02e0\3\2\2\2\u00a8\u02e6\3\2"+
		"\2\2\u00aa\u02e8\3\2\2\2\u00ac\u02ec\3\2\2\2\u00ae\u02f0\3\2\2\2\u00b0"+
		"\u02f4\3\2\2\2\u00b2\u02f8\3\2\2\2\u00b4\u02fc\3\2\2\2\u00b6\u02fe\3\2"+
		"\2\2\u00b8\u0300\3\2\2\2\u00ba\u030f\3\2\2\2\u00bc\u0313\3\2\2\2\u00be"+
		"\u0315\3\2\2\2\u00c0\u031d\3\2\2\2\u00c2\u0325\3\2\2\2\u00c4\u032d\3\2"+
		"\2\2\u00c6\u0332\3\2\2\2\u00c8\u0336\3\2\2\2\u00ca\u0342\3\2\2\2\u00cc"+
		"\u0344\3\2\2\2\u00ce\u0348\3\2\2\2\u00d0\u0357\3\2\2\2\u00d2\u0359\3\2"+
		"\2\2\u00d4\u036a\3\2\2\2\u00d6\u0373\3\2\2\2\u00d8\u00da\5&\24\2\u00d9"+
		"\u00d8\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00dd\3\2\2\2\u00db\u00de\5\4"+
		"\3\2\u00dc\u00de\5\6\4\2\u00dd\u00db\3\2\2\2\u00dd\u00dc\3\2\2\2\u00de"+
		"\3\3\2\2\2\u00df\u00e7\5\n\6\2\u00e0\u00e7\5\16\b\2\u00e1\u00e7\5\22\n"+
		"\2\u00e2\u00e7\5\20\t\2\u00e3\u00e7\5\26\f\2\u00e4\u00e7\5\24\13\2\u00e5"+
		"\u00e7\3\2\2\2\u00e6\u00df\3\2\2\2\u00e6\u00e0\3\2\2\2\u00e6\u00e1\3\2"+
		"\2\2\u00e6\u00e2\3\2\2\2\u00e6\u00e3\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6"+
		"\u00e5\3\2\2\2\u00e7\5\3\2\2\2\u00e8\u00e9\5\b\5\2\u00e9\7\3\2\2\2\u00ea"+
		"\u00f1\5*\26\2\u00eb\u00ec\5l\67\2\u00ec\u00ee\5H%\2\u00ed\u00ef\5J&\2"+
		"\u00ee\u00ed\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f1\3\2\2\2\u00f0\u00ea"+
		"\3\2\2\2\u00f0\u00eb\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f4\5z>\2\u00f3"+
		"\u00f5\5\34\17\2\u00f4\u00f3\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f7\3"+
		"\2\2\2\u00f6\u00f8\5\36\20\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8"+
		"\u00fa\3\2\2\2\u00f9\u00fb\5\32\16\2\u00fa\u00f9\3\2\2\2\u00fa\u00fb\3"+
		"\2\2\2\u00fb\t\3\2\2\2\u00fc\u00ff\7\31\2\2\u00fd\u0100\5\u0088E\2\u00fe"+
		"\u0100\5\u0086D\2\u00ff\u00fd\3\2\2\2\u00ff\u00fe\3\2\2\2\u0100\u0102"+
		"\3\2\2\2\u0101\u0103\5\f\7\2\u0102\u0101\3\2\2\2\u0102\u0103\3\2\2\2\u0103"+
		"\13\3\2\2\2\u0104\u0105\7\32\2\2\u0105\u0106\7\27\2\2\u0106\u0107\5\30"+
		"\r\2\u0107\r\3\2\2\2\u0108\u0109\7\31\2\2\u0109\u010a\7\27\2\2\u010a\u010b"+
		"\5\30\r\2\u010b\17\3\2\2\2\u010c\u010d\7\33\2\2\u010d\u010e\7\27\2\2\u010e"+
		"\u010f\5\30\r\2\u010f\u0110\7\32\2\2\u0110\u0111\7\26\2\2\u0111\u0112"+
		"\5\30\r\2\u0112\21\3\2\2\2\u0113\u0114\7\3\2\2\u0114\23\3\2\2\2\u0115"+
		"\u0116\7\34\2\2\u0116\u0117\7\27\2\2\u0117\u0118\5\30\r\2\u0118\25\3\2"+
		"\2\2\u0119\u011a\7\34\2\2\u011a\u011b\7\30\2\2\u011b\u011c\5\u00b6\\\2"+
		"\u011c\27\3\2\2\2\u011d\u011e\7S\2\2\u011e\31\3\2\2\2\u011f\u0120\7\32"+
		"\2\2\u0120\u0121\5\"\22\2\u0121\33\3\2\2\2\u0122\u0123\7\32\2\2\u0123"+
		"\u0124\5 \21\2\u0124\35\3\2\2\2\u0125\u0126\7\32\2\2\u0126\u0127\5$\23"+
		"\2\u0127\37\3\2\2\2\u0128\u0129\7N\2\2\u0129\u012a\7H\2\2\u012a\u012b"+
		"\5\u00d0i\2\u012b!\3\2\2\2\u012c\u012d\7O\2\2\u012d\u012e\7H\2\2\u012e"+
		"\u012f\5\u00d0i\2\u012f#\3\2\2\2\u0130\u0131\7P\2\2\u0131\u0132\7H\2\2"+
		"\u0132\u0133\5\u00d0i\2\u0133%\3\2\2\2\u0134\u0139\5(\25\2\u0135\u0136"+
		"\7,\2\2\u0136\u0138\5(\25\2\u0137\u0135\3\2\2\2\u0138\u013b\3\2\2\2\u0139"+
		"\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\'\3\2\2\2\u013b\u0139\3\2\2\2"+
		"\u013c\u013d\7?\2\2\u013d\u013e\7S\2\2\u013e\u013f\7H\2\2\u013f\u0140"+
		"\5\u00b8]\2\u0140)\3\2\2\2\u0141\u0142\7&\2\2\u0142\u0143\5,\27\2\u0143"+
		"+\3\2\2\2\u0144\u0147\7+\2\2\u0145\u0148\5.\30\2\u0146\u0148\5\60\31\2"+
		"\u0147\u0145\3\2\2\2\u0147\u0146\3\2\2\2\u0148\u0150\3\2\2\2\u0149\u014c"+
		"\7,\2\2\u014a\u014d\5.\30\2\u014b\u014d\5\60\31\2\u014c\u014a\3\2\2\2"+
		"\u014c\u014b\3\2\2\2\u014d\u014f\3\2\2\2\u014e\u0149\3\2\2\2\u014f\u0152"+
		"\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u0153\3\2\2\2\u0152"+
		"\u0150\3\2\2\2\u0153\u0154\7-\2\2\u0154-\3\2\2\2\u0155\u015b\5\62\32\2"+
		"\u0156\u0157\5\64\33\2\u0157\u0158\7.\2\2\u0158\u0159\7I\2\2\u0159\u015b"+
		"\3\2\2\2\u015a\u0155\3\2\2\2\u015a\u0156\3\2\2\2\u015b/\3\2\2\2\u015c"+
		"\u015d\58\35\2\u015d\61\3\2\2\2\u015e\u0161\5\64\33\2\u015f\u0160\7.\2"+
		"\2\u0160\u0162\5\66\34\2\u0161\u015f\3\2\2\2\u0162\u0163\3\2\2\2\u0163"+
		"\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164\63\3\2\2\2\u0165\u0166\7S\2\2"+
		"\u0166\65\3\2\2\2\u0167\u0168\7S\2\2\u0168\67\3\2\2\2\u0169\u016a\5<\37"+
		"\2\u016a\u016b\7+\2\2\u016b\u0170\5> \2\u016c\u016d\7,\2\2\u016d\u016f"+
		"\5> \2\u016e\u016c\3\2\2\2\u016f\u0172\3\2\2\2\u0170\u016e\3\2\2\2\u0170"+
		"\u0171\3\2\2\2\u0171\u0173\3\2\2\2\u0172\u0170\3\2\2\2\u0173\u0174\7-"+
		"\2\2\u0174\u0175\5:\36\2\u01759\3\2\2\2\u0176\u0177\7.\2\2\u0177\u0179"+
		"\7S\2\2\u0178\u0176\3\2\2\2\u0179\u017c\3\2\2\2\u017a\u0178\3\2\2\2\u017a"+
		"\u017b\3\2\2\2\u017b;\3\2\2\2\u017c\u017a\3\2\2\2\u017d\u017e\5B\"\2\u017e"+
		"=\3\2\2\2\u017f\u0182\5D#\2\u0180\u0182\5@!\2\u0181\u017f\3\2\2\2\u0181"+
		"\u0180\3\2\2\2\u0182?\3\2\2\2\u0183\u0184\7S\2\2\u0184\u0185\7H\2\2\u0185"+
		"\u0186\5D#\2\u0186A\3\2\2\2\u0187\u018a\7S\2\2\u0188\u0189\7.\2\2\u0189"+
		"\u018b\7S\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018bC\3\2\2\2\u018c"+
		"\u0191\5\64\33\2\u018d\u0191\5\62\32\2\u018e\u0191\58\35\2\u018f\u0191"+
		"\5F$\2\u0190\u018c\3\2\2\2\u0190\u018d\3\2\2\2\u0190\u018e\3\2\2\2\u0190"+
		"\u018f\3\2\2\2\u0191E\3\2\2\2\u0192\u0196\7V\2\2\u0193\u0196\7R\2\2\u0194"+
		"\u0196\5\u00ceh\2\u0195\u0192\3\2\2\2\u0195\u0193\3\2\2\2\u0195\u0194"+
		"\3\2\2\2\u0196G\3\2\2\2\u0197\u0198\7!\2\2\u0198\u0199\5L\'\2\u0199I\3"+
		"\2\2\2\u019a\u019b\7)\2\2\u019b\u019c\7V\2\2\u019c\u019d\7J\2\2\u019d"+
		"\u01a0\3\2\2\2\u019e\u019f\7*\2\2\u019f\u01a1\5P)\2\u01a0\u019e\3\2\2"+
		"\2\u01a0\u01a1\3\2\2\2\u01a1\u01a5\3\2\2\2\u01a2\u01a3\7*\2\2\u01a3\u01a5"+
		"\5P)\2\u01a4\u019a\3\2\2\2\u01a4\u01a2\3\2\2\2\u01a5K\3\2\2\2\u01a6\u01a9"+
		"\5X-\2\u01a7\u01a9\5N(\2\u01a8\u01a6\3\2\2\2\u01a8\u01a7\3\2\2\2\u01a9"+
		"M\3\2\2\2\u01aa\u01ab\7\4\2\2\u01ab\u01ac\7H\2\2\u01ac\u01ad\5R*\2\u01ad"+
		"O\3\2\2\2\u01ae\u01af\7V\2\2\u01af\u01b5\7J\2\2\u01b0\u01b1\7V\2\2\u01b1"+
		"\u01b5\7K\2\2\u01b2\u01b5\5R*\2\u01b3\u01b5\7#\2\2\u01b4\u01ae\3\2\2\2"+
		"\u01b4\u01b0\3\2\2\2\u01b4\u01b2\3\2\2\2\u01b4\u01b3\3\2\2\2\u01b5Q\3"+
		"\2\2\2\u01b6\u01b8\5T+\2\u01b7\u01b9\5V,\2\u01b8\u01b7\3\2\2\2\u01b8\u01b9"+
		"\3\2\2\2\u01b9S\3\2\2\2\u01ba\u01bb\7V\2\2\u01bb\u01bc\7L\2\2\u01bc\u01bd"+
		"\7V\2\2\u01bd\u01be\7L\2\2\u01be\u01bf\7V\2\2\u01bfU\3\2\2\2\u01c0\u01c1"+
		"\7V\2\2\u01c1\u01c2\7H\2\2\u01c2\u01c5\7V\2\2\u01c3\u01c4\7H\2\2\u01c4"+
		"\u01c6\7V\2\2\u01c5\u01c3\3\2\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01c8\3\2"+
		"\2\2\u01c7\u01c9\7Q\2\2\u01c8\u01c7\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9"+
		"W\3\2\2\2\u01ca\u01cb\b-\1\2\u01cb\u01d3\5\\/\2\u01cc\u01cd\7+\2\2\u01cd"+
		"\u01ce\5X-\2\u01ce\u01cf\7-\2\2\u01cf\u01d3\3\2\2\2\u01d0\u01d1\7/\2\2"+
		"\u01d1\u01d3\5X-\3\u01d2\u01ca\3\2\2\2\u01d2\u01cc\3\2\2\2\u01d2\u01d0"+
		"\3\2\2\2\u01d3\u01da\3\2\2\2\u01d4\u01d5\f\5\2\2\u01d5\u01d6\5Z.\2\u01d6"+
		"\u01d7\5X-\6\u01d7\u01d9\3\2\2\2\u01d8\u01d4\3\2\2\2\u01d9\u01dc\3\2\2"+
		"\2\u01da\u01d8\3\2\2\2\u01da\u01db\3\2\2\2\u01dbY\3\2\2\2\u01dc\u01da"+
		"\3\2\2\2\u01dd\u01de\t\2\2\2\u01de[\3\2\2\2\u01df\u01e0\5^\60\2\u01e0"+
		"\u01e1\5d\63\2\u01e1\u01e2\5`\61\2\u01e2\u01ee\3\2\2\2\u01e3\u01e4\5b"+
		"\62\2\u01e4\u01e5\5h\65\2\u01e5\u01e6\5^\60\2\u01e6\u01e7\7\60\2\2\u01e7"+
		"\u01e8\5`\61\2\u01e8\u01ee\3\2\2\2\u01e9\u01ea\5b\62\2\u01ea\u01eb\5j"+
		"\66\2\u01eb\u01ec\7\66\2\2\u01ec\u01ee\3\2\2\2\u01ed\u01df\3\2\2\2\u01ed"+
		"\u01e3\3\2\2\2\u01ed\u01e9\3\2\2\2\u01ee]\3\2\2\2\u01ef\u01f0\5D#\2\u01f0"+
		"_\3\2\2\2\u01f1\u01f2\5D#\2\u01f2a\3\2\2\2\u01f3\u01f4\5D#\2\u01f4c\3"+
		"\2\2\2\u01f5\u01fe\5f\64\2\u01f6\u01f7\7M\2\2\u01f7\u01f8\7+\2\2\u01f8"+
		"\u01f9\5f\64\2\u01f9\u01fa\7,\2\2\u01fa\u01fb\7V\2\2\u01fb\u01fc\7-\2"+
		"\2\u01fc\u01fe\3\2\2\2\u01fd\u01f5\3\2\2\2\u01fd\u01f6\3\2\2\2\u01fee"+
		"\3\2\2\2\u01ff\u0200\t\3\2\2\u0200g\3\2\2\2\u0201\u0209\7$\2\2\u0202\u0203"+
		"\7M\2\2\u0203\u0204\7+\2\2\u0204\u0205\7$\2\2\u0205\u0206\7,\2\2\u0206"+
		"\u0207\7V\2\2\u0207\u0209\7-\2\2\u0208\u0201\3\2\2\2\u0208\u0202\3\2\2"+
		"\2\u0209i\3\2\2\2\u020a\u020e\7%\2\2\u020b\u020c\7%\2\2\u020c\u020e\7"+
		"/\2\2\u020d\u020a\3\2\2\2\u020d\u020b\3\2\2\2\u020ek\3\2\2\2\u020f\u0210"+
		"\7=\2\2\u0210\u0211\5,\27\2\u0211m\3\2\2\2\u0212\u0215\5p9\2\u0213\u0215"+
		"\5r:\2\u0214\u0212\3\2\2\2\u0214\u0213\3\2\2\2\u0215o\3\2\2\2\u0216\u0217"+
		"\7B\2\2\u0217\u0218\7\67\2\2\u0218\u0219\7@\2\2\u0219\u021a\7C\2\2\u021a"+
		"\u021b\7\67\2\2\u021b\u021c\5x=\2\u021cq\3\2\2\2\u021d\u021e\7B\2\2\u021e"+
		"\u021f\7\67\2\2\u021f\u0222\7D\2\2\u0220\u0223\5t;\2\u0221\u0223\5v<\2"+
		"\u0222\u0220\3\2\2\2\u0222\u0221\3\2\2\2\u0223s\3\2\2\2\u0224\u0225\7"+
		"E\2\2\u0225\u0226\7\67\2\2\u0226\u0227\7R\2\2\u0227u\3\2\2\2\u0228\u0229"+
		"\7F\2\2\u0229\u022a\7\67\2\2\u022a\u022b\7R\2\2\u022bw\3\2\2\2\u022c\u022d"+
		"\5\u00b8]\2\u022dy\3\2\2\2\u022e\u022f\7\35\2\2\u022f\u0232\5|?\2\u0230"+
		"\u0231\7,\2\2\u0231\u0233\5\u0082B\2\u0232\u0230\3\2\2\2\u0232\u0233\3"+
		"\2\2\2\u0233{\3\2\2\2\u0234\u0239\5~@\2\u0235\u0236\7,\2\2\u0236\u0238"+
		"\5~@\2\u0237\u0235\3\2\2\2\u0238\u023b\3\2\2\2\u0239\u0237\3\2\2\2\u0239"+
		"\u023a\3\2\2\2\u023a}\3\2\2\2\u023b\u0239\3\2\2\2\u023c\u023d\7\'\2\2"+
		"\u023d\u023e\5\u00b4[\2\u023e\u023f\7\37\2\2\u023f\u0242\5\u0080A\2\u0240"+
		"\u0241\7 \2\2\u0241\u0243\5X-\2\u0242\u0240\3\2\2\2\u0242\u0243\3\2\2"+
		"\2\u0243\177\3\2\2\2\u0244\u0245\7S\2\2\u0245\u0247\7H\2\2\u0246\u0244"+
		"\3\2\2\2\u0246\u0247\3\2\2\2\u0247\u0248\3\2\2\2\u0248\u024b\7S\2\2\u0249"+
		"\u024a\7.\2\2\u024a\u024c\7S\2\2\u024b\u0249\3\2\2\2\u024b\u024c\3\2\2"+
		"\2\u024c\u0081\3\2\2\2\u024d\u0252\5\u0084C\2\u024e\u024f\7,\2\2\u024f"+
		"\u0251\5\u0084C\2\u0250\u024e\3\2\2\2\u0251\u0254\3\2\2\2\u0252\u0250"+
		"\3\2\2\2\u0252\u0253\3\2\2\2\u0253\u0083\3\2\2\2\u0254\u0252\3\2\2\2\u0255"+
		"\u0258\5\u0086D\2\u0256\u0258\5\u0088E\2\u0257\u0255\3\2\2\2\u0257\u0256"+
		"\3\2\2\2\u0258\u0085\3\2\2\2\u0259\u025a\7\5\2\2\u025a\u025b\5\u00b6\\"+
		"\2\u025b\u025c\5\u00b8]\2\u025c\u0087\3\2\2\2\u025d\u025e\7\6\2\2\u025e"+
		"\u025f\5\u00b6\\\2\u025f\u0260\5\u008aF\2\u0260\u0261\5\u008eH\2\u0261"+
		"\u0089\3\2\2\2\u0262\u0263\7\7\2\2\u0263\u0268\5\u008cG\2\u0264\u0265"+
		"\7,\2\2\u0265\u0267\5\u008cG\2\u0266\u0264\3\2\2\2\u0267\u026a\3\2\2\2"+
		"\u0268\u0266\3\2\2\2\u0268\u0269\3\2\2\2\u0269\u008b\3\2\2\2\u026a\u0268"+
		"\3\2\2\2\u026b\u026c\5\u0080A\2\u026c\u026d\7(\2\2\u026d\u026e\7S\2\2"+
		"\u026e\u008d\3\2\2\2\u026f\u0270\7\b\2\2\u0270\u0275\5\u0090I\2\u0271"+
		"\u0272\7,\2\2\u0272\u0274\5\u0090I\2\u0273\u0271\3\2\2\2\u0274\u0277\3"+
		"\2\2\2\u0275\u0273\3\2\2\2\u0275\u0276\3\2\2\2\u0276\u0278\3\2\2\2\u0277"+
		"\u0275\3\2\2\2\u0278\u0279\7\t\2\2\u0279\u008f\3\2\2\2\u027a\u027b\7R"+
		"\2\2\u027b\u027c\7H\2\2\u027c\u027d\7\b\2\2\u027d\u0282\5\u0092J\2\u027e"+
		"\u027f\7,\2\2\u027f\u0281\5\u0092J\2\u0280\u027e\3\2\2\2\u0281\u0284\3"+
		"\2\2\2\u0282\u0280\3\2\2\2\u0282\u0283\3\2\2\2\u0283\u0285\3\2\2\2\u0284"+
		"\u0282\3\2\2\2\u0285\u0286\7\t\2\2\u0286\u0091\3\2\2\2\u0287\u0288\5\u0094"+
		"K\2\u0288\u0289\7H\2\2\u0289\u028a\7\b\2\2\u028a\u028b\5\u0098M\2\u028b"+
		"\u028c\7\t\2\2\u028c\u0093\3\2\2\2\u028d\u0299\5\u0096L\2\u028e\u028f"+
		"\7\n\2\2\u028f\u0292\5\u0096L\2\u0290\u0291\7,\2\2\u0291\u0293\5\u0096"+
		"L\2\u0292\u0290\3\2\2\2\u0293\u0294\3\2\2\2\u0294\u0292\3\2\2\2\u0294"+
		"\u0295\3\2\2\2\u0295\u0296\3\2\2\2\u0296\u0297\7\13\2\2\u0297\u0299\3"+
		"\2\2\2\u0298\u028d\3\2\2\2\u0298\u028e\3\2\2\2\u0299\u0095\3\2\2\2\u029a"+
		"\u029f\7S\2\2\u029b\u029c\7.\2\2\u029c\u029e\7S\2\2\u029d\u029b\3\2\2"+
		"\2\u029e\u02a1\3\2\2\2\u029f\u029d\3\2\2\2\u029f\u02a0\3\2\2\2\u02a0\u0097"+
		"\3\2\2\2\u02a1\u029f\3\2\2\2\u02a2\u02a3\5\u009cO\2\u02a3\u02a4\7,\2\2"+
		"\u02a4\u02a5\5\u009aN\2\u02a5\u02ab\3\2\2\2\u02a6\u02a7\5\u009aN\2\u02a7"+
		"\u02a8\7,\2\2\u02a8\u02a9\5\u009cO\2\u02a9\u02ab\3\2\2\2\u02aa\u02a2\3"+
		"\2\2\2\u02aa\u02a6\3\2\2\2\u02ab\u0099\3\2\2\2\u02ac\u02ad\7\f\2\2\u02ad"+
		"\u02ae\7H\2\2\u02ae\u02af\7V\2\2\u02af\u009b\3\2\2\2\u02b0\u02b1\7\r\2"+
		"\2\u02b1\u02b2\7H\2\2\u02b2\u02b3\7\n\2\2\u02b3\u02b8\5\u009eP\2\u02b4"+
		"\u02b5\7,\2\2\u02b5\u02b7\5\u009eP\2\u02b6\u02b4\3\2\2\2\u02b7\u02ba\3"+
		"\2\2\2\u02b8\u02b6\3\2\2\2\u02b8\u02b9\3\2\2\2\u02b9\u02bb\3\2\2\2\u02ba"+
		"\u02b8\3\2\2\2\u02bb\u02bc\7\13\2\2\u02bc\u009d\3\2\2\2\u02bd\u02c6\7"+
		"\b\2\2\u02be\u02bf\5\u00a0Q\2\u02bf\u02c0\7,\2\2\u02c0\u02c1\5\u00a2R"+
		"\2\u02c1\u02c7\3\2\2\2\u02c2\u02c3\5\u00a2R\2\u02c3\u02c4\7,\2\2\u02c4"+
		"\u02c5\5\u00a0Q\2\u02c5\u02c7\3\2\2\2\u02c6\u02be\3\2\2\2\u02c6\u02c2"+
		"\3\2\2\2\u02c7\u02c8\3\2\2\2\u02c8\u02c9\7\t\2\2\u02c9\u009f\3\2\2\2\u02ca"+
		"\u02cb\7\16\2\2\u02cb\u02cc\7H\2\2\u02cc\u02cd\7V\2\2\u02cd\u00a1\3\2"+
		"\2\2\u02ce\u02cf\7\17\2\2\u02cf\u02d3\7H\2\2\u02d0\u02d4\5\u00a8U\2\u02d1"+
		"\u02d4\5\u00a4S\2\u02d2\u02d4\5\u00a6T\2\u02d3\u02d0\3\2\2\2\u02d3\u02d1"+
		"\3\2\2\2\u02d3\u02d2\3\2\2\2\u02d4\u00a3\3\2\2\2\u02d5\u02d6\7\n\2\2\u02d6"+
		"\u02db\5\u00a6T\2\u02d7\u02d8\7H\2\2\u02d8\u02da\5\u00a6T\2\u02d9\u02d7"+
		"\3\2\2\2\u02da\u02dd\3\2\2\2\u02db\u02d9\3\2\2\2\u02db\u02dc\3\2\2\2\u02dc"+
		"\u02de\3\2\2\2\u02dd\u02db\3\2\2\2\u02de\u02df\7\13\2\2\u02df\u00a5\3"+
		"\2\2\2\u02e0\u02e1\5\u00ceh\2\u02e1\u00a7\3\2\2\2\u02e2\u02e7\5\u00aa"+
		"V\2\u02e3\u02e7\5\u00acW\2\u02e4\u02e7\5\u00aeX\2\u02e5\u02e7\5\u00b0"+
		"Y\2\u02e6\u02e2\3\2\2\2\u02e6\u02e3\3\2\2\2\u02e6\u02e4\3\2\2\2\u02e6"+
		"\u02e5\3\2\2\2\u02e7\u00a9\3\2\2\2\u02e8\u02e9\7\n\2\2\u02e9\u02ea\5\u00b2"+
		"Z\2\u02ea\u02eb\7\13\2\2\u02eb\u00ab\3\2\2\2\u02ec\u02ed\7\n\2\2\u02ed"+
		"\u02ee\5\u00b2Z\2\u02ee\u02ef\7-\2\2\u02ef\u00ad\3\2\2\2\u02f0\u02f1\7"+
		"+\2\2\u02f1\u02f2\5\u00b2Z\2\u02f2\u02f3\7\13\2\2\u02f3\u00af\3\2\2\2"+
		"\u02f4\u02f5\7+\2\2\u02f5\u02f6\5\u00b2Z\2\u02f6\u02f7\7-\2\2\u02f7\u00b1"+
		"\3\2\2\2\u02f8\u02f9\7V\2\2\u02f9\u02fa\7\20\2\2\u02fa\u02fb\7V\2\2\u02fb"+
		"\u00b3\3\2\2\2\u02fc\u02fd\7S\2\2\u02fd\u00b5\3\2\2\2\u02fe\u02ff\7S\2"+
		"\2\u02ff\u00b7\3\2\2\2\u0300\u0301\5\u00ba^\2\u0301\u0302\7\21\2\2\u0302"+
		"\u0305\5\u00bc_\2\u0303\u0304\7H\2\2\u0304\u0306\5\u00c6d\2\u0305\u0303"+
		"\3\2\2\2\u0305\u0306\3\2\2\2\u0306\u0309\3\2\2\2\u0307\u0308\7L\2\2\u0308"+
		"\u030a\5\u00c8e\2\u0309\u0307\3\2\2\2\u0309\u030a\3\2\2\2\u030a\u030d"+
		"\3\2\2\2\u030b\u030c\7\22\2\2\u030c\u030e\5\u00c2b\2\u030d\u030b\3\2\2"+
		"\2\u030d\u030e\3\2\2\2\u030e\u00b9\3\2\2\2\u030f\u0310\7S\2\2\u0310\u00bb"+
		"\3\2\2\2\u0311\u0314\5\u00be`\2\u0312\u0314\5\u00c0a\2\u0313\u0311\3\2"+
		"\2\2\u0313\u0312\3\2\2\2\u0314\u00bd\3\2\2\2\u0315\u031a\7S\2\2\u0316"+
		"\u0317\7.\2\2\u0317\u0319\7S\2\2\u0318\u0316\3\2\2\2\u0319\u031c\3\2\2"+
		"\2\u031a\u0318\3\2\2\2\u031a\u031b\3\2\2\2\u031b\u00bf\3\2\2\2\u031c\u031a"+
		"\3\2\2\2\u031d\u031e\7W\2\2\u031e\u031f\7.\2\2\u031f\u0320\7W\2\2\u0320"+
		"\u0321\7.\2\2\u0321\u0322\7W\2\2\u0322\u0323\7.\2\2\u0323\u0324\7W\2\2"+
		"\u0324\u00c1\3\2\2\2\u0325\u032a\5\u00c4c\2\u0326\u0327\7\23\2\2\u0327"+
		"\u0329\5\u00c4c\2\u0328\u0326\3\2\2\2\u0329\u032c\3\2\2\2\u032a\u0328"+
		"\3\2\2\2\u032a\u032b\3\2\2\2\u032b\u00c3\3\2\2\2\u032c\u032a\3\2\2\2\u032d"+
		"\u0330\7S\2\2\u032e\u032f\7\67\2\2\u032f\u0331\t\4\2\2\u0330\u032e\3\2"+
		"\2\2\u0330\u0331\3\2\2\2\u0331\u00c5\3\2\2\2\u0332\u0333\7W\2\2\u0333"+
		"\u00c7\3\2\2\2\u0334\u0337\5\u00caf\2\u0335\u0337\5\u00ccg\2\u0336\u0334"+
		"\3\2\2\2\u0336\u0335\3\2\2\2\u0337\u033f\3\2\2\2\u0338\u033b\7L\2\2\u0339"+
		"\u033c\5\u00caf\2\u033a\u033c\5\u00ccg\2\u033b\u0339\3\2\2\2\u033b\u033a"+
		"\3\2\2\2\u033c\u033e\3\2\2\2\u033d\u0338\3\2\2\2\u033e\u0341\3\2\2\2\u033f"+
		"\u033d\3\2\2\2\u033f\u0340\3\2\2\2\u0340\u00c9\3\2\2\2\u0341\u033f\3\2"+
		"\2\2\u0342\u0343\7S\2\2\u0343\u00cb\3\2\2\2\u0344\u0345\7\b\2\2\u0345"+
		"\u0346\7S\2\2\u0346\u0347\7\t\2\2\u0347\u00cd\3\2\2\2\u0348\u0349\5\u00d6"+
		"l\2\u0349\u00cf\3\2\2\2\u034a\u034b\7\b\2\2\u034b\u0350\5\u00d2j\2\u034c"+
		"\u034d\7,\2\2\u034d\u034f\5\u00d2j\2\u034e\u034c\3\2\2\2\u034f\u0352\3"+
		"\2\2\2\u0350\u034e\3\2\2\2\u0350\u0351\3\2\2\2\u0351\u0353\3\2\2\2\u0352"+
		"\u0350\3\2\2\2\u0353\u0354\7\t\2\2\u0354\u0358\3\2\2\2\u0355\u0356\7\b"+
		"\2\2\u0356\u0358\7\t\2\2\u0357\u034a\3\2\2\2\u0357\u0355\3\2\2\2\u0358"+
		"\u00d1\3\2\2\2\u0359\u035a\7R\2\2\u035a\u035b\7H\2\2\u035b\u035c\5\u00d6"+
		"l\2\u035c\u00d3\3\2\2\2\u035d\u035e\7\n\2\2\u035e\u0363\5\u00d6l\2\u035f"+
		"\u0360\7,\2\2\u0360\u0362\5\u00d6l\2\u0361\u035f\3\2\2\2\u0362\u0365\3"+
		"\2\2\2\u0363\u0364\3\2\2\2\u0363\u0361\3\2\2\2\u0364\u0366\3\2\2\2\u0365"+
		"\u0363\3\2\2\2\u0366\u0367\7\13\2\2\u0367\u036b\3\2\2\2\u0368\u0369\7"+
		"\n\2\2\u0369\u036b\7\13\2\2\u036a\u035d\3\2\2\2\u036a\u0368\3\2\2\2\u036b"+
		"\u00d5\3\2\2\2\u036c\u0374\7R\2\2\u036d\u0374\7V\2\2\u036e\u0374\5\u00d0"+
		"i\2\u036f\u0374\5\u00d4k\2\u0370\u0374\7\24\2\2\u0371\u0374\7\25\2\2\u0372"+
		"\u0374\7\66\2\2\u0373\u036c\3\2\2\2\u0373\u036d\3\2\2\2\u0373\u036e\3"+
		"\2\2\2\u0373\u036f\3\2\2\2\u0373\u0370\3\2\2\2\u0373\u0371\3\2\2\2\u0373"+
		"\u0372\3\2\2\2\u0374\u00d7\3\2\2\2I\u00d9\u00dd\u00e6\u00ee\u00f0\u00f4"+
		"\u00f7\u00fa\u00ff\u0102\u0139\u0147\u014c\u0150\u015a\u0163\u0170\u017a"+
		"\u0181\u018a\u0190\u0195\u01a0\u01a4\u01a8\u01b4\u01b8\u01c5\u01c8\u01d2"+
		"\u01da\u01ed\u01fd\u0208\u020d\u0214\u0222\u0232\u0239\u0242\u0246\u024b"+
		"\u0252\u0257\u0268\u0275\u0282\u0294\u0298\u029f\u02aa\u02b8\u02c6\u02d3"+
		"\u02db\u02e6\u0305\u0309\u030d\u0313\u031a\u032a\u0330\u0336\u033b\u033f"+
		"\u0350\u0357\u0363\u036a\u0373";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}