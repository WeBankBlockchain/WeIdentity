package com.webank.weid.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.bcos.channel.client.TransactionSucCallback;
import org.bcos.web3j.abi.EventEncoder;
import org.bcos.web3j.abi.EventValues;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.DefaultBlockParameter;
import org.bcos.web3j.protocol.core.methods.request.EthFilter;
import org.bcos.web3j.protocol.core.methods.response.Log;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.bcos.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or
 * {@link org.bcos.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version none.
 */
public final class WeIdContract extends Contract {

    /**
     * The Constant ABI.
     */
    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[{\"name\":\"identity\",\"type\":\"address\"}],\"name\":\"isIdentityExist\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"identity\",\"type\":\"address\"},{\"name\":\"key\",\"type\":\"bytes32\"},{\"name\":\"value\",\"type\":\"bytes\"},{\"name\":\"updated\",\"type\":\"int256\"}],\"name\":\"setAttribute\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"identity\",\"type\":\"address\"}],\"name\":\"getLatestRelatedBlock\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"identity\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"key\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"bytes\"},{\"indexed\":false,\"name\":\"previousBlock\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"updated\",\"type\":\"int256\"}],\"name\":\"WeIdAttributeChanged\",\"type\":\"event\"}]";

    private static String BINARY =
        "6060604052341561000c57fe5b5b61041a8061001c6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063170abf9c146100515780633cf239db1461009f5780634298ab941461012e575bfe5b341561005957fe5b610085600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610178565b604051808215151515815260200191505060405180910390f35b34156100a757fe5b61012c600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080356000191690602001909190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919080359060200190919050506101f9565b005b341561013657fe5b610162600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506103a4565b6040518082815260200191505060405180910390f35b60008173ffffffffffffffffffffffffffffffffffffffff166000141580156101e15750600060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054600014155b156101ef57600190506101f4565b600090505b919050565b83338173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415156102365760006000fd5b8573ffffffffffffffffffffffffffffffffffffffff167fac4dd54786488e78364a1b80c3cb3682c4352995147d8987cbe5210a04261cf48686600060008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054876040518085600019166000191681526020018060200184815260200183815260200182810382528581815181526020019150805190602001908083836000831461031a575b80518252602083111561031a576020820191506020810190506020830392506102f6565b505050905090810190601f1680156103465780820380516001836020036101000a031916815260200191505b509550505050505060405180910390a243600060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b5b505050505050565b6000600060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490505b9190505600a165627a7a723058201b7ab5e5bb9720b2c6744299fc4c37ae5218af7f91d970d94a02efb326b037fe0029";

    private WeIdContract(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private WeIdContract(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private WeIdContract(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private WeIdContract(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    /**
     * Gets the WeIdentity DID attribute changed events.
     *
     * @param transactionReceipt the transaction receipt
     * @return the WeIdentity DID attribute changed events
     */
    public static List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
        TransactionReceipt transactionReceipt) {
        final Event event =
            new Event(
                "WeIdAttributeChanged",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Bytes32>() {
                    },
                    new TypeReference<DynamicBytes>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Int256>() {
                    }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<WeIdAttributeChangedEventResponse> responses =
            new ArrayList<WeIdAttributeChangedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            WeIdAttributeChangedEventResponse typedResponse = new WeIdAttributeChangedEventResponse();
            typedResponse.identity = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.key = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (DynamicBytes) eventValues.getNonIndexedValues().get(1);
            typedResponse.previousBlock = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.updated = (Int256) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    /**
     * Deploy.
     *
     * @param web3j the web3j
     * @param credentials the credentials
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @param initialWeiValue the initial wei value
     * @return the future
     */
    public static Future<WeIdContract> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue) {
        return deployAsync(
            WeIdContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "",
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
     * @return the future
     */
    public static Future<WeIdContract> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue) {
        return deployAsync(
            WeIdContract.class,
            web3j,
            transactionManager,
            gasPrice,
            gasLimit,
            BINARY,
            "",
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
     * @return the WeIdentity DID contract
     */
    public static WeIdContract load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new WeIdContract(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    /**
     * Load.
     *
     * @param contractAddress the contract address
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the WeIdentity DID contract
     */
    public static WeIdContract load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new WeIdContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit,
            false);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param credentials the credentials
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the WeIdentity DID contract
     */
    public static WeIdContract loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new WeIdContract(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the WeIdentity DID contract
     */
    public static WeIdContract loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new WeIdContract(contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    /**
     * WeIdentity DID attribute changed event observable.
     *
     * @param startBlock the start block
     * @param endBlock the end block
     * @return the observable
     */
    public Observable<WeIdAttributeChangedEventResponse> weIdAttributeChangedEventObservable(
        DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event =
            new Event(
                "WeIdAttributeChanged",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Bytes32>() {
                    },
                    new TypeReference<DynamicBytes>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Int256>() {
                    }));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j
            .ethLogObservable(filter)
            .map(
                new Func1<Log, WeIdAttributeChangedEventResponse>() {
                    @Override
                    public WeIdAttributeChangedEventResponse call(Log log) {
                        EventValues eventValues = extractEventParameters(event, log);
                        WeIdAttributeChangedEventResponse typedResponse =
                            new WeIdAttributeChangedEventResponse();
                        typedResponse.identity = (Address) eventValues.getIndexedValues().get(0);
                        typedResponse.key = (Bytes32) eventValues.getNonIndexedValues().get(0);
                        typedResponse.value = (DynamicBytes) eventValues.getNonIndexedValues()
                            .get(1);
                        typedResponse.previousBlock = (Uint256) eventValues.getNonIndexedValues()
                            .get(2);
                        typedResponse.updated = (Int256) eventValues.getNonIndexedValues().get(3);
                        return typedResponse;
                    }
                });
    }

    /**
     * Checks if is identity exist.
     *
     * @param identity the identity
     * @return the future
     */
    public Future<Bool> isIdentityExist(Address identity) {
        Function function =
            new Function(
                "isIdentityExist",
                Arrays.<Type>asList(identity),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Sets the attribute.
     *
     * @param identity the identity
     * @param key the key
     * @param value the value
     * @param updated the updated
     * @return the future
     */
    public Future<TransactionReceipt> setAttribute(
        Address identity, Bytes32 key, DynamicBytes value, Int256 updated) {
        Function function =
            new Function(
                "setAttribute",
                Arrays.<Type>asList(identity, key, value, updated),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Sets the attribute.
     *
     * @param identity the identity
     * @param key the key
     * @param value the value
     * @param updated the updated
     * @param callback the callback
     */
    public void setAttribute(
        Address identity,
        Bytes32 key,
        DynamicBytes value,
        Int256 updated,
        TransactionSucCallback callback) {
        Function function =
            new Function(
                "setAttribute",
                Arrays.<Type>asList(identity, key, value, updated),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Gets the latest related block.
     *
     * @param identity the identity
     * @return the latest related block
     */
    public Future<Uint256> getLatestRelatedBlock(Address identity) {
        Function function =
            new Function(
                "getLatestRelatedBlock",
                Arrays.<Type>asList(identity),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * The Class WeIdAttributeChangedEventResponse.
     */
    public static class WeIdAttributeChangedEventResponse {

        /**
         * The identity.
         */
        public Address identity;

        /**
         * The key.
         */
        public Bytes32 key;

        /**
         * The value.
         */
        public DynamicBytes value;

        /**
         * The previous block.
         */
        public Uint256 previousBlock;

        /**
         * The updated.
         */
        public Int256 updated;
    }
}
