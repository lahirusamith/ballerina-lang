/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.compiler.format;

import io.ballerinalang.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerinalang.compiler.syntax.tree.AnnotationNode;
import io.ballerinalang.compiler.syntax.tree.BasicLiteralNode;
import io.ballerinalang.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerinalang.compiler.syntax.tree.ExplicitNewExpressionNode;
import io.ballerinalang.compiler.syntax.tree.ExpressionListItemNode;
import io.ballerinalang.compiler.syntax.tree.ExpressionNode;
import io.ballerinalang.compiler.syntax.tree.ExpressionStatementNode;
import io.ballerinalang.compiler.syntax.tree.FunctionArgumentNode;
import io.ballerinalang.compiler.syntax.tree.FunctionBodyBlockNode;
import io.ballerinalang.compiler.syntax.tree.FunctionBodyNode;
import io.ballerinalang.compiler.syntax.tree.FunctionCallExpressionNode;
import io.ballerinalang.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerinalang.compiler.syntax.tree.FunctionSignatureNode;
import io.ballerinalang.compiler.syntax.tree.IdentifierToken;
import io.ballerinalang.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerinalang.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerinalang.compiler.syntax.tree.ImportPrefixNode;
import io.ballerinalang.compiler.syntax.tree.ImportSubVersionNode;
import io.ballerinalang.compiler.syntax.tree.ImportVersionNode;
import io.ballerinalang.compiler.syntax.tree.MetadataNode;
import io.ballerinalang.compiler.syntax.tree.Minutiae;
import io.ballerinalang.compiler.syntax.tree.MinutiaeList;
import io.ballerinalang.compiler.syntax.tree.Node;
import io.ballerinalang.compiler.syntax.tree.NodeFactory;
import io.ballerinalang.compiler.syntax.tree.NodeList;
import io.ballerinalang.compiler.syntax.tree.ParameterNode;
import io.ballerinalang.compiler.syntax.tree.ParenthesizedArgList;
import io.ballerinalang.compiler.syntax.tree.PositionalArgumentNode;
import io.ballerinalang.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerinalang.compiler.syntax.tree.RequiredParameterNode;
import io.ballerinalang.compiler.syntax.tree.ReturnTypeDescriptorNode;
import io.ballerinalang.compiler.syntax.tree.SeparatedNodeList;
import io.ballerinalang.compiler.syntax.tree.ServiceBodyNode;
import io.ballerinalang.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerinalang.compiler.syntax.tree.StatementNode;
import io.ballerinalang.compiler.syntax.tree.Token;
import io.ballerinalang.compiler.syntax.tree.TreeModifier;
import io.ballerinalang.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerinalang.compiler.syntax.tree.TypedBindingPatternNode;
import io.ballerinalang.compiler.syntax.tree.VariableDeclarationNode;
import io.ballerinalang.compiler.text.LinePosition;
import io.ballerinalang.compiler.text.LineRange;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

/**
 * Modifies the given tree to format the nodes.
 *
 * @since 2.0.0
 */
public class FormattingTreeModifier extends TreeModifier {
    @Override
    public ImportDeclarationNode transform(ImportDeclarationNode importDeclarationNode) {
        Token importKeyword = getToken(importDeclarationNode.importKeyword());
        Token semicolon = getToken(importDeclarationNode.semicolon());

        SeparatedNodeList<IdentifierToken> moduleNames = this.modifySeparatedNodeList(
                importDeclarationNode.moduleName());
        Node orgName = importDeclarationNode.orgName().orElse(null);
        Node prefix = importDeclarationNode.prefix().orElse(null);
        Node version = importDeclarationNode.version().orElse(null);

        if (orgName != null) {
            importDeclarationNode = importDeclarationNode.modify().withOrgName(this.modifyNode(orgName)).apply();
        }
        if (prefix != null) {
            importDeclarationNode = importDeclarationNode.modify().withPrefix(this.modifyNode(prefix)).apply();
        }
        if (version != null) {
            importDeclarationNode = importDeclarationNode.modify().withVersion(this.modifyNode(version)).apply();
        }

        return importDeclarationNode.modify()
                .withImportKeyword(formatToken(importKeyword, 0, 0, 0, 0))
                .withModuleName(moduleNames)
                .withSemicolon(formatToken(semicolon, 0, 0, 0, 1))
                .apply();
    }

