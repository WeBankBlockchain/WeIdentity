/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.protocol.base;

import java.security.SecureRandom;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.inf.RawSerializer;
import com.webank.weid.util.DataToolUtils;

/**
 * Created by Junqi Zhang on 2019/4/9.
 */
@Getter
@Setter
public class Challenge extends Version implements RawSerializer {

    private static final Logger logger = LoggerFactory.getLogger(Challenge.class);

    /**
     * the serialVersionUID.
     */
    private static final long serialVersionUID = 3783172794625195471L;

    /**
     * Specify who you want to challenge.
     */
    private String weId;

    /**
     * Specify a random alphanumeric nonce and WeIdentity DID owner will sign a credential which
     * include the nonce to prove the ownership of this WeIdentity DID. The relying party should
     * include a random alphanumeric (i.e. nonce) in the challenge, to prevent replay attacks. This
     * is also known as dynamic challenge.
     *
     */
    private String nonce;

    /**
     * Factory function which can help to create a brand new challenge object.
     *
     * @param userWeId Specify who you want to challenge. Most of the time you need to pass user's
     *     weId.
     * @param seed the verify seed
     * @return Challenge
     */
    public static Challenge create(String userWeId, String seed) {

        SecureRandom random = new SecureRandom();
        String randomSeed = seed + DataToolUtils.getUuId32();
        random.setSeed(randomSeed.getBytes());
        byte[] bytes = new byte[15];
        random.nextBytes(bytes);
        String nonce = Base64.encodeBase64String(bytes);

        Challenge challenge = new Challenge();
        challenge.setNonce(nonce);
        challenge.setWeId(userWeId);
        return challenge;
    }
    
    @Override
    public String toJson() {
        return DataToolUtils.addTagFromToJson(DataToolUtils.serialize(this));
    }
    
    /**
     * create Challenge with JSON String.
     * @param challengeJson the challenge JSON String
     * @return Challenge
     */
    public static Challenge fromJson(String challengeJson) {
        if (StringUtils.isBlank(challengeJson)) {
            logger.error("create Challenge with JSON String failed, "
                + "the Challenge JSON String is null");
            throw new DataTypeCastException("the Challenge JSON String is null.");
        }
        String challengeString = challengeJson;
        if (DataToolUtils.isValidFromToJson(challengeJson)) {
            challengeString = DataToolUtils.removeTagFromToJson(challengeJson);
        }
        return DataToolUtils.deserialize(challengeString, Challenge.class);
    }
    
    @Override
    public String toRawData() {
        return this.nonce;
    }

    private Challenge() {
        this.setVersion(1);
    }
}
