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
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
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
public final class CptData extends Contract {

    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptBytes32Array\",\"outputs\":[{\"name\":\"bytes32Array\",\"type\":\"bytes32[8]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptIntArray\",\"outputs\":[{\"name\":\"intArray\",\"type\":\"int256[8]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCpt\",\"outputs\":[{\"name\":\"publisher\",\"type\":\"address\"},{\"name\":\"intArray\",\"type\":\"int256[8]\"},{\"name\":\"bytes32Array\",\"type\":\"bytes32[8]\"},{\"name\":\"jsonSchemaArray\",\"type\":\"bytes32[128]\"},{\"name\":\"v\",\"type\":\"uint8\"},{\"name\":\"r\",\"type\":\"bytes32\"},{\"name\":\"s\",\"type\":\"bytes32\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"},{\"name\":\"cptPublisher\",\"type\":\"address\"},{\"name\":\"cptIntArray\",\"type\":\"int256[8]\"},{\"name\":\"cptBytes32Array\",\"type\":\"bytes32[8]\"},{\"name\":\"cptJsonSchemaArray\",\"type\":\"bytes32[128]\"},{\"name\":\"cptV\",\"type\":\"uint8\"},{\"name\":\"cptR\",\"type\":\"bytes32\"},{\"name\":\"cptS\",\"type\":\"bytes32\"}],\"name\":\"putCpt\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptJsonSchemaArray\",\"outputs\":[{\"name\":\"jsonSchemaArray\",\"type\":\"bytes32[128]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptPublisher\",\"outputs\":[{\"name\":\"publisher\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"publisher\",\"type\":\"address\"}],\"name\":\"getCptId\",\"outputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"isCptExist\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptSignature\",\"outputs\":[{\"name\":\"v\",\"type\":\"uint8\"},{\"name\":\"r\",\"type\":\"bytes32\"},{\"name\":\"s\",\"type\":\"bytes32\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[{\"name\":\"authorityIssuerDataAddress\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\"}]";
    private static String BINARY =
        "60606040526064600055621e8480600155341561001857fe5b6040516020806116b5833981016040528080519060200190919050505b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b505b61162d806100886000396000f30060606040523615610096576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806227baa41461009857806323b746f11461010257806325e556241461016c57806354ed7f4a146102ae578063628e526f146103b25780636da223b71461041c578063d19b82861461047c578063d4eb8a42146104c6578063e5741ff3146104fe575bfe5b34156100a057fe5b6100b66004808035906020019091905050610556565b60405180826008602002808383600083146100f0575b8051825260208311156100f0576020820191506020810190506020830392506100cc565b50505090500191505060405180910390f35b341561010a57fe5b6101206004808035906020019091905050610718565b604051808260086020028083836000831461015a575b80518252602083111561015a57602082019150602081019050602083039250610136565b50505090500191505060405180910390f35b341561017457fe5b61018a60048080359060200190919050506108da565b604051808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001876008602002808383600083146101f6575b8051825260208311156101f6576020820191506020810190506020830392506101d2565b50505090500186600860200280838360008314610232575b8051825260208311156102325760208201915060208101905060208303925061020e565b5050509050018560806020028083836000831461026e575b80518252602083111561026e5760208201915060208101905060208303925061024a565b5050509050018460ff1660ff1681526020018360001916600019168152602001826000191660001916815260200197505050505050505060405180910390f35b34156102b657fe5b610398600480803590602001909190803573ffffffffffffffffffffffffffffffffffffffff1690602001909190806101000190600880602002604051908101604052809291908260086020028082843782019150505050509190806101000190600880602002604051908101604052809291908260086020028082843782019150505050509190806110000190608080602002604051908101604052809291908260806020028082843782019150505050509190803560ff169060200190919080356000191690602001909190803560001916906020019091905050610af0565b604051808215151515815260200191505060405180910390f35b34156103ba57fe5b6103d06004808035906020019091905050610c64565b604051808260806020028083836000831461040a575b80518252602083111561040a576020820191506020810190506020830392506103e6565b50505090500191505060405180910390f35b341561042457fe5b61043a6004808035906020019091905050610e26565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561048457fe5b6104b0600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610fe2565b6040518082815260200191505060405180910390f35b34156104ce57fe5b6104e46004808035906020019091905050611105565b604051808215151515815260200191505060405180910390f35b341561050657fe5b61051c6004808035906020019091905050611150565b604051808460ff1660ff16815260200183600019166000191681526020018260001916600019168152602001935050505060405180910390f35b61055e61132c565b610566611359565b6003600084815260200190815260200160002060a060405190810160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200160018201600880602002604051908101604052809291908260088015610617576020028201915b815481526020019060010190808311610603575b5050505050815260200160098201600880602002604051908101604052809291908260088015610660576020028201915b81546000191681526020019060010190808311610648575b50505050508152602001601182016080806020026040519081016040528092919082608080156106a9576020028201915b81546000191681526020019060010190808311610691575b5050505050815260200160918201606060405190810160405290816000820160009054906101000a900460ff1660ff1660ff1681526020016001820154600019166000191681526020016002820154600019166000191681525050815250509050806040015191505b50919050565b6107206113b8565b610728611359565b6003600084815260200190815260200160002060a060405190810160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600182016008806020026040519081016040528092919082600880156107d9576020028201915b8154815260200190600101908083116107c5575b5050505050815260200160098201600880602002604051908101604052809291908260088015610822576020028201915b8154600019168152602001906001019080831161080a575b505050505081526020016011820160808060200260405190810160405280929190826080801561086b576020028201915b81546000191681526020019060010190808311610853575b5050505050815260200160918201606060405190810160405290816000820160009054906101000a900460ff1660ff1660ff1681526020016001820154600019166000191681526020016002820154600019166000191681525050815250509050806020015191505b50919050565b60006108e46113b8565b6108ec61132c565b6108f46113e1565b600060006000610902611359565b600360008a815260200190815260200160002060a060405190810160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600182016008806020026040519081016040528092919082600880156109b3576020028201915b81548152602001906001019080831161099f575b50505050508152602001600982016008806020026040519081016040528092919082600880156109fc576020028201915b815460001916815260200190600101908083116109e4575b5050505050815260200160118201608080602002604051908101604052809291908260808015610a45576020028201915b81546000191681526020019060010190808311610a2d575b5050505050815260200160918201606060405190810160405290816000820160009054906101000a900460ff1660ff1660ff1681526020016001820154600019166000191681526020016002820154600019166000191681525050815250509050806000015197508060200151965080604001519550806060015194508060800151600001519350806080015160200151925080608001516040015191505b50919395979092949650565b6000610afa61140e565b6060604051908101604052808660ff168152602001856000191681526020018460001916815250905060a0604051908101604052808a73ffffffffffffffffffffffffffffffffffffffff16815260200189815260200188815260200187815260200182815250600360008c815260200190815260200160002060008201518160000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550602082015181600101906008610bd292919061143b565b50604082015181600901906008610bea92919061147b565b50606082015181601101906080610c029291906114c1565b5060808201518160910160008201518160000160006101000a81548160ff021916908360ff16021790555060208201518160010190600019169055604082015181600201906000191690555050905050600191505b5098975050505050505050565b610c6c6113e1565b610c74611359565b6003600084815260200190815260200160002060a060405190810160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200160018201600880602002604051908101604052809291908260088015610d25576020028201915b815481526020019060010190808311610d11575b5050505050815260200160098201600880602002604051908101604052809291908260088015610d6e576020028201915b81546000191681526020019060010190808311610d56575b5050505050815260200160118201608080602002604051908101604052809291908260808015610db7576020028201915b81546000191681526020019060010190808311610d9f575b5050505050815260200160918201606060405190810160405290816000820160009054906101000a900460ff1660ff1660ff1681526020016001820154600019166000191681526020016002820154600019166000191681525050815250509050806060015191505b50919050565b6000610e30611359565b6003600084815260200190815260200160002060a060405190810160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200160018201600880602002604051908101604052809291908260088015610ee1576020028201915b815481526020019060010190808311610ecd575b5050505050815260200160098201600880602002604051908101604052809291908260088015610f2a576020028201915b81546000191681526020019060010190808311610f12575b5050505050815260200160118201608080602002604051908101604052809291908260808015610f73576020028201915b81546000191681526020019060010190808311610f5b575b5050505050815260200160918201606060405190810160405290816000820160009054906101000a900460ff1660ff1660ff1681526020016001820154600019166000191681526020016002820154600019166000191681525050815250509050806000015191505b50919050565b6000600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166392ff8129836000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15156110a657fe5b6102c65a03f115156110b457fe5b50505060405180519050156110eb5760006000815480929190600101919050559050621e847f8111156110e657600090505b6110ff565b600160008154809291906001019190505590505b5b919050565b600061110f6113b8565b61111883610718565b9050600081600060088110151561112b57fe5b6020020151141515611140576001915061114a565b6000915061114a565b5b50919050565b60006000600061115e611359565b6003600086815260200190815260200160002060a060405190810160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020016001820160088060200260405190810160405280929190826008801561120f576020028201915b8154815260200190600101908083116111fb575b5050505050815260200160098201600880602002604051908101604052809291908260088015611258576020028201915b81546000191681526020019060010190808311611240575b50505050508152602001601182016080806020026040519081016040528092919082608080156112a1576020028201915b81546000191681526020019060010190808311611289575b5050505050815260200160918201606060405190810160405290816000820160009054906101000a900460ff1660ff1660ff16815260200160018201546000191660001916815260200160028201546000191660001916815250508152505090508060800151600001519350806080015160200151925080608001516040015191505b509193909250565b610100604051908101604052806008905b60006000191681526020019060019003908161133d5790505090565b61128060405190810160405280600073ffffffffffffffffffffffffffffffffffffffff16815260200161138b611507565b8152602001611398611530565b81526020016113a561155d565b81526020016113b261158a565b81525090565b610100604051908101604052806008905b60008152602001906001900390816113c95790505090565b611000604051908101604052806080905b6000600019168152602001906001900390816113f25790505090565b606060405190810160405280600060ff168152602001600060001916815260200160006000191681525090565b826008810192821561146a579160200282015b8281111561146957825182559160200191906001019061144e565b5b50905061147791906115b7565b5090565b82600881019282156114b0579160200282015b828111156114af57825182906000191690559160200191906001019061148e565b5b5090506114bd91906115dc565b5090565b82608081019282156114f6579160200282015b828111156114f55782518290600019169055916020019190600101906114d4565b5b50905061150391906115dc565b5090565b610100604051908101604052806008905b60008152602001906001900390816115185790505090565b610100604051908101604052806008905b6000600019168152602001906001900390816115415790505090565b611000604051908101604052806080905b60006000191681526020019060019003908161156e5790505090565b606060405190810160405280600060ff168152602001600060001916815260200160006000191681525090565b6115d991905b808211156115d55760008160009055506001016115bd565b5090565b90565b6115fe91905b808211156115fa5760008160009055506001016115e2565b5090565b905600a165627a7a7230582017c9baa56cd21ffbeed32656015bc9ee695ae12bc6f654e6817353d6041ddec20029";

    private CptData(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private CptData(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private CptData(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private CptData(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static Future<CptData> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address authorityIssuerDataAddress) {
        String encodedConstructor =
            FunctionEncoder.encodeConstructor(Arrays.<Type>asList(authorityIssuerDataAddress));
        return deployAsync(
            CptData.class,
            web3j,
            credentials,
            gasPrice,
            gasLimit,
            BINARY,
            encodedConstructor,
            initialWeiValue);
    }

    public static Future<CptData> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address authorityIssuerDataAddress) {
        String encodedConstructor =
            FunctionEncoder.encodeConstructor(Arrays.<Type>asList(authorityIssuerDataAddress));
        return deployAsync(
            CptData.class,
            web3j,
            transactionManager,
            gasPrice,
            gasLimit,
            BINARY,
            encodedConstructor,
            initialWeiValue);
    }

    public static CptData load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptData(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    public static CptData load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptData(contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static CptData loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptData(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    public static CptData loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptData(contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    public Future<StaticArray<Bytes32>> getCptBytes32Array(Uint256 cptId) {
        Function function =
            new Function(
                "getCptBytes32Array",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray<Bytes32>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<StaticArray<Int256>> getCptIntArray(Uint256 cptId) {
        Function function =
            new Function(
                "getCptIntArray",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray<Int256>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<List<Type>> getCpt(Uint256 cptId) {
        Function function =
            new Function(
                "getCpt",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Address>() {
                    },
                    new TypeReference<StaticArray<Int256>>() {
                    },
                    new TypeReference<StaticArray<Bytes32>>() {
                    },
                    new TypeReference<StaticArray<Bytes32>>() {
                    },
                    new TypeReference<Uint8>() {
                    },
                    new TypeReference<Bytes32>() {
                    },
                    new TypeReference<Bytes32>() {
                    }));
        return executeCallMultipleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> putCpt(
        Uint256 cptId,
        Address cptPublisher,
        StaticArray<Int256> cptIntArray,
        StaticArray<Bytes32> cptBytes32Array,
        StaticArray<Bytes32> cptJsonSchemaArray,
        Uint8 cptV,
        Bytes32 cptR,
        Bytes32 cptS) {
        Function function =
            new Function(
                "putCpt",
                Arrays.<Type>asList(
                    cptId,
                    cptPublisher,
                    cptIntArray,
                    cptBytes32Array,
                    cptJsonSchemaArray,
                    cptV,
                    cptR,
                    cptS),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void putCpt(
        Uint256 cptId,
        Address cptPublisher,
        StaticArray<Int256> cptIntArray,
        StaticArray<Bytes32> cptBytes32Array,
        StaticArray<Bytes32> cptJsonSchemaArray,
        Uint8 cptV,
        Bytes32 cptR,
        Bytes32 cptS,
        TransactionSucCallback callback) {
        Function function =
            new Function(
                "putCpt",
                Arrays.<Type>asList(
                    cptId,
                    cptPublisher,
                    cptIntArray,
                    cptBytes32Array,
                    cptJsonSchemaArray,
                    cptV,
                    cptR,
                    cptS),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public Future<StaticArray<Bytes32>> getCptJsonSchemaArray(Uint256 cptId) {
        Function function =
            new Function(
                "getCptJsonSchemaArray",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray<Bytes32>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Address> getCptPublisher(Uint256 cptId) {
        Function function =
            new Function(
                "getCptPublisher",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Uint256> getCptId(Address publisher) {
        Function function =
            new Function(
                "getCptId",
                Arrays.<Type>asList(publisher),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Bool> isCptExist(Uint256 cptId) {
        Function function =
            new Function(
                "isCptExist",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<List<Type>> getCptSignature(Uint256 cptId) {
        Function function =
            new Function(
                "getCptSignature",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint8>() {
                    },
                    new TypeReference<Bytes32>() {
                    },
                    new TypeReference<Bytes32>() {
                    }));
        return executeCallMultipleValueReturnAsync(function);
    }
}
