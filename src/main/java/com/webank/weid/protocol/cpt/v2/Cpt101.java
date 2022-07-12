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
 * Data Authorization Token
 * <p>
 * Authorize data between WeIDs via the exposed Service Endpoint
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "duration",
    "fromWeId",
    "resourceId",
    "serviceUrl",
    "toWeId"
})
@Generated("jsonschema2pojo")
public class Cpt101 {

    /**
     * Authorize to this WeID
     * (Required)
     * 
     */
    @JsonProperty("duration")
    @JsonPropertyDescription("Authorize to this WeID")
    private Integer duration;
    /**
     * Authorize from this WeID
     * (Required)
     * 
     */
    @JsonProperty("fromWeId")
    @JsonPropertyDescription("Authorize from this WeID")
    private String fromWeId;
    /**
     * Authorized Resource ID
     * (Required)
     * 
     */
    @JsonProperty("resourceId")
    @JsonPropertyDescription("Authorized Resource ID")
    private String resourceId;
    /**
     * Service Endpoint URL
     * (Required)
     * 
     */
    @JsonProperty("serviceUrl")
    @JsonPropertyDescription("Service Endpoint URL")
    private String serviceUrl;
    /**
     * Duration of Validity in seconds
     * (Required)
     * 
     */
    @JsonProperty("toWeId")
    @JsonPropertyDescription("Duration of Validity in seconds")
    private String toWeId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Authorize to this WeID
     * (Required)
     * 
     */
    @JsonProperty("duration")
    public Integer getDuration() {
        return duration;
    }

    /**
     * Authorize to this WeID
     * (Required)
     * 
     */
    @JsonProperty("duration")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Authorize from this WeID
     * (Required)
     * 
     */
    @JsonProperty("fromWeId")
    public String getFromWeId() {
        return fromWeId;
    }

    /**
     * Authorize from this WeID
     * (Required)
     * 
     */
    @JsonProperty("fromWeId")
    public void setFromWeId(String fromWeId) {
        this.fromWeId = fromWeId;
    }

    /**
     * Authorized Resource ID
     * (Required)
     * 
     */
    @JsonProperty("resourceId")
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Authorized Resource ID
     * (Required)
     * 
     */
    @JsonProperty("resourceId")
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Service Endpoint URL
     * (Required)
     * 
     */
    @JsonProperty("serviceUrl")
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Service Endpoint URL
     * (Required)
     * 
     */
    @JsonProperty("serviceUrl")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Duration of Validity in seconds
     * (Required)
     * 
     */
    @JsonProperty("toWeId")
    public String getToWeId() {
        return toWeId;
    }

    /**
     * Duration of Validity in seconds
     * (Required)
     * 
     */
    @JsonProperty("toWeId")
    public void setToWeId(String toWeId) {
        this.toWeId = toWeId;
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
        sb.append(Cpt101 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("fromWeId");
        sb.append('=');
        sb.append(((this.fromWeId == null)?"<null>":this.fromWeId));
        sb.append(',');
        sb.append("resourceId");
        sb.append('=');
        sb.append(((this.resourceId == null)?"<null>":this.resourceId));
        sb.append(',');
        sb.append("serviceUrl");
        sb.append('=');
        sb.append(((this.serviceUrl == null)?"<null>":this.serviceUrl));
        sb.append(',');
        sb.append("toWeId");
        sb.append('=');
        sb.append(((this.toWeId == null)?"<null>":this.toWeId));
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
        result = ((result* 31)+((this.duration == null)? 0 :this.duration.hashCode()));
        result = ((result* 31)+((this.fromWeId == null)? 0 :this.fromWeId.hashCode()));
        result = ((result* 31)+((this.resourceId == null)? 0 :this.resourceId.hashCode()));
        result = ((result* 31)+((this.toWeId == null)? 0 :this.toWeId.hashCode()));
        result = ((result* 31)+((this.serviceUrl == null)? 0 :this.serviceUrl.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt101) == false) {
            return false;
        }
        Cpt101 rhs = ((Cpt101) other);
        return (((((((this.duration == rhs.duration)||((this.duration!= null)&&this.duration.equals(rhs.duration)))&&((this.fromWeId == rhs.fromWeId)||((this.fromWeId!= null)&&this.fromWeId.equals(rhs.fromWeId))))&&((this.resourceId == rhs.resourceId)||((this.resourceId!= null)&&this.resourceId.equals(rhs.resourceId))))&&((this.toWeId == rhs.toWeId)||((this.toWeId!= null)&&this.toWeId.equals(rhs.toWeId))))&&((this.serviceUrl == rhs.serviceUrl)||((this.serviceUrl!= null)&&this.serviceUrl.equals(rhs.serviceUrl))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
