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
import org.bcos.web3j.abi.FunctionEncoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.Type;
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
 * Auto generated code.<br> <strong>Do not modify!</strong><br> Please use the <a
 * href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link
 * org.bcos.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version none.
 */
public final class CommitteeMemberController extends Contract {

    /**
     * The Constant ABI.
     */
    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[],\"name\":\"getAllCommitteeMemberAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address[]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"removeCommitteeMember\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"isCommitteeMember\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"addCommitteeMember\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"inputs\":[{\"name\":\"committeeMemberDataAddress\",\"type\":\"address\"},{\"name\":\"roleControllerAddress\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"operation\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"retCode\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"CommitteeRetLog\",\"type\":\"event\"}]";

    private static String BINARY =
        "6060604052341561000c57fe5b604051604080610ec4833981016040528080519060200190919080519060200190919050505b81600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b50505b610dfd806100c76000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680633c0d9c1e1461005c5780637fad0d48146100d1578063e636d84b14610107578063e7f1f95314610155575bfe5b341561006457fe5b61006c61018b565b60405180806020018281038252838181518152602001915080519060200190602002808383600083146100be575b8051825260208311156100be5760208201915060208101905060208303925061009a565b5050509050019250505060405180910390f35b34156100d957fe5b610105600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610386565b005b341561010f57fe5b61013b600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610830565b604051808215151515815260200191505060405180910390f35b341561015d57fe5b610189600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610914565b005b610193610dbd565b600061019d610dbd565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e083a3ad6000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561022d57fe5b6102c65a03f1151561023b57fe5b505050604051805190509250826040518059106102555750595b908082528060200260200182016040525b509150600090505b8281101561037c57600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166396d5afbf826000604051602001526040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180828152602001915050602060405180830381600087803b151561030c57fe5b6102c65a03f1151561031a57fe5b50505060405180519050828281518110151561033257fe5b9060200190602002019073ffffffffffffffffffffffffffffffffffffffff16908173ffffffffffffffffffffffffffffffffffffffff16815250505b808060010191505061026e565b8193505b50505090565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e636d84b826000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b151561044857fe5b6102c65a03f1151561045657fe5b5050506040518051905015156104e2577f3bd22f9efc9b65160af79bb416016337d6b57061a60af4d5d6c98b0e5936f9a660016207a21c83604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a161082d565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663c6d8a3f36000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b15156105af57fe5b6102c65a03f115156105bd57fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b151561065157fe5b6102c65a03f1151561065f57fe5b5050506040518051905015156106eb577f3bd22f9efc9b65160af79bb416016337d6b57061a60af4d5d6c98b0e5936f9a660016207a21d83604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a161082d565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166378f341ef826040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050600060405180830381600087803b15156107a457fe5b6102c65a03f115156107b257fe5b5050507f3bd22f9efc9b65160af79bb416016337d6b57061a60af4d5d6c98b0e5936f9a66001600083604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a15b5b5b50565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e636d84b836000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15156108f457fe5b6102c65a03f1151561090257fe5b5050506040518051905090505b919050565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e636d84b826000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15156109d657fe5b6102c65a03f115156109e457fe5b5050506040518051905015610a6f577f3bd22f9efc9b65160af79bb416016337d6b57061a60af4d5d6c98b0e5936f9a660006207a21b83604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a1610dba565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663c6d8a3f36000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b1515610b3c57fe5b6102c65a03f11515610b4a57fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1515610bde57fe5b6102c65a03f11515610bec57fe5b505050604051805190501515610c78577f3bd22f9efc9b65160af79bb416016337d6b57061a60af4d5d6c98b0e5936f9a660006207a21d83604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a1610dba565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663d9d2619c826040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050600060405180830381600087803b1515610d3157fe5b6102c65a03f11515610d3f57fe5b5050507f3bd22f9efc9b65160af79bb416016337d6b57061a60af4d5d6c98b0e5936f9a66000600083604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a15b5b5b50565b6020604051908101604052806000815250905600a165627a7a72305820d5d91eb9c6e562c45e891a4ebd6864f4c7606499518234d73a7b91fb180cf8210029";

