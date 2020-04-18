// Generated from /Users/ali/CDQLParserV3/expression/Expression.g4 by ANTLR 4.6
// jshint ignore: start
var antlr4 = require('antlr4/index');
var ExpressionListener = require('./ExpressionListener').ExpressionListener;
var grammarFileName = "Expression.g4";

var serializedATN = ["\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd",
    "\u0003A\u016a\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t",
    "\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004",
    "\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0004",
    "\r\t\r\u0004\u000e\t\u000e\u0004\u000f\t\u000f\u0004\u0010\t\u0010\u0004",
    "\u0011\t\u0011\u0004\u0012\t\u0012\u0004\u0013\t\u0013\u0004\u0014\t",
    "\u0014\u0004\u0015\t\u0015\u0004\u0016\t\u0016\u0004\u0017\t\u0017\u0004",
    "\u0018\t\u0018\u0004\u0019\t\u0019\u0004\u001a\t\u001a\u0004\u001b\t",
    "\u001b\u0004\u001c\t\u001c\u0004\u001d\t\u001d\u0004\u001e\t\u001e\u0004",
    "\u001f\t\u001f\u0004 \t \u0004!\t!\u0004\"\t\"\u0004#\t#\u0004$\t$\u0004",
    "%\t%\u0004&\t&\u0004\'\t\'\u0004(\t(\u0004)\t)\u0003\u0002\u0003\u0002",
    "\u0003\u0002\u0007\u0002V\n\u0002\f\u0002\u000e\u0002Y\u000b\u0002\u0003",
    "\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0005",
    "\u0005a\n\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006f\n\u0006",
    "\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0005\u0007",
    "m\n\u0007\u0003\u0007\u0007\u0007p\n\u0007\f\u0007\u000e\u0007s\u000b",
    "\u0007\u0003\b\u0003\b\u0005\bw\n\b\u0003\t\u0003\t\u0003\t\u0007\t",
    "|\n\t\f\t\u000e\t\u007f\u000b\t\u0003\n\u0003\n\u0003\n\u0005\n\u0084",
    "\n\n\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0007\u000b\u008a",
    "\n\u000b\f\u000b\u000e\u000b\u008d\u000b\u000b\u0005\u000b\u008f\n\u000b",
    "\u0003\u000b\u0003\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\r\u0003",
    "\r\u0003\r\u0003\u000e\u0003\u000e\u0003\u000f\u0003\u000f\u0003\u000f",
    "\u0003\u000f\u0003\u000f\u0003\u000f\u0005\u000f\u00a2\n\u000f\u0003",
    "\u0010\u0003\u0010\u0003\u0010\u0003\u0010\u0003\u0010\u0003\u0010\u0005",
    "\u0010\u00aa\n\u0010\u0003\u0011\u0003\u0011\u0003\u0011\u0007\u0011",
    "\u00af\n\u0011\f\u0011\u000e\u0011\u00b2\u000b\u0011\u0003\u0012\u0003",
    "\u0012\u0003\u0012\u0007\u0012\u00b7\n\u0012\f\u0012\u000e\u0012\u00ba",
    "\u000b\u0012\u0003\u0013\u0003\u0013\u0003\u0013\u0007\u0013\u00bf\n",
    "\u0013\f\u0013\u000e\u0013\u00c2\u000b\u0013\u0003\u0014\u0003\u0014",
    "\u0003\u0014\u0007\u0014\u00c7\n\u0014\f\u0014\u000e\u0014\u00ca\u000b",
    "\u0014\u0003\u0015\u0003\u0015\u0003\u0015\u0007\u0015\u00cf\n\u0015",
    "\f\u0015\u000e\u0015\u00d2\u000b\u0015\u0003\u0016\u0003\u0016\u0003",
    "\u0016\u0007\u0016\u00d7\n\u0016\f\u0016\u000e\u0016\u00da\u000b\u0016",
    "\u0003\u0017\u0003\u0017\u0003\u0017\u0007\u0017\u00df\n\u0017\f\u0017",
    "\u000e\u0017\u00e2\u000b\u0017\u0003\u0018\u0003\u0018\u0003\u0018\u0007",
    "\u0018\u00e7\n\u0018\f\u0018\u000e\u0018\u00ea\u000b\u0018\u0003\u0019",
    "\u0003\u0019\u0003\u0019\u0007\u0019\u00ef\n\u0019\f\u0019\u000e\u0019",
    "\u00f2\u000b\u0019\u0003\u001a\u0003\u001a\u0003\u001a\u0007\u001a\u00f7",
    "\n\u001a\f\u001a\u000e\u001a\u00fa\u000b\u001a\u0003\u001b\u0003\u001b",
    "\u0003\u001b\u0007\u001b\u00ff\n\u001b\f\u001b\u000e\u001b\u0102\u000b",
    "\u001b\u0003\u001c\u0003\u001c\u0003\u001c\u0007\u001c\u0107\n\u001c",
    "\f\u001c\u000e\u001c\u010a\u000b\u001c\u0003\u001d\u0003\u001d\u0003",
    "\u001d\u0007\u001d\u010f\n\u001d\f\u001d\u000e\u001d\u0112\u000b\u001d",
    "\u0003\u001e\u0003\u001e\u0003\u001e\u0007\u001e\u0117\n\u001e\f\u001e",
    "\u000e\u001e\u011a\u000b\u001e\u0003\u001f\u0003\u001f\u0003\u001f\u0007",
    "\u001f\u011f\n\u001f\f\u001f\u000e\u001f\u0122\u000b\u001f\u0003 \u0003",
    " \u0003 \u0007 \u0127\n \f \u000e \u012a\u000b \u0003!\u0003!\u0003",
    "!\u0007!\u012f\n!\f!\u000e!\u0132\u000b!\u0003\"\u0003\"\u0003\"\u0005",
    "\"\u0137\n\"\u0003#\u0003#\u0005#\u013b\n#\u0003$\u0003$\u0003$\u0003",
    "$\u0003$\u0003$\u0003$\u0003$\u0003$\u0005$\u0146\n$\u0003%\u0003%\u0005",
    "%\u014a\n%\u0003%\u0003%\u0005%\u014e\n%\u0007%\u0150\n%\f%\u000e%\u0153",
    "\u000b%\u0003%\u0003%\u0003&\u0003&\u0003&\u0003&\u0007&\u015b\n&\f",
    "&\u000e&\u015e\u000b&\u0003&\u0003&\u0003\'\u0003\'\u0003\'\u0003\'",
    "\u0003(\u0003(\u0003)\u0003)\u0003)\u0002\u0002*\u0002\u0004\u0006\b",
    "\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.",
    "02468:<>@BDFHJLNP\u0002\r\u0003\u0002\n\u0015\u0003\u0002\u001d \u0003",
    "\u0002!&\u0003\u0002!%\u0003\u0002\')\u0003\u0002*+\u0003\u0002,.\u0004",
    "\u0002*+/5\u0003\u000223\u0003\u0002<>\u0003\u00029=\u016a\u0002R\u0003",
    "\u0002\u0002\u0002\u0004Z\u0003\u0002\u0002\u0002\u0006\\\u0003\u0002",
    "\u0002\u0002\b`\u0003\u0002\u0002\u0002\ne\u0003\u0002\u0002\u0002\f",
    "l\u0003\u0002\u0002\u0002\u000ev\u0003\u0002\u0002\u0002\u0010x\u0003",
    "\u0002\u0002\u0002\u0012\u0083\u0003\u0002\u0002\u0002\u0014\u0085\u0003",
    "\u0002\u0002\u0002\u0016\u0092\u0003\u0002\u0002\u0002\u0018\u0096\u0003",
    "\u0002\u0002\u0002\u001a\u0099\u0003\u0002\u0002\u0002\u001c\u009b\u0003",
    "\u0002\u0002\u0002\u001e\u00a3\u0003\u0002\u0002\u0002 \u00ab\u0003",
    "\u0002\u0002\u0002\"\u00b3\u0003\u0002\u0002\u0002$\u00bb\u0003\u0002",
    "\u0002\u0002&\u00c3\u0003\u0002\u0002\u0002(\u00cb\u0003\u0002\u0002",
    "\u0002*\u00d3\u0003\u0002\u0002\u0002,\u00db\u0003\u0002\u0002\u0002",
    ".\u00e3\u0003\u0002\u0002\u00020\u00eb\u0003\u0002\u0002\u00022\u00f3",
    "\u0003\u0002\u0002\u00024\u00fb\u0003\u0002\u0002\u00026\u0103\u0003",
    "\u0002\u0002\u00028\u010b\u0003\u0002\u0002\u0002:\u0113\u0003\u0002",
    "\u0002\u0002<\u011b\u0003\u0002\u0002\u0002>\u0123\u0003\u0002\u0002",
    "\u0002@\u012b\u0003\u0002\u0002\u0002B\u0136\u0003\u0002\u0002\u0002",
    "D\u0138\u0003\u0002\u0002\u0002F\u0145\u0003\u0002\u0002\u0002H\u0147",
    "\u0003\u0002\u0002\u0002J\u0156\u0003\u0002\u0002\u0002L\u0161\u0003",
    "\u0002\u0002\u0002N\u0165\u0003\u0002\u0002\u0002P\u0167\u0003\u0002",
    "\u0002\u0002RW\u0005\u0004\u0003\u0002ST\u0007\u0003\u0002\u0002TV\u0005",
    "\u0004\u0003\u0002US\u0003\u0002\u0002\u0002VY\u0003\u0002\u0002\u0002",
    "WU\u0003\u0002\u0002\u0002WX\u0003\u0002\u0002\u0002X\u0003\u0003\u0002",
    "\u0002\u0002YW\u0003\u0002\u0002\u0002Z[\u0005\u001c\u000f\u0002[\u0005",
    "\u0003\u0002\u0002\u0002\\]\u0005\u001e\u0010\u0002]\u0007\u0003\u0002",
    "\u0002\u0002^a\u0005\u0010\t\u0002_a\u0005\n\u0006\u0002`^\u0003\u0002",
    "\u0002\u0002`_\u0003\u0002\u0002\u0002a\t\u0003\u0002\u0002\u0002bf",
    "\u0005\f\u0007\u0002cd\u0007\u0004\u0002\u0002df\u0005\n\u0006\u0002",
    "eb\u0003\u0002\u0002\u0002ec\u0003\u0002\u0002\u0002f\u000b\u0003\u0002",
    "\u0002\u0002gm\u0005F$\u0002hi\u0007\u0004\u0002\u0002ij\u0005\f\u0007",
    "\u0002jk\u0005\u0014\u000b\u0002km\u0003\u0002\u0002\u0002lg\u0003\u0002",
    "\u0002\u0002lh\u0003\u0002\u0002\u0002mq\u0003\u0002\u0002\u0002np\u0005",
    "\u000e\b\u0002on\u0003\u0002\u0002\u0002ps\u0003\u0002\u0002\u0002q",
    "o\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002r\r\u0003\u0002\u0002",
    "\u0002sq\u0003\u0002\u0002\u0002tw\u0005\u0016\f\u0002uw\u0005\u0018",
    "\r\u0002vt\u0003\u0002\u0002\u0002vu\u0003\u0002\u0002\u0002w\u000f",
    "\u0003\u0002\u0002\u0002xy\u0005\f\u0007\u0002y}\u0005\u0014\u000b\u0002",
    "z|\u0005\u0012\n\u0002{z\u0003\u0002\u0002\u0002|\u007f\u0003\u0002",
    "\u0002\u0002}{\u0003\u0002\u0002\u0002}~\u0003\u0002\u0002\u0002~\u0011",
    "\u0003\u0002\u0002\u0002\u007f}\u0003\u0002\u0002\u0002\u0080\u0084",
    "\u0005\u0014\u000b\u0002\u0081\u0084\u0005\u0016\f\u0002\u0082\u0084",
    "\u0005\u0018\r\u0002\u0083\u0080\u0003\u0002\u0002\u0002\u0083\u0081",
    "\u0003\u0002\u0002\u0002\u0083\u0082\u0003\u0002\u0002\u0002\u0084\u0013",
    "\u0003\u0002\u0002\u0002\u0085\u008e\u0007\u0005\u0002\u0002\u0086\u008b",
    "\u0005\u0004\u0003\u0002\u0087\u0088\u0007\u0003\u0002\u0002\u0088\u008a",
    "\u0005\u0004\u0003\u0002\u0089\u0087\u0003\u0002\u0002\u0002\u008a\u008d",
    "\u0003\u0002\u0002\u0002\u008b\u0089\u0003\u0002\u0002\u0002\u008b\u008c",
    "\u0003\u0002\u0002\u0002\u008c\u008f\u0003\u0002\u0002\u0002\u008d\u008b",
    "\u0003\u0002\u0002\u0002\u008e\u0086\u0003\u0002\u0002\u0002\u008e\u008f",
    "\u0003\u0002\u0002\u0002\u008f\u0090\u0003\u0002\u0002\u0002\u0090\u0091",
    "\u0007\u0006\u0002\u0002\u0091\u0015\u0003\u0002\u0002\u0002\u0092\u0093",
    "\u0007\u0007\u0002\u0002\u0093\u0094\u0005\u0002\u0002\u0002\u0094\u0095",
    "\u0007\b\u0002\u0002\u0095\u0017\u0003\u0002\u0002\u0002\u0096\u0097",
    "\u0007\t\u0002\u0002\u0097\u0098\u0007>\u0002\u0002\u0098\u0019\u0003",
    "\u0002\u0002\u0002\u0099\u009a\t\u0002\u0002\u0002\u009a\u001b\u0003",
    "\u0002\u0002\u0002\u009b\u00a1\u0005 \u0011\u0002\u009c\u009d\u0007",
    "\u0016\u0002\u0002\u009d\u009e\u0005\u0004\u0003\u0002\u009e\u009f\u0007",
    "\u0017\u0002\u0002\u009f\u00a0\u0005\u0004\u0003\u0002\u00a0\u00a2\u0003",
    "\u0002\u0002\u0002\u00a1\u009c\u0003\u0002\u0002\u0002\u00a1\u00a2\u0003",
    "\u0002\u0002\u0002\u00a2\u001d\u0003\u0002\u0002\u0002\u00a3\u00a9\u0005",
    "\"\u0012\u0002\u00a4\u00a5\u0007\u0016\u0002\u0002\u00a5\u00a6\u0005",
    "\u0006\u0004\u0002\u00a6\u00a7\u0007\u0017\u0002\u0002\u00a7\u00a8\u0005",
    "\u0006\u0004\u0002\u00a8\u00aa\u0003\u0002\u0002\u0002\u00a9\u00a4\u0003",
    "\u0002\u0002\u0002\u00a9\u00aa\u0003\u0002\u0002\u0002\u00aa\u001f\u0003",
    "\u0002\u0002\u0002\u00ab\u00b0\u0005$\u0013\u0002\u00ac\u00ad\u0007",
    "\u0018\u0002\u0002\u00ad\u00af\u0005$\u0013\u0002\u00ae\u00ac\u0003",
    "\u0002\u0002\u0002\u00af\u00b2\u0003\u0002\u0002\u0002\u00b0\u00ae\u0003",
    "\u0002\u0002\u0002\u00b0\u00b1\u0003\u0002\u0002\u0002\u00b1!\u0003",
    "\u0002\u0002\u0002\u00b2\u00b0\u0003\u0002\u0002\u0002\u00b3\u00b8\u0005",
    "&\u0014\u0002\u00b4\u00b5\u0007\u0018\u0002\u0002\u00b5\u00b7\u0005",
    "&\u0014\u0002\u00b6\u00b4\u0003\u0002\u0002\u0002\u00b7\u00ba\u0003",
    "\u0002\u0002\u0002\u00b8\u00b6\u0003\u0002\u0002\u0002\u00b8\u00b9\u0003",
    "\u0002\u0002\u0002\u00b9#\u0003\u0002\u0002\u0002\u00ba\u00b8\u0003",
    "\u0002\u0002\u0002\u00bb\u00c0\u0005(\u0015\u0002\u00bc\u00bd\u0007",
    "\u0019\u0002\u0002\u00bd\u00bf\u0005(\u0015\u0002\u00be\u00bc\u0003",
    "\u0002\u0002\u0002\u00bf\u00c2\u0003\u0002\u0002\u0002\u00c0\u00be\u0003",
    "\u0002\u0002\u0002\u00c0\u00c1\u0003\u0002\u0002\u0002\u00c1%\u0003",
    "\u0002\u0002\u0002\u00c2\u00c0\u0003\u0002\u0002\u0002\u00c3\u00c8\u0005",
    "*\u0016\u0002\u00c4\u00c5\u0007\u0019\u0002\u0002\u00c5\u00c7\u0005",
    "*\u0016\u0002\u00c6\u00c4\u0003\u0002\u0002\u0002\u00c7\u00ca\u0003",
    "\u0002\u0002\u0002\u00c8\u00c6\u0003\u0002\u0002\u0002\u00c8\u00c9\u0003",
    "\u0002\u0002\u0002\u00c9\'\u0003\u0002\u0002\u0002\u00ca\u00c8\u0003",
    "\u0002\u0002\u0002\u00cb\u00d0\u0005,\u0017\u0002\u00cc\u00cd\u0007",
    "\u001a\u0002\u0002\u00cd\u00cf\u0005,\u0017\u0002\u00ce\u00cc\u0003",
    "\u0002\u0002\u0002\u00cf\u00d2\u0003\u0002\u0002\u0002\u00d0\u00ce\u0003",
    "\u0002\u0002\u0002\u00d0\u00d1\u0003\u0002\u0002\u0002\u00d1)\u0003",
    "\u0002\u0002\u0002\u00d2\u00d0\u0003\u0002\u0002\u0002\u00d3\u00d8\u0005",
    ".\u0018\u0002\u00d4\u00d5\u0007\u001a\u0002\u0002\u00d5\u00d7\u0005",
    ".\u0018\u0002\u00d6\u00d4\u0003\u0002\u0002\u0002\u00d7\u00da\u0003",
    "\u0002\u0002\u0002\u00d8\u00d6\u0003\u0002\u0002\u0002\u00d8\u00d9\u0003",
    "\u0002\u0002\u0002\u00d9+\u0003\u0002\u0002\u0002\u00da\u00d8\u0003",
    "\u0002\u0002\u0002\u00db\u00e0\u00050\u0019\u0002\u00dc\u00dd\u0007",
    "\u001b\u0002\u0002\u00dd\u00df\u00050\u0019\u0002\u00de\u00dc\u0003",
    "\u0002\u0002\u0002\u00df\u00e2\u0003\u0002\u0002\u0002\u00e0\u00de\u0003",
    "\u0002\u0002\u0002\u00e0\u00e1\u0003\u0002\u0002\u0002\u00e1-\u0003",
    "\u0002\u0002\u0002\u00e2\u00e0\u0003\u0002\u0002\u0002\u00e3\u00e8\u0005",
    "2\u001a\u0002\u00e4\u00e5\u0007\u001b\u0002\u0002\u00e5\u00e7\u0005",
    "2\u001a\u0002\u00e6\u00e4\u0003\u0002\u0002\u0002\u00e7\u00ea\u0003",
    "\u0002\u0002\u0002\u00e8\u00e6\u0003\u0002\u0002\u0002\u00e8\u00e9\u0003",
    "\u0002\u0002\u0002\u00e9/\u0003\u0002\u0002\u0002\u00ea\u00e8\u0003",
    "\u0002\u0002\u0002\u00eb\u00f0\u00054\u001b\u0002\u00ec\u00ed\u0007",
    "\u001c\u0002\u0002\u00ed\u00ef\u00054\u001b\u0002\u00ee\u00ec\u0003",
    "\u0002\u0002\u0002\u00ef\u00f2\u0003\u0002\u0002\u0002\u00f0\u00ee\u0003",
    "\u0002\u0002\u0002\u00f0\u00f1\u0003\u0002\u0002\u0002\u00f11\u0003",
    "\u0002\u0002\u0002\u00f2\u00f0\u0003\u0002\u0002\u0002\u00f3\u00f8\u0005",
    "6\u001c\u0002\u00f4\u00f5\u0007\u001c\u0002\u0002\u00f5\u00f7\u0005",
    "6\u001c\u0002\u00f6\u00f4\u0003\u0002\u0002\u0002\u00f7\u00fa\u0003",
    "\u0002\u0002\u0002\u00f8\u00f6\u0003\u0002\u0002\u0002\u00f8\u00f9\u0003",
    "\u0002\u0002\u0002\u00f93\u0003\u0002\u0002\u0002\u00fa\u00f8\u0003",
    "\u0002\u0002\u0002\u00fb\u0100\u00058\u001d\u0002\u00fc\u00fd\t\u0003",
    "\u0002\u0002\u00fd\u00ff\u00058\u001d\u0002\u00fe\u00fc\u0003\u0002",
    "\u0002\u0002\u00ff\u0102\u0003\u0002\u0002\u0002\u0100\u00fe\u0003\u0002",
    "\u0002\u0002\u0100\u0101\u0003\u0002\u0002\u0002\u01015\u0003\u0002",
    "\u0002\u0002\u0102\u0100\u0003\u0002\u0002\u0002\u0103\u0108\u0005:",
    "\u001e\u0002\u0104\u0105\t\u0003\u0002\u0002\u0105\u0107\u0005:\u001e",
    "\u0002\u0106\u0104\u0003\u0002\u0002\u0002\u0107\u010a\u0003\u0002\u0002",
    "\u0002\u0108\u0106\u0003\u0002\u0002\u0002\u0108\u0109\u0003\u0002\u0002",
    "\u0002\u01097\u0003\u0002\u0002\u0002\u010a\u0108\u0003\u0002\u0002",
    "\u0002\u010b\u0110\u0005<\u001f\u0002\u010c\u010d\t\u0004\u0002\u0002",
    "\u010d\u010f\u0005<\u001f\u0002\u010e\u010c\u0003\u0002\u0002\u0002",
    "\u010f\u0112\u0003\u0002\u0002\u0002\u0110\u010e\u0003\u0002\u0002\u0002",
    "\u0110\u0111\u0003\u0002\u0002\u0002\u01119\u0003\u0002\u0002\u0002",
    "\u0112\u0110\u0003\u0002\u0002\u0002\u0113\u0118\u0005<\u001f\u0002",
    "\u0114\u0115\t\u0005\u0002\u0002\u0115\u0117\u0005<\u001f\u0002\u0116",
    "\u0114\u0003\u0002\u0002\u0002\u0117\u011a\u0003\u0002\u0002\u0002\u0118",
    "\u0116\u0003\u0002\u0002\u0002\u0118\u0119\u0003\u0002\u0002\u0002\u0119",
    ";\u0003\u0002\u0002\u0002\u011a\u0118\u0003\u0002\u0002\u0002\u011b",
    "\u0120\u0005> \u0002\u011c\u011d\t\u0006\u0002\u0002\u011d\u011f\u0005",
    "> \u0002\u011e\u011c\u0003\u0002\u0002\u0002\u011f\u0122\u0003\u0002",
    "\u0002\u0002\u0120\u011e\u0003\u0002\u0002\u0002\u0120\u0121\u0003\u0002",
    "\u0002\u0002\u0121=\u0003\u0002\u0002\u0002\u0122\u0120\u0003\u0002",
    "\u0002\u0002\u0123\u0128\u0005@!\u0002\u0124\u0125\t\u0007\u0002\u0002",
    "\u0125\u0127\u0005@!\u0002\u0126\u0124\u0003\u0002\u0002\u0002\u0127",
    "\u012a\u0003\u0002\u0002\u0002\u0128\u0126\u0003\u0002\u0002\u0002\u0128",
    "\u0129\u0003\u0002\u0002\u0002\u0129?\u0003\u0002\u0002\u0002\u012a",
    "\u0128\u0003\u0002\u0002\u0002\u012b\u0130\u0005B\"\u0002\u012c\u012d",
    "\t\b\u0002\u0002\u012d\u012f\u0005B\"\u0002\u012e\u012c\u0003\u0002",
    "\u0002\u0002\u012f\u0132\u0003\u0002\u0002\u0002\u0130\u012e\u0003\u0002",
    "\u0002\u0002\u0130\u0131\u0003\u0002\u0002\u0002\u0131A\u0003\u0002",
    "\u0002\u0002\u0132\u0130\u0003\u0002\u0002\u0002\u0133\u0137\u0005D",
    "#\u0002\u0134\u0135\t\t\u0002\u0002\u0135\u0137\u0005B\"\u0002\u0136",
    "\u0133\u0003\u0002\u0002\u0002\u0136\u0134\u0003\u0002\u0002\u0002\u0137",
    "C\u0003\u0002\u0002\u0002\u0138\u013a\u0005\b\u0005\u0002\u0139\u013b",
    "\t\n\u0002\u0002\u013a\u0139\u0003\u0002\u0002\u0002\u013a\u013b\u0003",
    "\u0002\u0002\u0002\u013bE\u0003\u0002\u0002\u0002\u013c\u0146\u0007",
    "6\u0002\u0002\u013d\u0146\u0007>\u0002\u0002\u013e\u0146\u0005P)\u0002",
    "\u013f\u0146\u0005H%\u0002\u0140\u0146\u0005J&\u0002\u0141\u0142\u0007",
    "\u0005\u0002\u0002\u0142\u0143\u0005\u0002\u0002\u0002\u0143\u0144\u0007",
    "\u0006\u0002\u0002\u0144\u0146\u0003\u0002\u0002\u0002\u0145\u013c\u0003",
    "\u0002\u0002\u0002\u0145\u013d\u0003\u0002\u0002\u0002\u0145\u013e\u0003",
    "\u0002\u0002\u0002\u0145\u013f\u0003\u0002\u0002\u0002\u0145\u0140\u0003",
    "\u0002\u0002\u0002\u0145\u0141\u0003\u0002\u0002\u0002\u0146G\u0003",
    "\u0002\u0002\u0002\u0147\u0149\u0007\u0007\u0002\u0002\u0148\u014a\u0005",
    "\u0004\u0003\u0002\u0149\u0148\u0003\u0002\u0002\u0002\u0149\u014a\u0003",
    "\u0002\u0002\u0002\u014a\u0151\u0003\u0002\u0002\u0002\u014b\u014d\u0007",
    "\u0003\u0002\u0002\u014c\u014e\u0005\u0004\u0003\u0002\u014d\u014c\u0003",
    "\u0002\u0002\u0002\u014d\u014e\u0003\u0002\u0002\u0002\u014e\u0150\u0003",
    "\u0002\u0002\u0002\u014f\u014b\u0003\u0002\u0002\u0002\u0150\u0153\u0003",
    "\u0002\u0002\u0002\u0151\u014f\u0003\u0002\u0002\u0002\u0151\u0152\u0003",
    "\u0002\u0002\u0002\u0152\u0154\u0003\u0002\u0002\u0002\u0153\u0151\u0003",
    "\u0002\u0002\u0002\u0154\u0155\u0007\b\u0002\u0002\u0155I\u0003\u0002",
    "\u0002\u0002\u0156\u0157\u00077\u0002\u0002\u0157\u015c\u0005L\'\u0002",
    "\u0158\u0159\u0007\u0003\u0002\u0002\u0159\u015b\u0005L\'\u0002\u015a",
    "\u0158\u0003\u0002\u0002\u0002\u015b\u015e\u0003\u0002\u0002\u0002\u015c",
    "\u015a\u0003\u0002\u0002\u0002\u015c\u015d\u0003\u0002\u0002\u0002\u015d",
    "\u015f\u0003\u0002\u0002\u0002\u015e\u015c\u0003\u0002\u0002\u0002\u015f",
    "\u0160\u00078\u0002\u0002\u0160K\u0003\u0002\u0002\u0002\u0161\u0162",
    "\u0005N(\u0002\u0162\u0163\u0007\u0017\u0002\u0002\u0163\u0164\u0005",
    "\u0004\u0003\u0002\u0164M\u0003\u0002\u0002\u0002\u0165\u0166\t\u000b",
    "\u0002\u0002\u0166O\u0003\u0002\u0002\u0002\u0167\u0168\t\f\u0002\u0002",
    "\u0168Q\u0003\u0002\u0002\u0002&W`elqv}\u0083\u008b\u008e\u00a1\u00a9",
    "\u00b0\u00b8\u00c0\u00c8\u00d0\u00d8\u00e0\u00e8\u00f0\u00f8\u0100\u0108",
    "\u0110\u0118\u0120\u0128\u0130\u0136\u013a\u0145\u0149\u014d\u0151\u015c"].join("");


var atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

var decisionsToDFA = atn.decisionToState.map( function(ds, index) { return new antlr4.dfa.DFA(ds, index); });

var sharedContextCache = new antlr4.PredictionContextCache();

var literalNames = [ null, "','", "'new'", "'('", "')'", "'['", "']'", "'.'", 
                     "'='", "'*='", "'/='", "'%='", "'+='", "'-='", "'<<='", 
                     "'>>='", "'>>>='", "'&='", "'^='", "'|='", "'?'", "':'", 
                     "'||'", "'&&'", "'|'", "'^'", "'&'", "'=='", "'!='", 
                     "'==='", "'!=='", "'<'", "'>'", "'<='", "'>='", "'instanceof'", 
                     "'in'", "'<<'", "'>>'", "'>>>'", "'+'", "'-'", "'*'", 
                     "'/'", "'%'", "'delete'", "'void'", "'typeof'", "'++'", 
                     "'--'", "'~'", "'!'", "'this'", "'{'", "'}'", "'null'", 
                     "'true'", "'false'" ];

var symbolicNames = [ null, null, null, null, null, null, null, null, null, 
                      null, null, null, null, null, null, null, null, null, 
                      null, null, null, null, null, null, null, null, null, 
                      null, null, null, null, null, null, null, null, null, 
                      null, null, null, null, null, null, null, null, null, 
                      null, null, null, null, null, null, null, null, null, 
                      null, null, null, null, "StringLiteral", "NumericLiteral", 
                      "Identifier", "WS", "COMMENT", "LINE_COMMENT" ];

