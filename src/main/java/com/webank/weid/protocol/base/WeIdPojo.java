/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.protocol.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * WeIdentity DID information for created. 
 * @author v_wbgyang
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WeIdPojo extends WeIdBaseInfo {
    
    /**
     * the blockNum for the WeIdentity DID.
     */
    private Integer currentBlockNum;
    
    /**
     * the index for the blockNum.
     */
    private Integer index;
    
    /**
     * the previous blockNum
     */
    private Integer previousBlockNum;
}
