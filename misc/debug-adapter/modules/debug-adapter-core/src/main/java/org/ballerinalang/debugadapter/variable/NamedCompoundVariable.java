/*
 * Copyright (c) 2021, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package org.ballerinalang.debugadapter.variable;

import com.sun.jdi.Value;
import org.ballerinalang.debugadapter.SuspendedContext;

import java.util.Map;

/**
 * Implementation for ballerina variable types with named child variables (i.e. map entries, json elements, etc).
 *
 * @since 2.0.0
 */
public abstract class NamedCompoundVariable extends BCompoundVariable {

    private Map<String, Value> namedChildVariables;

    public NamedCompoundVariable(SuspendedContext context, String varName, BVariableType bVarType, Value jvmValue) {
        super(context, varName, bVarType, jvmValue);
    }

    /**
     * Returns JDI value representations of all the child variables, as a map of named child variables (i.e. map
     * entries, json elements, etc.)
     * <p>
     * Each compound variable type with named child variables must have their own implementation to compute/fetch
     * values.
     */
    protected abstract Map<String, Value> computeNamedChildVariables();

    public Map<String, Value> getNamedChildVariables() {
        if (namedChildVariables == null) {
            namedChildVariables = computeNamedChildVariables();
        }
        return namedChildVariables;
    }

    public Value getChildByName(String name) throws DebugVariableException {
        if (namedChildVariables == null) {
            namedChildVariables = computeNamedChildVariables();
        }

        if (!namedChildVariables.containsKey(name)) {
            throw new DebugVariableException("No child variables found with name: '" + name + "'");
        }
        return namedChildVariables.get(name);
    }
}
