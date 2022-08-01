package com.webank.weid.protocol.cpt;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;


/**
 * Metadata CPT
 * <p>
 * User request issuer to sign credential
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "context",
    "cptId",
    "credentialId",
    "expirationDate",
    "issuanceDate",
    "issuer"
})
@Generated("jsonschema2pojo")
@Data
public class Cpt110 {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("context")
    private String context;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cptId")
    private Integer cptId;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("credentialId")
    private String credentialId;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("expirationDate")
    private Integer expirationDate;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuanceDate")
    private Integer issuanceDate;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("issuer")
    private String issuer;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * getAdditionalProperties
     * @return additionalProperties
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     *
     * @param name name
     * @param value value
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Cpt110 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("context");
        sb.append('=');
        sb.append(((this.context == null)?"<null>":this.context));
        sb.append(',');
        sb.append("cptId");
        sb.append('=');
        sb.append(((this.cptId == null)?"<null>":this.cptId));
        sb.append(',');
        sb.append("credentialId");
        sb.append('=');
        sb.append(((this.credentialId == null)?"<null>":this.credentialId));
        sb.append(',');
        sb.append("expirationDate");
        sb.append('=');
        sb.append(((this.expirationDate == null)?"<null>":this.expirationDate));
        sb.append(',');
        sb.append("issuanceDate");
        sb.append('=');
        sb.append(((this.issuanceDate == null)?"<null>":this.issuanceDate));
        sb.append(',');
        sb.append("issuer");
        sb.append('=');
        sb.append(((this.issuer == null)?"<null>":this.issuer));
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
        result = ((result* 31)+((this.cptId == null)? 0 :this.cptId.hashCode()));
        result = ((result* 31)+((this.issuanceDate == null)? 0 :this.issuanceDate.hashCode()));
        result = ((result* 31)+((this.context == null)? 0 :this.context.hashCode()));
        result = ((result* 31)+((this.credentialId == null)? 0 :this.credentialId.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.issuer == null)? 0 :this.issuer.hashCode()));
        result = ((result* 31)+((this.expirationDate == null)? 0 :this.expirationDate.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt110) == false) {
            return false;
        }
        Cpt110 rhs = ((Cpt110) other);
        return ((((((((this.cptId == rhs.cptId)||((this.cptId!= null)&&this.cptId.equals(rhs.cptId)))&&((this.issuanceDate == rhs.issuanceDate)||((this.issuanceDate!= null)&&this.issuanceDate.equals(rhs.issuanceDate))))&&((this.context == rhs.context)||((this.context!= null)&&this.context.equals(rhs.context))))&&((this.credentialId == rhs.credentialId)||((this.credentialId!= null)&&this.credentialId.equals(rhs.credentialId))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.issuer == rhs.issuer)||((this.issuer!= null)&&this.issuer.equals(rhs.issuer))))&&((this.expirationDate == rhs.expirationDate)||((this.expirationDate!= null)&&this.expirationDate.equals(rhs.expirationDate))));
    }

}
