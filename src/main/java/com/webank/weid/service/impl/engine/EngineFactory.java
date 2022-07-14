

package com.webank.weid.service.impl.engine;

import com.webank.weid.constant.CnsType;
import com.webank.weid.service.impl.engine.fiscov2.AuthorityIssuerEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.CptServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.DataBucketServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.EvidenceServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.RawTransactionServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.WeIdServiceEngineV2;

public class EngineFactory {

    /**
     * create WeIdServiceEngine.
     * @return WeIdServiceEngine object
     */
    public static WeIdServiceEngine createWeIdServiceEngine() {
        return new WeIdServiceEngineV2();
    }

    /**
     * create CptServiceEngine.
     * @return CptServiceEngine object
     */
    public static CptServiceEngine createCptServiceEngine() {
        return new CptServiceEngineV2();
    }

    /**
     * create CptServiceEngine.
     * @return CptServiceEngine object
     */
    public static AuthorityIssuerServiceEngine createAuthorityIssuerServiceEngine() {
        return new AuthorityIssuerEngineV2();
    }

    /**
     * create EvidenceServiceEngine.
     * @param groupId 群组编号
     * @return EvidenceServiceEngine object
     */
    public static EvidenceServiceEngine createEvidenceServiceEngine(Integer groupId) {
        return new EvidenceServiceEngineV2(groupId);
    }

    /**
     * create RawTransactionServiceEngine.
     * @return RawTransactionServiceEngine object
     */
    public static RawTransactionServiceEngine createRawTransactionServiceEngine() {
        return new RawTransactionServiceEngineV2();
    }
    
    /**
     * create DataBucketServiceEngine.
     * @param cnsType cns类型枚举
     * @return DataBucketServiceEngine object
    */
    public static DataBucketServiceEngine createDataBucketServiceEngine(CnsType cnsType) {
        return new DataBucketServiceEngineV2(cnsType);
    }
}
