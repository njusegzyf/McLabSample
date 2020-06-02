package natlab.tame.builtin.shapeprop.ast;

import java.util.HashMap;

import natlab.McLabCore;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPScalar<V extends Value<V>> extends SPAbstractVectorExpr<V> {
	
	static boolean Debug = false;
	String s;
	
	public SPScalar (String s) {
		this.s = s;
	}
	
	/**
	 * only used to match a scalar input argument, so cannot be indexed as uppercase.
	 */
	public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
		if (isPatternSide) {
			// if the argument is empty, not matched.
			if (argValues.isEmpty()) {
				previousMatchResult.setIsError(true);
				return previousMatchResult;
			}
			// if the argument is not empty, but the current index pointing to no argument, not matched.
			else if (argValues.size() <= previousMatchResult.getHowManyMatched()) {
				previousMatchResult.setIsError(true);
				return previousMatchResult;
			}
			// if the argument is not empty, and the current index pointing to an argument, try to match.
			else {
				Value<V> argument = argValues.get(previousMatchResult.getHowManyMatched());
				if (((HasConstant)argument).getConstant()==null && ((HasShape)argument).getShape()==null) {
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
					lowercase.put(s, new DimValue());
					HashMap<String, Shape> uppercase = new HashMap<String, Shape>();
					uppercase.put(s, (new ShapeFactory()).newShapeFromIntegers((new DoubleConstant(1).getShape())));
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					if (Debug) System.out.println(match.getAllLowercase());
					if (Debug) System.out.println("inside empty constant value and shape value mathcing a Scalar!");
					return match;
				}
				// doesn't have constant info, but has shape info, so it may be a scalar if the shape is [1,1].
				else if (((HasConstant)argument).getConstant()==null && ((HasShape)argument).getShape()!=null) {
					if (((HasShape)argument).getShape().isScalar()) {
						// it's a scalar, but doesn't have a constant value, in this case, we can take advantage of its symbolic value.
						String symbolic = argument.getSymbolic();
						HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
						lowercase.put(s, new DimValue(null, symbolic));
						HashMap<String, Shape> uppercase = new HashMap<String, Shape>();
						uppercase.put(s, ((HasShape)argument).getShape());
						ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
						match.comsumeArg();
						match.saveLatestMatchedUppercase(s);
						return match;
					}
					else {
						if (Debug) System.out.println("it's not a scalar");
						previousMatchResult.setIsError(true);
						return previousMatchResult;						
					}
				}
				else if (((HasConstant)argument).getConstant() != null) {
					Constant c = ((HasConstant)argument).getConstant();

					String symbolic = argument.getSymbolic();
					HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();

					if (c instanceof DoubleConstant) {
						lowercase.put(s, new DimValue((int)((double)c.getValue()), symbolic));
					} else if (c instanceof CharConstant) {
						lowercase.put(s, new DimValue((int)c.getValue().toString().charAt(0), symbolic));
					} else if (c instanceof LogicalConstant) {
						lowercase.put(s, new DimValue(c.getValue().equals(Boolean.TRUE) ? 1 : 0, symbolic));
					} else {
					    if (McLabCore.options.debug()) {
					    	System.out.println("WARNING: Unsupported constant class " + c.getClass().toString() + ", setting value to null");
						}
						lowercase.put(s, new DimValue(null, symbolic));
					}
					HashMap<String, Shape> uppercase = new HashMap<String, Shape>();
					uppercase.put(s, ((HasShape)argument).getShape());
					ShapePropMatch<V> match = new ShapePropMatch<V>(previousMatchResult, lowercase, uppercase);
					match.comsumeArg();
					match.saveLatestMatchedUppercase(s);
					return match;
				}
				else {
					previousMatchResult.setIsError(true);
					return previousMatchResult;
				}				
			}
		}
		else {
			previousMatchResult.addToOutput(new ShapeFactory().getScalarShape());
			previousMatchResult.emitOneResult();
			return previousMatchResult;
		}
	}
	
	public String toString(){
		return s.toString();
	}
}
