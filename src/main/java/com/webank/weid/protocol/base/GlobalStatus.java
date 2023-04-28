package com.webank.weid.protocol.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.weid.blockchain.protocol.inf.JsonSerializer;
import com.webank.weid.contract.deploy.AddressProcess;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * The class of global status for running locally with database.
 *
 * @author afeexian
 */
@Data
public class GlobalStatus implements JsonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(GlobalStatus.class);

    private int authority_issuer_current_cpt_id = 1000;

    private int none_authority_issuer_current_cpt_id = 2000000;

    private int authority_issuer_current_policy_id = 1000;

    private int none_authority_issuer_current_policy_id = 2000000;

    private int presentationId = 1;

    //TODO:给文件加锁
    public static void storeStatusToFile(GlobalStatus globalStatus, String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(fileName);
            // if file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            mapper.writeValue(file, globalStatus);
        } catch (IOException e) {
            logger.error("writer file exception", e);
        }
    }

    public static GlobalStatus readStatusFromFile(String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(fileName);
            // if file does not exists, then create it
            if (file.exists()) {
                return mapper.readValue(file, GlobalStatus.class);
            }
            file.createNewFile();
            return new GlobalStatus();
        } catch (IOException e) {
            logger.error("writer file exception", e);
        }
        logger.error("readStatusFromFile() the {} does not exists.", fileName);
        return null;
    }
}
