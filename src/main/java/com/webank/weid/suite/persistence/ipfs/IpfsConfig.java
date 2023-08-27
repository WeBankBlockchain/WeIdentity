package com.webank.weid.suite.persistence.ipfs;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.util.PropertyUtils;
import io.ipfs.api.IPFS;

/**
 * ipfs配置类
 *
 * @author 刘家辉
 * @date 2023/08/24
 */
public class IpfsConfig {

    private static final String IPFS_API = PropertyUtils.getProperty(
            DataDriverConstant.IPFS_API);

    public  IPFS ipfsClient(){
        return new IPFS(IPFS_API);
    }

}
