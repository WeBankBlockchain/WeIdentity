

package com.webank.weid.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.request.timestamp.wesign.GetTimestampRequest;
import com.webank.weid.protocol.request.timestamp.wesign.VerifyTimestampRequest;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.timestamp.wesign.AccessTokenResponse;
import com.webank.weid.protocol.response.timestamp.wesign.GetTimestampResponse;
import com.webank.weid.protocol.response.timestamp.wesign.SignTicketResponse;
import com.webank.weid.protocol.response.timestamp.wesign.VerifyTimestampResponse;

/**
 * Util classes for trusted timestamp.
 *
 * @author chaoxinhu 2019.12
 */

public class TimestampUtils {

    private static final Logger logger = LoggerFactory.getLogger(TimestampUtils.class);

    private static String weSignAccessTokenUrl = PropertyUtils.getProperty("wesign.accessTokenUrl");
    private static String weSignTicketUrl = PropertyUtils.getProperty("wesign.signTicketUrl");
    private static String weSignTimestampUrl = PropertyUtils.getProperty("wesign.timestampUrl");
    private static String weSignAppId = PropertyUtils.getProperty("wesign.appId");
    private static String weSignSecret = PropertyUtils.getProperty("wesign.secret");
    private static final String WESIGN_EXTRAVAL_SEPARATOR = ";";

    public static final String WESIGN_AUTHORITY_NAME = "wesign";

