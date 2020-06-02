package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoFactory;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.path.FunctionReference;

/**
 * @author ant6n
 * 
 * extended by XU to support symbolic info. @ 6:26pm March 9th 2013 
 * TODO why we have abstract method newMatrixValue and getValuePropagator here, 
 * since we already have these two abstract methods in ValueFactory class. 
 * remove them?
 */
public abstract class AggrValueFactory<D extends MatrixValue<D>> extends ValueFactory<AggrValue<D>> {
	/**
	 * constructor builds shape factor
	 */
	ShapeFactory shapeFactory;
	isComplexInfoFactory isComplexFactory;
	public AggrValueFactory(){
		this.shapeFactory = new ShapeFactory();
		this.isComplexFactory = new isComplexInfoFactory(); //added by Vineet
	}
	
	
    /**
     * constructs a new Primitive Value from a constant, extended to support symbolic.
     * @param constant
     */
    abstract public D newMatrixValue(String symbolic, Constant constant);
    
    
    /**
     * returns a ValuePropagator
     * This should always be an AggrValuePropagator, containing a matrix value propagator
     */
    abstract public AggrValuePropagator<D> getValuePropagator();
    

    /**
     * creates a function handle value
     */
    public FunctionHandleValue<D> newFunctionHandleValue(FunctionReference f){
        return new FunctionHandleValue<D>(this, f);
    }
    
    /**
     * creates a function handle value, but already supplies some arguments (partial application)
     */
    public FunctionHandleValue<D> newFunctionHandlevalue(FunctionReference f,java.util.List<ValueSet<AggrValue<D>>> partialValues){
        return new FunctionHandleValue<D>(this,f,partialValues);
    }
    
        
    /**
     * creates an empty struct
     */
    public StructValue<D> newStruct(){
        return new StructValue<D>(this);
    }
    
    /**
     * creates an empty cell array
     */
    public CellValue<D> newCell(){
        return new CellValue<D>(this);
    }
    
    
    /**
     * returns the shape factory
     */
    public ShapeFactory getShapeFactory(){
    	return shapeFactory;
    }
    
    public isComplexInfoFactory getIsComplexInfoFactory(){
    	return isComplexFactory;
    }
}
