# FAQ

---

- **What can the user do on his/her/its data?**

1. User (can be a person, an object or an organization) has full control and ownership of the digital identifier without reliance on external authorities.
2. User’s digital identifier can be portable to other systems and be used as long as the identifiers support DIDs and DID methods.
3. Users can authorize the access of their data to a third party.
4. User can decide what information from digital credential should be disclosed whereas the validity and the authenticity of the data can still be preserved.

---

- **Which organizations or agencies should be involved in KYC (Know your client) under WeIdentity?**

It is all down to the nature of business on which organizations should be involved in KYC.
In other words, WeIdentity will never interfere in the existing KYC process. Nor it has any obligations of/influences over involving any organizations in the KYC process.

---

- **What kind of credentials are supported by WeIdentity? What kind of organizations this credential is associated with?**

WeIdentity can restructure and transform paper credentials to  digital forms in two categories.

1. Authoritative credential: A WeIdentity credential which is digitally signed by an issuing authority or organization. For example, identity card, driving license, passport, academic certificate and medical prescription.
2. Customized credential: A WeIdentity credential which is digitally signed by a person. For example: authorization certificate, promissory note and invitation.

---

- **How to apply WeIdentity in a business context like digital escrow, supply chain, trading, and gaming?**

WeIdentity can be applied in all business contexts where proof of one’s identity, access of authorized information or exchange of data are required. However, different business scenarios with different requirements will derive specific adoptions of the technical solution.

---

- **How to obtain the detailed information which is stored off-chain?**

WeIdentity never publishes private information on chain and such will only be stored separately off-chain. There are two ways in retrieving these information:
1. User can first download the information through User agent, then submit the information online or via QR code to the party who needs to access the information.
2. Or, the user can authorize User Agent to access the data directrly, if there is a data transfer channel already built between User Agent and data consumer.

---

- **How to register a new user or organization onboard to the chain?**

New user must go through User Agent to access the chain. And new organizations are advised to register while taking their roles and business scenarios into account:
1. In most business models, the transaction initiator normally plays the role of blockchain operator, who is expected to deploy all nodes for the consortium chain and provide an open platform solution for other business players to connect.
2. Other business players can either deploy their own nodes with permission from the initiator, or join the consortium blockchain through the open platform provided.

---

- **How can WeIdentity ensure the authoritativeness of information provided by the issuing organization? E.g. making sure a sustainable and stable provision of reliable data from genuine source?**

In a WeIdentity project, data credibility is built upon the trust and recognition people have on the data provider as an authority. It is down to the capability of the data provider (e.g. government) to maintain its authority of information and quality of data service.

---

- **Which organizations can participate in consensus decision and what are the requirements?**

Parties like data provider, data consumer and User Agent could be eligible to take part in consensus process, but such could vary with and depend on the nature of business or the role an organization takes.

---

- **How to migrate the data of WeIdentity DID to other platform ?**

1. WeIdentity SDK provides interface for exporting data (e.g. for user agent’s use when data migration requires) of WeIdentity Document into JSON file format.
2. WeIdentity SDK also provides interface for importing WeID Document from other WeID platforms. Once imported, the WeIdentity Document will be created with same attributes as origin.

---

- **How to migrate the data of Credential to other platform?**

In WeIdentity, Credential is implemented following the specification of W3C verifiable credentials and such can be stored or managed by different organizations in different use cases.
WeIdentity standardizes the function interface to allow the organization to import data to or export data from the platform with user’s permission.

---

- **Does WeIdentity provide batch processing interface?**

Currently batch processing is not yet ready, however it will be provided in a later release.

---

- **What is the difference between WeIdentity DID and Standard DID ?**

WeIdentity DID has further implemented a distributed and multi-centered identity authentication protocol (WeIdentity is required to be run on a distributed ledger platform) based on W3C DID specification to allow a person or anobject to be registered,identified and authenticated on chain.

---

- **What is CPT and how CPT is used?**

CPT (Claim Protocol Type) is a data structure which can be customized and used as a template to describe a type of Credential such as driving license and academic certificate, therefore each Credential must define its CPT.
For instance, employer can define a CPT and then register it on WeIdentity blockchain to describe the data structure of the employee access card to manage the access control within the company.

---

- **When a data consumer fetches user data from data provider, how to ensure the process is authorized?**

When Party A (data consumer) wants an access to User X’s data (data owner) from Party B (data provider), Party A must first receive an “Authorization Credential”(in CPT101 or customized CPT) from User X.

Once received, Party A then submits a data access request to Party B with User X’s “Authorization Credential” as an attachment through data transfer interface. After Party B receives and verifies the “Authorization Credential”, Party B then returns the requested data to Party A.

---

- **What to do when an issuer/a person loses a private key?**

Once the private key is lost, the ‘Recovery’ mechanism can help to reset the public key of authentication at WeIdentity. However it requires the owner of WeIdentity DID having assigned a user for key recovery upfront.
In the future, WeIdentity will support multiple recovery users. Any user, or a required number of users on the delegate list can reset the key.

---

- **How to re-issue or revoke an issued Credential?**

WeIdentity Credential allows organizational or individual issuers to revoke issued Credentials when needed.
To re-issue the Credential, WeIdentity will treat it as a new Credential with a new Credential ID.
At the same time, WeIdentity allows issuer to renew or extend the expirity date of a Credential.

---

- **Can Credential be forged? Any way to prevent the forgery?**  

WeIdentity Credentialcurrently adopts ECDSA signature algorithm, and will support RSA signature in future releases. The effort to forge a Credential is the same as to compromise an ECDSA/RSA private key with a given key-length, which makes forgery practically impossible if the private key of Credential is securely kept.

---
