pragma solidity ^0.4.4;
import "./RoleController.sol";

/**
 * @title AuthorityIssuerData
 * Authority Issuer data contract.
 */

contract AuthorityIssuerData {
    struct AuthorityIssuer {
        // [0]: name
        bytes32[16] attribBytes32;
        // [0]: create date
        int[16] attribInt;
        bytes accValue;
    }

    // Mapping key: address (WeIdentity DID address), value: Authority Issuer instance.
    mapping (address => AuthorityIssuer) private authorityIssuerMap;
    // Array used to index and count the address (WeIdentity DID address). Depends on mapping, no standalone creator.
    address[] private authorityIssuerArray;

    RoleController private roleController;

    // Constructor.
    function AuthorityIssuerData(address addr) public {
        roleController = RoleController(addr);
    }

    function isAuthorityIssuer(address addr) public constant returns (bool) {
        // Use LOCAL ARRAY INDEX here, not the RoleController data.
        // The latter one might lose track in the fresh-deploy or upgrade case.
        for (uint index = 0; index < authorityIssuerArray.length; index++) {
            if (authorityIssuerArray[index] == addr) {
                return true;
            }
        }
        return false;
    }

    function addAuthorityIssuerFromAddress(
        address addr,
        bytes32[16] attribBytes32,
        int[16] attribInt,
        bytes accValue
    )
        public
    {
        if (isAuthorityIssuer(addr)) {
            return;
        }
        if (!roleController.checkPermission(tx.origin, roleController.MODIFY_AUTHORITY_ISSUER())) {
            return;
        }
        roleController.addRole(addr, roleController.ROLE_AUTHORITY_ISSUER());
        AuthorityIssuer memory authorityIssuer =
            AuthorityIssuer(attribBytes32, attribInt, accValue);
        authorityIssuerMap[addr] = authorityIssuer;
        authorityIssuerArray.push(addr);
    }

    function deleteAuthorityIssuerFromAddress(address addr) public {
        if (!isAuthorityIssuer(addr)) {
            return;
        }
        if (!roleController.checkPermission(tx.origin, roleController.MODIFY_AUTHORITY_ISSUER())) {
            return;
        }
        roleController.removeRole(addr, roleController.ROLE_AUTHORITY_ISSUER());
        delete authorityIssuerMap[addr];
        uint datasetLength = authorityIssuerArray.length;
        for (uint index = 0; index < datasetLength; index++) {
            if (authorityIssuerArray[index] == addr) { break; }
        } 
        if (index != datasetLength-1) {
            authorityIssuerArray[index] = authorityIssuerArray[datasetLength-1];
        }
        delete authorityIssuerArray[datasetLength-1];
        authorityIssuerArray.length--;
    }

    function getDatasetLength() public constant returns (uint) {
        return authorityIssuerArray.length;
    }

    function getAuthorityIssuerFromIndex(uint index) public constant returns (address) {
        return authorityIssuerArray[index];
    }

    function getAuthorityIssuerInfoNonAccValue(address addr)
        public
        constant
        returns (
            bytes32[16],
            int[16]
        )
    {
        bytes32[16] memory allBytes32;
        int[16] memory allInt;
        for (uint index = 0; index < 16; index++) {
            allBytes32[index] = authorityIssuerMap[addr].attribBytes32[index];
            allInt[index] = authorityIssuerMap[addr].attribInt[index];
        }
        return (allBytes32, allInt);
    }

    function getAuthorityIssuerInfoAccValue(address addr) public constant returns (bytes) {
        return authorityIssuerMap[addr].accValue;
    }
}