    private CommitteeMemberController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private CommitteeMemberController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private CommitteeMemberController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private CommitteeMemberController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    /**
     * Gets the committee ret log events.
     *
     * @param transactionReceipt the transaction receipt
     * @return the committee ret log events
     */
    public static List<CommitteeRetLogEventResponse> getCommitteeRetLogEvents(
        TransactionReceipt transactionReceipt) {
        final Event event =
            new Event(
                "CommitteeRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Address>() {
                    }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<CommitteeRetLogEventResponse> responses =
            new ArrayList<CommitteeRetLogEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            CommitteeRetLogEventResponse typedResponse = new CommitteeRetLogEventResponse();
            typedResponse.operation = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.addr = (Address) eventValues.getNonIndexedValues().get(2);
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
     * @param committeeMemberDataAddress the committee member data address
     * @param roleControllerAddress the role controller address
     * @return the future
     */
    public static Future<CommitteeMemberController> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address committeeMemberDataAddress,
        Address roleControllerAddress) {
        String encodedConstructor =
            FunctionEncoder.encodeConstructor(
                Arrays.<Type>asList(committeeMemberDataAddress, roleControllerAddress));
        return deployAsync(
            CommitteeMemberController.class,
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
     * @param committeeMemberDataAddress the committee member data address
     * @param roleControllerAddress the role controller address
     * @return the future
     */
    public static Future<CommitteeMemberController> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address committeeMemberDataAddress,
        Address roleControllerAddress) {
        String encodedConstructor =
            FunctionEncoder.encodeConstructor(
                Arrays.<Type>asList(committeeMemberDataAddress, roleControllerAddress));
        return deployAsync(
            CommitteeMemberController.class,
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
     * @return the committee member controller
     */
    public static CommitteeMemberController load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberController(
            contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    /**
     * Load.
     *
     * @param contractAddress the contract address
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the committee member controller
     */
    public static CommitteeMemberController load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberController(
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
     * @return the committee member controller
     */
    public static CommitteeMemberController loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberController(
            contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the committee member controller
     */
    public static CommitteeMemberController loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CommitteeMemberController(
            contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    /**
     * Committee ret log event observable.
     *
     * @param startBlock the start block
     * @param endBlock the end block
     * @return the observable
     */
    public Observable<CommitteeRetLogEventResponse> committeeRetLogEventObservable(
        DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event =
            new Event(
                "CommitteeRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Address>() {
                    }));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j
            .ethLogObservable(filter)
            .map(
                new Func1<Log, CommitteeRetLogEventResponse>() {
                    @Override
                    public CommitteeRetLogEventResponse call(Log log) {
                        EventValues eventValues = extractEventParameters(event, log);
                        CommitteeRetLogEventResponse typedResponse = new CommitteeRetLogEventResponse();
                        typedResponse.operation = (Uint256) eventValues.getNonIndexedValues()
                            .get(0);
                        typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(1);
                        typedResponse.addr = (Address) eventValues.getNonIndexedValues().get(2);
                        return typedResponse;
                    }
                });
    }

    /**
     * Gets the all committee member address.
     *
     * @return the all committee member address
     */
    public Future<DynamicArray<Address>> getAllCommitteeMemberAddress() {
        Function function =
            new Function(
                "getAllCommitteeMemberAddress",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Removes the committee member.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<TransactionReceipt> removeCommitteeMember(Address addr) {
        Function function =
            new Function(
                "removeCommitteeMember",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Removes the committee member.
     *
     * @param addr the addr
     * @param callback the callback
     */
    public void removeCommitteeMember(Address addr, TransactionSucCallback callback) {
        Function function =
            new Function(
                "removeCommitteeMember",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
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

    /**
     * Adds the committee member.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<TransactionReceipt> addCommitteeMember(Address addr) {
        Function function =
            new Function(
                "addCommitteeMember",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Adds the committee member.
     *
     * @param addr the addr
     * @param callback the callback
     */
    public void addCommitteeMember(Address addr, TransactionSucCallback callback) {
        Function function =
            new Function(
                "addCommitteeMember",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * The Class CommitteeRetLogEventResponse.
     */
    public static class CommitteeRetLogEventResponse {

        /**
         * The operation.
         */
        public Uint256 operation;

        /**
         * The ret code.
         */
        public Uint256 retCode;

        /**
         * The addr.
         */
        public Address addr;
    }
}
