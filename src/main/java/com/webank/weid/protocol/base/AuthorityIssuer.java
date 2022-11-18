

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.util.DateUtils;

/**
 * The base data structure to handle Authority Issuer info.
 *
 * @author chaoxinhu 2018.10
 */
@Data
public class AuthorityIssuer {

    /**
     * Required: The WeIdentity DID of the Authority Issuer.
     */
    private String weId;

    /**
     * Required: The organization name of the Authority Issuer.
     */
    private String name;

    /**
     * Required: The create date of the Authority Issuer, in timestamp (Long) format.
     */
    private Long created;

    /**
     * Required: The accumulator value of the Authority Issuer.
     */
    private String accValue;

    /**
     * Optional: The description of this Authority Issuer.
     */
    private String description;

    /**
     * Optional: The extra String type information stored on chain.
     */
    private List<String> extraStr32;

    /**
     * Optional: The extra Integer type information stored on chain.
     */
    private List<Integer> extraInt;

    /**
     * Optional: whether this authority issuer is recognized by admin/committee, or not.
     */
    private boolean recognized = false;

    /**
     * Constructor with initialized values.
     *
     * @param weId the WeID
     * @param name the name
     * @param accValue the accumulator value (currently unused)
     * @param description the description
     * @param extraStr32 the extra String list
     * @param extraInt the extra Integer list
     */
    public AuthorityIssuer(
        String weId,
        String name,
        String accValue,
        String description,
        List<String> extraStr32,
        List<Integer> extraInt
    ) {
        this.weId = weId;
        this.name = name;
        this.created = DateUtils.getNoMillisecondTimeStamp();
        this.accValue = accValue;
        this.description = StringUtils.isEmpty(description) ? StringUtils.EMPTY : description;
        this.extraStr32 = extraStr32 == null ? new ArrayList<>() : extraStr32;
        this.extraInt = extraInt == null ? new ArrayList<>() : extraInt;
    }

    /**
     * Empty Constructor.
     */
    public AuthorityIssuer() {
        this.weId = StringUtils.EMPTY;
        this.name = StringUtils.EMPTY;
        this.created = DateUtils.getNoMillisecondTimeStamp();
        this.accValue = StringUtils.EMPTY;
        this.description = StringUtils.EMPTY;
        this.extraStr32 = new ArrayList<>();
        this.extraInt = new ArrayList<>();
    }

    public static com.webank.weid.blockchain.protocol.base.AuthorityIssuer toBlockChain(AuthorityIssuer issuer) {
        com.webank.weid.blockchain.protocol.base.AuthorityIssuer authorityIssuer = new com.webank.weid.blockchain.protocol.base.AuthorityIssuer();
        authorityIssuer.setWeId(issuer.getWeId());
        authorityIssuer.setName(issuer.getName());
        authorityIssuer.setCreated(issuer.getCreated());
        authorityIssuer.setAccValue(issuer.getAccValue());
        authorityIssuer.setDescription(issuer.getDescription());
        authorityIssuer.setExtraInt(issuer.getExtraInt());
        authorityIssuer.setExtraStr32(issuer.getExtraStr32());
        authorityIssuer.setRecognized(issuer.isRecognized());
        return authorityIssuer;
    }

    public static AuthorityIssuer fromBlockChain(com.webank.weid.blockchain.protocol.base.AuthorityIssuer issuer) {
        AuthorityIssuer authorityIssuer = new AuthorityIssuer();
        authorityIssuer.setWeId(issuer.getWeId());
        authorityIssuer.setName(issuer.getName());
        authorityIssuer.setCreated(issuer.getCreated());
        authorityIssuer.setAccValue(issuer.getAccValue());
        authorityIssuer.setDescription(issuer.getDescription());
        authorityIssuer.setExtraInt(issuer.getExtraInt());
        authorityIssuer.setExtraStr32(issuer.getExtraStr32());
        authorityIssuer.setRecognized(issuer.isRecognized());
        return authorityIssuer;
    }
}
