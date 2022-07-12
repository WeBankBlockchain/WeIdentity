package com.webank.weid.protocol.cpt.v2;

import com.webank.weid.protocol.base.CredentialPojo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
 * Trusted timestamping
 * <p>
 * Trusted timestamping envelope
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "authoritySignature",
    "claimHash",
    "credentialList",
    "timestamp",
    "timestampAuthority"
})
@Generated("jsonschema2pojo")
public class Cpt108 {

    /**
     * Signature value from Authority, signed by Timestamp authority. authoritySignature = sign( hashKey )
     * (Required)
     * 
     */
    @JsonProperty("authoritySignature")
    @JsonPropertyDescription("Signature value from Authority, signed by Timestamp authority. authoritySignature = sign( hashKey )")
    private String authoritySignature;
    /**
     * calculate the hash from the credentials.
     * (Required)
     * 
     */
    @JsonProperty("claimHash")
    @JsonPropertyDescription("calculate the hash from the credentials.")
    private String claimHash;
    /**
     * Original credential list to be signed
     * (Required)
     * 
     */
    @JsonProperty("credentialList")
    @JsonPropertyDescription("Original credential list to be signed")
    private List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
    /**
     * trusted timestamping provided by the trusted third party or by the consensus of each node in the consortium chain
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    @JsonPropertyDescription("trusted timestamping provided by the trusted third party or by the consensus of each node in the consortium chain")
    private Integer timestamp;
    /**
     * information about timestamp authority
     * (Required)
     * 
     */
    @JsonProperty("timestampAuthority")
    @JsonPropertyDescription("information about timestamp authority")
    private String timestampAuthority;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Signature value from Authority, signed by Timestamp authority. authoritySignature = sign( hashKey )
     * (Required)
     * 
     */
    @JsonProperty("authoritySignature")
    public String getAuthoritySignature() {
        return authoritySignature;
    }

    /**
     * Signature value from Authority, signed by Timestamp authority. authoritySignature = sign( hashKey )
     * (Required)
     * 
     */
    @JsonProperty("authoritySignature")
    public void setAuthoritySignature(String authoritySignature) {
        this.authoritySignature = authoritySignature;
    }

    /**
     * calculate the hash from the credentials.
     * (Required)
     * 
     */
    @JsonProperty("claimHash")
    public String getClaimHash() {
        return claimHash;
    }

    /**
     * calculate the hash from the credentials.
     * (Required)
     * 
     */
    @JsonProperty("claimHash")
    public void setClaimHash(String claimHash) {
        this.claimHash = claimHash;
    }

    /**
     * Original credential list to be signed
     * (Required)
     * 
     */
    @JsonProperty("credentialList")
    public List<CredentialPojo> getCredentialList() {
        return credentialList;
    }

    /**
     * Original credential list to be signed
     * (Required)
     * 
     */
    @JsonProperty("credentialList")
    public void setCredentialList(List<CredentialPojo> credentialList) {
        this.credentialList = credentialList;
    }

    /**
     * trusted timestamping provided by the trusted third party or by the consensus of each node in the consortium chain
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public Integer getTimestamp() {
        return timestamp;
    }

    /**
     * trusted timestamping provided by the trusted third party or by the consensus of each node in the consortium chain
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * information about timestamp authority
     * (Required)
     * 
     */
    @JsonProperty("timestampAuthority")
    public String getTimestampAuthority() {
        return timestampAuthority;
    }

    /**
     * information about timestamp authority
     * (Required)
     * 
     */
    @JsonProperty("timestampAuthority")
    public void setTimestampAuthority(String timestampAuthority) {
        this.timestampAuthority = timestampAuthority;
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
        sb.append(Cpt108 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("authoritySignature");
        sb.append('=');
        sb.append(((this.authoritySignature == null)?"<null>":this.authoritySignature));
        sb.append(',');
        sb.append("claimHash");
        sb.append('=');
        sb.append(((this.claimHash == null)?"<null>":this.claimHash));
        sb.append(',');
        sb.append("credentialList");
        sb.append('=');
        sb.append(((this.credentialList == null)?"<null>":this.credentialList));
        sb.append(',');
        sb.append("timestamp");
        sb.append('=');
        sb.append(((this.timestamp == null)?"<null>":this.timestamp));
        sb.append(',');
        sb.append("timestampAuthority");
        sb.append('=');
        sb.append(((this.timestampAuthority == null)?"<null>":this.timestampAuthority));
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
        result = ((result* 31)+((this.timestampAuthority == null)? 0 :this.timestampAuthority.hashCode()));
        result = ((result* 31)+((this.authoritySignature == null)? 0 :this.authoritySignature.hashCode()));
        result = ((result* 31)+((this.claimHash == null)? 0 :this.claimHash.hashCode()));
        result = ((result* 31)+((this.credentialList == null)? 0 :this.credentialList.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.timestamp == null)? 0 :this.timestamp.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt108) == false) {
            return false;
        }
        Cpt108 rhs = ((Cpt108) other);
        return (((((((this.timestampAuthority == rhs.timestampAuthority)||((this.timestampAuthority!= null)&&this.timestampAuthority.equals(rhs.timestampAuthority)))&&((this.authoritySignature == rhs.authoritySignature)||((this.authoritySignature!= null)&&this.authoritySignature.equals(rhs.authoritySignature))))&&((this.claimHash == rhs.claimHash)||((this.claimHash!= null)&&this.claimHash.equals(rhs.claimHash))))&&((this.credentialList == rhs.credentialList)||((this.credentialList!= null)&&this.credentialList.equals(rhs.credentialList))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.timestamp == rhs.timestamp)||((this.timestamp!= null)&&this.timestamp.equals(rhs.timestamp))));
    }

}
