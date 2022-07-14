

package com.webank.weid.protocol.response;

import java.math.BigInteger;

import lombok.Data;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Numeric;


/**
 * The basic transaction information. Caller can further use these information to track the detailed
 * transaction instance from blockchain.
 *
 * @author chaoxinhu 2019.4
 */

@Data
public class TransactionInfo {

    /**
     * The block number.
     */
    private BigInteger blockNumber;

    /**
     * The transaction hash value.
     */
    private String transactionHash;

    /**
     * The transaction index.
     */
    private BigInteger transactionIndex;

    /**
     * Constructor from a transactionReceipt.
     *
     * @param receipt the transaction receipt
     */
    public TransactionInfo(TransactionReceipt receipt) {
        if (receipt != null) {
            //this.blockNumber = receipt.getBlockNumber();
            this.blockNumber = new BigInteger(Numeric.cleanHexPrefix(receipt.getBlockNumber()), 16);
            this.transactionHash = receipt.getTransactionHash();
            //this.transactionIndex = receipt.getTransactionIndex();
            this.transactionIndex = new BigInteger(
                    Numeric.cleanHexPrefix(receipt.getTransactionIndex()), 16);
        }
    }

    /**
     * Constructor.
     *
     * @param blockNumber blockNumber
     * @param transactionHash transactionHash
     * @param transactionIndex transactionIndex
     */
    public TransactionInfo(BigInteger blockNumber,
        String transactionHash,
        BigInteger transactionIndex) {
        this.blockNumber = blockNumber;
        this.transactionHash = transactionHash;
        this.transactionIndex = transactionIndex;
    }
}
