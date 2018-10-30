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
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.StaticArray;
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
public final class AuthorityIssuerController extends Contract {

    /**
     * The Constant ABI.
     */
    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"getAuthorityIssuerInfoNonAccValue\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32[]\"},{\"name\":\"\",\"type\":\"int256[]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"removeAuthorityIssuer\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"},{\"name\":\"attribBytes32\",\"type\":\"bytes32[16]\"},{\"name\":\"attribInt\",\"type\":\"int256[16]\"},{\"name\":\"accValue\",\"type\":\"bytes\"}],\"name\":\"addAuthorityIssuer\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getAllAuthorityIssuerAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address[]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"isAuthorityIssuer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[{\"name\":\"authorityIssuerDataAddress\",\"type\":\"address\"},{\"name\":\"roleControllerAddress\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"operation\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"retCode\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"AuthorityIssuerRetLog\",\"type\":\"event\"}]";

    private static String BINARY =
        "6060604052341561000c57fe5b6040516040806113cd833981016040528080519060200190919080519060200190919050505b81600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b50505b611306806100c76000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806313b088ed146100675780634ce9d5ae146101565780635ed43ab21461018c5780637c1ae3701461025f57806392ff8129146102d4575bfe5b341561006f57fe5b61009b600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610322565b6040518080602001806020018381038352858181518152602001915080519060200190602002808383600083146100f1575b8051825260208311156100f1576020820191506020810190506020830392506100cd565b505050905001838103825284818151815260200191508051906020019060200280838360008314610141575b8051825260208311156101415760208201915060208101905060208303925061011d565b50505090500194505050505060405180910390f35b341561015e57fe5b61018a600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061051c565b005b341561019457fe5b61025d600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190806102000190601080602002604051908101604052809291908260106020028082843782019150505050509190806102000190601080602002604051908101604052809291908260106020028082843782019150505050509190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919050506109c6565b005b341561026757fe5b61026f610f69565b60405180806020018281038252838181518152602001915080519060200190602002808383600083146102c1575b8051825260208311156102c15760208201915060208101905060208303925061029d565b5050509050019250505060405180910390f35b34156102dc57fe5b610308600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611164565b604051808215151515815260200191505060405180910390f35b61032a611248565b61033261125c565b61033a611270565b61034261129d565b61034a611248565b61035261125c565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166313b088ed89600060405161040001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505061040060405180830381600087803b151561041857fe5b6102c65a03f1151561042657fe5b50505060405180610200018061020001604052809550819650505060106040518059106104505750595b908082528060200260200182016040525b50925060106040518059106104735750595b908082528060200260200182016040525b509150600090505b601081101561050b5784816010811015156104a357fe5b602002015183828151811015156104b657fe5b90602001906020020190600019169081600019168152505083816010811015156104dc57fe5b602002015182828151811015156104ef57fe5b90602001906020020181815250505b808060010191505061048c565b8282965096505b5050505050915091565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166392ff8129826000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15156105de57fe5b6102c65a03f115156105ec57fe5b505050604051805190501515610678577ffcb730e1916430caf8b1752daaa14b59e59354b89170fde494afa6a5f1190fa660016207a1ea83604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a16109c3565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ae23e1756000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561074557fe5b6102c65a03f1151561075357fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b15156107e757fe5b6102c65a03f115156107f557fe5b505050604051805190501515610881577ffcb730e1916430caf8b1752daaa14b59e59354b89170fde494afa6a5f1190fa660016207a1eb83604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a16109c3565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663c99ccd77826040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050600060405180830381600087803b151561093a57fe5b6102c65a03f1151561094857fe5b5050507ffcb730e1916430caf8b1752daaa14b59e59354b89170fde494afa6a5f1190fa66001600083604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a15b5b5b50565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166392ff8129856000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b1515610a8857fe5b6102c65a03f11515610a9657fe5b5050506040518051905015610b21577ffcb730e1916430caf8b1752daaa14b59e59354b89170fde494afa6a5f1190fa660006207a1e986604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a1610f63565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ae23e1756000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b1515610bee57fe5b6102c65a03f11515610bfc57fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1515610c9057fe5b6102c65a03f11515610c9e57fe5b505050604051805190501515610d2a577ffcb730e1916430caf8b1752daaa14b59e59354b89170fde494afa6a5f1190fa660006207a1eb86604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a1610f63565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16638e1a6209858585856040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200184601060200280838360008314610e03575b805182526020831115610e0357602082019150602081019050602083039250610ddf565b50505090500183601060200280838360008314610e3f575b805182526020831115610e3f57602082019150602081019050602083039250610e1b565b50505090500180602001828103825283818151815260200191508051906020019080838360008314610e90575b805182526020831115610e9057602082019150602081019050602083039250610e6c565b505050905090810190601f168015610ebc5780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b1515610eda57fe5b6102c65a03f11515610ee857fe5b5050507ffcb730e1916430caf8b1752daaa14b59e59354b89170fde494afa6a5f1190fa66000600086604051808481526020018381526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060405180910390a15b5b5b50505050565b610f716112c6565b6000610f7b6112c6565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e083a3ad6000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561100b57fe5b6102c65a03f1151561101957fe5b505050604051805190509250826040518059106110335750595b908082528060200260200182016040525b509150600090505b8281101561115a57600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16638667f0e2826000604051602001526040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180828152602001915050602060405180830381600087803b15156110ea57fe5b6102c65a03f115156110f857fe5b50505060405180519050828281518110151561111057fe5b9060200190602002019073ffffffffffffffffffffffffffffffffffffffff16908173ffffffffffffffffffffffffffffffffffffffff16815250505b808060010191505061104c565b8193505b50505090565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166392ff8129836000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b151561122857fe5b6102c65a03f1151561123657fe5b5050506040518051905090505b919050565b602060405190810160405280600081525090565b602060405190810160405280600081525090565b610200604051908101604052806010905b6000600019168152602001906001900390816112815790505090565b610200604051908101604052806010905b60008152602001906001900390816112ae5790505090565b6020604051908101604052806000815250905600a165627a7a72305820a83d48fffdad25e8997dd8b583401316fe665436937f7b6b716f247a95ce58e00029";

    private AuthorityIssuerController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private AuthorityIssuerController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private AuthorityIssuerController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private AuthorityIssuerController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    /**
     * Gets the authority issuer ret log events.
     *
     * @param transactionReceipt the transaction receipt
     * @return the authority issuer ret log events
     */
    public static List<AuthorityIssuerRetLogEventResponse> getAuthorityIssuerRetLogEvents(
        TransactionReceipt transactionReceipt) {
        final Event event =
            new Event(
                "AuthorityIssuerRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Address>() {
                    }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<AuthorityIssuerRetLogEventResponse> responses =
            new ArrayList<AuthorityIssuerRetLogEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            AuthorityIssuerRetLogEventResponse typedResponse = new AuthorityIssuerRetLogEventResponse();
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
     * @param authorityIssuerDataAddress the authority issuer data address
     * @param roleControllerAddress the role controller address
     * @return the future
     */
    public static Future<AuthorityIssuerController> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address authorityIssuerDataAddress,
        Address roleControllerAddress) {
        String encodedConstructor =
            FunctionEncoder.encodeConstructor(
                Arrays.<Type>asList(authorityIssuerDataAddress, roleControllerAddress));
        return deployAsync(
            AuthorityIssuerController.class,
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
     * @param authorityIssuerDataAddress the authority issuer data address
     * @param roleControllerAddress the role controller address
     * @return the future
     */
    public static Future<AuthorityIssuerController> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address authorityIssuerDataAddress,
        Address roleControllerAddress) {
        String encodedConstructor =
            FunctionEncoder.encodeConstructor(
                Arrays.<Type>asList(authorityIssuerDataAddress, roleControllerAddress));
        return deployAsync(
            AuthorityIssuerController.class,
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
     * @param web3j the web 3 j
     * @param credentials the credentials
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the authority issuer controller
     */
    public static AuthorityIssuerController load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerController(
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
     * @return the authority issuer controller
     */
    public static AuthorityIssuerController load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerController(
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
     * @return the authority issuer controller
     */
    public static AuthorityIssuerController loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerController(
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
     * @return the authority issuer controller
     */
    public static AuthorityIssuerController loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerController(
            contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    /**
     * Authority issuer ret log event observable.
     *
     * @param startBlock the start block
     * @param endBlock the end block
     * @return the observable
     */
    public Observable<AuthorityIssuerRetLogEventResponse> authorityIssuerRetLogEventObservable(
        DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event =
            new Event(
                "AuthorityIssuerRetLog",
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
                new Func1<Log, AuthorityIssuerRetLogEventResponse>() {
                    @Override
                    public AuthorityIssuerRetLogEventResponse call(Log log) {
                        EventValues eventValues = extractEventParameters(event, log);
                        AuthorityIssuerRetLogEventResponse typedResponse =
                            new AuthorityIssuerRetLogEventResponse();
                        typedResponse.operation = (Uint256) eventValues.getNonIndexedValues()
                            .get(0);
                        typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(1);
                        typedResponse.addr = (Address) eventValues.getNonIndexedValues().get(2);
                        return typedResponse;
                    }
                });
    }

    /**
     * Gets the authority issuer info non acc value.
     *
     * @param addr the addr
     * @return the authority issuer info non acc value
     */
    public Future<List<Type>> getAuthorityIssuerInfoNonAccValue(Address addr) {
        Function function =
            new Function(
                "getAuthorityIssuerInfoNonAccValue",
                Arrays.<Type>asList(addr),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<DynamicArray<Bytes32>>() {
                    },
                    new TypeReference<DynamicArray<Int256>>() {
                    }));
        return executeCallMultipleValueReturnAsync(function);
    }

    /**
     * Removes the authority issuer.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<TransactionReceipt> removeAuthorityIssuer(Address addr) {
        Function function =
            new Function(
                "removeAuthorityIssuer",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Removes the authority issuer.
     *
     * @param addr the addr
     * @param callback the callback
     */
    public void removeAuthorityIssuer(Address addr, TransactionSucCallback callback) {
        Function function =
            new Function(
                "removeAuthorityIssuer",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Adds the authority issuer.
     *
     * @param addr the addr
     * @param attribBytes32 the attrib bytes 32
     * @param attribInt the attrib int
     * @param accValue the acc value
     * @return the future
     */
    public Future<TransactionReceipt> addAuthorityIssuer(
        Address addr,
        StaticArray<Bytes32> attribBytes32,
        StaticArray<Int256> attribInt,
        DynamicBytes accValue) {
        Function function =
            new Function(
                "addAuthorityIssuer",
                Arrays.<Type>asList(addr, attribBytes32, attribInt, accValue),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Adds the authority issuer.
     *
     * @param addr the addr
     * @param attribBytes32 the attrib bytes 32
     * @param attribInt the attrib int
     * @param accValue the acc value
     * @param callback the callback
     */
    public void addAuthorityIssuer(
        Address addr,
        StaticArray<Bytes32> attribBytes32,
        StaticArray<Int256> attribInt,
        DynamicBytes accValue,
        TransactionSucCallback callback) {
        Function function =
            new Function(
                "addAuthorityIssuer",
                Arrays.<Type>asList(addr, attribBytes32, attribInt, accValue),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Gets the all authority issuer address.
     *
     * @return the all authority issuer address
     */
    public Future<DynamicArray<Address>> getAllAuthorityIssuerAddress() {
        Function function =
            new Function(
                "getAllAuthorityIssuerAddress",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Checks if is authority issuer.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<Bool> isAuthorityIssuer(Address addr) {
        Function function =
            new Function(
                "isAuthorityIssuer",
                Arrays.<Type>asList(addr),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * The Class AuthorityIssuerRetLogEventResponse.
     */
    public static class AuthorityIssuerRetLogEventResponse {

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
