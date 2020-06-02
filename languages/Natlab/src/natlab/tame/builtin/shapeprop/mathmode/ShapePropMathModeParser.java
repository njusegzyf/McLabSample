package natlab.tame.builtin.shapeprop.mathmode;

import natlab.tame.builtin.shapeprop.mathmode.ast.*;
import java.util.ArrayList;
import java.util.*;
import beaver.*;

/**
 * This class is a LALR parser generated by
 * <a href="http://beaver.sourceforge.net">Beaver</a> v0.9.6.2
 * from the grammar specification "mathmode.grammar".
 */
public class ShapePropMathModeParser extends Parser {
	static public class Terminals {
		static public final short EOF = 0;
		static public final short MINUS = 1;
		static public final short LPARENT = 2;
		static public final short ID = 3;
		static public final short LOWERCASE = 4;
		static public final short NUMBER = 5;
		static public final short RPARENT = 6;
		static public final short PLUS = 7;
		static public final short GT = 8;
		static public final short LT = 9;
		static public final short GE = 10;
		static public final short LE = 11;
		static public final short EQ = 12;
		static public final short NE = 13;
		static public final short AND = 14;
		static public final short LBRACKET = 15;
		static public final short RBRACKET = 16;
		static public final short TIMES = 17;
		static public final short DIV = 18;
		static public final short POWER = 19;
		static public final short COMMA = 20;
	}

	static final ParsingTables PARSING_TABLES = new ParsingTables(
		"U9ojKxbAGq4KFSbBZ376k2GnY$k#fh59OsDZRMDZOsDZOoE8WX0G144GH6pq1mIryQFSuhx" +
		"7PJmpkT51WmHru2ntxiozxysR1#008LJ0XpuuwXXUHD609hIW3Hte#iXLeQT8pcEoPUZ$be" +
		"iZY1h4aFoJo$UEeEwVwltKe$VBdDzmjU5wmus6cmst6cutt6cus$0WoZ50LmctEbGA0OHHY" +
		"uGwWeizegmKutdvk4hN3t1Ta6VxcGiNoLLBjjW#Dnn#1U#VNA5t$9$gbTeZF$Sxhk6fQXT2" +
		"0k3zA6FL21b8PM36Dj5B18azQtwJ61A$HQJ53nD9OaJYVMAKsD6nug2CQI$vSSjFY4wAJeb" +
		"EYyw8pehE4xlM$YNBhxDLrehzbf#r$8OyLqxYEIkNBdY95oo$RVb5qOpz71pBeYkYg$RxMF" +
		"lNH5EQr6sUrPkw8j#e2qQNUfRHQpwlpapqehBKKtL2$qpaY2VYaNWWxitQEtLEVysYCNvbX" +
		"rmANBmT81o1YxV48VG$vX0UGdktqRY1W#GxEZYQ2W==");

	private final Action[] actions;

	public ShapePropMathModeParser() {
		super(PARSING_TABLES);
		actions = new Action[] {
			new Action() {	// [0] mathexpr = LBRACKET expr.e RBRACKET
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 2];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return e;
				}
			},
			new Action() {	// [1] expr = expr.e PLUS T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.PLUS, e,f);
				}
			},
			new Action() {	// [2] expr = expr.e MINUS T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.MINUS, e,f);
				}
			},
			new Action() {	// [3] expr = expr.e GT T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.GT,  e,f);
				}
			},
			new Action() {	// [4] expr = expr.e LT T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.LT,  e,f);
				}
			},
			new Action() {	// [5] expr = expr.e GE T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.GE, e,f);
				}
			},
			new Action() {	// [6] expr = expr.e LE T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.LE, e,f);
				}
			},
			new Action() {	// [7] expr = expr.e EQ T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.EQ, e,f);
				}
			},
			new Action() {	// [8] expr = expr.e NE T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.NE,  e,f);
				}
			},
			new Action() {	// [9] expr = expr.e AND T.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return new SPMathModeBinOpExpr(BinOp.AND,  e,f);
				}
			},
			new Action() {	// [10] expr = T.e
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return e;
				}
			},
			Action.NONE,  	// [11] T = 
			new Action() {	// [12] T = P.f TIMES T.e
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_f = _symbols[offset + 1];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					final Symbol _symbol_e = _symbols[offset + 3];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return new SPMathModeBinOpExpr(BinOp.TIMES,  f,e);
				}
			},
			new Action() {	// [13] T = P.f DIV T.e
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_f = _symbols[offset + 1];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					final Symbol _symbol_e = _symbols[offset + 3];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return new SPMathModeBinOpExpr(BinOp.DIV,  f,e);
				}
			},
			new Action() {	// [14] T = P.p
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_p = _symbols[offset + 1];
					final SPMathModeAbstractExpr p = (SPMathModeAbstractExpr) _symbol_p.value;
					return p;
				}
			},
			new Action() {	// [15] P = F.f POWER P.e
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_f = _symbols[offset + 1];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					final Symbol _symbol_e = _symbols[offset + 3];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return new SPMathModeBinOpExpr(BinOp.POW,  f,e);
				}
			},
			new Action() {	// [16] P = F.f
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_f = _symbols[offset + 1];
					final SPMathModeAbstractExpr f = (SPMathModeAbstractExpr) _symbol_f.value;
					return f;
				}
			},
			new Action() {	// [17] F = LPARENT expr.e RPARENT
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 2];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return e;
				}
			},
			new Action() {	// [18] F = ID.i LPARENT arglist.l RPARENT
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_i = _symbols[offset + 1];
					final String i = (String) _symbol_i.value;
					final Symbol _symbol_l = _symbols[offset + 3];
					final SPMathModeArglist l = (SPMathModeArglist) _symbol_l.value;
					return new SPMathModeFuncCallExpr(i, l);
				}
			},
			new Action() {	// [19] F = LOWERCASE.l
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_l = _symbols[offset + 1];
					final String l = (String) _symbol_l.value;
					return new SPMathModeLowercaseExpr(l);
				}
			},
			new Action() {	// [20] F = MINUS F.l
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_l = _symbols[offset + 2];
					final SPMathModeAbstractExpr l = (SPMathModeAbstractExpr) _symbol_l.value;
					return new SPMathModeUnOpExpr(l);
				}
			},
			new Action() {	// [21] F = NUMBER.n
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_n = _symbols[offset + 1];
					final Number n = (Number) _symbol_n.value;
					return new SPMathModeNumberExpr(n);
				}
			},
			new Action() {	// [22] arglist = expr.e
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					return new SPMathModeArglist(e,null);
				}
			},
			new Action() {	// [23] arglist = expr.e COMMA arglist.l
				public Symbol reduce(Symbol[] _symbols, int offset) {
					final Symbol _symbol_e = _symbols[offset + 1];
					final SPMathModeAbstractExpr e = (SPMathModeAbstractExpr) _symbol_e.value;
					final Symbol _symbol_l = _symbols[offset + 3];
					final SPMathModeArglist l = (SPMathModeArglist) _symbol_l.value;
					return new SPMathModeArglist(e,l);
				}
			}
		};
	}

	protected Symbol invokeReduceAction(int rule_num, int offset) {
		return actions[rule_num].reduce(_symbols, offset);
	}
}
