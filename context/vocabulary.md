# WeIdentity Verifiable Credentials and Representations Vocabulary
Date: 2019 / 05 / 10

Editors: 
- Junqi Zhang (junqizhang@webank.com)
- Chaoxin Hu (chaoxinhu@webank.com)

## Abstract
This document describes the Vocabulary of the Verifiable Credentials and Representations of WeIdentity. The purpose is to be used as the complement of the original W3C Verifiable Credentials Vocabulary (https://www.w3.org/2018/credentials/) - where a set of WeIdentity specific terms are not listed and they are illustrated here. 

## Introduction
This document describes the RDFS vocabulary description used for WeIdentity Verifiable Credentials and Representations along with the default JSON-LD Context.

This specification makes use of the following namespaces:

weidentity: https://fintech.webank.com/weidentity/vocab

## Classes
### Secp256k1

An Secp256k1 is used for digital signatures on datasets. By default, this signature mechanism uses a ```SHA-256 digest``` and ```ECDSA signature``` with SECP256K1 as the specific algorithm-scheme to perform the digital signature, authentication, and verification.

- Identifier
Secp256k1
- Status
stable
- Parent Class
Signature algorithm in Proof
- Expected properties
creator, created, signature
- Signature Properties
    - Default Canonicalization Algorithm: https://w3id.org/rdf#URDNA2015
    - Default Signature Algorithm: http://www.w3.org/2000/09/xmldsig#rsa-sha256

The example below shows how a basic JSON-LD signature is expressed in a JSON-LD snippet. Note that the signature property is directly embedded in the object. The signature algorithm specifies how the signature can be generated and verified.

```java
{
  "@context": ["https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1"]
  "signature": {
    "type": "Secp256k1",
    "creator": "did:weid:0xca35b7d915458ef540ade6068dfe2f44e8fa733c",
    "created": "2018-11-05T13:12:54Z",
    "signature": "OGCzNGVkMzVmMmQ31DIyOWM32MzQzNmEx3goYzI4ZDY3NjI4NTIy5Tk="
  }
}
```