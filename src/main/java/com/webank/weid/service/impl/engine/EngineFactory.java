/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.service.impl.engine;

import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.service.impl.engine.fiscov1.AuthorityIssuerEngineV1;
import com.webank.weid.service.impl.engine.fiscov1.CptServiceEngineV1;
import com.webank.weid.service.impl.engine.fiscov1.EvidenceServiceEngineV1;
import com.webank.weid.service.impl.engine.fiscov1.RawTransactionServiceEngineV1;
import com.webank.weid.service.impl.engine.fiscov1.WeIdServiceEngineV1;
import com.webank.weid.service.impl.engine.fiscov2.AuthorityIssuerEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.CptServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.DataBucketServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.EvidenceServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.RawTransactionServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.WeIdServiceEngineV2;
import com.webank.weid.util.PropertyUtils;

public class EngineFactory {

    /**
     * fisco bcos version, default 1.3.x
     */
    private static String fiscoVersion = PropertyUtils.getProperty("bcos.version", "1.3");

    /**
     * create WeIdServiceEngine.
     * @return WeIdServiceEngine object
     */
    public static WeIdServiceEngine createWeIdServiceEngine() {
        if (fiscoVersion.startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            return new WeIdServiceEngineV1();
        }
        return new WeIdServiceEngineV2();
    }

    /**
     * create CptServiceEngine.
     * @return CptServiceEngine object
     */
    public static CptServiceEngine createCptServiceEngine() {
        if (fiscoVersion.startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            return new CptServiceEngineV1();
        }
        return new CptServiceEngineV2();
    }

    /**
     * create CptServiceEngine.
     * @return CptServiceEngine object
     */
    public static AuthorityIssuerServiceEngine createAuthorityIssuerServiceEngine() {
        if (fiscoVersion.startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            return new AuthorityIssuerEngineV1();
        }
        return new AuthorityIssuerEngineV2();
    }

    /**
     * create EvidenceServiceEngine.
     * @param groupId 群组编号
     * @return EvidenceServiceEngine object
     */
    public static EvidenceServiceEngine createEvidenceServiceEngine(Integer groupId) {
        if (fiscoVersion.startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            return new EvidenceServiceEngineV1();
        }
        return new EvidenceServiceEngineV2(groupId);
    }

    /**
     * create RawTransactionServiceEngine.
     * @return RawTransactionServiceEngine object
     */
    public static RawTransactionServiceEngine createRawTransactionServiceEngine() {
        if (fiscoVersion.startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            return new RawTransactionServiceEngineV1();
        }
        return new RawTransactionServiceEngineV2();
    }
    
    /**
     * create DataBucketServiceEngine.
     * @param cnsType cns类型枚举
     * @return DataBucketServiceEngine object
    */
    public static DataBucketServiceEngine createDataBucketServiceEngine(CnsType cnsType) {
        if (fiscoVersion.startsWith(WeIdConstant.FISCO_BCOS_1_X_VERSION_PREFIX)) {
            return null;
        }
        return new DataBucketServiceEngineV2(cnsType);
    }
}
