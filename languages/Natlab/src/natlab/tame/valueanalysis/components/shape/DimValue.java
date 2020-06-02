package natlab.tame.valueanalysis.components.shape;

/**
 * This is the value class for shape's dimensions, contain only two fields, 
 * one is Integer value, the other is String symbolic which for the case 
 * we may not know the exact value of this dimension, we can use the symbolic 
 * to represent this dimension or compare this dimension with other shape's 
 * certain dimension.
 * TODO do we also need to add range value info here?
 * @author Xu
 *
 */
public class DimValue {
	Integer intValue;
	String symbolic;
	
	public DimValue() {}
	
	public DimValue(Integer value, String symbolic) {
		this.intValue = value;
		this.symbolic = symbolic;
	}
	
	/**
	 * my own clone method, not using java clone.
	 * @return
	 */
	DimValue cloneThisValue() {
		DimValue instance = new DimValue();
		instance.intValue = this.intValue;
		instance.symbolic = this.symbolic;
		return instance;
	}
	
	public boolean hasIntValue() {
		return this.intValue!=null;
	}
	
	public boolean hasSymbolic() {
		return this.symbolic!=null;
	}
	
	public Integer getIntValue() {
		return this.intValue;
	}
	
	public String getSymbolic() {
		return this.symbolic;
	}
	
	/**
	 * used when we want to know whether a certain dimension is equal to 1
	 * @return
	 */
	public boolean equalsOne() {
		if (intValue!=null&&intValue==1) return true;
		return false;
	}
	
	public boolean equals(DimValue o) {
		if (intValue != null && o.hasIntValue()) {
			if (intValue.equals(o.getIntValue())) 
				return true;
			else 
				return false;
		}
		else if (intValue == null && !o.hasIntValue()) {
			if (symbolic !=null && o.hasSymbolic() 
					&& symbolic.equals(o.getSymbolic())) 
				return true;
			else if (symbolic == null && !o.hasSymbolic())
				return true;
			else 
				return false;
		}
		return false;
	}
	@Override
	public String toString() {
		return hasIntValue() ? intValue.toString() : (symbolic!=null ? symbolic : "?");
	}
	
	public int hashCode() {
		if (intValue != null)
			return intValue;
		if (symbolic != null)
			return symbolic.hashCode();
		return super.hashCode();
	}
}
