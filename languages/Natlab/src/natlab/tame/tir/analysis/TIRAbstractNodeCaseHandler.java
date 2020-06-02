// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

/**
 * A TIR Node case handler that just calls the parent for every case,
 * analaoguous to AbstractNodeCaseHandler
 */

package natlab.tame.tir.analysis;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRAbstractCreateFunctionHandleStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRBreakStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCommentStmt;
import natlab.tame.tir.TIRContinueStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRCreateFunctionReferenceStmt;
import natlab.tame.tir.TIRCreateLambdaStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRGlobalStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRPersistentSmt;
import natlab.tame.tir.TIRReturnStmt;
import natlab.tame.tir.TIRStatementList;
import natlab.tame.tir.TIRStmt;
import natlab.tame.tir.TIRTryStmt;
import natlab.tame.tir.TIRWhileStmt;
import nodecases.AbstractNodeCaseHandler;
import ast.Stmt;

public abstract class TIRAbstractNodeCaseHandler extends
        AbstractNodeCaseHandler implements TIRNodeCaseHandler {

    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
        caseAssignStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node) {
        caseTIRAbstractAssignStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignFromVarStmt(TIRAbstractAssignFromVarStmt node){
    	caseTIRAbstractAssignStmt(node);
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node) {
        caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node) {
        caseTIRAbstractAssignFromVarStmt(node);
    }

    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node) {
        caseTIRAbstractAssignToVarStmt(node);
    }
    
    @Override
    public void caseTIRAbstractCreateFunctionHandleStmt(TIRAbstractCreateFunctionHandleStmt node) {
        caseTIRAbstractAssignToVarStmt(node);
    }
    
    @Override
    public void caseTIRCreateFunctionReferenceStmt(
    		TIRCreateFunctionReferenceStmt node) {
    	caseTIRAbstractCreateFunctionHandleStmt(node);
    }
    
    @Override
    public void caseTIRCreateLambdaStmt(TIRCreateLambdaStmt node) {
    	caseTIRAbstractCreateFunctionHandleStmt(node);
    }

    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node) {
        caseTIRAbstractAssignToVarStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node) {
        caseTIRAbstractAssignStmt(node);
    }

    @Override
    public void caseTIRCallStmt(TIRCallStmt node) {
        caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node) {
        caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node) {
        caseTIRAbstractAssignFromVarStmt(node);
    }

    @Override
    public void caseTIRCommaSeparatedList(TIRCommaSeparatedList node) {
        caseList(node);
    }

    @Override
    public void caseTIRCommentStmt(TIRCommentStmt node) {
        caseEmptyStmt(node);
    }

    @Override
    public void caseTIRForStmt(TIRForStmt node) {
        caseForStmt(node);
    }

    @Override
    public void caseTIRFunction(TIRFunction node) {
        caseFunction(node);
    }

    @Override
    public void caseTIRIfStmt(TIRIfStmt node) {
        caseIfStmt(node);
    }

    @Override
    public void caseTIRStatementList(TIRStatementList node) {
        caseList(node);
    }

    @Override
    public void caseTIRStmt(TIRStmt node) {
        caseStmt((Stmt)node);
    }

    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node) {
        caseWhileStmt(node);
    }

    @Override
    public void caseTIRBreakStmt(TIRBreakStmt node) {
        caseBreakStmt(node);
    }

    @Override
    public void caseTIRContinueStmt(TIRContinueStmt node) {
        caseContinueStmt(node);
    }

    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node) {
        caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node) {
        caseTIRAbstractAssignFromVarStmt(node);
    }

    @Override
    public void caseTIRGlobalStmt(TIRGlobalStmt node) {
        caseGlobalStmt(node);
    }

    @Override
    public void caseTIRPersistentStmt(TIRPersistentSmt node) {
        casePersistentStmt(node);
    }

    @Override
    public void caseTIRTryStmt(TIRTryStmt node) {
        caseTryStmt(node);
    }
    
    public void caseTIRReturnStmt(TIRReturnStmt node){
        caseReturnStmt(node);
    }
}
