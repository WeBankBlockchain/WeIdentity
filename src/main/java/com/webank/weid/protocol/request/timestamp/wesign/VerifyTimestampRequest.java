

package com.webank.weid.protocol.request.timestamp.wesign;

import lombok.Data;

@Data
public class VerifyTimestampRequest {
    String webankAppId;
    String version = "1.0.0";
    String nonce;
    String sign;
    String plainHash;
    String b64TimeStamp;
    String hashAlg = "SHA1";
}
