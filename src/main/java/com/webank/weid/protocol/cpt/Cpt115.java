package com.webank.weid.protocol.cpt;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

/**
 * Metadata CPT
 * <p>
 * Income Certificate: Income certificate from authorized companies
 * 
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "employBeginDate",
        "salary",
        "income",
        "issuanceDate",
        "position",
        "name",
        "issuerName"
})
@Generated("jsonschema2pojo")
@Data
public class Cpt115 {
    /*
     * Certicate atrribute
     */
    @JsonProperty("id")
    @JsonPropertyDescription("Id of this certificate")
    private String id;

    @JsonProperty("employBeginDate")
    @JsonPropertyDescription("Date to begin employing")
    private Long employBeginDate;

    @JsonProperty("salary")
    @JsonPropertyDescription("Monthly salary")
    private int salary;

    @JsonProperty("income")
    @JsonPropertyDescription("Total Income including monthly salary and all bonus according to your achievement")
    private int income;

    @JsonProperty("issuanceDate")
    @JsonPropertyDescription("IssuanceDate of this certificate")
    private Long issuanceDate;

    @JsonProperty("position")
    @JsonPropertyDescription("Position")
    private String position;

    /*
     * Possessor attribute
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Possessor's name of this certificate")
    private String name;

    /*
     * issuer attribute
     */

    @JsonProperty("issuerName")
    @JsonPropertyDescription("Name of this company(who issue this certificate)")
    private String issuerName;

}