package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tir.TIRNode;
import ast.ASTNode;

public class UDChain implements TamerPlusAnalysis {
	public static boolean DEBUG = false;

	private Map<TIRNode, Map<String, Set<TIRNode>>> fUDMap;
	private UsedVariablesNameCollector fUsedVariablesNameCollector;
	private ReachingDefinitions fReachingDefinitionsAnalysis;

	public UDChain(ASTNode<?> tree) {
		fUDMap = new HashMap<>();
	}

	@Override
	public void analyze(AnalysisEngine engine) {
		fUsedVariablesNameCollector = engine.getUsedVariablesAnalysis();
		fReachingDefinitionsAnalysis = engine.getReachingDefinitionsAnalysis();

		constructUDChain();

		if (DEBUG)
			printUDChain();
	}

	private void constructUDChain() {
		for (TIRNode visitedStmt : fReachingDefinitionsAnalysis
				.getVisitedStmtsOrderedList()) {
			Set<String> usedVariables = fUsedVariablesNameCollector
					.getUsedVariablesForNode(visitedStmt);
			if (usedVariables.isEmpty()) {
				continue;
			}
			fUDMap.put(
					visitedStmt,
					getUsedVariablesToDefinitionsMapForNode(visitedStmt,
							usedVariables));
		}
	}

	private Map<String, Set<TIRNode>> getUsedVariablesToDefinitionsMapForNode(
			TIRNode node, Set<String> usedVariables) {
		Map<String, Set<TIRNode>> usedVariablesToDefinitionsMap = new HashMap<>();
		Map<String, Set<TIRNode>> variableToReachingDefinitionsMap = fReachingDefinitionsAnalysis
				.getInSetForNode(node);
		for (String variableName : variableToReachingDefinitionsMap.keySet()) {
			// If the variable is not declared in that stmt, add it to the map.
			// We want to keep the used variables only.
			if (usedVariables.contains(variableName)) {
				usedVariablesToDefinitionsMap.put(variableName,
						variableToReachingDefinitionsMap.get(variableName));
			}
		}
		return usedVariablesToDefinitionsMap;
	}

	/**
	 * Returns the Use Definition chain
	 * 
	 * @return map - key: node, value: map - key: used variable, value: set of
	 *         definitions of that variable
	 */
	public Map<TIRNode, Map<String, Set<TIRNode>>> getChain() {
		return fUDMap;
	}

	/**
	 * Returns the definitions map for a use statement
	 * 
	 * @param useStmt
	 * @return map - key: used variable, value: set of definitions of that
	 *         variable
	 */
	public Map<String, Set<TIRNode>> getDefinitionsMapFoUseStmt(TIRNode useStmt) {
		return fUDMap.get(useStmt);
	}

	private void printUDChain() {
		System.out.println("\nUse Definition Chain analysis result:");
		StringBuilder sb = new StringBuilder();
		LinkedList<TIRNode> visitedStmts = fReachingDefinitionsAnalysis
				.getVisitedStmtsOrderedList();
		for (TIRNode visitedStmt : visitedStmts) {
			sb.append("------- ").append(NodePrinter.printNode(visitedStmt))
					.append(" -------\n");
			Map<String, Set<TIRNode>> definitionSiteMap = fUDMap
					.get(visitedStmt);
			if (definitionSiteMap == null) {
				continue;
			}
			printVariableToReachingDefinitionsMap(definitionSiteMap, sb);
		}
		System.out.println(sb.append("\n").toString());
	}

	private void printVariableToReachingDefinitionsMap(
			Map<String, Set<TIRNode>> definitionSiteMap, StringBuilder sb) {
		for (Map.Entry<String, Set<TIRNode>> entry : definitionSiteMap
				.entrySet()) {
			printVariableToReachingDefinitionsMapEntry(entry, sb);
		}
	}

	private void printVariableToReachingDefinitionsMapEntry(
			Map.Entry<String, Set<TIRNode>> entry, StringBuilder sb) {
		Set<TIRNode> defStmts = entry.getValue();
		if (!defStmts.isEmpty()) {
			sb.append("Var ").append(entry.getKey()).append("\n");
			for (TIRNode defStmt : defStmts) {
				sb.append("\t").append(NodePrinter.printNode(defStmt))
						.append("\n");
			}
		}
	}

	public LinkedList<TIRNode> getVisitedStmtsOrderedList() {
		return fReachingDefinitionsAnalysis.getVisitedStmtsOrderedList();
	}
}
