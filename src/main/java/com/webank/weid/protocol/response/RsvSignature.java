

package com.webank.weid.protocol.response;

import lombok.Data;

import org.fisco.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint8;

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
}
