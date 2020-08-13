/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.projects;

import java.util.Objects;
import java.util.UUID;

/**
 * A unique identifier of a {@code Module} instance.
 *
 * @since 2.0.0
 */
public final class ModuleId {
    private final UUID id;
    private final String moduleDirPath;
    private final PackageId packageId;

    private ModuleId(UUID id, String moduleDirPath, PackageId packageId) {
        this.id = id;
        this.moduleDirPath = moduleDirPath;
        this.packageId = packageId;
    }

    public static ModuleId create(String moduleDirPath, PackageId packageId) {
        return new ModuleId(UUID.randomUUID(), moduleDirPath, packageId);
    }

    public UUID id() {
        return id;
    }

    public PackageId packageId() {
        return packageId;
    }

    @Override
    public String toString() {
        return "ModuleId{" +
                "id=" + id +
                ", moduleDir='" + moduleDirPath + '\'' +
                ", packageId=" + packageId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModuleId moduleId = (ModuleId) o;
        return id.equals(moduleId.id) &&
                packageId.equals(moduleId.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, packageId);
    }
}
