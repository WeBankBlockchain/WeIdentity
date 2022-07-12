package com.webank.weid.protocol.cpt;

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
 * Trusted Timestamp
 * <p>
 * Trusted Timestamp from authorized 3rd-party, or chain consensus
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "claimHash",
    "hashKey",
    "signatureList",
    "timestamp"
})
@Generated("jsonschema2pojo")
public class Cpt109 {

    /**
     * calculate the hash from the entire list rather than from any single credential
     * (Required)
     * 
     */
    @JsonProperty("claimHash")
    @JsonPropertyDescription("calculate the hash from the entire list rather than from any single credential")
    private String claimHash;
    /**
     * hashKey = hash(claimHash + timestamp) hashKey will be the key in the smart contract
     * (Required)
     * 
     */
    @JsonProperty("hashKey")
    @JsonPropertyDescription("hashKey = hash(claimHash + timestamp) hashKey will be the key in the smart contract")
    private String hashKey;
    /**
     * signed by Timestamp authority signature = sign( hashKey )
     * (Required)
     * 
     */
    @JsonProperty("signatureList")
    @JsonPropertyDescription("signed by Timestamp authority signature = sign( hashKey )")
    private String signatureList;
    /**
     * trusted timestamp provided by the trusted third party or by the consensus of each node in the consortium chain
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    @JsonPropertyDescription("trusted timestamp provided by the trusted third party or by the consensus of each node in the consortium chain")
    private Integer timestamp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * calculate the hash from the entire list rather than from any single credential
     * (Required)
     * 
     */
    @JsonProperty("claimHash")
    public String getClaimHash() {
        return claimHash;
    }

    /**
     * calculate the hash from the entire list rather than from any single credential
     * (Required)
     * 
     */
    @JsonProperty("claimHash")
    public void setClaimHash(String claimHash) {
        this.claimHash = claimHash;
    }

    /**
     * hashKey = hash(claimHash + timestamp) hashKey will be the key in the smart contract
     * (Required)
     * 
     */
    @JsonProperty("hashKey")
    public String getHashKey() {
        return hashKey;
    }

    /**
     * hashKey = hash(claimHash + timestamp) hashKey will be the key in the smart contract
     * (Required)
     * 
     */
    @JsonProperty("hashKey")
    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    /**
     * signed by Timestamp authority signature = sign( hashKey )
     * (Required)
     * 
     */
    @JsonProperty("signatureList")
    public String getSignatureList() {
        return signatureList;
    }

    /**
     * signed by Timestamp authority signature = sign( hashKey )
     * (Required)
     * 
     */
    @JsonProperty("signatureList")
    public void setSignatureList(String signatureList) {
        this.signatureList = signatureList;
    }

    /**
     * trusted timestamp provided by the trusted third party or by the consensus of each node in the consortium chain
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public Integer getTimestamp() {
        return timestamp;
    }

    /**
     * trusted timestamp provided by the trusted third party or by the consensus of each node in the consortium chain
     * (Required)
     * 
     */
    @JsonProperty("timestamp")
    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
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
        sb.append(Cpt109 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("claimHash");
        sb.append('=');
        sb.append(((this.claimHash == null)?"<null>":this.claimHash));
        sb.append(',');
        sb.append("hashKey");
        sb.append('=');
        sb.append(((this.hashKey == null)?"<null>":this.hashKey));
        sb.append(',');
        sb.append("signatureList");
        sb.append('=');
        sb.append(((this.signatureList == null)?"<null>":this.signatureList));
        sb.append(',');
        sb.append("timestamp");
        sb.append('=');
        sb.append(((this.timestamp == null)?"<null>":this.timestamp));
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
        result = ((result* 31)+((this.signatureList == null)? 0 :this.signatureList.hashCode()));
        result = ((result* 31)+((this.hashKey == null)? 0 :this.hashKey.hashCode()));
        result = ((result* 31)+((this.claimHash == null)? 0 :this.claimHash.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.timestamp == null)? 0 :this.timestamp.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt109) == false) {
            return false;
        }
        Cpt109 rhs = ((Cpt109) other);
        return ((((((this.signatureList == rhs.signatureList)||((this.signatureList!= null)&&this.signatureList.equals(rhs.signatureList)))&&((this.hashKey == rhs.hashKey)||((this.hashKey!= null)&&this.hashKey.equals(rhs.hashKey))))&&((this.claimHash == rhs.claimHash)||((this.claimHash!= null)&&this.claimHash.equals(rhs.claimHash))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.timestamp == rhs.timestamp)||((this.timestamp!= null)&&this.timestamp.equals(rhs.timestamp))));
    }

}