    @Override
    public ImportOrgNameNode transform(ImportOrgNameNode importOrgNameNode) {
        Token orgName = getToken(importOrgNameNode.orgName());
        Token slashToken = getToken(importOrgNameNode.slashToken());

        return importOrgNameNode.modify()
                .withOrgName(formatToken(orgName, 1, 0, 0, 0))
                .withSlashToken(formatToken(slashToken, 0, 0, 0, 0))
                .apply();
    }

    @Override
    public ImportPrefixNode transform(ImportPrefixNode importPrefixNode) {
        Token asKeyword = getToken(importPrefixNode.asKeyword());
        Token prefix = getToken(importPrefixNode.prefix());

        return importPrefixNode.modify()
                .withAsKeyword(formatToken(asKeyword, 1, 0, 0, 0))
                .withPrefix(formatToken(prefix, 1, 0, 0, 0))
                .apply();
    }

    @Override
    public ImportVersionNode transform(ImportVersionNode importVersionNode) {
        Token versionKeyword = getToken(importVersionNode.versionKeyword());
        Node versionNumber = this.modifyNode(importVersionNode.versionNumber());

        return importVersionNode.modify()
                .withVersionKeyword(formatToken(versionKeyword, 1, 0, 0, 0))
                .withVersionNumber(versionNumber)
                .apply();
    }

    @Override
    public ImportSubVersionNode transform(ImportSubVersionNode importSubVersionNode) {
        Token leadingDot = getToken(importSubVersionNode.leadingDot().orElse(null));
        Token versionNumber = getToken(importSubVersionNode.versionNumber());

        if (leadingDot != null) {
            importSubVersionNode = importSubVersionNode.modify().withLeadingDot(formatToken(leadingDot, 0, 0, 0, 0))
                    .apply();
        }

        return importSubVersionNode.modify()
                .withVersionNumber(formatToken(versionNumber, 1, 0, 0, 0))
                .apply();
    }

    @Override
    public IdentifierToken transform(IdentifierToken identifier) {
        Token identifierToken = getToken(identifier);

        return (IdentifierToken) formatToken(identifierToken, 0, 0, 0, 0);
    }

    @Override
    public FunctionDefinitionNode transform(FunctionDefinitionNode functionDefinitionNode) {
        Token visibilityQualifier = getToken(functionDefinitionNode.visibilityQualifier().orElse(null));
        Token functionKeyword = getToken(functionDefinitionNode.functionKeyword());
        Token functionName = getToken(functionDefinitionNode.functionName());

        FunctionSignatureNode functionSignatureNode = this.modifyNode(functionDefinitionNode.functionSignature());
        Token functionSignatureOpenPara = getToken(functionSignatureNode.openParenToken());
        Token functionSignatureClosePara = getToken(functionSignatureNode.closeParenToken());

        FunctionBodyNode functionBodyNode = this.modifyNode(functionDefinitionNode.functionBody());

        if (visibilityQualifier != null) {
            functionDefinitionNode = functionDefinitionNode.modify()
                    .withVisibilityQualifier(formatToken(visibilityQualifier, 0, 0, 0, 0))
                    .apply();
        }
        return functionDefinitionNode.modify()
                .withFunctionKeyword(formatToken(functionKeyword, 1, 0, 0, 0))
                .withFunctionName((IdentifierToken) formatToken(functionName, 1, 0, 0, 0))
                .withFunctionSignature(functionSignatureNode
                        .modify(functionSignatureOpenPara, functionSignatureNode.parameters(),
                                functionSignatureClosePara, null))
                .withFunctionBody(functionBodyNode)
                .apply();
    }

    @Override
    public FunctionSignatureNode transform(FunctionSignatureNode functionSignatureNode) {
        Token openPara = getToken(functionSignatureNode.openParenToken());
        Token closePara = getToken(functionSignatureNode.closeParenToken());

        NodeList<ParameterNode> parameters = this.modifyNodeList(functionSignatureNode.parameters());
        ReturnTypeDescriptorNode returnTypeDescriptorNode = functionSignatureNode.returnTypeDesc().orElse(null);

        if (returnTypeDescriptorNode != null) {
            returnTypeDescriptorNode = this.modifyNode(returnTypeDescriptorNode);
            functionSignatureNode = functionSignatureNode.modify()
                    .withReturnTypeDesc(returnTypeDescriptorNode)
                    .apply();
        }

        return functionSignatureNode.modify()
                .withOpenParenToken(formatToken(openPara, 0, 0, 0, 0))
                .withCloseParenToken(formatToken(closePara, 0, 0, 0, 0))
                .withParameters(parameters)
                .apply();
    }

