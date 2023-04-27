

package com.webank.weid.exception;

import com.webank.weid.blockchain.constant.ErrorCode;

/**
 * Private Key IllegalException.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class PrivateKeyIllegalException extends WeIdBaseException {

    public PrivateKeyIllegalException(Throwable cause) {
        super(ErrorCode.WEID_PRIVATEKEY_INVALID.getCodeDesc(), cause);
    }

    public PrivateKeyIllegalException() {
        super(ErrorCode.WEID_PRIVATEKEY_INVALID.getCodeDesc());
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.WEID_PRIVATEKEY_INVALID;
    }
}
