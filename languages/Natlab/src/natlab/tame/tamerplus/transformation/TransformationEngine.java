package natlab.tame.tamerplus.transformation;

import natlab.tame.tamerplus.analysis.AnalysisEngine;
import ast.ASTNode;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class TransformationEngine
{
    private ASTNode<?> fTree;
    private ClassToInstanceMap<TamerPlusTransformation> fTransformationCache = MutableClassToInstanceMap.create();
    private AnalysisEngine fAnalysisEngine;
    
    public static TransformationEngine forAST(ASTNode<?> tree)
    {
        return new TransformationEngine(tree);
    }
    
    public ASTNode<?> getAST()
    {
        return fTree;
    }
    
    public AnalysisEngine getAnalysisEngine()
    {
        return fAnalysisEngine;
    }
    
    private <T extends TamerPlusTransformation> T construct(Class<T> clazz)
    {
        T transformation = null;
        try
        {
            transformation = clazz.getConstructor(ASTNode.class).newInstance(fTree);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        transformation.transform(this);
        return transformation;
    }
    
    private <T extends TamerPlusTransformation> T getOrCreate(Class<T> clazz)
    {
        if (!fTransformationCache.containsKey(clazz))
        {
            fTransformationCache.put(clazz, construct(clazz));
        }
        return fTransformationCache.getInstance(clazz);
    }
    
    public RenameVariablesForTIRNodes getVariableRenameTransformation()
    {
        return getOrCreate(RenameVariablesForTIRNodes.class);
    }
    
    public TIRToMcSAFIRWithoutTemp getTIRToMcSAFIRWithoutTemp()
    {
        return getOrCreate(TIRToMcSAFIRWithoutTemp.class);
    }
    
    private TransformationEngine(ASTNode<?> tree)
    {
        fTree = tree;
        fAnalysisEngine = AnalysisEngine.forAST(fTree);
    }
}
