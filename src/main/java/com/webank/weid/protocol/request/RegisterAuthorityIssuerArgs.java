

package com.webank.weid.protocol.request;

import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.PresentationPolicyE;
import lombok.Data;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.WeIdPrivateKey;

import java.util.HashMap;
import java.util.Map;

/**
 * The Arguments for SDK RegisterAuthorityIssuer.
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class RegisterAuthorityIssuerArgs {

    /**
     * Required: The authority issuer information.
     */
    private AuthorityIssuer authorityIssuer;

    /**
     * Required: The WeIdentity DID private key for sending transaction.
     */
    private WeIdPrivateKey weIdPrivateKey;

    /**
     * transfer RegisterAuthorityIssuerArgs class to weid-blockchain.
     * @param args the RegisterAuthorityIssuerArgs object
     * @return RegisterAuthorityIssuerArgs object in weid-blockchain
     */
    public static com.webank.weid.blockchain.protocol.request.RegisterAuthorityIssuerArgs toBlockChain(RegisterAuthorityIssuerArgs args) {
        com.webank.weid.blockchain.protocol.request.RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new com.webank.weid.blockchain.protocol.request.RegisterAuthorityIssuerArgs();
        com.webank.weid.blockchain.protocol.base.AuthorityIssuer authorityIssuer = AuthorityIssuer.toBlockChain(args.getAuthorityIssuer());
        registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuer);
        com.webank.weid.blockchain.protocol.base.WeIdPrivateKey weIdPrivateKey = new com.webank.weid.blockchain.protocol.base.WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(args.getWeIdPrivateKey().getPrivateKey());
        registerAuthorityIssuerArgs.setWeIdPrivateKey(weIdPrivateKey);
        return registerAuthorityIssuerArgs;
    }
}
