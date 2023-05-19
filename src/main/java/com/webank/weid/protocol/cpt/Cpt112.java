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
 * User request issuer to sign credential
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "major",
    "issuanceDate",
    "name",
    "gender",
    "birthDate",
    "issuerName"
})
@Generated("jsonschema2pojo")
@Data
public class Cpt112 {
  /*
   * Certicate atrribute
   */

  @JsonProperty("id")
  @JsonPropertyDescription("Id of this diploma")
  private String id;

  @JsonProperty("major")
  @JsonPropertyDescription("Major of this diploma")
  private String major;

  @JsonProperty("issuanceDate")
  @JsonPropertyDescription("IssuanceDate of this diploma")
  private Long issuanceDate;

  /*
   * Possessor attribute
   */

  @JsonProperty("name")
  @JsonPropertyDescription("Name of this possessor")
  private String name;

  @JsonProperty("gender")
  @JsonPropertyDescription("Gender of the possessor")
  private String gender;

  @JsonProperty("birthDate")
  @JsonPropertyDescription("BirthDate of the possessor")
  private Long birthDate;

  /*
   * issuer attribute
   */

  @JsonProperty("issuerName")
  @JsonPropertyDescription("Issuer's name of this diploma")
  private String issuerName;

}
