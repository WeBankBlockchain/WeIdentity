package com.webank.weid.protocol.cpt;

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
import lombok.Data;


/**
 * API Endpoint
 * <p>
 * API Endpoint address disclosure
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "argType",
    "description",
    "endpointName",
    "hostport",
    "id",
    "version"
})
@Generated("jsonschema2pojo")
@Data
public class Cpt105 {

    /**
     * Argument types in sequence
     * (Required)
     * 
     */
    @JsonProperty("argType")
    @JsonPropertyDescription("Argument types in sequence")
    private List<String> argType = new ArrayList<String>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("description")
    private String description;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpointName")
    private String endpointName;
    /**
     * Network host and port
     * (Required)
     * 
     */
    @JsonProperty("hostport")
    @JsonPropertyDescription("Network host and port")
    private String hostport;
    /**
     * Owner WeIdentity DID
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("Owner WeIdentity DID")
    private String id;
    /**
     * API Version
     * (Required)
     * 
     */
    @JsonProperty("version")
    @JsonPropertyDescription("API Version")
    private String version;
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
        sb.append(Cpt105 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("argType");
        sb.append('=');
        sb.append(((this.argType == null)?"<null>":this.argType));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("endpointName");
        sb.append('=');
        sb.append(((this.endpointName == null)?"<null>":this.endpointName));
        sb.append(',');
        sb.append("hostport");
        sb.append('=');
        sb.append(((this.hostport == null)?"<null>":this.hostport));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null)?"<null>":this.version));
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
        result = ((result* 31)+((this.argType == null)? 0 :this.argType.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.endpointName == null)? 0 :this.endpointName.hashCode()));
        result = ((result* 31)+((this.hostport == null)? 0 :this.hostport.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.version == null)? 0 :this.version.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt105) == false) {
            return false;
        }
        Cpt105 rhs = ((Cpt105) other);
        return ((((((((this.argType == rhs.argType)||((this.argType!= null)&&this.argType.equals(rhs.argType)))&&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))&&((this.endpointName == rhs.endpointName)||((this.endpointName!= null)&&this.endpointName.equals(rhs.endpointName))))&&((this.hostport == rhs.hostport)||((this.hostport!= null)&&this.hostport.equals(rhs.hostport))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.version == rhs.version)||((this.version!= null)&&this.version.equals(rhs.version))));
    }

}
