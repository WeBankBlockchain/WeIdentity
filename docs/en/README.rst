

.. image:: ../zh_CN/docs/images/weidentity-logo.png
   :alt: weidentity-logo.jpeg

----

What is WeIdentity?
===================

WeIdentity is a blockchain solution on Open Consortium Chain to serve as a hub for identity authentication by establishing identity of entities (e.g. persons or objects) on the chain and allowing the interchange of such information among organizations when authorized.
WeIdentity is an open source platform built by WeBank and promotes the values of Open Consortium Chain: streamline resources, collaborate to produce values and serve the public.

Modules
-------

WeIdentity includes two major modules: WeIdentity DID and WeIdentity Credential.

Decentralized Identifiers (WeIdentity DID)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Traditionally, user identity is issued and managed by single centralized organization. With the advent of blockchain technology, it is possible to publish and distribute user identity on chain to allow more than one organization to manage on as a multi-centers solution
WeIdentity DID Module has come with a distributed identification protocol based on \ `FISCO-BCOS Blockchain Platform <https://fisco-bcos-documentation-en.readthedocs.io/en/latest/>`_\ , and \ `W3C DID specification <https://w3c-ccg.github.io/did-spec/>`_\, to create identities on chain and associate it with any person or object in the real world. Moreover, DID ensures the Entity with full rights of control and ownership of the identities.

The design goals of WeIdentity DID:

.. raw:: html

    <embed>
      <table border='1' style="border-collapse:collapse" class='tables'>
         <tr>
            <th width="100">Objective</th>
            <th>Description</th>
         </tr>
         <tr>
            <td>Distributed</td>
            <td>Distributed ID registration model removes the dependencies on traditional single, centralized registration organization. </td>
         </tr>
         <tr>
            <td>Open Sourced</td>
            <td>The solution is fully open sourced and publicly available.</td>
         </tr>
         <tr>
            <td>Privacy</td>
            <td>The actual content of identity or credential are stored off-chain to maintain minimal information on the chain. Such can prevent any 3rd party from speculating the actual identity in real-world using the information on the chain.</td>
         </tr>
         <tr>
            <td>Portability</td>
            <td>Data on WeIdentity can be ported into mainstream blockchain platforms or those which are compliant with WeIdentitie’s specification.</td>
         </tr>
         <tr>
            <td>Interoperability</td>
            <td>Provides standard interfaces to support cross chains or cross platforms interoperation.</td>
         </tr>
         <tr>
            <td>Extensibility</td>
            <td>The data model can be extended for different business scenarios while maintaining its operability, portability and simplicity.</td>
         </tr>
      </table>
      <br />
    </embed>



WeIdentity Credential
^^^^^^^^^^^^^^^^^^^^^

There are a lot of credentials describing identity in daily life such as personal identity card, driving license, account book, prescription, graduate certificate, property ownership certificate and credit report. WeIdentity Credential offers a complete set of `W3C Verifiable Credentials <https://w3c.github.io/vc-data-model/>`_ based solutions designated to standardize and digitize such credentials into a verifiable and interchangeable format.

WeIdentity encourages certificate organization to issue their own standardized credential templates to enrich the ecosystem on open consortium chain.

More
^^^^

*
   `Use Cases and Scenarios (Chinese Version) <https://weidentity.readthedocs.io/zh_CN/latest/docs/use-cases.html>`_

*
   `WeIdentity Specification (Chinese Version) <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-spec.html>`_

*
   `FAQ <https://weidentity.readthedocs.io/en/latest/docs/faq.html>`_


Current Status
---------------

WeIdentity is running on top of FISCO-BCOS with JAVA SDK provided for developers, please review the Installation & Deployment guide and SDK user guide listed below:


.. raw:: html

    <embed>
      <table border='1' style="border-collapse:collapse" class='tables'>
         <tr>
            <th width="20%">Integration Method</th>
            <th width="40%">Documentation</th>
            <th width="40%">Status</th>
         </tr>
         <tr>
            <td style="text-align:center"><a href="https://github.com/WeBankFinTech/weid-java-sdk">JAVA SDK</a></td>
            <td>
               <ul>
                 <li><a href="https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-installation.html">Installation and Deployment Guide (Chinese Version)</a></li>
                 <li><a href="https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html">SDK User Guide (Chinese Version)</a></li>
                 <li><a href="https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-rest.html">RestService Guide (Chinese Version)</a></li>
               </ul>
            </td>
            <td>
               <a href="https://travis-ci.org/WeBankFinTech/weid-java-sdk"><img src="https://travis-ci.org/WeBankFinTech/weid-java-sdk.svg?branch=develop" /></a>
               <a href="https://github.com/WeBankFinTech/weid-java-sdk/releases/latest"><img src="https://img.shields.io/github/release/WeBankFinTech/weid-java-sdk.svg" /></a>
               <a href="https://search.maven.org/search?q=g:%22com.webank%22%20AND%20a:%22weidentity-java-sdk%22"><img src="https://img.shields.io/maven-central/v/com.webank/weidentity-java-sdk.svg?label=Maven%20Central" /></a>
               <a href="https://app.codacy.com/app/webankadmin/weid-java-sdk?utm_source=github.com&utm_medium=referral&utm_content=WeBankFinTech/weid-java-sdk&utm_campaign=Badge_Grade_Dashboard"><img src="https://api.codacy.com/project/badge/Grade/9fc044b36fff4985bd69f1232380d5ee" /></a>
               <a href="https://www.codefactor.io/repository/github/WeBankFinTech/weid-java-sdk"><img src="https://www.codefactor.io/repository/github/WeBankFinTech/weid-java-sdk/badge" /></a>
               <a href="https://codecov.io/gh/WeBankFinTech/weid-java-sdk"><img src="https://codecov.io/gh/WeBankFinTech/weid-java-sdk/branch/master/graph/badge.svg" /></a>
               <a href="https://www.gnu.org/licenses/lgpl-3.0"><img src="https://img.shields.io/badge/license-GNU%20LGPL%20v3.0-blue.svg" /></a>
            </td>
         </tr>
      </table>
      <br />
    </embed>

Contact Us
----------

Email：weidentity@webank.com