var ruleNames =  [ "expression", "assignmentExpression", "assignmentExpressionNoIn", 
                   "leftHandSideExpression", "newExpression", "memberExpression", 
                   "memberExpressionSuffix", "callExpression", "callExpressionSuffix", 
                   "arguments", "indexSuffix", "propertyReferenceSuffix", 
                   "assignmentOperator", "conditionalExpression", "conditionalExpressionNoIn", 
                   "logicalORExpression", "logicalORExpressionNoIn", "logicalANDExpression", 
                   "logicalANDExpressionNoIn", "bitwiseORExpression", "bitwiseORExpressionNoIn", 
                   "bitwiseXORExpression", "bitwiseXORExpressionNoIn", "bitwiseANDExpression", 
                   "bitwiseANDExpressionNoIn", "equalityExpression", "equalityExpressionNoIn", 
                   "relationalExpression", "relationalExpressionNoIn", "shiftExpression", 
                   "additiveExpression", "multiplicativeExpression", "unaryExpression", 
                   "postfixExpression", "primaryExpression", "arrayLiteral", 
                   "objectLiteral", "propertyNameAndValue", "propertyName", 
                   "literal" ];

function ExpressionParser (input) {
	antlr4.Parser.call(this, input);
    this._interp = new antlr4.atn.ParserATNSimulator(this, atn, decisionsToDFA, sharedContextCache);
    this.ruleNames = ruleNames;
    this.literalNames = literalNames;
    this.symbolicNames = symbolicNames;
    return this;
}

ExpressionParser.prototype = Object.create(antlr4.Parser.prototype);
ExpressionParser.prototype.constructor = ExpressionParser;

Object.defineProperty(ExpressionParser.prototype, "atn", {
	get : function() {
		return atn;
	}
});

ExpressionParser.EOF = antlr4.Token.EOF;
ExpressionParser.T__0 = 1;
ExpressionParser.T__1 = 2;
ExpressionParser.T__2 = 3;
ExpressionParser.T__3 = 4;
ExpressionParser.T__4 = 5;
ExpressionParser.T__5 = 6;
ExpressionParser.T__6 = 7;
ExpressionParser.T__7 = 8;
ExpressionParser.T__8 = 9;
ExpressionParser.T__9 = 10;
ExpressionParser.T__10 = 11;
ExpressionParser.T__11 = 12;
ExpressionParser.T__12 = 13;
ExpressionParser.T__13 = 14;
ExpressionParser.T__14 = 15;
ExpressionParser.T__15 = 16;
ExpressionParser.T__16 = 17;
ExpressionParser.T__17 = 18;
ExpressionParser.T__18 = 19;
ExpressionParser.T__19 = 20;
ExpressionParser.T__20 = 21;
ExpressionParser.T__21 = 22;
ExpressionParser.T__22 = 23;
ExpressionParser.T__23 = 24;
ExpressionParser.T__24 = 25;
ExpressionParser.T__25 = 26;
ExpressionParser.T__26 = 27;
ExpressionParser.T__27 = 28;
ExpressionParser.T__28 = 29;
ExpressionParser.T__29 = 30;
ExpressionParser.T__30 = 31;
ExpressionParser.T__31 = 32;
ExpressionParser.T__32 = 33;
ExpressionParser.T__33 = 34;
ExpressionParser.T__34 = 35;
ExpressionParser.T__35 = 36;
ExpressionParser.T__36 = 37;
ExpressionParser.T__37 = 38;
ExpressionParser.T__38 = 39;
ExpressionParser.T__39 = 40;
ExpressionParser.T__40 = 41;
ExpressionParser.T__41 = 42;
ExpressionParser.T__42 = 43;
ExpressionParser.T__43 = 44;
ExpressionParser.T__44 = 45;
ExpressionParser.T__45 = 46;
ExpressionParser.T__46 = 47;
ExpressionParser.T__47 = 48;
ExpressionParser.T__48 = 49;
ExpressionParser.T__49 = 50;
ExpressionParser.T__50 = 51;
ExpressionParser.T__51 = 52;
ExpressionParser.T__52 = 53;
ExpressionParser.T__53 = 54;
ExpressionParser.T__54 = 55;
ExpressionParser.T__55 = 56;
ExpressionParser.T__56 = 57;
ExpressionParser.StringLiteral = 58;
ExpressionParser.NumericLiteral = 59;
ExpressionParser.Identifier = 60;
ExpressionParser.WS = 61;
ExpressionParser.COMMENT = 62;
ExpressionParser.LINE_COMMENT = 63;

