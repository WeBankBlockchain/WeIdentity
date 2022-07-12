package com.webank.weid.protocol.cpt.v2;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * User CPT
 * <p>
 * User request issuer to sign credential
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cptId",
    "credentialSignatureRequest",
    "userNonce"
})
@Generated("jsonschema2pojo")
public class Cpt111 {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cptId")
    private String cptId;
    /**
     * credential Signature Request
     * (Required)
     * 
     */
    @JsonProperty("credentialSignatureRequest")
    @JsonPropertyDescription("credential Signature Request")
    private String credentialSignatureRequest;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userNonce")
    private String userNonce;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cptId")
    public String getCptId() {
        return cptId;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cptId")
    public void setCptId(String cptId) {
        this.cptId = cptId;
    }

    /**
     * credential Signature Request
     * (Required)
     * 
     */
    @JsonProperty("credentialSignatureRequest")
    public String getCredentialSignatureRequest() {
        return credentialSignatureRequest;
    }

    /**
     * credential Signature Request
     * (Required)
     *
     */
    @JsonProperty("credentialSignatureRequest")
    public void setCredentialSignatureRequest(String credentialSignatureRequest) {
        this.credentialSignatureRequest = credentialSignatureRequest;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userNonce")
    public String getUserNonce() {
        return userNonce;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userNonce")
    public void setUserNonce(String userNonce) {
        this.userNonce = userNonce;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Cpt111 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("cptId");
        sb.append('=');
        sb.append(((this.cptId == null)?"<null>":this.cptId));
        sb.append(',');
        sb.append("credentialSignatureRequest");
        sb.append('=');
        sb.append(((this.credentialSignatureRequest == null)?"<null>":this.credentialSignatureRequest));
        sb.append(',');
        sb.append("userNonce");
        sb.append('=');
        sb.append(((this.userNonce == null)?"<null>":this.userNonce));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.cptId == null)? 0 :this.cptId.hashCode()));
        result = ((result* 31)+((this.credentialSignatureRequest == null)? 0 :this.credentialSignatureRequest.hashCode()));
        result = ((result* 31)+((this.userNonce == null)? 0 :this.userNonce.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt111) == false) {
            return false;
        }
        Cpt111 rhs = ((Cpt111) other);
        return (((((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties)))&&((this.cptId == rhs.cptId)||((this.cptId!= null)&&this.cptId.equals(rhs.cptId))))&&((this.credentialSignatureRequest == rhs.credentialSignatureRequest)||((this.credentialSignatureRequest!= null)&&this.credentialSignatureRequest.equals(rhs.credentialSignatureRequest))))&&((this.userNonce == rhs.userNonce)||((this.userNonce!= null)&&this.userNonce.equals(rhs.userNonce))));
    }

}
