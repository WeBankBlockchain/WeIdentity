

package com.webank.weid.protocol.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.util.DataToolUtils;

/**
 * The base data structure to handle WeIdentity DID Document info.
 *
 * @author afeexian 2022.8.29
 */
@Data
public class WeIdDocument implements JsonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(WeIdDocument.class);

    /**
     *  the serialVersionUID.
     */
    private static final long serialVersionUID = 411522771907189878L;

    /**
     * Required: The id.
     */
    private String id;

    /**
     * Required: The authentication list.
     */
    private List<AuthenticationProperty> authentication = new ArrayList<>();

    /**
     * Required: The service list.
     */
    private List<ServiceProperty> service = new ArrayList<>();
    
    @Override
    public String toJson() {
        return DataToolUtils.addTagFromToJson(DataToolUtils.serialize(this));
    }
   
    /**
     * create WeIdDocument with JSON String.
     * @param weIdDocumentJson the weIdDocument JSON String
     * @return WeIdDocument
     */
    public static WeIdDocument fromJson(String weIdDocumentJson) {
        if (StringUtils.isBlank(weIdDocumentJson)) {
            logger.error("create WeIdDocument with JSON String failed, "
                + "the WeIdDocument JSON String is null");
            throw new DataTypeCastException("the WeIdDocument JSON String is null.");
        }
        String weIdDocumentString = weIdDocumentJson;
        if (DataToolUtils.isValidFromToJson(weIdDocumentJson)) {
            weIdDocumentString = DataToolUtils.removeTagFromToJson(weIdDocumentJson);
        }
        return DataToolUtils.deserialize(weIdDocumentString, WeIdDocument.class);
    }

    /**
     * create WeIdDocument with document from weid-blockchain.
     * @param document the weIdDocument JSON String
     * @return WeIdDocument
     */
    public static WeIdDocument fromBlockChain(com.webank.weid.blockchain.protocol.base.WeIdDocument document) {
        WeIdDocument weIdDocument = new WeIdDocument();
        weIdDocument.setId(document.getId());
        List<AuthenticationProperty> authenticationList = new ArrayList<>();
        for(com.webank.weid.blockchain.protocol.base.AuthenticationProperty authentication : document.getAuthentication()){
            AuthenticationProperty authenticationProperty = AuthenticationProperty.fromBlockChain(authentication);
            authenticationList.add(authenticationProperty);
        }
        weIdDocument.setAuthentication(authenticationList);
        List<ServiceProperty> serviceList = new ArrayList<>();
        for(com.webank.weid.blockchain.protocol.base.ServiceProperty service : document.getService()){
            ServiceProperty serviceProperty = ServiceProperty.fromBlockChain(service);
            serviceList.add(serviceProperty);
        }
        weIdDocument.setService(serviceList);
        return weIdDocument;
    }

    /**
     * change WeIdDocument to document for weid-blockchain.
     * @param document the weIdDocument JSON String
     * @return WeIdDocument
     */
    public static com.webank.weid.blockchain.protocol.base.WeIdDocument toBlockChain(WeIdDocument document) {
        com.webank.weid.blockchain.protocol.base.WeIdDocument weIdDocument = new com.webank.weid.blockchain.protocol.base.WeIdDocument();
        weIdDocument.setId(document.getId());
        List<com.webank.weid.blockchain.protocol.base.AuthenticationProperty> authenticationList = new ArrayList<>();
        for(AuthenticationProperty authentication : document.getAuthentication()){
            com.webank.weid.blockchain.protocol.base.AuthenticationProperty authenticationProperty = AuthenticationProperty.toBlockChain(authentication);
            authenticationList.add(authenticationProperty);
        }
        weIdDocument.setAuthentication(authenticationList);
        List<com.webank.weid.blockchain.protocol.base.ServiceProperty> serviceList = new ArrayList<>();
        for(ServiceProperty service : document.getService()){
            com.webank.weid.blockchain.protocol.base.ServiceProperty serviceProperty = ServiceProperty.toBlockChain(service);
            serviceList.add(serviceProperty);
        }
        weIdDocument.setService(serviceList);
        return weIdDocument;
    }
}
