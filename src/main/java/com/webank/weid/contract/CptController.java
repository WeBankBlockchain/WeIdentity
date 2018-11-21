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
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
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
public final class CptController extends Contract {

    public static final String ABI =
        "[{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptDynamicBytes32Array\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32[]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"},{\"name\":\"publisher\",\"type\":\"address\"},{\"name\":\"intArray\",\"type\":\"int256[8]\"},{\"name\":\"bytes32Array\",\"type\":\"bytes32[8]\"},{\"name\":\"jsonSchemaArray\",\"type\":\"bytes32[128]\"},{\"name\":\"v\",\"type\":\"uint8\"},{\"name\":\"r\",\"type\":\"bytes32\"},{\"name\":\"s\",\"type\":\"bytes32\"}],\"name\":\"updateCpt\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"queryCpt\",\"outputs\":[{\"name\":\"publisher\",\"type\":\"address\"},{\"name\":\"intArray\",\"type\":\"int256[]\"},{\"name\":\"bytes32Array\",\"type\":\"bytes32[]\"},{\"name\":\"jsonSchemaArray\",\"type\":\"bytes32[]\"},{\"name\":\"v\",\"type\":\"uint8\"},{\"name\":\"r\",\"type\":\"bytes32\"},{\"name\":\"s\",\"type\":\"bytes32\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptDynamicIntArray\",\"outputs\":[{\"name\":\"\",\"type\":\"int256[]\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"publisher\",\"type\":\"address\"},{\"name\":\"intArray\",\"type\":\"int256[8]\"},{\"name\":\"bytes32Array\",\"type\":\"bytes32[8]\"},{\"name\":\"jsonSchemaArray\",\"type\":\"bytes32[128]\"},{\"name\":\"v\",\"type\":\"uint8\"},{\"name\":\"r\",\"type\":\"bytes32\"},{\"name\":\"s\",\"type\":\"bytes32\"}],\"name\":\"registerCpt\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"cptId\",\"type\":\"uint256\"}],\"name\":\"getCptDynamicJsonSchemaArray\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32[]\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[{\"name\":\"cptDataAddress\",\"type\":\"address\"},{\"name\":\"weIdContractAddress\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"retCode\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"cptId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"cptVersion\",\"type\":\"int256\"}],\"name\":\"RegisterCptRetLog\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"retCode\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"cptId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"cptVersion\",\"type\":\"int256\"}],\"name\":\"UpdateCptRetLog\",\"type\":\"event\"}]";
    private static String BINARY =
        "6060604052341561000c57fe5b604051604080611724833981016040528080519060200190919080519060200190919050505b81600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b50505b61165d806100c76000396000f30060606040523615610076576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063102b508d1461007857806351c10fee146100fb5780635ca35abf146101ff578063672ddcf01461038c578063a8550a521461040f578063b4af70901461050a575bfe5b341561008057fe5b610096600480803590602001909190505061058d565b60405180806020018281038252838181518152602001915080519060200190602002808383600083146100e8575b8051825260208311156100e8576020820191506020810190506020830392506100c4565b5050509050019250505060405180910390f35b341561010357fe5b6101e5600480803590602001909190803573ffffffffffffffffffffffffffffffffffffffff1690602001909190806101000190600880602002604051908101604052809291908260086020028082843782019150505050509190806101000190600880602002604051908101604052809291908260086020028082843782019150505050509190806110000190608080602002604051908101604052809291908260806020028082843782019150505050509190803560ff1690602001909190803560001916906020019091908035600019169060200190919050506106e0565b604051808215151515815260200191505060405180910390f35b341561020757fe5b61021d6004808035906020019091905050610c6e565b604051808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018060200180602001806020018760ff1660ff1681526020018660001916600019168152602001856000191660001916815260200184810384528a8181518152602001915080519060200190602002808383600083146102d1575b8051825260208311156102d1576020820191506020810190506020830392506102ad565b505050905001848103835289818151815260200191508051906020019060200280838360008314610321575b805182526020831115610321576020820191506020810190506020830392506102fd565b505050905001848103825288818151815260200191508051906020019060200280838360008314610371575b8051825260208311156103715760208201915060208101905060208303925061034d565b5050509050019a505050505050505050505060405180910390f35b341561039457fe5b6103aa6004808035906020019091905050610e31565b60405180806020018281038252838181518152602001915080519060200190602002808383600083146103fc575b8051825260208311156103fc576020820191506020810190506020830392506103d8565b5050509050019250505060405180910390f35b341561041757fe5b6104f0600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190806101000190600880602002604051908101604052809291908260086020028082843782019150505050509190806101000190600880602002604051908101604052809291908260086020028082843782019150505050509190806110000190608080602002604051908101604052809291908260806020028082843782019150505050509190803560ff169060200190919080356000191690602001909190803560001916906020019091905050610f7b565b604051808215151515815260200191505060405180910390f35b341561051257fe5b6105286004808035906020019091905050611432565b604051808060200182810382528381815181526020019150805190602001906020028083836000831461057a575b80518252602083111561057a57602082019150602081019050602083039250610556565b5050509050019250505060405180910390f35b610595611586565b61059d61159a565b6105a5611586565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166227baa486600060405161010001526040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082815260200191505061010060405180830381600087803b151561063e57fe5b6102c65a03f1151561064c57fe5b50505060405180610100016040529250600860405180591061066b5750595b908082528060200260200182016040525b509150600090505b60088110156106d457828160088110151561069b57fe5b602002015182828151811015156106ae57fe5b9060200190602002019060001916908160001916815250505b8080600101915050610684565b8193505b505050919050565b60006106ea6115c7565b60006000600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663170abf9c8c6000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15156107b057fe5b6102c65a03f115156107be57fe5b505050604051805190501515610823577f2614d1ec3482cc2505bf211c39bee96c28940521311ec70c9ebf14d3896fd1966207a24f6000600060405180848152602001838152602001828152602001935050505060405180910390a160009350610c5f565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663d4eb8a428d6000604051602001526040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180828152602001915050602060405180830381600087803b15156108b957fe5b6102c65a03f115156108c757fe5b5050506040518051905015610c0957600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b746f18d600060405161010001526040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082815260200191505061010060405180830381600087803b151561096e57fe5b6102c65a03f1151561097c57fe5b50505060405180610100016040529250600183600060088110151561099d57fe5b6020020151019150818a60006008811015156109b557fe5b6020020181815250508260016008811015156109cd57fe5b60200201519050808a60016008811015156109e457fe5b602002018181525050600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166354ed7f4a8d8d8d8d8d8d8d8d6000604051602001526040518963ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808981526020018873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200187600860200280838360008314610ad9575b805182526020831115610ad957602082019150602081019050602083039250610ab5565b50505090500186600860200280838360008314610b15575b805182526020831115610b1557602082019150602081019050602083039250610af1565b50505090500185608060200280838360008314610b51575b805182526020831115610b5157602082019150602081019050602083039250610b2d565b5050509050018460ff1660ff1681526020018360001916600019168152602001826000191660001916815260200198505050505050505050602060405180830381600087803b1515610b9f57fe5b6102c65a03f11515610bad57fe5b50505060405180519050507f2614d1ec3482cc2505bf211c39bee96c28940521311ec70c9ebf14d3896fd19660008d8460405180848152602001838152602001828152602001935050505060405180910390a160019350610c5f565b7f2614d1ec3482cc2505bf211c39bee96c28940521311ec70c9ebf14d3896fd1966207a24d6000600060405180848152602001838152602001828152602001935050505060405180910390a160009350610c5f565b5b50505098975050505050505050565b6000610c786115f0565b610c80611586565b610c88611586565b600060006000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16636da223b7896000604051602001526040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180828152602001915050602060405180830381600087803b1515610d2457fe5b6102c65a03f11515610d3257fe5b505050604051805190509650610d4788610e31565b9550610d528861058d565b9450610d5d88611432565b9350600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e5741ff3896000604051606001526040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180828152602001915050606060405180830381600087803b1515610df557fe5b6102c65a03f11515610e0357fe5b505050604051805190602001805190602001805190508093508194508295505050505b919395979092949650565b610e396115f0565b610e416115c7565b610e496115f0565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b746f186600060405161010001526040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082815260200191505061010060405180830381600087803b1515610ee357fe5b6102c65a03f11515610ef157fe5b505050604051806101000160405292506008604051805910610f105750595b908082528060200260200182016040525b509150600090505b6008811015610f6f578281600881101515610f4057fe5b60200201518282815181101515610f5357fe5b90602001906020020181815250505b8080600101915050610f29565b8193505b505050919050565b600060006000600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663170abf9c8b6000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b151561104357fe5b6102c65a03f1151561105157fe5b5050506040518051905015156110b6577fa17f6f29c43d53fdf8a8d5fc788d118621cdca690e8ee29962c3e2fbe70d5eb36207a24f6000600060405180848152602001838152602001828152602001935050505060405180910390a160009250611425565b600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663d19b82868b6000604051602001526040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b151561117857fe5b6102c65a03f1151561118657fe5b50505060405180519050915060008214156111f0577fa17f6f29c43d53fdf8a8d5fc788d118621cdca690e8ee29962c3e2fbe70d5eb36207a24e6000600060405180848152602001838152602001828152602001935050505060405180910390a160009250611425565b600190508089600060088110151561120457fe5b602002018181525050600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166354ed7f4a838c8c8c8c8c8c8c6000604051602001526040518963ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808981526020018873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001876008602002808383600083146112f9575b8051825260208311156112f9576020820191506020810190506020830392506112d5565b50505090500186600860200280838360008314611335575b80518252602083111561133557602082019150602081019050602083039250611311565b50505090500185608060200280838360008314611371575b8051825260208311156113715760208201915060208101905060208303925061134d565b5050509050018460ff1660ff1681526020018360001916600019168152602001826000191660001916815260200198505050505050505050602060405180830381600087803b15156113bf57fe5b6102c65a03f115156113cd57fe5b50505060405180519050507fa17f6f29c43d53fdf8a8d5fc788d118621cdca690e8ee29962c3e2fbe70d5eb36000838360405180848152602001838152602001828152602001935050505060405180910390a1600192505b5050979650505050505050565b61143a611586565b611442611604565b61144a611586565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663628e526f86600060405161100001526040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082815260200191505061100060405180830381600087803b15156114e457fe5b6102c65a03f115156114f257fe5b5050506040518061100001604052925060806040518059106115115750595b908082528060200260200182016040525b509150600090505b608081101561157a57828160808110151561154157fe5b6020020151828281518110151561155457fe5b9060200190602002019060001916908160001916815250505b808060010191505061152a565b8193505b505050919050565b602060405190810160405280600081525090565b610100604051908101604052806008905b6000600019168152602001906001900390816115ab5790505090565b610100604051908101604052806008905b60008152602001906001900390816115d85790505090565b602060405190810160405280600081525090565b611000604051908101604052806080905b60006000191681526020019060019003908161161557905050905600a165627a7a72305820dd501eaa174400033398fdf036941c4cae857a968d83582de1fa61c2d611ca0f0029";

    private CptController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private CptController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private CptController(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private CptController(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static List<RegisterCptRetLogEventResponse> getRegisterCptRetLogEvents(
        TransactionReceipt transactionReceipt) {
        final Event event =
            new Event(
                "RegisterCptRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Int256>() {
                    }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<RegisterCptRetLogEventResponse> responses =
            new ArrayList<RegisterCptRetLogEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            RegisterCptRetLogEventResponse typedResponse = new RegisterCptRetLogEventResponse();
            typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.cptId = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.cptVersion = (Int256) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public static List<UpdateCptRetLogEventResponse> getUpdateCptRetLogEvents(
        TransactionReceipt transactionReceipt) {
        final Event event =
            new Event(
                "UpdateCptRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Int256>() {
                    }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<UpdateCptRetLogEventResponse> responses =
            new ArrayList<UpdateCptRetLogEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            UpdateCptRetLogEventResponse typedResponse = new UpdateCptRetLogEventResponse();
            typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.cptId = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.cptVersion = (Int256) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public static Future<CptController> deploy(
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address cptDataAddress,
        Address weIdContractAddress) {
        String encodedConstructor =
            FunctionEncoder
                .encodeConstructor(Arrays.<Type>asList(cptDataAddress, weIdContractAddress));
        return deployAsync(
            CptController.class,
            web3j,
            credentials,
            gasPrice,
            gasLimit,
            BINARY,
            encodedConstructor,
            initialWeiValue);
    }

    public static Future<CptController> deploy(
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit,
        BigInteger initialWeiValue,
        Address cptDataAddress,
        Address weIdContractAddress) {
        String encodedConstructor =
            FunctionEncoder
                .encodeConstructor(Arrays.<Type>asList(cptDataAddress, weIdContractAddress));
        return deployAsync(
            CptController.class,
            web3j,
            transactionManager,
            gasPrice,
            gasLimit,
            BINARY,
            encodedConstructor,
            initialWeiValue);
    }

    public static CptController load(
        String contractAddress,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptController(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    public static CptController load(
        String contractAddress,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptController(contractAddress, web3j, transactionManager, gasPrice, gasLimit,
            false);
    }

    public static CptController loadByName(
        String contractName,
        Web3j web3j,
        Credentials credentials,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptController(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    public static CptController loadByName(
        String contractName,
        Web3j web3j,
        TransactionManager transactionManager,
        BigInteger gasPrice,
        BigInteger gasLimit) {
        return new CptController(contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    public Observable<RegisterCptRetLogEventResponse> registerCptRetLogEventObservable(
        DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event =
            new Event(
                "RegisterCptRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
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
                new Func1<Log, RegisterCptRetLogEventResponse>() {
                    @Override
                    public RegisterCptRetLogEventResponse call(Log log) {
                        EventValues eventValues = extractEventParameters(event, log);
                        RegisterCptRetLogEventResponse typedResponse = new RegisterCptRetLogEventResponse();
                        typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(0);
                        typedResponse.cptId = (Uint256) eventValues.getNonIndexedValues().get(1);
                        typedResponse.cptVersion = (Int256) eventValues.getNonIndexedValues()
                            .get(2);
                        return typedResponse;
                    }
                });
    }

    public Observable<UpdateCptRetLogEventResponse> updateCptRetLogEventObservable(
        DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event =
            new Event(
                "UpdateCptRetLog",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>() {
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
                new Func1<Log, UpdateCptRetLogEventResponse>() {
                    @Override
                    public UpdateCptRetLogEventResponse call(Log log) {
                        EventValues eventValues = extractEventParameters(event, log);
                        UpdateCptRetLogEventResponse typedResponse = new UpdateCptRetLogEventResponse();
                        typedResponse.retCode = (Uint256) eventValues.getNonIndexedValues().get(0);
                        typedResponse.cptId = (Uint256) eventValues.getNonIndexedValues().get(1);
                        typedResponse.cptVersion = (Int256) eventValues.getNonIndexedValues()
                            .get(2);
                        return typedResponse;
                    }
                });
    }

    public Future<DynamicArray<Bytes32>> getCptDynamicBytes32Array(Uint256 cptId) {
        Function function =
            new Function(
                "getCptDynamicBytes32Array",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> updateCpt(
        Uint256 cptId,
        Address publisher,
        StaticArray<Int256> intArray,
        StaticArray<Bytes32> bytes32Array,
        StaticArray<Bytes32> jsonSchemaArray,
        Uint8 v,
        Bytes32 r,
        Bytes32 s) {
        Function function =
            new Function(
                "updateCpt",
                Arrays.<Type>asList(cptId, publisher, intArray, bytes32Array, jsonSchemaArray, v, r,
                    s),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void updateCpt(
        Uint256 cptId,
        Address publisher,
        StaticArray<Int256> intArray,
        StaticArray<Bytes32> bytes32Array,
        StaticArray<Bytes32> jsonSchemaArray,
        Uint8 v,
        Bytes32 r,
        Bytes32 s,
        TransactionSucCallback callback) {
        Function function =
            new Function(
                "updateCpt",
                Arrays.<Type>asList(cptId, publisher, intArray, bytes32Array, jsonSchemaArray, v, r,
                    s),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public Future<List<Type>> queryCpt(Uint256 cptId) {
        Function function =
            new Function(
                "queryCpt",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Address>() {
                    },
                    new TypeReference<DynamicArray<Int256>>() {
                    },
                    new TypeReference<DynamicArray<Bytes32>>() {
                    },
                    new TypeReference<DynamicArray<Bytes32>>() {
                    },
                    new TypeReference<Uint8>() {
                    },
                    new TypeReference<Bytes32>() {
                    },
                    new TypeReference<Bytes32>() {
                    }));
        return executeCallMultipleValueReturnAsync(function);
    }

    public Future<DynamicArray<Int256>> getCptDynamicIntArray(Uint256 cptId) {
        Function function =
            new Function(
                "getCptDynamicIntArray",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Int256>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> registerCpt(
        Address publisher,
        StaticArray<Int256> intArray,
        StaticArray<Bytes32> bytes32Array,
        StaticArray<Bytes32> jsonSchemaArray,
        Uint8 v,
        Bytes32 r,
        Bytes32 s) {
        Function function =
            new Function(
                "registerCpt",
                Arrays.<Type>asList(publisher, intArray, bytes32Array, jsonSchemaArray, v, r, s),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void registerCpt(
        Address publisher,
        StaticArray<Int256> intArray,
        StaticArray<Bytes32> bytes32Array,
        StaticArray<Bytes32> jsonSchemaArray,
        Uint8 v,
        Bytes32 r,
        Bytes32 s,
        TransactionSucCallback callback) {
        Function function =
            new Function(
                "registerCpt",
                Arrays.<Type>asList(publisher, intArray, bytes32Array, jsonSchemaArray, v, r, s),
                Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public Future<DynamicArray<Bytes32>> getCptDynamicJsonSchemaArray(Uint256 cptId) {
        Function function =
            new Function(
                "getCptDynamicJsonSchemaArray",
                Arrays.<Type>asList(cptId),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }

    public static class RegisterCptRetLogEventResponse {

        public Uint256 retCode;

        public Uint256 cptId;

        public Int256 cptVersion;
    }

    public static class UpdateCptRetLogEventResponse {

        public Uint256 retCode;

        public Uint256 cptId;

        public Int256 cptVersion;
    }
}
