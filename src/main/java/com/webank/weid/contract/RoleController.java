package com.webank.weid.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;

import org.bcos.channel.client.TransactionSucCallback;
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
 * Auto generated code.<br> <strong>Do not modify!</strong><br> Please use the <a
 * href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link
 * org.bcos.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version none.
 */
public final class RoleController extends Contract {

    /**
     * The Constant ABI.
     */
    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[],\"name\":\"MODIFY_ADMIN\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"},{\"name\":\"role\",\"type\":\"uint256\"}],\"name\":\"checkRole\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"ROLE_COMMITTEE\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"},{\"name\":\"role\",\"type\":\"uint256\"}],\"name\":\"removeRole\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"MODIFY_KEY_CPT\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"},{\"name\":\"role\",\"type\":\"uint256\"}],\"name\":\"addRole\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"},{\"name\":\"operation\",\"type\":\"uint256\"}],\"name\":\"checkPermission\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"MODIFY_AUTHORITY_ISSUER\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"MODIFY_COMMITTEE\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"ROLE_ADMIN\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"ROLE_AUTHORITY_ISSUER\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"type\":\"constructor\"}]";

    private static String BINARY =
        "6060604052341561000c57fe5b5b6001600060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506001600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506001600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b610905806101266000396000f300606060405236156100ad576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680633b20a8c9146100af578063505ef22f146100d5578063793387461461012c57806379db5f67146101525780637b2c0041146101915780637fde1c8a146101b7578063a1a63f65146101f6578063ae23e1751461024d578063c6d8a3f314610273578063d391014b14610299578063f017b58c146102bf575bfe5b34156100b757fe5b6100bf6102e5565b6040518082815260200191505060405180910390f35b34156100dd57fe5b610112600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506102ea565b604051808215151515815260200191505060405180910390f35b341561013457fe5b61013c61040a565b6040518082815260200191505060405180910390f35b341561015a57fe5b61018f600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803590602001909190505061040f565b005b341561019957fe5b6101a161056d565b6040518082815260200191505060405180910390f35b34156101bf57fe5b6101f4600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610572565b005b34156101fe57fe5b610233600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919080359060200190919050506106d0565b604051808215151515815260200191505060405180910390f35b341561025557fe5b61025d6108c5565b6040518082815260200191505060405180910390f35b341561027b57fe5b6102836108ca565b6040518082815260200191505060405180910390f35b34156102a157fe5b6102a96108cf565b6040518082815260200191505060405180910390f35b34156102c757fe5b6102cf6108d4565b6040518082815260200191505060405180910390f35b60ca81565b6000606482141561034957600060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff169050610404565b60658214156103a657600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff169050610404565b606682141561040357600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff169050610404565b5b92915050565b606581565b6064811415610482576104233260c86106d0565b15610481576000600060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b60658114156104f5576104963260c96106d0565b156104f4576000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b6066811415610568576105093260ca6106d0565b15610567576000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b5b5050565b60cb81565b60648114156105e5576105863260c86106d0565b156105e4576001600060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b6065811415610658576105f93260c96106d0565b15610657576001600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b60668114156106cb5761066c3260ca6106d0565b156106ca576001600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b5b5050565b600060c882141561078b57600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff168061077c5750600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b1561078a57600190506108bf565b5b60c98214156107f057600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16156107ef57600190506108bf565b5b60ca82141561085557600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161561085457600190506108bf565b5b60cb8214156108ba57600060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16156108b957600190506108bf565b5b600090505b92915050565b60c881565b60c981565b606681565b6064815600a165627a7a72305820f08a5fae2ed2fe140d4fc0f6b4e3aa5c80196bab99534965c17585d375f848a60029";

    private RoleController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private RoleController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private RoleController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private RoleController(
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
     * @return the future
     */
    public static Future<RoleController> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue) {
        return deployAsync(
            RoleController.class, web3j, credentials, gasPrice, gasLimit, BINARY, "",
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
    public static Future<RoleController> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue) {
        return deployAsync(
            RoleController.class,
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
     * @return the role controller
     */
    public static RoleController load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new RoleController(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    /**
     * Load.
     *
     * @param contractAddress the contract address
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the role controller
     */
    public static RoleController load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new RoleController(
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
     * @return the role controller
     */
    public static RoleController loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new RoleController(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the role controller
     */
    public static RoleController loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new RoleController(contractName, web3j, transactionManager, gasPrice, gasLimit,
            true);
    }

    /**
     * Modify admin.
     *
     * @return the future
     */
    public Future<Uint256> MODIFY_ADMIN() {
        Function function =
            new Function(
                "MODIFY_ADMIN",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Check role.
     *
     * @param addr the addr
     * @param role the role
     * @return the future
     */
    public Future<Bool> checkRole(Address addr, Uint256 role) {
        Function function =
            new Function(
                "checkRole",
                Arrays.<Type>asList(addr, role),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Role committee.
     *
     * @return the future
     */
    public Future<Uint256> ROLE_COMMITTEE() {
        Function function =
            new Function(
                "ROLE_COMMITTEE",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Removes the role.
     *
     * @param addr the addr
     * @param role the role
     * @return the future
     */
    public Future<TransactionReceipt> removeRole(Address addr, Uint256 role) {
        Function function =
            new Function(
                "removeRole",
                Arrays.<Type>asList(addr, role),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Removes the role.
     *
     * @param addr the addr
     * @param role the role
     * @param callback the callback
     */
    public void removeRole(Address addr, Uint256 role, TransactionSucCallback callback) {
        Function function =
            new Function(
                "removeRole",
                Arrays.<Type>asList(addr, role),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Modify key cpt.
     *
     * @return the future
     */
    public Future<Uint256> MODIFY_KEY_CPT() {
        Function function =
            new Function(
                "MODIFY_KEY_CPT",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Adds the role.
     *
     * @param addr the addr
     * @param role the role
     * @return the future
     */
    public Future<TransactionReceipt> addRole(Address addr, Uint256 role) {
        Function function =
            new Function(
                "addRole", Arrays.<Type>asList(addr, role),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Adds the role.
     *
     * @param addr the addr
     * @param role the role
     * @param callback the callback
     */
    public void addRole(Address addr, Uint256 role, TransactionSucCallback callback) {
        Function function =
            new Function(
                "addRole", Arrays.<Type>asList(addr, role),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    /**
     * Check permission.
     *
     * @param addr the addr
     * @param operation the operation
     * @return the future
     */
    public Future<Bool> checkPermission(Address addr, Uint256 operation) {
        Function function =
            new Function(
                "checkPermission",
                Arrays.<Type>asList(addr, operation),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Modify authority issuer.
     *
     * @return the future
     */
    public Future<Uint256> MODIFY_AUTHORITY_ISSUER() {
        Function function =
            new Function(
                "MODIFY_AUTHORITY_ISSUER",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Modify committee.
     *
     * @return the future
     */
    public Future<Uint256> MODIFY_COMMITTEE() {
        Function function =
            new Function(
                "MODIFY_COMMITTEE",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Role admin.
     *
     * @return the future
     */
    public Future<Uint256> ROLE_ADMIN() {
        Function function =
            new Function(
                "ROLE_ADMIN",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Role authority issuer.
     *
     * @return the future
     */
    public Future<Uint256> ROLE_AUTHORITY_ISSUER() {
        Function function =
            new Function(
                "ROLE_AUTHORITY_ISSUER",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }
}
