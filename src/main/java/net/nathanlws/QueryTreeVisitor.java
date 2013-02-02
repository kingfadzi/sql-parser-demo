/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package net.nathanlws;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

public class QueryTreeVisitor implements Visitor {
    private boolean onFirst = true;
    private boolean didPrint = false;

    private static String qualify(String s) {
        return (s != null) ? s + "." : "";
    }

    @Override
    public Visitable visit(Visitable visitable) throws StandardException {
        QueryTreeNode node = (QueryTreeNode)visitable;

        if(onFirst) {
            System.out.println("Statement: " + node.getClass().getSimpleName());
            onFirst = false;
        }

        String column = null;
        if(node.getNodeType() == NodeTypes.COLUMN_REFERENCE) {
            ColumnReference ref = (ColumnReference)node;
            column = qualify(ref.getSchemaName()) + qualify(ref.getTableName()) + ref.getColumnName();
        } else if(node.getNodeType() == NodeTypes.ALL_RESULT_COLUMN) {
            column = "*";
        }

        if(column != null) {
            System.out.print(didPrint ? ", " : "  ");
            System.out.print(column);
            didPrint = true;
        }

        return visitable;
    }

    @Override
    public boolean visitChildrenFirst(Visitable node) {
        return false;
    }

    @Override
    public boolean stopTraversal() {
        return false;
    }

    @Override
    public boolean skipChildren(Visitable node) throws StandardException {
        return false;
    }
}
