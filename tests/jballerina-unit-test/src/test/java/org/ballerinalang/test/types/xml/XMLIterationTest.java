/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ballerinalang.test.types.xml;

import org.ballerinalang.core.model.values.BInteger;
import org.ballerinalang.core.model.values.BRefType;
import org.ballerinalang.core.model.values.BValue;
import org.ballerinalang.core.model.values.BValueArray;
import org.ballerinalang.core.model.values.BXML;
import org.ballerinalang.core.model.values.BXMLItem;
import org.ballerinalang.core.model.values.BXMLSequence;
import org.ballerinalang.test.BAssertUtil;
import org.ballerinalang.test.BCompileUtil;
import org.ballerinalang.test.BRunUtil;
import org.ballerinalang.test.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.StringJoiner;

/**
 * Test cases for XML iteration.
 *
 * @since 0.982.0
 */
public class XMLIterationTest {

    private CompileResult result, negative;

    String[][] authors = new String[][]{{"Giada De Laurentiis"}, {"J. K. Rowling"},
            {"James McGovern", "Per Bothner", "Kurt Cagle", "James Linn", "Vaidyanathan Nagarajan"}, {"Erik T. Ray"}};

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/types/xml/xml_iteration.bal");
        negative = BCompileUtil.compile("test-src/types/xml/xml_iteration_negative.bal");
    }

    @Test
    public void testNegative() {
        Assert.assertEquals(negative.getErrorCount(), 17);
        int index = 0;
        BAssertUtil.validateError(negative, index++,
                                  "invalid tuple binding pattern: attempted to infer a tuple type, but found 'xml'",
                                  13, 17);
        BAssertUtil.validateError(negative, index++, "incompatible types: expected " +
                "'function ((xml:Element|xml:Comment|xml:ProcessingInstruction|xml:Text)) returns ()'," +
                " found 'function ([int,xml,string]) returns ()'", 18, 19);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'other', found 'xml:Element'",
                29, 13);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: 'xml:Element' is not an iterable collection",
                29, 34);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'record {| xml:Element value; |}?', found 'record " +
                        "{| (xml:Element|xml:Comment|xml:ProcessingInstruction|xml:Text) value; |}?'",
                33, 54);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'other', found 'xml:Comment'",
                40, 13);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: 'xml:Comment' is not an iterable collection",
                40, 34);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'record {| xml:Comment value; |}?', found 'record " +
                        "{| (xml:Element|xml:Comment|xml:ProcessingInstruction|xml:Text) value; |}?'",
                44, 54);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'other', found 'xml:ProcessingInstruction'",
                51, 13);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: 'xml:ProcessingInstruction' is not an iterable collection",
                51, 48);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'record {| xml:ProcessingInstruction value; |}?', " +
                        "found 'record {| (xml:Element|xml:Comment|xml:ProcessingInstruction|xml:Text) value; |}?'",
                55, 63);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'other', found '(xml:Element|xml:Text)'",
                63, 13);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: '(xml:Element|xml:Text)' is not an iterable collection",
                63, 44);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'other', found '(xml:Element|xml:Text)'",
                68, 13);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: '(xml<xml:Element>|xml<xml:Text>)' is not an iterable collection",
                68, 44);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'record {| (xml:Element|xml:Text) value; |}?', found 'record " +
                        "{| (xml:Element|xml:Comment|xml:ProcessingInstruction|xml:Text) value; |}?'",
                72, 68);
        BAssertUtil.validateError(negative, index++,
                "incompatible types: expected 'record {| (xml:Element|xml:Text) value; |}?', found 'record " +
                        "{| (xml:Element|xml:Comment|xml:ProcessingInstruction|xml:Text) value; |}?'",
                73, 68);
    }

    @Test
    public void testXMLForeach() {
        BRunUtil.invoke(result, "foreachTest");
    }

    @Test
    public void testXMLForeachOp() {
        BRunUtil.invoke(result, "foreachOpTest");
    }

    @Test
    public void testXMLTypesForeachOp() {
        BRunUtil.invoke(result, "testXmlElementSequenceIteration");
        BRunUtil.invoke(result, "testXmlTextSequenceIteration");
        BRunUtil.invoke(result, "testXmlCommentSequenceIteration");
        BRunUtil.invoke(result, "testXmlPISequenceIteration");
        BRunUtil.invoke(result, "testXmlUnionSequenceIteration");
        BRunUtil.invoke(result, "testXmlSequenceIteration");
    }

    @Test
    public void testXMLMapOp() {
        BRunUtil.invoke(result, "mapOpTest");
    }

    @Test
    public void testXMLFilterOp() {
        BValue[] returns = BRunUtil.invoke(result, "filterOpTest");
    }

    @Test
    public void testXMLChainedIterableOps() {
        BValue[] returns = BRunUtil.invoke(result, "chainedIterableOps");

        Assert.assertNotNull(returns);

        BValueArray resArray = ((BXMLSequence) returns[0]).value();
        Assert.assertEquals(((BXMLSequence) resArray.getRefValue(0)).getTextValue().stringValue(), authors[0][0]);
        Assert.assertEquals(((BXMLSequence) resArray.getRefValue(1)).getTextValue().stringValue(), authors[1][0]);
    }

    @Test(groups = { "disableOnOldParser" },
            description = "Test iterating over xml elements where some elements are characters")
    public void testXMLCompoundCharacterSequenceIteration() {
        BValue[] results = BRunUtil.invoke(result, "xmlSequenceIter");
        Assert.assertEquals(result.getDiagnostics().length, 0);
        String str = results[0].stringValue();
        Assert.assertEquals(str, "<book>the book</book>\nbit of text\\u2702\\u2705\n");
    }

    @Test(groups = { "disableOnOldParser" },
            description = "Test iterating over xml sequence where all elements are character items")
    public void testXMLCharacterSequenceIteration() {
        BValue[] results = BRunUtil.invoke(result, "xmlCharItemIter");
        Assert.assertEquals(result.getDiagnostics().length, 0);
        String str = results[0].stringValue();
        Assert.assertEquals(str, "bit of text\\u2702\\u2705\n");
    }
}
