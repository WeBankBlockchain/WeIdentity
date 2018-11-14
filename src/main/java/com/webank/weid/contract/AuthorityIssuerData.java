package com.webank.weid.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.bcos.channel.client.TransactionSucCallback;
import org.bcos.web3j.abi.FunctionEncoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
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
public final class AuthorityIssuerData extends Contract {

    /**
     * The Constant ABI.
     */
    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"getAuthorityIssuerInfoNonAccValue\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32[16]\"},{\"name\":\"\",\"type\":\"int256[16]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getAuthorityIssuerFromIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"},{\"name\":\"attribBytes32\",\"type\":\"bytes32[16]\"},{\"name\":\"attribInt\",\"type\":\"int256[16]\"},{\"name\":\"accValue\",\"type\":\"bytes\"}],\"name\":\"addAuthorityIssuerFromAddress\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"isAuthorityIssuer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"getAuthorityIssuerInfoAccValue\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"deleteAuthorityIssuerFromAddress\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getDatasetLength\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\"}]";

    private static String BINARY =
        "6060604052341561000c57fe5b604051602080611446833981016040528080519060200190919050505b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b505b6113ca8061007c6000396000f30060606040523615610081576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806313b088ed146100835780638667f0e2146101405780638e1a6209146101a057806392ff812914610273578063b4000950146102c1578063c99ccd771461037e578063e083a3ad146103b4575bfe5b341561008b57fe5b6100b7600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506103da565b60405180836010602002808383600083146100f1575b8051825260208311156100f1576020820191506020810190506020830392506100cd565b5050509050018260106020028083836000831461012d575b80518252602083111561012d57602082019150602081019050602083039250610109565b5050509050019250505060405180910390f35b341561014857fe5b61015e6004808035906020019091905050610509565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34156101a857fe5b610271600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190806102000190601080602002604051908101604052809291908260106020028082843782019150505050509190806102000190601080602002604051908101604052809291908260106020028082843782019150505050509190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190505061054f565b005b341561027b57fe5b6102a7600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061098d565b604051808215151515815260200191505060405180910390f35b34156102c957fe5b6102f5600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610a34565b6040518080602001828103825283818151815260200191508051906020019080838360008314610344575b80518252602083111561034457602082019150602081019050602083039250610320565b505050905090810190601f1680156103705780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561038657fe5b6103b2600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610b1f565b005b34156103bc57fe5b6103c461104c565b6040518082815260200191505060405180910390f35b6103e261105a565b6103ea611087565b6103f261105a565b6103fa611087565b6000600090505b60108110156104fa57600060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000018160108110151561045957fe5b0160005b5054838260108110151561046d57fe5b60200201906000191690816000191681525050600060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020601001816010811015156104cf57fe5b0160005b505482826010811015156104e357fe5b6020020181815250505b8080600101915050610401565b8282945094505b505050915091565b600060018281548110151561051a57fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690505b919050565b6105576110b0565b6105608561098d565b1561056a57610986565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ae23e1756000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b151561063757fe5b6102c65a03f1151561064557fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b15156106d957fe5b6102c65a03f115156106e757fe5b5050506040518051905015156106fc57610986565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16637fde1c8a86600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f017b58c6000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b15156107c957fe5b6102c65a03f115156107d757fe5b505050604051805190506040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050600060405180830381600087803b151561086257fe5b6102c65a03f1151561087057fe5b50505060606040519081016040528085815260200184815260200183815250905080600060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000820151816000019060106108e89291906110e5565b5060208201518160100190601061090092919061112b565b50604082015181602001908051906020019061091d92919061116b565b509050506001805480600101828161093591906111eb565b916000526020600020900160005b87909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505b5050505050565b60006000600090505b600180549050811015610a29578273ffffffffffffffffffffffffffffffffffffffff166001828154811015156109c957fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415610a1b5760019150610a2e565b5b8080600101915050610996565b600091505b50919050565b610a3c611217565b600060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206020018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610b125780601f10610ae757610100808354040283529160200191610b12565b820191906000526020600020905b815481529060010190602001808311610af557829003601f168201915b505050505090505b919050565b60006000610b2c8361098d565b1515610b3757611047565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a1a63f6532600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ae23e1756000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b1515610c0457fe5b6102c65a03f11515610c1257fe5b505050604051805190506000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1515610ca657fe5b6102c65a03f11515610cb457fe5b505050604051805190501515610cc957611047565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166379db5f6784600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f017b58c6000604051602001526040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401809050602060405180830381600087803b1515610d9657fe5b6102c65a03f11515610da457fe5b505050604051805190506040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050600060405180830381600087803b1515610e2f57fe5b6102c65a03f11515610e3d57fe5b505050600060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000600082016000610e91919061122b565b601082016000610ea1919061123e565b602082016000610eb19190611251565b50506001805490509150600090505b81811015610f4a578273ffffffffffffffffffffffffffffffffffffffff16600182815481101515610eee57fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415610f3c57610f4a565b5b8080600101915050610ec0565b6001820381141515610fef57600160018303815481101515610f6857fe5b906000526020600020900160005b9054906101000a900473ffffffffffffffffffffffffffffffffffffffff16600182815481101515610fa457fe5b906000526020600020900160005b6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b60016001830381548110151561100157fe5b906000526020600020900160005b6101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905560018054809190600190036110459190611299565b505b505050565b600060018054905090505b90565b610200604051908101604052806010905b60006000191681526020019060019003908161106b5790505090565b610200604051908101604052806010905b60008152602001906001900390816110985790505090565b610420604051908101604052806110c56112c5565b81526020016110d26112f2565b81526020016110df61131b565b81525090565b826010810192821561111a579160200282015b828111156111195782518290600019169055916020019190600101906110f8565b5b509050611127919061132f565b5090565b826010810192821561115a579160200282015b8281111561115957825182559160200191906001019061113e565b5b5090506111679190611354565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106111ac57805160ff19168380011785556111da565b828001600101855582156111da579182015b828111156111d95782518255916020019190600101906111be565b5b5090506111e79190611379565b5090565b815481835581811511611212578183600052602060002091820191016112119190611379565b5b505050565b602060405190810160405280600081525090565b50806010019061123b919061132f565b50565b50806010019061124e9190611354565b50565b50805460018160011615610100020316600290046000825580601f106112775750611296565b601f0160209004906000526020600020908101906112959190611379565b5b50565b8154818355818115116112c0578183600052602060002091820191016112bf9190611379565b5b505050565b610200604051908101604052806010905b6000600019168152602001906001900390816112d65790505090565b610200604051908101604052806010905b60008152602001906001900390816113035790505090565b602060405190810160405280600081525090565b61135191905b8082111561134d576000816000905550600101611335565b5090565b90565b61137691905b8082111561137257600081600090555060010161135a565b5090565b90565b61139b91905b8082111561139757600081600090555060010161137f565b5090565b905600a165627a7a72305820ee9c55852cfd4cfce443b2d8924617df969dffe8c7af316269554d0e4f41b77e0029";

    private AuthorityIssuerData(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private AuthorityIssuerData(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private AuthorityIssuerData(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private AuthorityIssuerData(
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
    public static Future<AuthorityIssuerData> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address addr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(addr));
        return deployAsync(
            AuthorityIssuerData.class,
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
    public static Future<AuthorityIssuerData> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address addr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(addr));
        return deployAsync(
            AuthorityIssuerData.class,
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
     * @return the authority issuer data
     */
    public static AuthorityIssuerData load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerData(contractAddress, web3j, credentials, gasPrice, gasLimit,
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
     * @return the authority issuer data
     */
    public static AuthorityIssuerData load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerData(
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
     * @return the authority issuer data
     */
    public static AuthorityIssuerData loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerData(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    /**
     * Load by name.
     *
     * @param contractName the contract name
     * @param web3j the web3j
     * @param transactionManager the transaction manager
     * @param gasPrice the gas price
     * @param gasLimit the gas limit
     * @return the authority issuer data
     */
    public static AuthorityIssuerData loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new AuthorityIssuerData(
            contractName, web3j, transactionManager, gasPrice, gasLimit, true);
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
                    new TypeReference<StaticArray<Bytes32>>() {
                    },
                    new TypeReference<StaticArray<Int256>>() {
                    }));
        return executeCallMultipleValueReturnAsync(function);
    }

    /**
     * Gets the authority issuer from index.
     *
     * @param index the index
     * @return the authority issuer from index
     */
    public Future<Address> getAuthorityIssuerFromIndex(Uint256 index) {
        Function function =
            new Function(
                "getAuthorityIssuerFromIndex",
                Arrays.<Type>asList(index),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Adds the authority issuer from address.
     *
     * @param addr the addr
     * @param attribBytes32 the attrib bytes 32
     * @param attribInt the attrib int
     * @param accValue the acc value
     * @return the future
     */
    public Future<TransactionReceipt> addAuthorityIssuerFromAddress(
        Address addr,
        StaticArray<Bytes32> attribBytes32,
        StaticArray<Int256> attribInt,
        DynamicBytes accValue) {
        Function function =
            new Function(
                "addAuthorityIssuerFromAddress",
                Arrays.<Type>asList(addr, attribBytes32, attribInt, accValue),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Adds the authority issuer from address.
     *
     * @param addr the addr
     * @param attribBytes32 the attrib bytes 32
     * @param attribInt the attrib int
     * @param accValue the acc value
     * @param callback the callback
     */
    public void addAuthorityIssuerFromAddress(
        Address addr,
        StaticArray<Bytes32> attribBytes32,
        StaticArray<Int256> attribInt,
        DynamicBytes accValue,
        TransactionSucCallback callback) {
        Function function =
            new Function(
                "addAuthorityIssuerFromAddress",
                Arrays.<Type>asList(addr, attribBytes32, attribInt, accValue),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
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
     * Gets the authority issuer info acc value.
     *
     * @param addr the addr
     * @return the authority issuer info acc value
     */
    public Future<DynamicBytes> getAuthorityIssuerInfoAccValue(Address addr) {
        Function function =
            new Function(
                "getAuthorityIssuerInfoAccValue",
                Arrays.<Type>asList(addr),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    /**
     * Delete authority issuer from address.
     *
     * @param addr the addr
     * @return the future
     */
    public Future<TransactionReceipt> deleteAuthorityIssuerFromAddress(Address addr) {
        Function function =
            new Function(
                "deleteAuthorityIssuerFromAddress",
                Arrays.<Type>asList(addr),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    /**
     * Delete authority issuer from address.
     *
     * @param addr the addr
     * @param callback the callback
     */
    public void deleteAuthorityIssuerFromAddress(Address addr, TransactionSucCallback callback) {
        Function function =
            new Function(
                "deleteAuthorityIssuerFromAddress",
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
}
