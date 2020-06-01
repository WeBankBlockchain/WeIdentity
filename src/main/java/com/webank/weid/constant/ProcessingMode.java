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

package com.webank.weid.constant;

/**
 * 上链处理模式.
 * @author v_wbgyang
 *
 */
public enum ProcessingMode {
    
    /**
     * 立即上链模式，此模式下会立即将数据发送至区块链节点.
     */
    IMMEDIATE,
    
    /**
     * 批量延迟上链模式，此模式下会先将数据存入介质中，然后异步去上链处理.
     */
    PERIODIC_AND_BATCH
}