    @Override
    public RequiredParameterNode transform(RequiredParameterNode requiredParameterNode) {
        Token paramName = getToken(requiredParameterNode.paramName().orElse(null));
        Token leadingComma = getToken(requiredParameterNode.leadingComma().orElse(null));
        Token visibilityQualifier = getToken(requiredParameterNode.visibilityQualifier().orElse(null));

        NodeList<AnnotationNode> annotations = this.modifyNodeList(requiredParameterNode.annotations());
        Node typeName = this.modifyNode(requiredParameterNode.typeName());

        if (leadingComma != null) {
            requiredParameterNode = requiredParameterNode.modify()
                    .withLeadingComma(formatToken(leadingComma, 0, 1, 0, 0))
                    .apply();
        }
        if (paramName != null) {
            requiredParameterNode = requiredParameterNode.modify()
                    .withParamName(formatToken(paramName, 1, 0, 0, 0))
                    .apply();
        }
        if (visibilityQualifier != null) {
            requiredParameterNode = requiredParameterNode.modify()
                    .withVisibilityQualifier(formatToken(visibilityQualifier, 0, 0, 0, 0))
                    .apply();
        }
        return requiredParameterNode.modify()
                .withAnnotations(annotations)
                .withTypeName(typeName)
                .apply();
    }

    @Override
    public BuiltinSimpleNameReferenceNode transform(BuiltinSimpleNameReferenceNode builtinSimpleNameReferenceNode) {
        Token name = getToken(builtinSimpleNameReferenceNode.name());

        return builtinSimpleNameReferenceNode.modify()
                .withName(formatToken(name, 0, 0, 0, 0))
                .apply();
    }

    @Override
    public FunctionBodyBlockNode transform(FunctionBodyBlockNode functionBodyBlockNode) {
        Token functionBodyOpenBrace = getToken(functionBodyBlockNode.openBraceToken());
        Token functionBodyCloseBrace = getToken(functionBodyBlockNode.closeBraceToken());

        NodeList<StatementNode> statements = this.modifyNodeList(functionBodyBlockNode.statements());

        return functionBodyBlockNode.modify()
                .withOpenBraceToken(formatToken(functionBodyOpenBrace, 1, 0, 0, 1))
                .withCloseBraceToken(formatToken(functionBodyCloseBrace, 0, 0, 0, 0))
                .withStatements(statements)
                .apply();
    }

    @Override
    public ExpressionStatementNode transform(ExpressionStatementNode expressionStatementNode) {
        ExpressionNode expression = this.modifyNode(expressionStatementNode.expression());
        Token semicolonToken = expressionStatementNode.semicolonToken();

        return expressionStatementNode.modify()
                .withExpression(expression)
                .withSemicolonToken(formatToken(semicolonToken, 0, 0, 0, 0))
                .apply();
    }

    @Override
    public FunctionCallExpressionNode transform(FunctionCallExpressionNode functionCallExpressionNode) {
        Node functionName = this.modifyNode(functionCallExpressionNode.functionName());
        Token functionCallOpenPara = getToken(functionCallExpressionNode.openParenToken());
        Token functionCallClosePara = getToken(functionCallExpressionNode.closeParenToken());
        NodeList<FunctionArgumentNode> arguments = this.modifyNodeList(functionCallExpressionNode.arguments());

        return functionCallExpressionNode.modify()
                .withFunctionName(functionName)
                .withOpenParenToken(formatToken(functionCallOpenPara, 0, 0, 0, 0))
                .withCloseParenToken(formatToken(functionCallClosePara, 0, 0, 0, 0))
                .withArguments(arguments)
                .apply();
    }

