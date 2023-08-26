package com.webank.weid.suite.persistence.ipfs;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.util.PropertyUtils;
import io.ipfs.api.IPFS;

/**
 * ipf配置
 *
 * @author 刘家辉
 * @date 2023/08/24
 */
public class IpfsConfig {

    /**
     * ipfs的api
     */
    private static final String IPFS_API = PropertyUtils.getProperty(
            DataDriverConstant.IPFS_API);

    /**
     * 获得ipfs客户端 可重用
     *
     * @return {@link IPFS}
     */
    public  IPFS ipfsClient(){
        return new IPFS(IPFS_API);
    }

}
