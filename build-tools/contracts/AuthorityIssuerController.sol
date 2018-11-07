pragma solidity ^0.4.4;
import "./AuthorityIssuerData.sol";
import "./RoleController.sol";

/**
 * @title AuthorityIssuerController
 * Issuer contract manages authority issuer info.
 */

contract AuthorityIssuerController {

    AuthorityIssuerData private authorityIssuerData;
    RoleController private roleController;

    // Event structure to store tx records
    uint constant private OPERATION_ADD = 0;
    uint constant private OPERATION_REMOVE = 1;
    uint constant private RETURN_CODE_SUCCESS = 0;
    uint constant private RETURN_CODE_FAILURE_ALREADY_EXISTS = 500201;
    uint constant private RETURN_CODE_FAILURE_NOT_EXIST = 500202;
    uint constant private RETURN_CODE_FAILURE_NO_PERMISSION = 500203;
    event AuthorityIssuerRetLog(uint operation, uint retCode, address addr);

    // Constructor.
    function AuthorityIssuerController(
        address authorityIssuerDataAddress,
        address roleControllerAddress
    ) public {
        authorityIssuerData = AuthorityIssuerData(authorityIssuerDataAddress);
        roleController = RoleController(roleControllerAddress);
    }

    function addAuthorityIssuer(
        address addr,
        bytes32[16] attribBytes32,
        int[16] attribInt,
        bytes accValue
    )
        public
    {
        if (authorityIssuerData.isAuthorityIssuer(addr)) {
            AuthorityIssuerRetLog(OPERATION_ADD, RETURN_CODE_FAILURE_ALREADY_EXISTS, addr);
            return;
        } else if (!roleController.checkPermission(tx.origin, roleController.MODIFY_AUTHORITY_ISSUER())) {
            AuthorityIssuerRetLog(OPERATION_ADD, RETURN_CODE_FAILURE_NO_PERMISSION, addr);
            return;
        } else {
            authorityIssuerData.addAuthorityIssuerFromAddress(addr, attribBytes32, attribInt, accValue);
            AuthorityIssuerRetLog(OPERATION_ADD, RETURN_CODE_SUCCESS, addr);
        }
    }

    function removeAuthorityIssuer(address addr) public {
        if (!authorityIssuerData.isAuthorityIssuer(addr)) {
            AuthorityIssuerRetLog(OPERATION_REMOVE, RETURN_CODE_FAILURE_NOT_EXIST, addr);
            return;
        } else if (!roleController.checkPermission(tx.origin, roleController.MODIFY_AUTHORITY_ISSUER())) {
            AuthorityIssuerRetLog(OPERATION_REMOVE, RETURN_CODE_FAILURE_NO_PERMISSION, addr);
            return;
        } else {
            authorityIssuerData.deleteAuthorityIssuerFromAddress(addr);
            AuthorityIssuerRetLog(OPERATION_REMOVE, RETURN_CODE_SUCCESS, addr);
        }
    }

    function getAllAuthorityIssuerAddress() public constant returns (address[]) {
        // solidity 0.4.4 restrictions, use per-index access
        uint datasetLength = authorityIssuerData.getDatasetLength();
        address[] memory issuerArray = new address[](datasetLength);
        for (uint index = 0; index < datasetLength; index++) {
            issuerArray[index] = authorityIssuerData.getAuthorityIssuerFromIndex(index);
        }
        return issuerArray;
    }

    function getAuthorityIssuerInfoNonAccValue(address addr)
        public
        constant
        returns (
            bytes32[],
            int[]
        )
    {
        // Due to the current limitations of bcos web3j, return dynamic bytes32 and int array instead.
        bytes32[16] memory allBytes32;
        int[16] memory allInt;
        (allBytes32, allInt) = authorityIssuerData.getAuthorityIssuerInfoNonAccValue(addr);
        bytes32[] memory finalBytes32 = new bytes32[](16);
        int[] memory finalInt = new int[](16);
        for (uint index = 0; index < 16; index++) {
            finalBytes32[index] = allBytes32[index];
            finalInt[index] = allInt[index];
        }
        return (finalBytes32, finalInt);
    }

    function isAuthorityIssuer(address addr) public constant returns (bool) {
        return authorityIssuerData.isAuthorityIssuer(addr);
    }
}