pragma solidity ^0.4.4;

import "./AuthorityIssuerData.sol";

contract CptData {

    uint constant private AUTHORITY_ISSUER_MAX_CPT_ID = 1999999;

    uint authority_issuer_start_id = 100;

    uint none_authority_issuer_start_id = 2000000;

    AuthorityIssuerData private authorityIssuerData;

    function CptData(
		address authorityIssuerDataAddress
	) 
	    public
    {
        authorityIssuerData = AuthorityIssuerData(authorityIssuerDataAddress);
    }

    struct Signature {
        uint8 v; 
        bytes32 r; 
        bytes32 s;
    }

    struct Cpt {
        //store the weid address of cpt publisher
        address publisher;
        //intArray[0] store cpt version, int[1] store created, int[2] store updated and left are  preserved int fields
        int[8] intArray;
        //all are  preserved bytes32 fields
        bytes32[8] bytes32Array;
        //store json schema
        bytes32[128] jsonSchemaArray;
        //store signature
        Signature signature;
    }

    mapping (uint => Cpt) private cptMap;

    function putCpt(
	    uint cptId, 
		address cptPublisher, 
		int[8] cptIntArray, 
		bytes32[8] cptBytes32Array,
		bytes32[128] cptJsonSchemaArray, 
		uint8 cptV, 
		bytes32 cptR, 
		bytes32 cptS
	) 
		public 
		returns (bool) 
    {
        Signature memory cptSignature = Signature({v: cptV, r: cptR, s: cptS});
        cptMap[cptId] = Cpt({publisher: cptPublisher, intArray: cptIntArray, bytes32Array: cptBytes32Array, jsonSchemaArray:cptJsonSchemaArray, signature: cptSignature});
        return true;
    }

    function getCptId(
        address publisher
    ) 
        public 
        constant
        returns 
        (uint cptId)
    {
        if (authorityIssuerData.isAuthorityIssuer(publisher)) {
            cptId = authority_issuer_start_id++;
            if (cptId > AUTHORITY_ISSUER_MAX_CPT_ID) {
                cptId = 0;
            }
        } else {
            cptId = none_authority_issuer_start_id++;
        }
    }

    function getCpt(
	    uint cptId
	) 
		public 
		constant 
		returns (
		address publisher, 
		int[8] intArray, 
		bytes32[8] bytes32Array,
		bytes32[128] jsonSchemaArray, 
		uint8 v, 
		bytes32 r, 
		bytes32 s) 
    {
        Cpt memory cpt = cptMap[cptId];
        publisher = cpt.publisher;
        intArray = cpt.intArray;
        bytes32Array = cpt.bytes32Array;
        jsonSchemaArray = cpt.jsonSchemaArray;
        v = cpt.signature.v;
        r = cpt.signature.r;
        s = cpt.signature.s;
    } 

    function getCptPublisher(
        uint cptId
    ) 
        public 
        constant 
        returns (address publisher)
    {
        Cpt memory cpt = cptMap[cptId];
        publisher = cpt.publisher;
    }

    function getCptIntArray(
        uint cptId
    ) 
        public 
        constant 
        returns (int[8] intArray)
    {
        Cpt memory cpt = cptMap[cptId];
        intArray = cpt.intArray;
    }

    function getCptJsonSchemaArray(
        uint cptId
    ) 
        public 
        constant 
        returns (bytes32[128] jsonSchemaArray)
    {
        Cpt memory cpt = cptMap[cptId];
        jsonSchemaArray = cpt.jsonSchemaArray;
    }

    function getCptBytes32Array(
        uint cptId
    ) 
        public 
        constant 
        returns (bytes32[8] bytes32Array)
    {
        Cpt memory cpt = cptMap[cptId];
        bytes32Array = cpt.bytes32Array;
    }

    function getCptSignature(
        uint cptId
    ) 
        public 
        constant 
        returns (uint8 v, bytes32 r, bytes32 s) 
    {
        Cpt memory cpt = cptMap[cptId];
        v = cpt.signature.v;
        r = cpt.signature.r;
        s = cpt.signature.s;
    }

    function isCptExist(
        uint cptId
    ) 
        public 
        constant 
        returns (bool)
    {
        int[8] memory intArray = getCptIntArray(cptId);
        if (intArray[0] != 0) {
            return true;
        } else {
            return false;
        }
    }
}