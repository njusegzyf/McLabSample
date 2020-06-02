// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.rewrite.threeaddress;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.Program;

public class LeftThreeAddressRewrite extends AbstractLocalRewrite
{
    private VFPreorderAnalysis nameResolver;

    public LeftThreeAddressRewrite( ASTNode tree )
    {
        super( tree );
    }

    public void caseProgram( Program node )
    {
        nameResolver = new VFPreorderAnalysis( node );
        nameResolver.analyze();
        rewriteChildren( node );
    }

    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );
        Expr lhs = node.getLHS();
        ExpressionCollector ec = null;
        try{
            ec = new ExpressionCollector( lhs, nameResolver.getFlowSets().get(node) );
        }catch(NullPointerException e){
            rethrowWithMoreInfoNR( nameResolver, e );
        }

        Expr newLHS = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            newNode = new TransformedNode( ec.getNewAssignments() );
            node.setLHS( newLHS );
            newNode.add( node );
        }
    }

    private void rethrowWithMoreInfoNR( VFPreorderAnalysis nr, NullPointerException e)
    {
        if( nr==null || nr.getFlowSets()==null ){
            String m = "nameResolver not initialized correctly, was this rewriter constructed with an " +
                "ASTNode containing a Program node? Because it's supposed to be!";
            throw new NullPointerException(m);
        }
        else
            throw e;
    }
}