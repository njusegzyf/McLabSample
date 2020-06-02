package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tamerplus.utils.TamerPlusUtils;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;

import com.google.common.collect.Sets;

 public class UsedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<String>> implements TamerPlusAnalysis 
{
    public static boolean DEBUG = false;
    
    private HashSet<String> fCurrentVariablesSet;
    public Map<TIRNode, HashSet<String>> fNodeToUsedVariablesMap;
    
    public UsedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fNodeToUsedVariablesMap = new HashMap<>();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        super.analyze();
        
        if (DEBUG) printNodeToUsedVariablesMapContent();
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentVariablesSet = newInitialFlow();
       // TODO, this might cause an error(asNameList)!
       fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getArguments().asNameList()));
       fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getConditionVarName().getID());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getCondition().getName().getID());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
        caseWhileStmt(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        
        fCurrentVariablesSet.add(node.getLowerName().getID());
        if (node.hasIncr() ) fCurrentVariablesSet.add(node.getIncName().getID());
        fCurrentVariablesSet.add(node.getUpperName().getID());

        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getSourceName().getID());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getValueName().getID());
        fCurrentVariablesSet.add(node.getDotName().getID());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getValueName().getID());
        fCurrentVariablesSet.add(node.getCellArrayName().getID());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getIndices().asNameList()));   
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getValueName().getID());
        fCurrentVariablesSet.add(node.getArrayName().getID());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getIndices().asNameList()));
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getArrayName().getID());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getIndices().asNameList()));
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getCellArrayName().getID());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getIndices().asNameList()));
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getDotName().getID());
        fCurrentVariablesSet.add(node.getFieldName().getID());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
        
    @Override
    public HashSet<String> merge(HashSet<String> in1, HashSet<String> in2)
    {
        return new HashSet<>(Sets.union(in1, in2));
    }

    @Override
    public HashSet<String> copy(HashSet<String> source)
    {
        return new HashSet<>(source);
    }
    
    public HashSet<String> newInitialFlow()
    {
        return new HashSet<>();
    }

    /**
     * Returns the set of used variables for a given node
     * @param node
     * @return set of used variables
     */
    public Set<String> getUsedVariablesForNode(TIRNode node)
    {
        Set<String> set = fNodeToUsedVariablesMap.get(node);
        if (set == null)
        {
            return new HashSet<>();
        }
        return set;
    }
    
    private void printNodeToUsedVariablesMapContent()
    {
        System.out.println("\nUsed variables names collector analysis results:");
        for (Map.Entry<TIRNode, HashSet<String>> entry : fNodeToUsedVariablesMap.entrySet())
        {
           System.out.print(NodePrinter.printNode(entry.getKey()) + "\t");
           for(String usedVariableName : entry.getValue())
           {
               System.out.print(usedVariableName + " ");
           }
           System.out.println();
        }
        System.out.println();
    }
}
