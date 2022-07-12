package com.webank.weid.protocol.cpt;

import com.webank.weid.protocol.base.Credential;
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
 * Embedded Signature
 * <p>
 * Embedded Signature object for multi-sign
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "credentialList"
})
@Generated("jsonschema2pojo")
public class Cpt106 {

    /**
     * Original credential list to be signed
     * (Required)
     * 
     */
    @JsonProperty("credentialList")
    @JsonPropertyDescription("Original credential list to be signed")
    private List<Credential> credentialList = new ArrayList<Credential>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Original credential list to be signed
     * (Required)
     * 
     */
    @JsonProperty("credentialList")
    public List<Credential> getCredentialList() {
        return credentialList;
    }

    /**
     * Original credential list to be signed
     * (Required)
     * 
     */
    @JsonProperty("credentialList")
    public void setCredentialList(List<Credential> credentialList) {
        this.credentialList = credentialList;
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
        sb.append(Cpt106 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("credentialList");
        sb.append('=');
        sb.append(((this.credentialList == null)?"<null>":this.credentialList));
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
        result = ((result* 31)+((this.credentialList == null)? 0 :this.credentialList.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt106) == false) {
            return false;
        }
        Cpt106 rhs = ((Cpt106) other);
        return (((this.credentialList == rhs.credentialList)||((this.credentialList!= null)&&this.credentialList.equals(rhs.credentialList)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
