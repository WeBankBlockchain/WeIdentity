package com.webank.weid.protocol.cpt;

import com.webank.weid.protocol.base.Challenge;
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
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "challenge",
    "id",
    "proof"
})
@Generated("jsonschema2pojo")
@Data
public class Cpt103 {

    /**
     * The challenge
     * (Required)
     * 
     */
    @JsonProperty("challenge")
    @JsonPropertyDescription("The challenge")
    private Challenge challenge;
    /**
     * The entity's weidentity did
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("The entity's weidentity did")
    private String id;
    /**
     * The proof
     * (Required)
     * 
     */
    @JsonProperty("proof")
    @JsonPropertyDescription("The proof")
    private String proof;
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
        sb.append(Cpt103 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("challenge");
        sb.append('=');
        sb.append(((this.challenge == null)?"<null>":this.challenge));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("proof");
        sb.append('=');
        sb.append(((this.proof == null)?"<null>":this.proof));
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
        result = ((result* 31)+((this.challenge == null)? 0 :this.challenge.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.proof == null)? 0 :this.proof.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt103) == false) {
            return false;
        }
        Cpt103 rhs = ((Cpt103) other);
        return (((((this.challenge == rhs.challenge)||((this.challenge!= null)&&this.challenge.equals(rhs.challenge)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.proof == rhs.proof)||((this.proof!= null)&&this.proof.equals(rhs.proof))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