    @Override
    public QualifiedNameReferenceNode transform(QualifiedNameReferenceNode qualifiedNameReferenceNode) {
        Token modulePrefix = getToken(qualifiedNameReferenceNode.modulePrefix());
        Token identifier = getToken(qualifiedNameReferenceNode.identifier());
        Token colon = getToken((Token) qualifiedNameReferenceNode.colon());

        return qualifiedNameReferenceNode.modify()
                .withModulePrefix(formatToken(modulePrefix, 0, 0, 0, 0))
                .withIdentifier((IdentifierToken) formatToken(identifier, 0, 0, 0, 0))
                .withColon(formatToken(colon, 0, 0, 0, 0))
                .apply();
    }

    @Override
    public PositionalArgumentNode transform(PositionalArgumentNode positionalArgumentNode) {
        ExpressionNode expression = this.modifyNode(positionalArgumentNode.expression());

        Token leadingComma = getToken(positionalArgumentNode.leadingComma().orElse(null));

        if (leadingComma != null) {
            return positionalArgumentNode.modify()
                    .withExpression(expression)
                    .withLeadingComma(formatToken(leadingComma, 0, 0, 0, 0))
                    .apply();
        }

        return positionalArgumentNode.modify()
                .withExpression(expression)
                .apply();
    }

    @Override
    public BasicLiteralNode transform(BasicLiteralNode basicLiteralNode) {
        Token literalToken = getToken(basicLiteralNode.literalToken());

        return basicLiteralNode.modify()
                .withLiteralToken(formatToken(literalToken, 0, 0, 0, 0))
                .apply();
    }


    @Override
    public ServiceDeclarationNode transform(ServiceDeclarationNode serviceDeclarationNode) {
        Token serviceKeyword = getToken(serviceDeclarationNode.serviceKeyword());
        IdentifierToken serviceName = (IdentifierToken) getToken(serviceDeclarationNode.serviceName());
        Token onKeyword = getToken(serviceDeclarationNode.onKeyword());

        MetadataNode metadata = this.modifyNode(serviceDeclarationNode.metadata());
        NodeList<ExpressionNode> expressions = this.modifyNodeList(serviceDeclarationNode.expressions());
        Node serviceBody = this.modifyNode(serviceDeclarationNode.serviceBody());

        return serviceDeclarationNode.modify()
                .withServiceKeyword(formatToken(serviceKeyword, 0, 0, 1, 0))
                .withServiceName((IdentifierToken) formatToken(serviceName, 1, 0, 0, 0))
                .withOnKeyword(formatToken(onKeyword, 1, 0, 0, 0))
                .withExpressions(expressions)
                .withMetadata(metadata)
                .withServiceBody(serviceBody)
                .apply();
    }

    @Override
    public ServiceBodyNode transform(ServiceBodyNode serviceBodyNode) {
        Token openBraceToken = getToken(serviceBodyNode.openBraceToken());
        Token closeBraceToken = getToken(serviceBodyNode.closeBraceToken());
        NodeList<Node> resources = this.modifyNodeList(serviceBodyNode.resources());

        return serviceBodyNode.modify()
                .withOpenBraceToken(formatToken(openBraceToken, 1, 0, 0, 1))
                .withCloseBraceToken(formatToken(closeBraceToken, 0, 0, 1, 0))
                .withResources(resources)
                .apply();
    }

    @Override
    public ExpressionListItemNode transform(ExpressionListItemNode expressionListItemNode) {
        ExpressionNode expression = this.modifyNode(expressionListItemNode.expression());
        Token commaToken = getToken(expressionListItemNode.leadingComma().orElse(null));

        if (commaToken != null) {
            return expressionListItemNode.modify()
                    .withExpression(expression)
                    .withLeadingComma(formatToken(commaToken, 0, 0, 0, 0))
                    .apply();
        }
        return expressionListItemNode.modify()
                .withExpression(expression)
                .apply();
    }

    @Override
    public ExplicitNewExpressionNode transform(ExplicitNewExpressionNode explicitNewExpressionNode) {
        Token newKeywordToken = getToken(explicitNewExpressionNode.newKeyword());
        Node parenthesizedArgList = this.modifyNode(explicitNewExpressionNode.parenthesizedArgList());
        TypeDescriptorNode typeDescriptorNode = this.modifyNode(explicitNewExpressionNode.typeDescriptor());

        return explicitNewExpressionNode.modify()
                .withNewKeyword(formatToken(newKeywordToken, 1, 1, 0, 0))
                .withParenthesizedArgList(parenthesizedArgList)
                .withTypeDescriptor(typeDescriptorNode)
                .apply();
    }

