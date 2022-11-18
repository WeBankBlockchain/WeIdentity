

package com.webank.weid.protocol.response;

import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.ServiceProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import lombok.Data;

import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint8;

import java.util.ArrayList;
import java.util.List;


/**
 * The internal base RSV signature data class.
 *
 * @author lingfenghe
 */
@Data
public class RsvSignature {

    /**
     * The v value.
     */
    private Uint8 v;

    /**
     * The r value.
     */
    private Bytes32 r;

    /**
     * The s value.
     */
    private Bytes32 s;

    /**
     * todo support sm2
     */
//    private Bytes32 pub;

    /**
     * transfer RsvSignature from weid-blockchain.
     * @param signature the RsvSignature class in weid-blockchain
     * @return RsvSignature
     */
    public static RsvSignature fromBlockChain(com.webank.weid.blockchain.protocol.response.RsvSignature signature) {
        RsvSignature rsvSignature = new RsvSignature();
        rsvSignature.setV(signature.getV());
        rsvSignature.setS(signature.getS());
        rsvSignature.setR(signature.getR());
        return rsvSignature;
    }

    /**
     * transfer RsvSignature to weid-blockchain.
     * @param signature the RsvSignature class
     * @return RsvSignature
     */
    public static com.webank.weid.blockchain.protocol.response.RsvSignature toBlockChain(RsvSignature signature) {
        com.webank.weid.blockchain.protocol.response.RsvSignature rsvSignature = new com.webank.weid.blockchain.protocol.response.RsvSignature();
        rsvSignature.setV(signature.getV());
        rsvSignature.setS(signature.getS());
        rsvSignature.setR(signature.getR());
        return rsvSignature;
    }
}