ExpressionParser.RULE_expression = 0;
ExpressionParser.RULE_assignmentExpression = 1;
ExpressionParser.RULE_assignmentExpressionNoIn = 2;
ExpressionParser.RULE_leftHandSideExpression = 3;
ExpressionParser.RULE_newExpression = 4;
ExpressionParser.RULE_memberExpression = 5;
ExpressionParser.RULE_memberExpressionSuffix = 6;
ExpressionParser.RULE_callExpression = 7;
ExpressionParser.RULE_callExpressionSuffix = 8;
ExpressionParser.RULE_arguments = 9;
ExpressionParser.RULE_indexSuffix = 10;
ExpressionParser.RULE_propertyReferenceSuffix = 11;
ExpressionParser.RULE_assignmentOperator = 12;
ExpressionParser.RULE_conditionalExpression = 13;
ExpressionParser.RULE_conditionalExpressionNoIn = 14;
ExpressionParser.RULE_logicalORExpression = 15;
ExpressionParser.RULE_logicalORExpressionNoIn = 16;
ExpressionParser.RULE_logicalANDExpression = 17;
ExpressionParser.RULE_logicalANDExpressionNoIn = 18;
ExpressionParser.RULE_bitwiseORExpression = 19;
ExpressionParser.RULE_bitwiseORExpressionNoIn = 20;
ExpressionParser.RULE_bitwiseXORExpression = 21;
ExpressionParser.RULE_bitwiseXORExpressionNoIn = 22;
ExpressionParser.RULE_bitwiseANDExpression = 23;
ExpressionParser.RULE_bitwiseANDExpressionNoIn = 24;
ExpressionParser.RULE_equalityExpression = 25;
ExpressionParser.RULE_equalityExpressionNoIn = 26;
ExpressionParser.RULE_relationalExpression = 27;
ExpressionParser.RULE_relationalExpressionNoIn = 28;
ExpressionParser.RULE_shiftExpression = 29;
ExpressionParser.RULE_additiveExpression = 30;
ExpressionParser.RULE_multiplicativeExpression = 31;
ExpressionParser.RULE_unaryExpression = 32;
ExpressionParser.RULE_postfixExpression = 33;
ExpressionParser.RULE_primaryExpression = 34;
ExpressionParser.RULE_arrayLiteral = 35;
ExpressionParser.RULE_objectLiteral = 36;
ExpressionParser.RULE_propertyNameAndValue = 37;
ExpressionParser.RULE_propertyName = 38;
ExpressionParser.RULE_literal = 39;

function ExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_expression;
    return this;
}

ExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ExpressionContext.prototype.constructor = ExpressionContext;

ExpressionContext.prototype.assignmentExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AssignmentExpressionContext);
    } else {
        return this.getTypedRuleContext(AssignmentExpressionContext,i);
    }
};

ExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterExpression(this);
	}
};

ExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitExpression(this);
	}
};




ExpressionParser.ExpressionContext = ExpressionContext;

ExpressionParser.prototype.expression = function() {

    var localctx = new ExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 0, ExpressionParser.RULE_expression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 80;
        this.assignmentExpression();
        this.state = 85;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__0) {
            this.state = 81;
            this.match(ExpressionParser.T__0);
            this.state = 82;
            this.assignmentExpression();
            this.state = 87;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function AssignmentExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_assignmentExpression;
    return this;
}

AssignmentExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
AssignmentExpressionContext.prototype.constructor = AssignmentExpressionContext;

AssignmentExpressionContext.prototype.conditionalExpression = function() {
    return this.getTypedRuleContext(ConditionalExpressionContext,0);
};

AssignmentExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterAssignmentExpression(this);
	}
};

AssignmentExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitAssignmentExpression(this);
	}
};




ExpressionParser.AssignmentExpressionContext = AssignmentExpressionContext;

ExpressionParser.prototype.assignmentExpression = function() {

    var localctx = new AssignmentExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 2, ExpressionParser.RULE_assignmentExpression);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 88;
        this.conditionalExpression();
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function AssignmentExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_assignmentExpressionNoIn;
    return this;
}

AssignmentExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
AssignmentExpressionNoInContext.prototype.constructor = AssignmentExpressionNoInContext;

AssignmentExpressionNoInContext.prototype.conditionalExpressionNoIn = function() {
    return this.getTypedRuleContext(ConditionalExpressionNoInContext,0);
};

AssignmentExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterAssignmentExpressionNoIn(this);
	}
};

AssignmentExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitAssignmentExpressionNoIn(this);
	}
};




ExpressionParser.AssignmentExpressionNoInContext = AssignmentExpressionNoInContext;

ExpressionParser.prototype.assignmentExpressionNoIn = function() {

    var localctx = new AssignmentExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 4, ExpressionParser.RULE_assignmentExpressionNoIn);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 90;
        this.conditionalExpressionNoIn();
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function LeftHandSideExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_leftHandSideExpression;
    return this;
}

LeftHandSideExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
LeftHandSideExpressionContext.prototype.constructor = LeftHandSideExpressionContext;

LeftHandSideExpressionContext.prototype.callExpression = function() {
    return this.getTypedRuleContext(CallExpressionContext,0);
};

LeftHandSideExpressionContext.prototype.newExpression = function() {
    return this.getTypedRuleContext(NewExpressionContext,0);
};

LeftHandSideExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterLeftHandSideExpression(this);
	}
};

LeftHandSideExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitLeftHandSideExpression(this);
	}
};




ExpressionParser.LeftHandSideExpressionContext = LeftHandSideExpressionContext;

ExpressionParser.prototype.leftHandSideExpression = function() {

    var localctx = new LeftHandSideExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 6, ExpressionParser.RULE_leftHandSideExpression);
    try {
        this.state = 94;
        this._errHandler.sync(this);
        var la_ = this._interp.adaptivePredict(this._input,1,this._ctx);
        switch(la_) {
        case 1:
            this.enterOuterAlt(localctx, 1);
            this.state = 92;
            this.callExpression();
            break;

        case 2:
            this.enterOuterAlt(localctx, 2);
            this.state = 93;
            this.newExpression();
            break;

        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function NewExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_newExpression;
    return this;
}

NewExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
NewExpressionContext.prototype.constructor = NewExpressionContext;

NewExpressionContext.prototype.memberExpression = function() {
    return this.getTypedRuleContext(MemberExpressionContext,0);
};

NewExpressionContext.prototype.newExpression = function() {
    return this.getTypedRuleContext(NewExpressionContext,0);
};

NewExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterNewExpression(this);
	}
};

NewExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitNewExpression(this);
	}
};




ExpressionParser.NewExpressionContext = NewExpressionContext;

ExpressionParser.prototype.newExpression = function() {

    var localctx = new NewExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 8, ExpressionParser.RULE_newExpression);
    try {
        this.state = 99;
        this._errHandler.sync(this);
        var la_ = this._interp.adaptivePredict(this._input,2,this._ctx);
        switch(la_) {
        case 1:
            this.enterOuterAlt(localctx, 1);
            this.state = 96;
            this.memberExpression();
            break;

        case 2:
            this.enterOuterAlt(localctx, 2);
            this.state = 97;
            this.match(ExpressionParser.T__1);
            this.state = 98;
            this.newExpression();
            break;

        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function MemberExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_memberExpression;
    return this;
}

MemberExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
MemberExpressionContext.prototype.constructor = MemberExpressionContext;

MemberExpressionContext.prototype.primaryExpression = function() {
    return this.getTypedRuleContext(PrimaryExpressionContext,0);
};

MemberExpressionContext.prototype.memberExpression = function() {
    return this.getTypedRuleContext(MemberExpressionContext,0);
};

MemberExpressionContext.prototype.arguments = function() {
    return this.getTypedRuleContext(ArgumentsContext,0);
};

MemberExpressionContext.prototype.memberExpressionSuffix = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(MemberExpressionSuffixContext);
    } else {
        return this.getTypedRuleContext(MemberExpressionSuffixContext,i);
    }
};

MemberExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterMemberExpression(this);
	}
};

MemberExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitMemberExpression(this);
	}
};




ExpressionParser.MemberExpressionContext = MemberExpressionContext;

ExpressionParser.prototype.memberExpression = function() {

    var localctx = new MemberExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 10, ExpressionParser.RULE_memberExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 106;
        this._errHandler.sync(this);
        switch(this._input.LA(1)) {
        case ExpressionParser.T__2:
        case ExpressionParser.T__4:
        case ExpressionParser.T__51:
        case ExpressionParser.T__52:
        case ExpressionParser.T__54:
        case ExpressionParser.T__55:
        case ExpressionParser.T__56:
        case ExpressionParser.StringLiteral:
        case ExpressionParser.NumericLiteral:
        case ExpressionParser.Identifier:
            this.state = 101;
            this.primaryExpression();
            break;
        case ExpressionParser.T__1:
            this.state = 102;
            this.match(ExpressionParser.T__1);
            this.state = 103;
            this.memberExpression();
            this.state = 104;
            this.arguments();
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
        }
        this.state = 111;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__4 || _la===ExpressionParser.T__6) {
            this.state = 108;
            this.memberExpressionSuffix();
            this.state = 113;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function MemberExpressionSuffixContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_memberExpressionSuffix;
    return this;
}

MemberExpressionSuffixContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
MemberExpressionSuffixContext.prototype.constructor = MemberExpressionSuffixContext;

MemberExpressionSuffixContext.prototype.indexSuffix = function() {
    return this.getTypedRuleContext(IndexSuffixContext,0);
};

MemberExpressionSuffixContext.prototype.propertyReferenceSuffix = function() {
    return this.getTypedRuleContext(PropertyReferenceSuffixContext,0);
};

MemberExpressionSuffixContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterMemberExpressionSuffix(this);
	}
};

MemberExpressionSuffixContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitMemberExpressionSuffix(this);
	}
};




ExpressionParser.MemberExpressionSuffixContext = MemberExpressionSuffixContext;

ExpressionParser.prototype.memberExpressionSuffix = function() {

    var localctx = new MemberExpressionSuffixContext(this, this._ctx, this.state);
    this.enterRule(localctx, 12, ExpressionParser.RULE_memberExpressionSuffix);
    try {
        this.state = 116;
        this._errHandler.sync(this);
        switch(this._input.LA(1)) {
        case ExpressionParser.T__4:
            this.enterOuterAlt(localctx, 1);
            this.state = 114;
            this.indexSuffix();
            break;
        case ExpressionParser.T__6:
            this.enterOuterAlt(localctx, 2);
            this.state = 115;
            this.propertyReferenceSuffix();
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function CallExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_callExpression;
    return this;
}

CallExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
CallExpressionContext.prototype.constructor = CallExpressionContext;

CallExpressionContext.prototype.memberExpression = function() {
    return this.getTypedRuleContext(MemberExpressionContext,0);
};

CallExpressionContext.prototype.arguments = function() {
    return this.getTypedRuleContext(ArgumentsContext,0);
};

CallExpressionContext.prototype.callExpressionSuffix = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(CallExpressionSuffixContext);
    } else {
        return this.getTypedRuleContext(CallExpressionSuffixContext,i);
    }
};

CallExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterCallExpression(this);
	}
};

CallExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitCallExpression(this);
	}
};




ExpressionParser.CallExpressionContext = CallExpressionContext;

ExpressionParser.prototype.callExpression = function() {

    var localctx = new CallExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 14, ExpressionParser.RULE_callExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 118;
        this.memberExpression();
        this.state = 119;
        this.arguments();
        this.state = 123;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__2) | (1 << ExpressionParser.T__4) | (1 << ExpressionParser.T__6))) !== 0)) {
            this.state = 120;
            this.callExpressionSuffix();
            this.state = 125;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function CallExpressionSuffixContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_callExpressionSuffix;
    return this;
}

CallExpressionSuffixContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
CallExpressionSuffixContext.prototype.constructor = CallExpressionSuffixContext;

CallExpressionSuffixContext.prototype.arguments = function() {
    return this.getTypedRuleContext(ArgumentsContext,0);
};

CallExpressionSuffixContext.prototype.indexSuffix = function() {
    return this.getTypedRuleContext(IndexSuffixContext,0);
};

CallExpressionSuffixContext.prototype.propertyReferenceSuffix = function() {
    return this.getTypedRuleContext(PropertyReferenceSuffixContext,0);
};

CallExpressionSuffixContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterCallExpressionSuffix(this);
	}
};

CallExpressionSuffixContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitCallExpressionSuffix(this);
	}
};




ExpressionParser.CallExpressionSuffixContext = CallExpressionSuffixContext;

ExpressionParser.prototype.callExpressionSuffix = function() {

    var localctx = new CallExpressionSuffixContext(this, this._ctx, this.state);
    this.enterRule(localctx, 16, ExpressionParser.RULE_callExpressionSuffix);
    try {
        this.state = 129;
        this._errHandler.sync(this);
        switch(this._input.LA(1)) {
        case ExpressionParser.T__2:
            this.enterOuterAlt(localctx, 1);
            this.state = 126;
            this.arguments();
            break;
        case ExpressionParser.T__4:
            this.enterOuterAlt(localctx, 2);
            this.state = 127;
            this.indexSuffix();
            break;
        case ExpressionParser.T__6:
            this.enterOuterAlt(localctx, 3);
            this.state = 128;
            this.propertyReferenceSuffix();
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function ArgumentsContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_arguments;
    return this;
}

ArgumentsContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ArgumentsContext.prototype.constructor = ArgumentsContext;

ArgumentsContext.prototype.assignmentExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AssignmentExpressionContext);
    } else {
        return this.getTypedRuleContext(AssignmentExpressionContext,i);
    }
};

ArgumentsContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterArguments(this);
	}
};

ArgumentsContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitArguments(this);
	}
};




ExpressionParser.ArgumentsContext = ArgumentsContext;

ExpressionParser.prototype.arguments = function() {

    var localctx = new ArgumentsContext(this, this._ctx, this.state);
    this.enterRule(localctx, 18, ExpressionParser.RULE_arguments);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 131;
        this.match(ExpressionParser.T__2);
        this.state = 140;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__1) | (1 << ExpressionParser.T__2) | (1 << ExpressionParser.T__4))) !== 0) || ((((_la - 40)) & ~0x1f) == 0 && ((1 << (_la - 40)) & ((1 << (ExpressionParser.T__39 - 40)) | (1 << (ExpressionParser.T__40 - 40)) | (1 << (ExpressionParser.T__44 - 40)) | (1 << (ExpressionParser.T__45 - 40)) | (1 << (ExpressionParser.T__46 - 40)) | (1 << (ExpressionParser.T__47 - 40)) | (1 << (ExpressionParser.T__48 - 40)) | (1 << (ExpressionParser.T__49 - 40)) | (1 << (ExpressionParser.T__50 - 40)) | (1 << (ExpressionParser.T__51 - 40)) | (1 << (ExpressionParser.T__52 - 40)) | (1 << (ExpressionParser.T__54 - 40)) | (1 << (ExpressionParser.T__55 - 40)) | (1 << (ExpressionParser.T__56 - 40)) | (1 << (ExpressionParser.StringLiteral - 40)) | (1 << (ExpressionParser.NumericLiteral - 40)) | (1 << (ExpressionParser.Identifier - 40)))) !== 0)) {
            this.state = 132;
            this.assignmentExpression();
            this.state = 137;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            while(_la===ExpressionParser.T__0) {
                this.state = 133;
                this.match(ExpressionParser.T__0);
                this.state = 134;
                this.assignmentExpression();
                this.state = 139;
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
        }

        this.state = 142;
        this.match(ExpressionParser.T__3);
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function IndexSuffixContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_indexSuffix;
    return this;
}

IndexSuffixContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
IndexSuffixContext.prototype.constructor = IndexSuffixContext;

IndexSuffixContext.prototype.expression = function() {
    return this.getTypedRuleContext(ExpressionContext,0);
};

IndexSuffixContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterIndexSuffix(this);
	}
};

IndexSuffixContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitIndexSuffix(this);
	}
};




ExpressionParser.IndexSuffixContext = IndexSuffixContext;

ExpressionParser.prototype.indexSuffix = function() {

    var localctx = new IndexSuffixContext(this, this._ctx, this.state);
    this.enterRule(localctx, 20, ExpressionParser.RULE_indexSuffix);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 144;
        this.match(ExpressionParser.T__4);
        this.state = 145;
        this.expression();
        this.state = 146;
        this.match(ExpressionParser.T__5);
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function PropertyReferenceSuffixContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_propertyReferenceSuffix;
    return this;
}

PropertyReferenceSuffixContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
PropertyReferenceSuffixContext.prototype.constructor = PropertyReferenceSuffixContext;

PropertyReferenceSuffixContext.prototype.Identifier = function() {
    return this.getToken(ExpressionParser.Identifier, 0);
};

PropertyReferenceSuffixContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterPropertyReferenceSuffix(this);
	}
};

PropertyReferenceSuffixContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitPropertyReferenceSuffix(this);
	}
};




ExpressionParser.PropertyReferenceSuffixContext = PropertyReferenceSuffixContext;

ExpressionParser.prototype.propertyReferenceSuffix = function() {

    var localctx = new PropertyReferenceSuffixContext(this, this._ctx, this.state);
    this.enterRule(localctx, 22, ExpressionParser.RULE_propertyReferenceSuffix);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 148;
        this.match(ExpressionParser.T__6);
        this.state = 149;
        this.match(ExpressionParser.Identifier);
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function AssignmentOperatorContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_assignmentOperator;
    return this;
}

AssignmentOperatorContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
AssignmentOperatorContext.prototype.constructor = AssignmentOperatorContext;


AssignmentOperatorContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterAssignmentOperator(this);
	}
};

AssignmentOperatorContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitAssignmentOperator(this);
	}
};




ExpressionParser.AssignmentOperatorContext = AssignmentOperatorContext;

ExpressionParser.prototype.assignmentOperator = function() {

    var localctx = new AssignmentOperatorContext(this, this._ctx, this.state);
    this.enterRule(localctx, 24, ExpressionParser.RULE_assignmentOperator);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 151;
        _la = this._input.LA(1);
        if(!((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__7) | (1 << ExpressionParser.T__8) | (1 << ExpressionParser.T__9) | (1 << ExpressionParser.T__10) | (1 << ExpressionParser.T__11) | (1 << ExpressionParser.T__12) | (1 << ExpressionParser.T__13) | (1 << ExpressionParser.T__14) | (1 << ExpressionParser.T__15) | (1 << ExpressionParser.T__16) | (1 << ExpressionParser.T__17) | (1 << ExpressionParser.T__18))) !== 0))) {
        this._errHandler.recoverInline(this);
        }
        else {
        	this._errHandler.reportMatch(this);
            this.consume();
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function ConditionalExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_conditionalExpression;
    return this;
}

ConditionalExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ConditionalExpressionContext.prototype.constructor = ConditionalExpressionContext;

ConditionalExpressionContext.prototype.logicalORExpression = function() {
    return this.getTypedRuleContext(LogicalORExpressionContext,0);
};

ConditionalExpressionContext.prototype.assignmentExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AssignmentExpressionContext);
    } else {
        return this.getTypedRuleContext(AssignmentExpressionContext,i);
    }
};

ConditionalExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterConditionalExpression(this);
	}
};

ConditionalExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitConditionalExpression(this);
	}
};




ExpressionParser.ConditionalExpressionContext = ConditionalExpressionContext;

ExpressionParser.prototype.conditionalExpression = function() {

    var localctx = new ConditionalExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 26, ExpressionParser.RULE_conditionalExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 153;
        this.logicalORExpression();
        this.state = 159;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if(_la===ExpressionParser.T__19) {
            this.state = 154;
            this.match(ExpressionParser.T__19);
            this.state = 155;
            this.assignmentExpression();
            this.state = 156;
            this.match(ExpressionParser.T__20);
            this.state = 157;
            this.assignmentExpression();
        }

    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function ConditionalExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_conditionalExpressionNoIn;
    return this;
}

ConditionalExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ConditionalExpressionNoInContext.prototype.constructor = ConditionalExpressionNoInContext;

ConditionalExpressionNoInContext.prototype.logicalORExpressionNoIn = function() {
    return this.getTypedRuleContext(LogicalORExpressionNoInContext,0);
};

ConditionalExpressionNoInContext.prototype.assignmentExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AssignmentExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(AssignmentExpressionNoInContext,i);
    }
};

ConditionalExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterConditionalExpressionNoIn(this);
	}
};

ConditionalExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitConditionalExpressionNoIn(this);
	}
};




ExpressionParser.ConditionalExpressionNoInContext = ConditionalExpressionNoInContext;

ExpressionParser.prototype.conditionalExpressionNoIn = function() {

    var localctx = new ConditionalExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 28, ExpressionParser.RULE_conditionalExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 161;
        this.logicalORExpressionNoIn();
        this.state = 167;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if(_la===ExpressionParser.T__19) {
            this.state = 162;
            this.match(ExpressionParser.T__19);
            this.state = 163;
            this.assignmentExpressionNoIn();
            this.state = 164;
            this.match(ExpressionParser.T__20);
            this.state = 165;
            this.assignmentExpressionNoIn();
        }

    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function LogicalORExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_logicalORExpression;
    return this;
}

LogicalORExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
LogicalORExpressionContext.prototype.constructor = LogicalORExpressionContext;

LogicalORExpressionContext.prototype.logicalANDExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(LogicalANDExpressionContext);
    } else {
        return this.getTypedRuleContext(LogicalANDExpressionContext,i);
    }
};

LogicalORExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterLogicalORExpression(this);
	}
};

LogicalORExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitLogicalORExpression(this);
	}
};




ExpressionParser.LogicalORExpressionContext = LogicalORExpressionContext;

ExpressionParser.prototype.logicalORExpression = function() {

    var localctx = new LogicalORExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 30, ExpressionParser.RULE_logicalORExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 169;
        this.logicalANDExpression();
        this.state = 174;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__21) {
            this.state = 170;
            this.match(ExpressionParser.T__21);
            this.state = 171;
            this.logicalANDExpression();
            this.state = 176;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function LogicalORExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_logicalORExpressionNoIn;
    return this;
}

LogicalORExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
LogicalORExpressionNoInContext.prototype.constructor = LogicalORExpressionNoInContext;

