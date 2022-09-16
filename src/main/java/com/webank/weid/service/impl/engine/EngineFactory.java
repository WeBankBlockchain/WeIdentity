

package com.webank.weid.service.impl.engine;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.service.impl.engine.fiscov2.AuthorityIssuerEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.CptServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.DataBucketServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.EvidenceServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.RawTransactionServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov2.WeIdServiceEngineV2;
import com.webank.weid.service.impl.engine.fiscov3.AuthorityIssuerEngineV3;
import com.webank.weid.service.impl.engine.fiscov3.EvidenceServiceEngineV3;
import com.webank.weid.service.impl.engine.fiscov3.CptServiceEngineV3;
import com.webank.weid.service.impl.engine.fiscov3.DataBucketServiceEngineV3;
import com.webank.weid.service.impl.engine.fiscov3.RawTransactionServiceEngineV3;
import com.webank.weid.service.impl.engine.fiscov3.WeIdServiceEngineV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加上V2
 */
public class EngineFactory {

    private static final Logger logger = LoggerFactory.getLogger(EngineFactory.class);

    /**
     * The Fisco Config bundle.
     */
    protected static final FiscoConfig fiscoConfig;

    protected static final Boolean isVer2;

    static {
        fiscoConfig = new FiscoConfig();
        if (!fiscoConfig.load()) {
            logger.error("[BaseService] Failed to load Fisco-BCOS blockchain node information.");
            System.exit(1);
        }
        isVer2 = fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_2_X_VERSION_PREFIX);
    }

    /**
     * create WeIdServiceEngine.
     * @return WeIdServiceEngine object
     */
    public static WeIdServiceEngine createWeIdServiceEngine() {
        if (isVer2) {
            return new WeIdServiceEngineV2();
        } else {
            return new WeIdServiceEngineV3();
        }
    }

    /**
     * create CptServiceEngine.
     * @return CptServiceEngine object
     */
    public static CptServiceEngine createCptServiceEngine() {
        if (isVer2) {
            return new CptServiceEngineV2();
        } else {
            return new CptServiceEngineV3();
        }
    }

    /**
     * create CptServiceEngine.
     * @return CptServiceEngine object
     */
    public static AuthorityIssuerServiceEngine createAuthorityIssuerServiceEngine() {
        if (isVer2) {
            return new AuthorityIssuerEngineV2();
        } else {
            return new AuthorityIssuerEngineV3();
        }
    }

    /**
     * create EvidenceServiceEngine.
     * @param groupId 群组编号
     * @return EvidenceServiceEngine object
     */
    public static EvidenceServiceEngine createEvidenceServiceEngine(String groupId) {
        if (isVer2) {
            return new EvidenceServiceEngineV2(groupId);
        } else {
            return new EvidenceServiceEngineV3(groupId);
        }
    }

    /**
     * create RawTransactionServiceEngine.
     * @return RawTransactionServiceEngine object
     */
    public static RawTransactionServiceEngine createRawTransactionServiceEngine() {
        if (isVer2) {
            return new RawTransactionServiceEngineV2();
        } else {
            return new RawTransactionServiceEngineV3();
        }
    }
    
    /**
     * create DataBucketServiceEngine.
     * @param cnsType cns类型枚举
     * @return DataBucketServiceEngine object
    */
    public static DataBucketServiceEngine createDataBucketServiceEngine(CnsType cnsType) {
        if (isVer2) {
            return new DataBucketServiceEngineV2(cnsType);
        } else {
            return new DataBucketServiceEngineV3(cnsType);
        }
    }
}