    /**
     * Create a WeSign timestamp bundle for Credential use. Extra values as SIGN and Nonce are
     * packed with signature value.
     *
     * @param hashValue the hash value
     * @return the map used for claim
     */
    public static ResponseData<HashMap<String, Object>> createWeSignTimestamp(String hashValue) {
        if (StringUtils.isBlank(PropertyUtils.getProperty("wesign.accessTokenUrl"))) {
            logger.error("WeSign configuration not ready.");
            return new ResponseData<>(null, ErrorCode.TIMESTAMP_SERVICE_UNCONFIGURED);
        }
        String timestampValue;
        Long timestamp;
        String extra;
        try {
            String accessToken = TimestampUtils.getWeSignAccessToken();
            if (StringUtils.isBlank(accessToken)) {
                logger.error("Failed to acquire an access token.");
                return new ResponseData<>(null, ErrorCode.TIMESTAMP_SERVICE_WESIGN_ERROR);
            }
            String signTicket = TimestampUtils.getWeSignTicketString(accessToken);
            if (StringUtils.isBlank(signTicket)) {
                logger.error("Failed to acquire a sign ticket.");
                return new ResponseData<>(null, ErrorCode.TIMESTAMP_SERVICE_WESIGN_ERROR);
            }
            String nonce = TimestampUtils.generateNonce(32, 32).get(0);
            String weSignHash = TimestampUtils.getWeSignHash(hashValue);
            GetTimestampResponse getResp = TimestampUtils
                .getTimestamp(signTicket, nonce, weSignHash);
            if (getResp.getCode() != 0) {
                logger.error("Failed to acquire a valid timestamp.");
                return new ResponseData<>(null, ErrorCode.TIMESTAMP_SERVICE_WESIGN_ERROR);
            }
            timestampValue = getResp.getResult().getData().getB64TimeStamp();
            String signParam = getWeSignParam(nonce, signTicket);
            VerifyTimestampResponse verifyResp = TimestampUtils
                .verifyTimestamp(signParam, nonce, weSignHash, timestampValue);
            if (verifyResp.getCode() != 0) {
                logger.error("Failed to verify a current timestamp.");
                return new ResponseData<>(null, ErrorCode.TIMESTAMP_SERVICE_WESIGN_ERROR);
            }
            timestamp = verifyResp.getResult().getData().getSignTime().getTime();
            extra = WESIGN_EXTRAVAL_SEPARATOR + signParam + WESIGN_EXTRAVAL_SEPARATOR + nonce;
        } catch (Exception e) {
            logger.error("Error occurred during calling WeSign service: ", e);
            return new ResponseData<>(null, ErrorCode.TIMESTAMP_SERVICE_WESIGN_ERROR.getCode(),
                e.getMessage());
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("timestampAuthority", WESIGN_AUTHORITY_NAME);
        map.put("claimHash", hashValue);
        map.put("authoritySignature", timestampValue + extra);
        map.put("timestamp", timestamp);
        return new ResponseData<>(map, ErrorCode.SUCCESS);
    }

    /**
     * Verify a WeSign Timestamp value. Extra values as nonce and SIGN must be unmodified.
     *
     * @param hashValue the hash value
     * @param authoritySignature the authoritySignature with nonce and SIGN
     * @param timestamp the timestamp value
     * @return true if passed the check, false otherwise
     */
    public static ResponseData<Boolean> verifyWeSignTimestamp(
        String hashValue,
        String authoritySignature,
        Long timestamp) {
        if (StringUtils.isBlank(PropertyUtils.getProperty("wesign.accessTokenUrl"))) {
            logger.error("WeSign configuration not ready.");
            return new ResponseData<>(false, ErrorCode.TIMESTAMP_SERVICE_UNCONFIGURED);
        }
        try {
            String weSignHash = TimestampUtils.getWeSignHash(hashValue);
            String[] values = authoritySignature.split(WESIGN_EXTRAVAL_SEPARATOR);
            String timestampValue = values[0];
            String signParam = values[1];
            String nonce = values[2];
            VerifyTimestampResponse verifyResp = TimestampUtils
                .verifyTimestamp(signParam, nonce, weSignHash, timestampValue);
            if (verifyResp.getCode() != 0) {
                return new ResponseData<>(false, ErrorCode.TIMESTAMP_VERIFICATION_FAILED);
            }
            Long verifiedTimestamp = verifyResp.getResult().getData().getSignTime().getTime();
            if (!verifiedTimestamp.equals(timestamp)) {
                return new ResponseData<>(false, ErrorCode.TIMESTAMP_VERIFICATION_FAILED);
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("Error occurred during processing authority signature: ", e);
            return new ResponseData<>(false, ErrorCode.TIMESTAMP_SERVICE_BASE_ERROR);
        }
    }

    /**
     * Get WeSign access token.
     *
     * @return access token
     * @throws Exception any exception
     */
    public static String getWeSignAccessToken() throws Exception {

        AccessTokenResponse accessTokenResponse = getWeSignAccessTokenUrl();
        int code = accessTokenResponse.getCode();
        String msg = accessTokenResponse.getMsg();
        if (0 != code) {
            logger.error(
                "Error occurred during getting access token. code: " + code + ", msg: " + msg);
            return null;
        }
        return accessTokenResponse.getAccess_token();
    }

    private static AccessTokenResponse getWeSignAccessTokenUrl() throws Exception {
        return getWeSignAccessTokenUrl(
            weSignAccessTokenUrl
                + "?app_id={0}&secret={1}&grant_type=client_credential&version=1.0.0",
            weSignAppId,
            weSignSecret
        );
    }

    private static AccessTokenResponse getWeSignAccessTokenUrl(String urlExp, String appId,
        String secret) throws Exception {

        logger.info("request arg::urlExp: {}, appId:: {}, secret: {}", urlExp, appId, secret);
        if (StringUtils.isBlank(urlExp) || StringUtils.isBlank(appId) || StringUtils
            .isBlank(secret)) {
            return null;
        }
        try {
            String accessTokenUrl = MessageFormat.format(urlExp, appId, secret);
            logger.debug("[getWeSignAccessTokenUrl]:{}", accessTokenUrl);

            String responseData = HttpClient.doGet(accessTokenUrl, true);
            logger.debug("[getWeSignAccessTokenUrl]: response: {}", responseData);

            if (null == responseData) {
                return null;
            }
            return DataToolUtils.deserialize(responseData, AccessTokenResponse.class);
        } catch (Exception e) {
            logger.error("Error occurred during getting access token: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get WeSign Sign ticket.
     *
     * @param accessToken the access token
     * @return the sign ticket
     * @throws Exception any exception
     */
    public static String getWeSignTicketString(String accessToken) throws Exception {

        SignTicketResponse signTicketResponse = getSignTicket(accessToken);
        int code = signTicketResponse.getCode();
        String msg = signTicketResponse.getMsg();
        if (0 != code) {
            logger.error(
                "Error occurred during getting sign ticket. code: " + code + ", msg: " + msg);
            return null;
        }
        return signTicketResponse.getTickets().get(0).getValue();
    }

    private static SignTicketResponse getSignTicket(String accessToken) throws Exception {

        return getSignTicket(
            weSignTicketUrl + "?app_id={0}&access_token={1}&type=SIGN&version=1.0.0",
            weSignAppId,
            accessToken
        );
    }

    private static SignTicketResponse getSignTicket(String urlExp, String appId,
        String accessToken) throws Exception {

        logger.info("request arg::urlExp: {}, appId:: {}, accessToken: {}", urlExp, appId,
            accessToken);
        if (StringUtils.isBlank(urlExp) || StringUtils.isBlank(appId) || StringUtils
            .isBlank(accessToken)) {
            return null;
        }
        try {
            String accessTokenUrl = MessageFormat.format(urlExp, appId, accessToken);
            logger.debug("[getSignTicket]:{}", accessTokenUrl);

            String responseData = HttpClient.doGet(accessTokenUrl, true);
            logger.info("[getSignTicket]: response: {}", responseData);

            if (StringUtils.isBlank(responseData)) {
                return null;
            }
            return DataToolUtils.deserialize(responseData, SignTicketResponse.class);
        } catch (Exception e) {
            logger.error("Error occurred during getting sign ticket: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get timestamp response.
     *
     * @param signTicket the sign ticket
     * @param nonce the nonce value
     * @param hashValue the hash value
     * @return response
     * @throws Exception any exception
     */
    public static GetTimestampResponse getTimestamp(String signTicket, String nonce,
        String hashValue) throws Exception {
        return getTimestamp(
            weSignTimestampUrl,
            weSignAppId,
            nonce,
            getWeSignParam(nonce, signTicket),
            hashValue
        );
    }

    private static GetTimestampResponse getTimestamp(String urlExp, String appId, String nonce,
        String signParam,
        String hashValue) throws Exception {

        if (StringUtils.isBlank(urlExp) || StringUtils.isBlank(appId) || StringUtils
            .isBlank(hashValue) || StringUtils.isBlank(nonce) || StringUtils.isBlank(signParam)) {
            return null;
        }
        try {
            String accessTokenUrl = urlExp;
            GetTimestampRequest req = new GetTimestampRequest();
            req.setNonce(nonce);
            req.setPlainHash(hashValue);
            req.setWebankAppId(appId);
            req.setSign(signParam);
            String responseData = HttpClient.doPost(accessTokenUrl, req, true);
            logger.info("[getTimestamp]: response: {}", responseData);

            if (StringUtils.isBlank(responseData)) {
                return null;
            }
            return DataToolUtils.deserialize(responseData, GetTimestampResponse.class);
        } catch (Exception e) {
            logger.error("Error occurred during getting sign ticket: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verify a timestamp.
     *
     * @param signParam the sign param
     * @param nonce the nonce value
     * @param hashValue the hash value
     * @param timestampValue the timestamp value
     * @return response
     * @throws Exception any exception
     */
    public static VerifyTimestampResponse verifyTimestamp(String signParam, String nonce,
        String hashValue, String timestampValue) throws Exception {
        return verifyTimestamp(
            weSignTimestampUrl,
            weSignAppId,
            nonce,
            signParam,
            hashValue,
            timestampValue
        );
    }

    private static VerifyTimestampResponse verifyTimestamp(String urlExp, String appId,
        String nonce,
        String signParam, String hashValue, String timestampValue) throws Exception {

        if (StringUtils.isBlank(urlExp) || StringUtils.isBlank(appId) || StringUtils
            .isBlank(hashValue) || StringUtils.isBlank(nonce) || StringUtils.isBlank(signParam)) {
            return null;
        }
        try {
            String accessTokenUrl = urlExp;
            VerifyTimestampRequest req = new VerifyTimestampRequest();
            req.setNonce(nonce);
            req.setPlainHash(hashValue);
            req.setWebankAppId(appId);
            req.setSign(signParam);
            req.setB64TimeStamp(timestampValue);
            String responseData = HttpClient.doPost(accessTokenUrl, req, true);
            logger.info("[getSignTicket]: response: {}", responseData);

            if (StringUtils.isBlank(responseData)) {
                return null;
            }
            return DataToolUtils.deserialize(responseData, VerifyTimestampResponse.class);
        } catch (Exception e) {
            logger.error("Error occurred during getting sign ticket: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Generate an arbitrary length nonce.
     *
     * @param length length
     * @param num number of nonce values
     * @return nonce list
     */
    public static List<String> generateNonce(int length, long num) {

        List<String> results = new ArrayList<String>();

        for (int j = 0; j < num; j++) {
            String val = "";

            Random random = new Random();
            for (int i = 0; i < length; i++) {
                String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
                if ("char".equalsIgnoreCase(charOrNum)) {
                    int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                    val += (char) (choice + random.nextInt(26));
                } else if ("num".equalsIgnoreCase(charOrNum)) {
                    val += String.valueOf(random.nextInt(10));
                }
            }
            val = val.toLowerCase();
            if (results.contains(val)) {
                continue;
            } else {
                results.add(val);
            }
        }
        return results;
    }

    private static String sha1Sign(List<String> values, String ticket) {
        if (values == null) {
            return StringUtils.EMPTY;
        }

        values.removeAll(Collections.singleton(null));
        values.add(ticket);

        Collections.sort(values);

        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }
        return Hashing.sha1().hashString(sb, Charsets.UTF_8).toString().toUpperCase();
    }

    /**
     * Get WeSign specific SIGN parameter.
     *
     * @param nonce the nonce value
     * @param ticket the sign ticket value
     * @return SIGN
     */
    public static String getWeSignParam(String nonce, String ticket) {
        ArrayList<String> list = new ArrayList<>();
        list.add(weSignAppId);
        list.add(nonce);
        list.add("1.0.0");
        return sha1Sign(list, ticket);
    }

    /**
     * Get WeSign specific Hash based on SHA1.
     *
     * @param originalText original text
     * @return Hash value
     */
    public static String getWeSignHash(String originalText) {
        return Hex
            .toHexString(Hashing.sha1().hashString(originalText, Charsets.UTF_8).asBytes());
    }
}