LogicalORExpressionNoInContext.prototype.logicalANDExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(LogicalANDExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(LogicalANDExpressionNoInContext,i);
    }
};

LogicalORExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterLogicalORExpressionNoIn(this);
	}
};

LogicalORExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitLogicalORExpressionNoIn(this);
	}
};




ExpressionParser.LogicalORExpressionNoInContext = LogicalORExpressionNoInContext;

ExpressionParser.prototype.logicalORExpressionNoIn = function() {

    var localctx = new LogicalORExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 32, ExpressionParser.RULE_logicalORExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 177;
        this.logicalANDExpressionNoIn();
        this.state = 182;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__21) {
            this.state = 178;
            this.match(ExpressionParser.T__21);
            this.state = 179;
            this.logicalANDExpressionNoIn();
            this.state = 184;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function LogicalANDExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_logicalANDExpression;
    return this;
}

LogicalANDExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
LogicalANDExpressionContext.prototype.constructor = LogicalANDExpressionContext;

LogicalANDExpressionContext.prototype.bitwiseORExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(BitwiseORExpressionContext);
    } else {
        return this.getTypedRuleContext(BitwiseORExpressionContext,i);
    }
};

LogicalANDExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterLogicalANDExpression(this);
	}
};

LogicalANDExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitLogicalANDExpression(this);
	}
};




ExpressionParser.LogicalANDExpressionContext = LogicalANDExpressionContext;

ExpressionParser.prototype.logicalANDExpression = function() {

    var localctx = new LogicalANDExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 34, ExpressionParser.RULE_logicalANDExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 185;
        this.bitwiseORExpression();
        this.state = 190;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__22) {
            this.state = 186;
            this.match(ExpressionParser.T__22);
            this.state = 187;
            this.bitwiseORExpression();
            this.state = 192;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function LogicalANDExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_logicalANDExpressionNoIn;
    return this;
}

LogicalANDExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
LogicalANDExpressionNoInContext.prototype.constructor = LogicalANDExpressionNoInContext;

LogicalANDExpressionNoInContext.prototype.bitwiseORExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(BitwiseORExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(BitwiseORExpressionNoInContext,i);
    }
};

LogicalANDExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterLogicalANDExpressionNoIn(this);
	}
};

LogicalANDExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitLogicalANDExpressionNoIn(this);
	}
};




ExpressionParser.LogicalANDExpressionNoInContext = LogicalANDExpressionNoInContext;

ExpressionParser.prototype.logicalANDExpressionNoIn = function() {

    var localctx = new LogicalANDExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 36, ExpressionParser.RULE_logicalANDExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 193;
        this.bitwiseORExpressionNoIn();
        this.state = 198;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__22) {
            this.state = 194;
            this.match(ExpressionParser.T__22);
            this.state = 195;
            this.bitwiseORExpressionNoIn();
            this.state = 200;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function BitwiseORExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_bitwiseORExpression;
    return this;
}

BitwiseORExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
BitwiseORExpressionContext.prototype.constructor = BitwiseORExpressionContext;

BitwiseORExpressionContext.prototype.bitwiseXORExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(BitwiseXORExpressionContext);
    } else {
        return this.getTypedRuleContext(BitwiseXORExpressionContext,i);
    }
};

BitwiseORExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterBitwiseORExpression(this);
	}
};

BitwiseORExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitBitwiseORExpression(this);
	}
};




ExpressionParser.BitwiseORExpressionContext = BitwiseORExpressionContext;

ExpressionParser.prototype.bitwiseORExpression = function() {

    var localctx = new BitwiseORExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 38, ExpressionParser.RULE_bitwiseORExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 201;
        this.bitwiseXORExpression();
        this.state = 206;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__23) {
            this.state = 202;
            this.match(ExpressionParser.T__23);
            this.state = 203;
            this.bitwiseXORExpression();
            this.state = 208;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function BitwiseORExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_bitwiseORExpressionNoIn;
    return this;
}

BitwiseORExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
BitwiseORExpressionNoInContext.prototype.constructor = BitwiseORExpressionNoInContext;

BitwiseORExpressionNoInContext.prototype.bitwiseXORExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(BitwiseXORExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(BitwiseXORExpressionNoInContext,i);
    }
};

BitwiseORExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterBitwiseORExpressionNoIn(this);
	}
};

BitwiseORExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitBitwiseORExpressionNoIn(this);
	}
};




ExpressionParser.BitwiseORExpressionNoInContext = BitwiseORExpressionNoInContext;

ExpressionParser.prototype.bitwiseORExpressionNoIn = function() {

    var localctx = new BitwiseORExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 40, ExpressionParser.RULE_bitwiseORExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 209;
        this.bitwiseXORExpressionNoIn();
        this.state = 214;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__23) {
            this.state = 210;
            this.match(ExpressionParser.T__23);
            this.state = 211;
            this.bitwiseXORExpressionNoIn();
            this.state = 216;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function BitwiseXORExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_bitwiseXORExpression;
    return this;
}

BitwiseXORExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
BitwiseXORExpressionContext.prototype.constructor = BitwiseXORExpressionContext;

BitwiseXORExpressionContext.prototype.bitwiseANDExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(BitwiseANDExpressionContext);
    } else {
        return this.getTypedRuleContext(BitwiseANDExpressionContext,i);
    }
};

BitwiseXORExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterBitwiseXORExpression(this);
	}
};

BitwiseXORExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitBitwiseXORExpression(this);
	}
};




ExpressionParser.BitwiseXORExpressionContext = BitwiseXORExpressionContext;

ExpressionParser.prototype.bitwiseXORExpression = function() {

    var localctx = new BitwiseXORExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 42, ExpressionParser.RULE_bitwiseXORExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 217;
        this.bitwiseANDExpression();
        this.state = 222;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__24) {
            this.state = 218;
            this.match(ExpressionParser.T__24);
            this.state = 219;
            this.bitwiseANDExpression();
            this.state = 224;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function BitwiseXORExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_bitwiseXORExpressionNoIn;
    return this;
}

BitwiseXORExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
BitwiseXORExpressionNoInContext.prototype.constructor = BitwiseXORExpressionNoInContext;

BitwiseXORExpressionNoInContext.prototype.bitwiseANDExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(BitwiseANDExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(BitwiseANDExpressionNoInContext,i);
    }
};

BitwiseXORExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterBitwiseXORExpressionNoIn(this);
	}
};

BitwiseXORExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitBitwiseXORExpressionNoIn(this);
	}
};




ExpressionParser.BitwiseXORExpressionNoInContext = BitwiseXORExpressionNoInContext;

ExpressionParser.prototype.bitwiseXORExpressionNoIn = function() {

    var localctx = new BitwiseXORExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 44, ExpressionParser.RULE_bitwiseXORExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 225;
        this.bitwiseANDExpressionNoIn();
        this.state = 230;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__24) {
            this.state = 226;
            this.match(ExpressionParser.T__24);
            this.state = 227;
            this.bitwiseANDExpressionNoIn();
            this.state = 232;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function BitwiseANDExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_bitwiseANDExpression;
    return this;
}

BitwiseANDExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
BitwiseANDExpressionContext.prototype.constructor = BitwiseANDExpressionContext;

BitwiseANDExpressionContext.prototype.equalityExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(EqualityExpressionContext);
    } else {
        return this.getTypedRuleContext(EqualityExpressionContext,i);
    }
};

BitwiseANDExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterBitwiseANDExpression(this);
	}
};

BitwiseANDExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitBitwiseANDExpression(this);
	}
};




ExpressionParser.BitwiseANDExpressionContext = BitwiseANDExpressionContext;

ExpressionParser.prototype.bitwiseANDExpression = function() {

    var localctx = new BitwiseANDExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 46, ExpressionParser.RULE_bitwiseANDExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 233;
        this.equalityExpression();
        this.state = 238;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__25) {
            this.state = 234;
            this.match(ExpressionParser.T__25);
            this.state = 235;
            this.equalityExpression();
            this.state = 240;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function BitwiseANDExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_bitwiseANDExpressionNoIn;
    return this;
}

BitwiseANDExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
BitwiseANDExpressionNoInContext.prototype.constructor = BitwiseANDExpressionNoInContext;

BitwiseANDExpressionNoInContext.prototype.equalityExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(EqualityExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(EqualityExpressionNoInContext,i);
    }
};

BitwiseANDExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterBitwiseANDExpressionNoIn(this);
	}
};

BitwiseANDExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitBitwiseANDExpressionNoIn(this);
	}
};




ExpressionParser.BitwiseANDExpressionNoInContext = BitwiseANDExpressionNoInContext;

ExpressionParser.prototype.bitwiseANDExpressionNoIn = function() {

    var localctx = new BitwiseANDExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 48, ExpressionParser.RULE_bitwiseANDExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 241;
        this.equalityExpressionNoIn();
        this.state = 246;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__25) {
            this.state = 242;
            this.match(ExpressionParser.T__25);
            this.state = 243;
            this.equalityExpressionNoIn();
            this.state = 248;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function EqualityExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_equalityExpression;
    return this;
}

EqualityExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
EqualityExpressionContext.prototype.constructor = EqualityExpressionContext;

EqualityExpressionContext.prototype.relationalExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(RelationalExpressionContext);
    } else {
        return this.getTypedRuleContext(RelationalExpressionContext,i);
    }
};

EqualityExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterEqualityExpression(this);
	}
};

EqualityExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitEqualityExpression(this);
	}
};




ExpressionParser.EqualityExpressionContext = EqualityExpressionContext;

ExpressionParser.prototype.equalityExpression = function() {

    var localctx = new EqualityExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 50, ExpressionParser.RULE_equalityExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 249;
        this.relationalExpression();
        this.state = 254;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__26) | (1 << ExpressionParser.T__27) | (1 << ExpressionParser.T__28) | (1 << ExpressionParser.T__29))) !== 0)) {
            this.state = 250;
            _la = this._input.LA(1);
            if(!((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__26) | (1 << ExpressionParser.T__27) | (1 << ExpressionParser.T__28) | (1 << ExpressionParser.T__29))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 251;
            this.relationalExpression();
            this.state = 256;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function EqualityExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_equalityExpressionNoIn;
    return this;
}

EqualityExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
EqualityExpressionNoInContext.prototype.constructor = EqualityExpressionNoInContext;

EqualityExpressionNoInContext.prototype.relationalExpressionNoIn = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(RelationalExpressionNoInContext);
    } else {
        return this.getTypedRuleContext(RelationalExpressionNoInContext,i);
    }
};

EqualityExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterEqualityExpressionNoIn(this);
	}
};

EqualityExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitEqualityExpressionNoIn(this);
	}
};




ExpressionParser.EqualityExpressionNoInContext = EqualityExpressionNoInContext;

ExpressionParser.prototype.equalityExpressionNoIn = function() {

    var localctx = new EqualityExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 52, ExpressionParser.RULE_equalityExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 257;
        this.relationalExpressionNoIn();
        this.state = 262;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__26) | (1 << ExpressionParser.T__27) | (1 << ExpressionParser.T__28) | (1 << ExpressionParser.T__29))) !== 0)) {
            this.state = 258;
            _la = this._input.LA(1);
            if(!((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__26) | (1 << ExpressionParser.T__27) | (1 << ExpressionParser.T__28) | (1 << ExpressionParser.T__29))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 259;
            this.relationalExpressionNoIn();
            this.state = 264;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function RelationalExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_relationalExpression;
    return this;
}

RelationalExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
RelationalExpressionContext.prototype.constructor = RelationalExpressionContext;

RelationalExpressionContext.prototype.shiftExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(ShiftExpressionContext);
    } else {
        return this.getTypedRuleContext(ShiftExpressionContext,i);
    }
};

RelationalExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterRelationalExpression(this);
	}
};

RelationalExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitRelationalExpression(this);
	}
};




ExpressionParser.RelationalExpressionContext = RelationalExpressionContext;

ExpressionParser.prototype.relationalExpression = function() {

    var localctx = new RelationalExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 54, ExpressionParser.RULE_relationalExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 265;
        this.shiftExpression();
        this.state = 270;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(((((_la - 31)) & ~0x1f) == 0 && ((1 << (_la - 31)) & ((1 << (ExpressionParser.T__30 - 31)) | (1 << (ExpressionParser.T__31 - 31)) | (1 << (ExpressionParser.T__32 - 31)) | (1 << (ExpressionParser.T__33 - 31)) | (1 << (ExpressionParser.T__34 - 31)) | (1 << (ExpressionParser.T__35 - 31)))) !== 0)) {
            this.state = 266;
            _la = this._input.LA(1);
            if(!(((((_la - 31)) & ~0x1f) == 0 && ((1 << (_la - 31)) & ((1 << (ExpressionParser.T__30 - 31)) | (1 << (ExpressionParser.T__31 - 31)) | (1 << (ExpressionParser.T__32 - 31)) | (1 << (ExpressionParser.T__33 - 31)) | (1 << (ExpressionParser.T__34 - 31)) | (1 << (ExpressionParser.T__35 - 31)))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 267;
            this.shiftExpression();
            this.state = 272;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function RelationalExpressionNoInContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_relationalExpressionNoIn;
    return this;
}

RelationalExpressionNoInContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
RelationalExpressionNoInContext.prototype.constructor = RelationalExpressionNoInContext;

RelationalExpressionNoInContext.prototype.shiftExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(ShiftExpressionContext);
    } else {
        return this.getTypedRuleContext(ShiftExpressionContext,i);
    }
};

RelationalExpressionNoInContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterRelationalExpressionNoIn(this);
	}
};

RelationalExpressionNoInContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitRelationalExpressionNoIn(this);
	}
};




ExpressionParser.RelationalExpressionNoInContext = RelationalExpressionNoInContext;

ExpressionParser.prototype.relationalExpressionNoIn = function() {

    var localctx = new RelationalExpressionNoInContext(this, this._ctx, this.state);
    this.enterRule(localctx, 56, ExpressionParser.RULE_relationalExpressionNoIn);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 273;
        this.shiftExpression();
        this.state = 278;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(((((_la - 31)) & ~0x1f) == 0 && ((1 << (_la - 31)) & ((1 << (ExpressionParser.T__30 - 31)) | (1 << (ExpressionParser.T__31 - 31)) | (1 << (ExpressionParser.T__32 - 31)) | (1 << (ExpressionParser.T__33 - 31)) | (1 << (ExpressionParser.T__34 - 31)))) !== 0)) {
            this.state = 274;
            _la = this._input.LA(1);
            if(!(((((_la - 31)) & ~0x1f) == 0 && ((1 << (_la - 31)) & ((1 << (ExpressionParser.T__30 - 31)) | (1 << (ExpressionParser.T__31 - 31)) | (1 << (ExpressionParser.T__32 - 31)) | (1 << (ExpressionParser.T__33 - 31)) | (1 << (ExpressionParser.T__34 - 31)))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 275;
            this.shiftExpression();
            this.state = 280;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function ShiftExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_shiftExpression;
    return this;
}

ShiftExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ShiftExpressionContext.prototype.constructor = ShiftExpressionContext;

ShiftExpressionContext.prototype.additiveExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AdditiveExpressionContext);
    } else {
        return this.getTypedRuleContext(AdditiveExpressionContext,i);
    }
};

ShiftExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterShiftExpression(this);
	}
};

ShiftExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitShiftExpression(this);
	}
};




ExpressionParser.ShiftExpressionContext = ShiftExpressionContext;

ExpressionParser.prototype.shiftExpression = function() {

    var localctx = new ShiftExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 58, ExpressionParser.RULE_shiftExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 281;
        this.additiveExpression();
        this.state = 286;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(((((_la - 37)) & ~0x1f) == 0 && ((1 << (_la - 37)) & ((1 << (ExpressionParser.T__36 - 37)) | (1 << (ExpressionParser.T__37 - 37)) | (1 << (ExpressionParser.T__38 - 37)))) !== 0)) {
            this.state = 282;
            _la = this._input.LA(1);
            if(!(((((_la - 37)) & ~0x1f) == 0 && ((1 << (_la - 37)) & ((1 << (ExpressionParser.T__36 - 37)) | (1 << (ExpressionParser.T__37 - 37)) | (1 << (ExpressionParser.T__38 - 37)))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 283;
            this.additiveExpression();
            this.state = 288;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function AdditiveExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_additiveExpression;
    return this;
}

AdditiveExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
AdditiveExpressionContext.prototype.constructor = AdditiveExpressionContext;

AdditiveExpressionContext.prototype.multiplicativeExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(MultiplicativeExpressionContext);
    } else {
        return this.getTypedRuleContext(MultiplicativeExpressionContext,i);
    }
};

AdditiveExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterAdditiveExpression(this);
	}
};

AdditiveExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitAdditiveExpression(this);
	}
};




ExpressionParser.AdditiveExpressionContext = AdditiveExpressionContext;

ExpressionParser.prototype.additiveExpression = function() {

    var localctx = new AdditiveExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 60, ExpressionParser.RULE_additiveExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 289;
        this.multiplicativeExpression();
        this.state = 294;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__39 || _la===ExpressionParser.T__40) {
            this.state = 290;
            _la = this._input.LA(1);
            if(!(_la===ExpressionParser.T__39 || _la===ExpressionParser.T__40)) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 291;
            this.multiplicativeExpression();
            this.state = 296;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function MultiplicativeExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_multiplicativeExpression;
    return this;
}

MultiplicativeExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
MultiplicativeExpressionContext.prototype.constructor = MultiplicativeExpressionContext;

MultiplicativeExpressionContext.prototype.unaryExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(UnaryExpressionContext);
    } else {
        return this.getTypedRuleContext(UnaryExpressionContext,i);
    }
};

MultiplicativeExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterMultiplicativeExpression(this);
	}
};

MultiplicativeExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitMultiplicativeExpression(this);
	}
};




ExpressionParser.MultiplicativeExpressionContext = MultiplicativeExpressionContext;

ExpressionParser.prototype.multiplicativeExpression = function() {

    var localctx = new MultiplicativeExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 62, ExpressionParser.RULE_multiplicativeExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 297;
        this.unaryExpression();
        this.state = 302;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(((((_la - 42)) & ~0x1f) == 0 && ((1 << (_la - 42)) & ((1 << (ExpressionParser.T__41 - 42)) | (1 << (ExpressionParser.T__42 - 42)) | (1 << (ExpressionParser.T__43 - 42)))) !== 0)) {
            this.state = 298;
            _la = this._input.LA(1);
            if(!(((((_la - 42)) & ~0x1f) == 0 && ((1 << (_la - 42)) & ((1 << (ExpressionParser.T__41 - 42)) | (1 << (ExpressionParser.T__42 - 42)) | (1 << (ExpressionParser.T__43 - 42)))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 299;
            this.unaryExpression();
            this.state = 304;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function UnaryExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_unaryExpression;
    return this;
}

UnaryExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
UnaryExpressionContext.prototype.constructor = UnaryExpressionContext;

UnaryExpressionContext.prototype.postfixExpression = function() {
    return this.getTypedRuleContext(PostfixExpressionContext,0);
};

UnaryExpressionContext.prototype.unaryExpression = function() {
    return this.getTypedRuleContext(UnaryExpressionContext,0);
};

UnaryExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterUnaryExpression(this);
	}
};

UnaryExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitUnaryExpression(this);
	}
};




ExpressionParser.UnaryExpressionContext = UnaryExpressionContext;

ExpressionParser.prototype.unaryExpression = function() {

    var localctx = new UnaryExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 64, ExpressionParser.RULE_unaryExpression);
    var _la = 0; // Token type
    try {
        this.state = 308;
        this._errHandler.sync(this);
        switch(this._input.LA(1)) {
        case ExpressionParser.T__1:
        case ExpressionParser.T__2:
        case ExpressionParser.T__4:
        case ExpressionParser.T__51:
        case ExpressionParser.T__52:
        case ExpressionParser.T__54:
        case ExpressionParser.T__55:
        case ExpressionParser.T__56:
        case ExpressionParser.StringLiteral:
        case ExpressionParser.NumericLiteral:
        case ExpressionParser.Identifier:
            this.enterOuterAlt(localctx, 1);
            this.state = 305;
            this.postfixExpression();
            break;
        case ExpressionParser.T__39:
        case ExpressionParser.T__40:
        case ExpressionParser.T__44:
        case ExpressionParser.T__45:
        case ExpressionParser.T__46:
        case ExpressionParser.T__47:
        case ExpressionParser.T__48:
        case ExpressionParser.T__49:
        case ExpressionParser.T__50:
            this.enterOuterAlt(localctx, 2);
            this.state = 306;
            _la = this._input.LA(1);
            if(!(((((_la - 40)) & ~0x1f) == 0 && ((1 << (_la - 40)) & ((1 << (ExpressionParser.T__39 - 40)) | (1 << (ExpressionParser.T__40 - 40)) | (1 << (ExpressionParser.T__44 - 40)) | (1 << (ExpressionParser.T__45 - 40)) | (1 << (ExpressionParser.T__46 - 40)) | (1 << (ExpressionParser.T__47 - 40)) | (1 << (ExpressionParser.T__48 - 40)) | (1 << (ExpressionParser.T__49 - 40)) | (1 << (ExpressionParser.T__50 - 40)))) !== 0))) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
            this.state = 307;
            this.unaryExpression();
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function PostfixExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_postfixExpression;
    return this;
}

PostfixExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
PostfixExpressionContext.prototype.constructor = PostfixExpressionContext;

PostfixExpressionContext.prototype.leftHandSideExpression = function() {
    return this.getTypedRuleContext(LeftHandSideExpressionContext,0);
};

PostfixExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterPostfixExpression(this);
	}
};

PostfixExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitPostfixExpression(this);
	}
};




ExpressionParser.PostfixExpressionContext = PostfixExpressionContext;

ExpressionParser.prototype.postfixExpression = function() {

    var localctx = new PostfixExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 66, ExpressionParser.RULE_postfixExpression);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 310;
        this.leftHandSideExpression();
        this.state = 312;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if(_la===ExpressionParser.T__47 || _la===ExpressionParser.T__48) {
            this.state = 311;
            _la = this._input.LA(1);
            if(!(_la===ExpressionParser.T__47 || _la===ExpressionParser.T__48)) {
            this._errHandler.recoverInline(this);
            }
            else {
            	this._errHandler.reportMatch(this);
                this.consume();
            }
        }

    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function PrimaryExpressionContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_primaryExpression;
    return this;
}

PrimaryExpressionContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
PrimaryExpressionContext.prototype.constructor = PrimaryExpressionContext;

