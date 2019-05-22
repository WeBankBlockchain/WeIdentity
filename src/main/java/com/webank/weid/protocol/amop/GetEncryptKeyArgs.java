package com.webank.weid.protocol.amop;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * the request body for get EncryptKey.
 * 
 * @author tonychen 2019年5月7日.
 *
 */
@Getter
@Setter
public class GetEncryptKeyArgs extends AmopBaseMsgArgs {

    /**
     * the encrypKey Id.
     */
    private String keyId;
}