    @Override
    public ParenthesizedArgList transform(ParenthesizedArgList parenthesizedArgList) {
        Token openParenToken = getToken(parenthesizedArgList.openParenToken());
        Token closeParenToken = getToken(parenthesizedArgList.closeParenToken());
        NodeList<FunctionArgumentNode> arguments = this.modifyNodeList(parenthesizedArgList.arguments());

        return parenthesizedArgList.modify()
                .withArguments(arguments)
                .withOpenParenToken(formatToken(openParenToken, 0, 0, 0, 0))
                .withCloseParenToken(formatToken(closeParenToken, 0, 0, 0, 0))
                .apply();
    }

    @Override
    public VariableDeclarationNode transform(VariableDeclarationNode variableDeclarationNode) {
        Token semicolonToken = this.modifyToken(variableDeclarationNode.semicolonToken());
        Token equalToken = this.modifyToken(variableDeclarationNode.equalsToken().orElse(null));
        Token finalToken = this.modifyToken(variableDeclarationNode.finalKeyword().orElse(null));
        ExpressionNode initializerNode = this.modifyNode(variableDeclarationNode.initializer().orElse(null));
        NodeList<AnnotationNode> annotationNodes = this.modifyNodeList(variableDeclarationNode.annotations());
        TypedBindingPatternNode typedBindingPatternNode = this.modifyNode(
                variableDeclarationNode.typedBindingPattern());

        if (equalToken != null) {
            variableDeclarationNode = variableDeclarationNode.modify().withEqualsToken(
                    formatToken(equalToken, 1, 1, 0, 0)).apply();
        }
        if (finalToken != null) {
            variableDeclarationNode = variableDeclarationNode.modify().withFinalKeyword(
                    formatToken(finalToken, 0, 0, 0, 0)).apply();
        }
        if (initializerNode != null) {
            variableDeclarationNode = variableDeclarationNode.modify().withInitializer(this.modifyNode(initializerNode))
                    .apply();
        }

        return variableDeclarationNode.modify()
                .withAnnotations(annotationNodes)
                .withSemicolonToken(formatToken(semicolonToken, 0, 0, 0, 0))
                .withTypedBindingPattern(typedBindingPatternNode)
                .apply();
    }

    private Token formatToken(Token token, int leadingSpaces, int trailingSpaces, int leadingNewLines,
                              int trailingNewLines) {
        MinutiaeList leadingMinutiaeList = token.leadingMinutiae();
        MinutiaeList trailingMinutiaeList = token.trailingMinutiae();

        MinutiaeList newLeadingMinutiaeList = modifyMinutiaeList(leadingMinutiaeList, leadingSpaces, leadingNewLines);
        MinutiaeList newTrailingMinutiaeList = modifyMinutiaeList(trailingMinutiaeList, trailingSpaces,
                trailingNewLines);

        return token.modify(newLeadingMinutiaeList, newTrailingMinutiaeList);
    }

    private MinutiaeList modifyMinutiaeList(MinutiaeList minutiaeList, int spaces, int newLines) {
        Minutiae minutiae = NodeFactory.createWhitespaceMinutiae(getWhiteSpaces(spaces, newLines));
        return minutiaeList.add(minutiae);
    }

    private String getWhiteSpaces(int column, int newLines) {
        StringBuilder whiteSpaces = new StringBuilder();
        for (int i = 0; i <= (newLines - 1); i++) {
            whiteSpaces.append("\n");
        }
        for (int i = 0; i <= (column - 1); i++) {
            whiteSpaces.append(" ");
        }

        return whiteSpaces.toString();
    }

    private <T extends Token> Token getToken(T node) {
        if (node == null) return node;
        return node.modify(AbstractNodeFactory.createEmptyMinutiaeList(),
                AbstractNodeFactory.createEmptyMinutiaeList());
    }

    private DiagnosticPos getPosition(Node node) {
        if (node == null) {
            return null;
        }
        LineRange lineRange = node.lineRange();
        LinePosition startPos = lineRange.startLine();
        LinePosition endPos = lineRange.endLine();
        return new DiagnosticPos(null, startPos.line() + 1, endPos.line() + 1,
                startPos.offset() + 1, endPos.offset() + 1);
    }
}