PrimaryExpressionContext.prototype.Identifier = function() {
    return this.getToken(ExpressionParser.Identifier, 0);
};

PrimaryExpressionContext.prototype.literal = function() {
    return this.getTypedRuleContext(LiteralContext,0);
};

PrimaryExpressionContext.prototype.arrayLiteral = function() {
    return this.getTypedRuleContext(ArrayLiteralContext,0);
};

PrimaryExpressionContext.prototype.objectLiteral = function() {
    return this.getTypedRuleContext(ObjectLiteralContext,0);
};

PrimaryExpressionContext.prototype.expression = function() {
    return this.getTypedRuleContext(ExpressionContext,0);
};

PrimaryExpressionContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterPrimaryExpression(this);
	}
};

PrimaryExpressionContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitPrimaryExpression(this);
	}
};




ExpressionParser.PrimaryExpressionContext = PrimaryExpressionContext;

ExpressionParser.prototype.primaryExpression = function() {

    var localctx = new PrimaryExpressionContext(this, this._ctx, this.state);
    this.enterRule(localctx, 68, ExpressionParser.RULE_primaryExpression);
    try {
        this.state = 323;
        this._errHandler.sync(this);
        switch(this._input.LA(1)) {
        case ExpressionParser.T__51:
            this.enterOuterAlt(localctx, 1);
            this.state = 314;
            this.match(ExpressionParser.T__51);
            break;
        case ExpressionParser.Identifier:
            this.enterOuterAlt(localctx, 2);
            this.state = 315;
            this.match(ExpressionParser.Identifier);
            break;
        case ExpressionParser.T__54:
        case ExpressionParser.T__55:
        case ExpressionParser.T__56:
        case ExpressionParser.StringLiteral:
        case ExpressionParser.NumericLiteral:
            this.enterOuterAlt(localctx, 3);
            this.state = 316;
            this.literal();
            break;
        case ExpressionParser.T__4:
            this.enterOuterAlt(localctx, 4);
            this.state = 317;
            this.arrayLiteral();
            break;
        case ExpressionParser.T__52:
            this.enterOuterAlt(localctx, 5);
            this.state = 318;
            this.objectLiteral();
            break;
        case ExpressionParser.T__2:
            this.enterOuterAlt(localctx, 6);
            this.state = 319;
            this.match(ExpressionParser.T__2);
            this.state = 320;
            this.expression();
            this.state = 321;
            this.match(ExpressionParser.T__3);
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function ArrayLiteralContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_arrayLiteral;
    return this;
}

ArrayLiteralContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ArrayLiteralContext.prototype.constructor = ArrayLiteralContext;

ArrayLiteralContext.prototype.assignmentExpression = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AssignmentExpressionContext);
    } else {
        return this.getTypedRuleContext(AssignmentExpressionContext,i);
    }
};

ArrayLiteralContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterArrayLiteral(this);
	}
};

ArrayLiteralContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitArrayLiteral(this);
	}
};




ExpressionParser.ArrayLiteralContext = ArrayLiteralContext;

ExpressionParser.prototype.arrayLiteral = function() {

    var localctx = new ArrayLiteralContext(this, this._ctx, this.state);
    this.enterRule(localctx, 70, ExpressionParser.RULE_arrayLiteral);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 325;
        this.match(ExpressionParser.T__4);
        this.state = 327;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        if((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__1) | (1 << ExpressionParser.T__2) | (1 << ExpressionParser.T__4))) !== 0) || ((((_la - 40)) & ~0x1f) == 0 && ((1 << (_la - 40)) & ((1 << (ExpressionParser.T__39 - 40)) | (1 << (ExpressionParser.T__40 - 40)) | (1 << (ExpressionParser.T__44 - 40)) | (1 << (ExpressionParser.T__45 - 40)) | (1 << (ExpressionParser.T__46 - 40)) | (1 << (ExpressionParser.T__47 - 40)) | (1 << (ExpressionParser.T__48 - 40)) | (1 << (ExpressionParser.T__49 - 40)) | (1 << (ExpressionParser.T__50 - 40)) | (1 << (ExpressionParser.T__51 - 40)) | (1 << (ExpressionParser.T__52 - 40)) | (1 << (ExpressionParser.T__54 - 40)) | (1 << (ExpressionParser.T__55 - 40)) | (1 << (ExpressionParser.T__56 - 40)) | (1 << (ExpressionParser.StringLiteral - 40)) | (1 << (ExpressionParser.NumericLiteral - 40)) | (1 << (ExpressionParser.Identifier - 40)))) !== 0)) {
            this.state = 326;
            this.assignmentExpression();
        }

        this.state = 335;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__0) {
            this.state = 329;
            this.match(ExpressionParser.T__0);
            this.state = 331;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            if((((_la) & ~0x1f) == 0 && ((1 << _la) & ((1 << ExpressionParser.T__1) | (1 << ExpressionParser.T__2) | (1 << ExpressionParser.T__4))) !== 0) || ((((_la - 40)) & ~0x1f) == 0 && ((1 << (_la - 40)) & ((1 << (ExpressionParser.T__39 - 40)) | (1 << (ExpressionParser.T__40 - 40)) | (1 << (ExpressionParser.T__44 - 40)) | (1 << (ExpressionParser.T__45 - 40)) | (1 << (ExpressionParser.T__46 - 40)) | (1 << (ExpressionParser.T__47 - 40)) | (1 << (ExpressionParser.T__48 - 40)) | (1 << (ExpressionParser.T__49 - 40)) | (1 << (ExpressionParser.T__50 - 40)) | (1 << (ExpressionParser.T__51 - 40)) | (1 << (ExpressionParser.T__52 - 40)) | (1 << (ExpressionParser.T__54 - 40)) | (1 << (ExpressionParser.T__55 - 40)) | (1 << (ExpressionParser.T__56 - 40)) | (1 << (ExpressionParser.StringLiteral - 40)) | (1 << (ExpressionParser.NumericLiteral - 40)) | (1 << (ExpressionParser.Identifier - 40)))) !== 0)) {
                this.state = 330;
                this.assignmentExpression();
            }

            this.state = 337;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
        this.state = 338;
        this.match(ExpressionParser.T__5);
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function ObjectLiteralContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_objectLiteral;
    return this;
}

ObjectLiteralContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ObjectLiteralContext.prototype.constructor = ObjectLiteralContext;

ObjectLiteralContext.prototype.propertyNameAndValue = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(PropertyNameAndValueContext);
    } else {
        return this.getTypedRuleContext(PropertyNameAndValueContext,i);
    }
};

ObjectLiteralContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterObjectLiteral(this);
	}
};

ObjectLiteralContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitObjectLiteral(this);
	}
};




ExpressionParser.ObjectLiteralContext = ObjectLiteralContext;

ExpressionParser.prototype.objectLiteral = function() {

    var localctx = new ObjectLiteralContext(this, this._ctx, this.state);
    this.enterRule(localctx, 72, ExpressionParser.RULE_objectLiteral);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 340;
        this.match(ExpressionParser.T__52);
        this.state = 341;
        this.propertyNameAndValue();
        this.state = 346;
        this._errHandler.sync(this);
        _la = this._input.LA(1);
        while(_la===ExpressionParser.T__0) {
            this.state = 342;
            this.match(ExpressionParser.T__0);
            this.state = 343;
            this.propertyNameAndValue();
            this.state = 348;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
        }
        this.state = 349;
        this.match(ExpressionParser.T__53);
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function PropertyNameAndValueContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_propertyNameAndValue;
    return this;
}

PropertyNameAndValueContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
PropertyNameAndValueContext.prototype.constructor = PropertyNameAndValueContext;

PropertyNameAndValueContext.prototype.propertyName = function() {
    return this.getTypedRuleContext(PropertyNameContext,0);
};

PropertyNameAndValueContext.prototype.assignmentExpression = function() {
    return this.getTypedRuleContext(AssignmentExpressionContext,0);
};

PropertyNameAndValueContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterPropertyNameAndValue(this);
	}
};

PropertyNameAndValueContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitPropertyNameAndValue(this);
	}
};




ExpressionParser.PropertyNameAndValueContext = PropertyNameAndValueContext;

ExpressionParser.prototype.propertyNameAndValue = function() {

    var localctx = new PropertyNameAndValueContext(this, this._ctx, this.state);
    this.enterRule(localctx, 74, ExpressionParser.RULE_propertyNameAndValue);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 351;
        this.propertyName();
        this.state = 352;
        this.match(ExpressionParser.T__20);
        this.state = 353;
        this.assignmentExpression();
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function PropertyNameContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_propertyName;
    return this;
}

PropertyNameContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
PropertyNameContext.prototype.constructor = PropertyNameContext;

PropertyNameContext.prototype.Identifier = function() {
    return this.getToken(ExpressionParser.Identifier, 0);
};

PropertyNameContext.prototype.StringLiteral = function() {
    return this.getToken(ExpressionParser.StringLiteral, 0);
};

PropertyNameContext.prototype.NumericLiteral = function() {
    return this.getToken(ExpressionParser.NumericLiteral, 0);
};

PropertyNameContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterPropertyName(this);
	}
};

PropertyNameContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitPropertyName(this);
	}
};




ExpressionParser.PropertyNameContext = PropertyNameContext;

ExpressionParser.prototype.propertyName = function() {

    var localctx = new PropertyNameContext(this, this._ctx, this.state);
    this.enterRule(localctx, 76, ExpressionParser.RULE_propertyName);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 355;
        _la = this._input.LA(1);
        if(!(((((_la - 58)) & ~0x1f) == 0 && ((1 << (_la - 58)) & ((1 << (ExpressionParser.StringLiteral - 58)) | (1 << (ExpressionParser.NumericLiteral - 58)) | (1 << (ExpressionParser.Identifier - 58)))) !== 0))) {
        this._errHandler.recoverInline(this);
        }
        else {
        	this._errHandler.reportMatch(this);
            this.consume();
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function LiteralContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = ExpressionParser.RULE_literal;
    return this;
}

LiteralContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
LiteralContext.prototype.constructor = LiteralContext;

LiteralContext.prototype.StringLiteral = function() {
    return this.getToken(ExpressionParser.StringLiteral, 0);
};

LiteralContext.prototype.NumericLiteral = function() {
    return this.getToken(ExpressionParser.NumericLiteral, 0);
};

LiteralContext.prototype.enterRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.enterLiteral(this);
	}
};

LiteralContext.prototype.exitRule = function(listener) {
    if(listener instanceof ExpressionListener ) {
        listener.exitLiteral(this);
	}
};




ExpressionParser.LiteralContext = LiteralContext;

ExpressionParser.prototype.literal = function() {

    var localctx = new LiteralContext(this, this._ctx, this.state);
    this.enterRule(localctx, 78, ExpressionParser.RULE_literal);
    var _la = 0; // Token type
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 357;
        _la = this._input.LA(1);
        if(!(((((_la - 55)) & ~0x1f) == 0 && ((1 << (_la - 55)) & ((1 << (ExpressionParser.T__54 - 55)) | (1 << (ExpressionParser.T__55 - 55)) | (1 << (ExpressionParser.T__56 - 55)) | (1 << (ExpressionParser.StringLiteral - 55)) | (1 << (ExpressionParser.NumericLiteral - 55)))) !== 0))) {
        this._errHandler.recoverInline(this);
        }
        else {
        	this._errHandler.reportMatch(this);
            this.consume();
        }
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};


exports.ExpressionParser = ExpressionParser;
