/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import lombok.Data;

/**
 * The base data structure for the CPT.
 *
 * @author lingfenghe
 */
@Data
public class Cpt {

    /**
     * Base info of cpt.
     */
    private CptBaseInfo cptBaseInfo = new CptBaseInfo();

    /**
     * The cpt json schema.
     */
    private String cptJsonSchema;

    /**
     * The meta data.
     */
    private MetaData metaData = new MetaData();

    /**
     * Gets the cpt id.
     *
     * @return the cpt id
     */
    public Integer getCptId() {
        return cptBaseInfo.getCptId();
    }

    /**
     * Sets the cpt id.
     *
     * @param cptId the new cpt id
     */
    public void setCptId(Integer cptId) {
        cptBaseInfo.setCptId(cptId);
    }

    /**
     * Gets the cpt version.
     *
     * @return the cpt version
     */
    public Integer getCptVersion() {
        return cptBaseInfo.getCptVersion();
    }

    /**
     * Sets the cpt version.
     *
     * @param cptVersion the new cpt version
     */
    public void setCptVersion(Integer cptVersion) {
        cptBaseInfo.setCptVersion(cptVersion);
    }

    /**
     * Gets the cpt publisher.
     *
     * @return the cpt publisher
     */
    public String getCptPublisher() {
        return metaData.getCptPublisher();
    }

    /**
     * Sets the cpt publisher.
     *
     * @param cptPublisher the new cpt publisher
     */
    public void setCptPublisher(String cptPublisher) {
        metaData.setCptPublisher(cptPublisher);
    }

    /**
     * Gets the cpt signature.
     *
     * @return the cpt signature
     */
    public String getCptSignature() {
        return metaData.getCptSignature();
    }

    /**
     * Sets the cpt signature.
     *
     * @param cptSignature the new cpt signature
     */
    public void setCptSignature(String cptSignature) {
        metaData.setCptSignature(cptSignature);
    }

    /**
     * Gets the created.
     *
     * @return the created
     */
    public long getCreated() {
        return metaData.getCreated();
    }

    /**
     * Sets the created.
     *
     * @param created the new created
     */
    public void setCreated(long created) {
        metaData.setCreated(created);
    }

    /**
     * Gets the updated.
     *
     * @return the updated
     */
    public long getUpdated() {
        return metaData.getUpdated();
    }

    /**
     * Sets the updated.
     *
     * @param updated the new updated
     */
    public void setUpdated(long updated) {
        metaData.setUpdated(updated);
    }

    /**
     * The base data structure for CPT meta data.
     */
    @Data
    public static class MetaData {

        /**
         * The weIdentity DID of the publisher who register this CPT.
         */
        private String cptPublisher;

        /**
         * The cpt signature for the weIdentity DID and json schema data in Base64.
         */
        private String cptSignature;

        /**
         * The cpt create timestamp.
         */
        private long created;

        /**
         * The cpt update timestamp.
         */
        private long updated;
    }
}
