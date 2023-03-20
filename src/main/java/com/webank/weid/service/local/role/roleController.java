package com.webank.weid.service.local.role;

import com.webank.weid.service.local.CptServiceLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class roleController {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(roleController.class);

    /**
     * Role related Constants.
     */
    public Integer ROLE_AUTHORITY_ISSUER = 100;
    public Integer ROLE_COMMITTEE = 101;
    public Integer ROLE_ADMIN = 102;

    /**
     * Operation related Constants.
     */
    public Integer MODIFY_AUTHORITY_ISSUER = 200;
    public Integer MODIFY_COMMITTEE = 201;
    public Integer MODIFY_ADMIN = 202;
    public Integer MODIFY_KEY_CPT = 203;


}
