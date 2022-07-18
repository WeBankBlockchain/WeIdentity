/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.webank.weid.service.fisco.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author marsli
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CnsInfo {
    private String name;
    private String version;
    private String address;
    private String abi;


    public CnsInfo(org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo cnsInfo) {
        this.name = cnsInfo.getName();
        this.version = cnsInfo.getVersion();
        this.address = cnsInfo.getAddress();
        this.abi = cnsInfo.getAbi();
    }

    public CnsInfo(String name, String version, String address, String abi) {
        this.name = name;
        this.version = version;
        this.address = address;
        this.abi = abi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CnsInfo cnsInfo = (CnsInfo) o;
        return Objects.equals(name, cnsInfo.name)
                && Objects.equals(version, cnsInfo.version)
                && Objects.equals(address, cnsInfo.address)
                && Objects.equals(abi, cnsInfo.abi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, address, abi);
    }

    @Override
    public String toString() {
        return "CnsInfo{"
                + "name='"
                + name
                + '\''
                + ", version='"
                + version
                + '\''
                + ", address='"
                + address
                + '\''
                + ", abi='"
                + abi
                + '\''
                + '}';
    }
}
