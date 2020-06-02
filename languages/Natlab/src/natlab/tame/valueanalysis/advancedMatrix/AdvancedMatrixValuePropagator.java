package natlab.tame.valueanalysis.advancedMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import natlab.tame.builtin.Builtin;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.CellValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixPropagator;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.ConstantPropagator;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoPropagator;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.rangeValue.RangeValue;
import natlab.tame.valueanalysis.components.rangeValue.RangeValuePropagator;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapePropagator;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;

public class AdvancedMatrixValuePropagator extends
		MatrixPropagator<AdvancedMatrixValue> {
	static boolean Debug = false;
	ConstantPropagator<AggrValue<AdvancedMatrixValue>> constantProp = ConstantPropagator
			.getInstance();
	ClassPropagator<AggrValue<AdvancedMatrixValue>> classProp = ClassPropagator
			.getInstance();
	ShapePropagator<AggrValue<AdvancedMatrixValue>> shapeProp = ShapePropagator
			.getInstance();
	
	static RangeValuePropagator<AggrValue<AdvancedMatrixValue>> rangeValueProp = 
			RangeValuePropagator.getInstance();
	isComplexInfoPropagator<AggrValue<AdvancedMatrixValue>> isComplexInfoProp = isComplexInfoPropagator
			.getInstance();

	static AdvancedMatrixValueFactory factory = new AdvancedMatrixValueFactory();
	
	public AdvancedMatrixValuePropagator() {
		super(new AdvancedMatrixValueFactory());
	}

	/**
	 * base case
	 */
	@Override
	public Res<AggrValue<AdvancedMatrixValue>> caseBuiltin(Builtin builtin,
			Args<AggrValue<AdvancedMatrixValue>> arg) {
		System.out.println("Processing "+ builtin.getName());
		Constant cResult = builtin.visit(constantProp, arg);
		if (cResult != null) {
			return Res
					.<AggrValue<AdvancedMatrixValue>> newInstance(new AdvancedMatrixValue(
							null, cResult));
		}

		// if the result is not a constant, just do mclass propagation
		List<Set<ClassReference>> matchClassResult = builtin.visit(classProp,
				arg);
		if (matchClassResult == null) { // class prop returned error
			return Res.newErrorResult(builtin.getName()
					+ " is not defined for arguments " + arg + "as class");
		}
		// deal with shape XU added
		List<Shape> matchShapeResult = builtin
				.visit(shapeProp, arg);
		// FIXME - commented to stop visiting shape propogation

		if (matchShapeResult == null) {
			if (Debug)
				System.out.println("shape results are empty");
		}

		RangeValue rangeValueResult = builtin.visit(rangeValueProp, arg);
		
		List<isComplexInfo> matchisComplexInfoResult = builtin
				.visit(isComplexInfoProp, arg);
		if (matchisComplexInfoResult == null) {
			System.out.println("no complexinfo results for "+ builtin.getName());

		}

		// build results out of the result classes and shape XU modified, not
		// finished!!!
		return matchResultToRes(matchClassResult, matchShapeResult,rangeValueResult,
				matchisComplexInfoResult);

	}

	private Res<AggrValue<AdvancedMatrixValue>> matchResultToRes(
			List<Set<ClassReference>> matchClassResult,
			List<Shape> matchShapeResult,
			RangeValue rangeValueResult,
			List<isComplexInfo> matchisComplexInfoResult) {
		// go through and fill in result
		Res<AggrValue<AdvancedMatrixValue>> result = Res.newInstance();
//		
//		
//		if (matchShapeResult!=null) {
//			for (int counter=0; counter<matchShapeResult.size(); counter++) {
//				HashMap<ClassReference, AggrValue<AdvancedMatrixValue>> map = 
//						new HashMap<ClassReference, AggrValue<AdvancedMatrixValue>>();
//				Set<ClassReference> values = matchClassResult.get(counter);
//				for (ClassReference classRef : values) {
//					map.put(classRef, new AdvancedMatrixValue(null, 
//							new AdvancedMatrixValue(null, 
//									(PrimitiveClassReference) classRef),
//							matchShapeResult.get(counter), 
//							matchisComplexInfoResult.get(0)));
//				}
//				result.add(ValueSet.newInstance(map));
//			}
//			return result;			
//		}
//		else {
//			for (Set<ClassReference> values : matchClassResult) {
//		
//			HashMap<ClassReference, AggrValue<AdvancedMatrixValue>> map = new HashMap<ClassReference, AggrValue<AdvancedMatrixValue>>();
//
//			for (ClassReference classRef : values) {
//
//				
//
//					map.put(classRef, new AdvancedMatrixValue(null, 
//							new AdvancedMatrixValue(null, 
//									(PrimitiveClassReference) classRef),
//							null, // FIXME - commented to
//														// stop
//														// visiting shape
//														// propogation
//							matchisComplexInfoResult.get(0)));// FIXME a
//																// little
//																// bit
//																// tricky
//				
//			}
//			result.add(ValueSet.newInstance(map));
//		}
//		return result;
//		}
		
		if (matchShapeResult!=null) {
			for (int counter=0; counter<matchShapeResult.size(); counter++) {
				HashMap<ClassReference, AggrValue<AdvancedMatrixValue>> map = 
						new HashMap<ClassReference, AggrValue<AdvancedMatrixValue>>();
				Set<ClassReference> values = matchClassResult.get(counter);
				for (ClassReference classRef : values) {
					map.put(classRef, factory.newMatrixValueFromClassShapeRange(
							null
							, (PrimitiveClassReference)classRef
							, matchShapeResult.get(counter)
							, rangeValueResult
							, matchisComplexInfoResult.get(0)
							));
				}
				result.add(ValueSet.newInstance(map));
			}
			return result;			
		}
		else {
			for (Set<ClassReference> values : matchClassResult) {
				HashMap<ClassReference, AggrValue<AdvancedMatrixValue>> map = 
						new HashMap<ClassReference, AggrValue<AdvancedMatrixValue>>();
				for (ClassReference classRef : values) {
					map.put(classRef, factory.newMatrixValueFromClassShapeRange(
							null
							, (PrimitiveClassReference)classRef
							, null
							, rangeValueResult
							, matchisComplexInfoResult.get(0)
							));
				}
				result.add(ValueSet.newInstance(map));
			}
			return result;	
		}
	}

	@Override
	public Res<AggrValue<AdvancedMatrixValue>> caseAbstractConcatenation(
			Builtin builtin, Args<AggrValue<AdvancedMatrixValue>> arg) {

		List<Shape> matchShapeResult = builtin
				.visit(shapeProp, arg);
		if (Debug)
			System.out.println("shapeProp results are " + matchShapeResult);
		if (matchShapeResult == null) {
			if (Debug)
				System.out.println("shape results are empty");
		}

		List<isComplexInfo> matchisComplexInfoResult = builtin
				.visit(isComplexInfoProp, arg);
		if (matchisComplexInfoResult == null) {
			System.out.println("no complexinfo results");
		}
		// this block ends
//		return Res
//				.<AggrValue<AdvancedMatrixValue>> newInstance(new AdvancedMatrixValue(null, 
//						new AdvancedMatrixValue(null, 
//								(PrimitiveClassReference) getDominantCatArgClass(arg)),
//						matchShapeResult.get(0), matchisComplexInfoResult
//								.get(0)));// FIXME a little bit
//											// tricky
		Res<AggrValue<AdvancedMatrixValue>> result = Res.newInstance();
		if (matchShapeResult != null) {
			for (int counter = 0; counter < matchShapeResult.size(); counter++) {
				HashMap<ClassReference, AggrValue<AdvancedMatrixValue>> map = 
						new HashMap<ClassReference, AggrValue<AdvancedMatrixValue>>();
				map.put((PrimitiveClassReference)getDominantCatArgClass(arg)
						, factory.newMatrixValueFromClassShapeRange(
						null
						, (PrimitiveClassReference)getDominantCatArgClass(arg)
						, matchShapeResult.get(counter)
						, null
						, matchisComplexInfoResult.get(0)
						));
				result.add(ValueSet.newInstance(map));
			}
	        return result;
		}
		else {
			HashMap<ClassReference, AggrValue<AdvancedMatrixValue>> map = 
					new HashMap<ClassReference, AggrValue<AdvancedMatrixValue>>();
			map.put((PrimitiveClassReference)getDominantCatArgClass(arg)
					, factory.newMatrixValueFromClassShapeRange(
					null
					, (PrimitiveClassReference)getDominantCatArgClass(arg)
					, null
					, null
					, matchisComplexInfoResult.get(0)
					));
			result.add(ValueSet.newInstance(map));
			return result;
		}
	}
	
	@Override
	public Res<AggrValue<AdvancedMatrixValue>> caseCellhorzcat(Builtin builtin,
			Args<AggrValue<AdvancedMatrixValue>> elements) {
		ValueSet<AggrValue<AdvancedMatrixValue>> values = ValueSet
				.newInstance(elements);
		Shape shape = factory.getShapeFactory()
				.newShapeFromValues(
						Args.newInstance(factory.newMatrixValue(null, 1),
								factory.newMatrixValue(null, elements.size())));
		return Res
				.<AggrValue<AdvancedMatrixValue>> newInstance(new CellValue<AdvancedMatrixValue>(
						this.factory, shape, values));
	}

	@Override
	public Res<AggrValue<AdvancedMatrixValue>> caseCellvertcat(Builtin builtin,
			Args<AggrValue<AdvancedMatrixValue>> elements) {
		ValueSet<AggrValue<AdvancedMatrixValue>> values = ValueSet
				.newInstance(elements);
		Shape shape = factory.getShapeFactory()
				.newShapeFromValues(
						Args.newInstance(
								factory.newMatrixValue(null, elements.size()),
								factory.newMatrixValue(null, 1)));
		return Res
				.<AggrValue<AdvancedMatrixValue>> newInstance(new CellValue<AdvancedMatrixValue>(
						this.factory, shape, values));
	}

	@Override
	public Res<AggrValue<AdvancedMatrixValue>> caseCell(Builtin builtin,
			Args<AggrValue<AdvancedMatrixValue>> arg) {
		return Res
				.<AggrValue<AdvancedMatrixValue>> newInstance(new CellValue<AdvancedMatrixValue>(
						this.factory, factory.getShapeFactory()
								.newShapeFromValues(arg), ValueSet
								.<AggrValue<AdvancedMatrixValue>> newInstance()));
	}
}
