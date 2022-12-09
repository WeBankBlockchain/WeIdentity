

package com.webank.weid;

import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.service.impl.*;
import com.webank.weid.service.rpc.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * Test base class.
 *
 * @author v_wbgyang
 */
public abstract class BaseTest {

    protected AuthorityIssuerService authorityIssuerService;
    protected CptService cptService;
    protected WeIdService weIdService;
    protected CredentialService credentialService;
    protected CredentialPojoService credentialPojoService;
    protected EvidenceService evidenceService;
    protected PolicyService policyService;

    /**
     * the private key of sdk is a BigInteger,which needs to be used when registering authority.
     */
    protected String privateKey;
    
    static {
        // mock DB
        MockMysqlDriver.mockMysqlDriver();
        MockIssuerClient.mockMakeCredentialTemplate();
    }

    /**
     * initialization some for test.
     */
    @Before
    public void setUp()  {

        authorityIssuerService = new AuthorityIssuerServiceImpl();
        cptService = new CptServiceImpl();
        weIdService = new WeIdServiceImpl();
        credentialService = new CredentialServiceImpl();
        evidenceService = new EvidenceServiceImpl();
        credentialPojoService = new CredentialPojoServiceImpl();
        policyService = new PolicyServiceImpl();

        //privateKey = TestBaseUtil.readPrivateKeyFromFile("ecdsa_key");
        privateKey = TestBaseUtil.readPrivateKeyFromFile("private_key");

        testInit();
    }

    /**
     * tearDown some for test.
     */
    @After
    public void tearDown() {

        authorityIssuerService = null;
        cptService = null;
        weIdService = null;
        credentialService = null;
        evidenceService = null;
        credentialPojoService = null;

        testFinalize();
    }

    public void testInit() {
        Assert.assertTrue(true);
    }

    public void testFinalize() {
        Assert.assertTrue(true);
    }
}