/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.model.tree.statements;

import org.ballerinalang.model.tree.SimpleVariableNode;
import org.ballerinalang.model.tree.expressions.ExpressionNode;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;

import java.util.List;

/**
 * {@code MatchNode} represents a type switch statement in Ballerina.
 *
 * @since 0.966.0
 */
public interface MatchNode {

    /**
     * {@code MatchStatementBindingPatternNode} is the base interface for any patterns inside a match statement.
     *
     * @since 0.966.0
     */
    interface MatchStatementBindingPatternNode {

        StatementNode getStatement();
    }

    /**
     * {@code MatchStatementTypedBindingPatternNode} represents a pattern inside a type switch statement.
     *
     * @since 0.966.0
     */
    interface MatchStatementTypedBindingPatternNode extends MatchStatementBindingPatternNode {

        SimpleVariableNode getVariableNode();
    }

    /**
     * {@code MatchStatementStaticBindingPatternNode} represents a static pattern inside a match statement.
     *
     * @since 0.985.0
     */
    interface MatchStatementStaticBindingPatternNode extends MatchStatementBindingPatternNode {

        BLangExpression getLiteral();
    }

    ExpressionNode getExpression();

    List<? extends MatchStatementTypedBindingPatternNode> getTypedPatternClauses();
}
