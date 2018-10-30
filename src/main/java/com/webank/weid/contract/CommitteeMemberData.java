package com.webank.weid.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;
import org.bcos.channel.client.TransactionSucCallback;
import org.bcos.web3j.abi.FunctionEncoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.bcos.web3j.tx.TransactionManager;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or
 * {@link org.bcos.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version none.
 */
public final class CommitteeMemberData extends Contract {

    /**
     * The Constant ABI.
     */
    public static final String ABI =
        "[{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"deleteCommitteeMemberFromAddress\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getCommitteeMemberAddressFromIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"addCommitteeMemberFromAddress\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getDatasetLength\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"isCommitteeMember\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\"}]";

    private static String BINARY =
        "6060604052341561000c57fe5b604051602080610c05833981016040528080519060200190919050505b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b505b610b898061007c6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806378f341ef1461006757806396d5afbf1461009d578063d9d2619c146100fd578063e083a3ad14610133578063e636d84b14610159575bfe5b341561006f57fe5b61009b600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506101a7565b005b34156100a557fe5b6100bb6004808035906020019091905050610661565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561010557fe5b610131600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506106a7565b005b341561013b57fe5b610143610a2b565b6040518082815260200191505060405180910390f35b341561016157fe5b61018d600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610a39565b604051808215151515815260200191505060405180910390f35b600060006101b483610a39565b15156101bf5761065c565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663c6d8a3f36000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561028c57fe5b6102c65a03f1151561029a57fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b151561032e57fe5b6102c65a03f1151561033c57fe5b5050506040518051905015156103515761065c565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166379db5f6784600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663793387466000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561041e57fe5b6102c65a03f1151561042c57fe5b505050604051805190506040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050600060405180830381600087803b15156104b757fe5b6102c65a03f115156104c557fe5b5050506000805490509150600090505b8181101561055f578273ffffffffffffffffffffffffffffffffffffffff1660008281548110151561050357fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156105515761055f565b5b80806001019150506104d5565b60018203811415156106045760006001830381548110151561057d57fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000828154811015156105b957fe5b906000526020600020900160005b6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b60006001830381548110151561061657fe5b906000526020600020900160005b6101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600080548091906001900361065a9190610ae0565b505b505050565b600060008281548110151561067257fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690505b919050565b6106b081610a39565b156106ba57610a28565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663c6d8a3f36000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561078757fe5b6102c65a03f1151561079557fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b151561082957fe5b6102c65a03f1151561083757fe5b50505060405180519050151561084c57610a28565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16637fde1c8a82600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663793387466000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561091957fe5b6102c65a03f1151561092757fe5b505050604051805190506040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050600060405180830381600087803b15156109b257fe5b6102c65a03f115156109c057fe5b505050600080548060010182816109d79190610b0c565b916000526020600020900160005b83909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505b50565b600060008054905090505b90565b60006000600090505b600080549050811015610ad5578273ffffffffffffffffffffffffffffffffffffffff16600082815481101515610a7557fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415610ac75760019150610ada565b5b8080600101915050610a42565b600091505b50919050565b815481835581811511610b0757818360005260206000209182019101610b069190610b38565b5b505050565b815481835581811511610b3357818360005260206000209182019101610b329190610b38565b5b505050565b610b5a91905b80821115610b56576000816000905550600101610b3e565b5090565b905600a165627a7a723058206e28f60e0dd7d3ff84bb268f7eaf713c2b9e70bd1864ae4f4fc08b915738d4f50029";

    private CommitteeMemberData(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private CommitteeMemberData(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private CommitteeMemberData(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private CommitteeMemberData(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    /**
     * Deploy.
     *
     * @param web3j the web3j
     * @param credentials the credentials
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @param initialWeiValue the initial wei value
     * @param addr the addr
     * @return the future
     */
    public static Future<CommitteeMemberData> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address addr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(addr));
        return deployAsync(
            CommitteeMemberData.class,
            web3j,
            credentials,
            gasPrice,
            gasLimit,
            BINARY,
            encodedConstructor,
            initialWeiValue);
    }

    /**
     * Deploy.
     *
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @param initialWeiValue the initial wei value
     * @param addr the addr
     * @return the future
     */
    public static Future<CommitteeMemberData> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address addr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(addr));
        return deployAsync(
            CommitteeMemberData.class,
            web3j,
            transactionManager,
            gasPrice,
            gasLimit,
            BINARY,
            encodedConstructor,
            initialWeiValue);
    }

    /**
     * Load.
     *
     * @param contractAddress the contract address
     * @param web3j the web3j
     * @param credentials the credentials
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the committee member data
     */
    public static CommitteeMemberData load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberData(contractAddress, web3j, credentials, gasPrice, gasLimit,
            false);
    }

    /**
     * Load.
     *
     * @param contractAddress the contract address
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the committee member data
     */
    public static CommitteeMemberData load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberData(
            contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param credentials the credentials
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the committee member data
     */
    public static CommitteeMemberData loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberData(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the committee member data
     */
    public static CommitteeMemberData loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberData(
            contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    /**
     * Delete committee member from address.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<TransactionReceipt> deleteCommitteeMemberFromAddress(Address addr) {
        Function function =
            new Function(
                "deleteCommitteeMemberFromAddress",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Delete committee member from address.
     *
     * @param addr the addr
     * @param callback the callback
     */
    public void deleteCommitteeMemberFromAddress(Address addr, TransactionSucCallback callback) {
        Function function =
            new Function(
                "deleteCommitteeMemberFromAddress",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Gets the committee member address from index.
     *
     * @param index the index
     * @return the committee member address from index
     */
    public Future<Address> getCommitteeMemberAddressFromIndex(Uint256 index) {
        Function function =
            new Function(
                "getCommitteeMemberAddressFromIndex",
                Arrays.<Type>asList(index),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Adds the committee member from address.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<TransactionReceipt> addCommitteeMemberFromAddress(Address addr) {
        Function function =
            new Function(
                "addCommitteeMemberFromAddress",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Adds the committee member from address.
     *
     * @param addr the addr
     * @param callback the callback
     */
    public void addCommitteeMemberFromAddress(Address addr, TransactionSucCallback callback) {
        Function function =
            new Function(
                "addCommitteeMemberFromAddress",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Gets the dataset length.
     *
     * @return the dataset length
     */
    public Future<Uint256> getDatasetLength() {
        Function function =
            new Function(
                "getDatasetLength",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Checks if is committee member.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<Bool> isCommitteeMember(Address addr) {
        Function function =
            new Function(
                "isCommitteeMember",
                Arrays.<Type>asList(addr),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }
}
