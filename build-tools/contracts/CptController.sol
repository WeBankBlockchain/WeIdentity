pragma solidity ^0.4.4;

import "./CptData.sol";
import "./WeIdContract.sol";

contract CptController {

    uint constant private CPT_NOT_EXIST = 500301;
    uint constant private AUTHORITY_ISSUER_CPT_ID_EXCEED_MAX = 500302;
    uint constant private CPT_PUBLISHER_NOT_EXIST = 500303;

    CptData private cptData;
    WeIdContract private weIdContract;

    function CptController(
	    address cptDataAddress,
        address weIdContractAddress
	) 
	    public
    {
        cptData = CptData(cptDataAddress);
        weIdContract = WeIdContract(weIdContractAddress);
    }

    event RegisterCptRetLog(
		uint retCode, 
		uint cptId, 
		int cptVersion
	);

    event UpdateCptRetLog(
		uint retCode, 
		uint cptId, 
		int cptVersion
	);

    function registerCpt(
	    address publisher, 
		int[8] intArray, 
		bytes32[8] bytes32Array,
		bytes32[128] jsonSchemaArray, 
		uint8 v, 
		bytes32 r, 
		bytes32 s
	) 
	    public 
		returns (bool) 
	{
        if (!weIdContract.isIdentityExist(publisher)) {
            RegisterCptRetLog(CPT_PUBLISHER_NOT_EXIST, 0, 0);
            return false;
        }

        uint cptId = cptData.getCptId(publisher); 
        if (cptId == 0) {
            RegisterCptRetLog(AUTHORITY_ISSUER_CPT_ID_EXCEED_MAX, 0, 0);
			return false;
        }
        int cptVersion = 1;
        intArray[0] = cptVersion;
        cptData.putCpt(cptId, publisher, intArray, bytes32Array, jsonSchemaArray, v, r, s);

        RegisterCptRetLog(0, cptId, cptVersion);
        return true;
    }

    function updateCpt(
	    uint cptId, 
		address publisher, 
		int[8] intArray, 
		bytes32[8] bytes32Array,
		bytes32[128] jsonSchemaArray, 
		uint8 v, 
		bytes32 r, 
		bytes32 s
	) 
	    public 
		returns (bool) 
	{
        if (!weIdContract.isIdentityExist(publisher)) {
            UpdateCptRetLog(CPT_PUBLISHER_NOT_EXIST, 0, 0);
            return false;
        }

        if (cptData.isCptExist(cptId)) {
            int[8] memory cptIntArray = cptData.getCptIntArray(cptId);
            int cptVersion = cptIntArray[0] + 1;
            intArray[0] = cptVersion;
            int created = cptIntArray[1];
            intArray[1] = created;
            cptData.putCpt(cptId, publisher, intArray, bytes32Array, jsonSchemaArray, v, r, s);
            UpdateCptRetLog(0, cptId, cptVersion);
            return true;
        } else {
            UpdateCptRetLog(CPT_NOT_EXIST, 0, 0);
            return false;
        }
    }

    function queryCpt(
	    uint cptId
	) 
	    public 
		constant 
		returns (
        address publisher, 
        int[] intArray, 
        bytes32[] bytes32Array,
        bytes32[] jsonSchemaArray, 
        uint8 v, 
        bytes32 r, 
        bytes32 s)
    {
        publisher = cptData.getCptPublisher(cptId);
        intArray = getCptDynamicIntArray(cptId);
        bytes32Array = getCptDynamicBytes32Array(cptId);
        jsonSchemaArray = getCptDynamicJsonSchemaArray(cptId);
        (v, r, s) = cptData.getCptSignature(cptId);
    }

    function getCptDynamicIntArray(
        uint cptId
    ) 
        public
        constant 
        returns (int[])
    {
        int[8] memory staticIntArray = cptData.getCptIntArray(cptId);
        int[] memory dynamicIntArray = new int[](8);
        for (uint i = 0; i < 8; i++) {
            dynamicIntArray[i] = staticIntArray[i];
        }
        return dynamicIntArray;
    }

    function getCptDynamicBytes32Array(
        uint cptId
    ) 
        public 
        constant 
        returns (bytes32[])
    {
        bytes32[8] memory staticBytes32Array = cptData.getCptBytes32Array(cptId);
        bytes32[] memory dynamicBytes32Array = new bytes32[](8);
        for (uint i = 0; i < 8; i++) {
            dynamicBytes32Array[i] = staticBytes32Array[i];
        }
        return dynamicBytes32Array;
    }

    function getCptDynamicJsonSchemaArray(
        uint cptId
    ) 
        public 
        constant 
        returns (bytes32[])
    {
        bytes32[128] memory staticBytes32Array = cptData.getCptJsonSchemaArray(cptId);
        bytes32[] memory dynamicBytes32Array = new bytes32[](128);
        for (uint i = 0; i < 128; i++) {
            dynamicBytes32Array[i] = staticBytes32Array[i];
        }
        return dynamicBytes32Array;
    } 
}