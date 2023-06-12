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
 * Academic Degree Certificate: Academic degree certificate from authorized
 * universities or educational institutions
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "major",
        "issuanceDate",
        "degree",
        "degreeType",
        "name",
        "gender",
        "birthDate",
        "issuerName"
})
@Generated("jsonschema2pojo")
@Data
public class Cpt113 {
    /*
     * Certicate atrribute
     */

    @JsonProperty("id")
    @JsonPropertyDescription("Id of this certificate")
    private String id;

    @JsonProperty("issuanceDate")
    @JsonPropertyDescription("IssuanceDate of this certificate")
    private Long issuanceDate;

    @JsonProperty("major")
    @JsonPropertyDescription("Major of this certificate")
    private String major;

    @JsonProperty("degree")
    @JsonPropertyDescription("Degree")
    private String degree;

    @JsonProperty("degreeType")
    @JsonPropertyDescription("Degree type")
    private String degreeType;

    /*
     * Possessor attribute
     */

    @JsonProperty("name")
    @JsonPropertyDescription("Possessor's name of this certificate")
    private String name;

    @JsonProperty("gender")
    @JsonPropertyDescription("Gender of the Possessor")
    private String gender;

    @JsonProperty("birthDate")
    @JsonPropertyDescription("BirthDate of the Possessor")
    private Long birthDate;

    /*
     * issuer attribute
     */

    @JsonProperty("issuerName")
    @JsonPropertyDescription("Issuer's name of this certificate")
    private String issuerName;

}
