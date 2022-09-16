

package com.webank.weid.protocol.base;

import lombok.Data;

import com.webank.weid.constant.WeIdConstant.PublicKeyType;
import org.junit.Assert;

import java.util.List;

/**
 * The base data structure for AuthenticationProperty.
 *
 * @author tonychen 2018.10.8
 */
@Data
public class AuthenticationProperty {

    /**
     * Required: The verification method id.
     */
    private String id;

    /**
     * Required: The verification method type.
     */
    private String type = "Ed25519VerificationKey2020";

    /**
     * Required: The verification method controller.
     */
    private String controller;

    /**
     * Required: The verification method material.
     */
    private String publicKeyMultibase;

    public String toString() {
        return this.id + ',' + this.type + ',' + this.controller + ',' + this.publicKeyMultibase;
    }

    public static AuthenticationProperty fromString(String authString) {
        String[] result = authString.split(",");
        AuthenticationProperty authenticationProperty = new AuthenticationProperty();
        authenticationProperty.setId(result[0]);
        authenticationProperty.setController(result[2]);
        authenticationProperty.setPublicKeyMultibase(result[3]);
        return authenticationProperty;
    }
}
