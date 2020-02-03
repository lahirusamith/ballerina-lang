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
package org.ballerinalang.langserver.completions.util.sorters;

import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.completion.LSCompletionItem;
import org.eclipse.lsp4j.CompletionItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Default Item Sorter.
 */
public class DefaultItemSorter extends CompletionItemSorter {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompletionItem> sortItems(LSContext ctx, List<LSCompletionItem> completionItems) {
        List<CompletionItem> cItems = new ArrayList<>();
        for (LSCompletionItem lsCItem : completionItems) {
            CompletionItem completionItem = lsCItem.getCompletionItem();
            this.setPriority(completionItem);
            cItems.add(completionItem);
        }
        return cItems;
    }

    @Nonnull
    @Override
    protected List<Class> getAttachedContexts() {
        return Collections.singletonList(DefaultItemSorter.class);
    }
}
