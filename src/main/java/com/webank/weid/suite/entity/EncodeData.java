/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.entity;

import java.util.List;

import lombok.Getter;

import com.webank.weid.protocol.base.WeIdAuthentication;

/**
 * 编辑码的实体类，封装了需要编解码的数据.
 * @author v_wbgyang
 *
 */
@Getter
public class EncodeData {

    /**
     * 机构名称.
     */
    private String orgId;
   
    /**
     * 待编解码字符串.
     */
    private String data;
   
    /**
     * 待编解码字符串数据编号.
     */
    private String id;
    
    /**
     * 协议数据指定用户.
     */
    private List<String> verifiers;
    
    /**
     * 解码者身份信息.
     */
    private WeIdAuthentication weIdAuthentication;
    
    /**
     * 密文方式的过期时间.
     */
    private int expireTime;
   
    private EncodeData(String id, String orgId, String data) {
        this.id = id;
        this.orgId = orgId;
        this.data = data;
    }
    
    /**
     * 构建编解码对象.
     * @param orgId 协议所属机构
     * @param id 数据编号
     * @param data 需要编解码数据
     * @param verifiers 协议数据指定用户
     * @param expireTime 协议加密时秘钥的有效时间,单位(秒)
     */
    public EncodeData(
        String id,
        String orgId,
        String data,
        List<String> verifiers,
        int expireTime
    ) {
        this(id, orgId, data);
        this.verifiers = verifiers;
        this.expireTime = expireTime;
    }
    
    /**
     * 构建编解码对象.
     * @param orgId 协议所属机构
     * @param id 数据编号
     * @param data 需要编解码数据
     * @param weIdAuthentication 解码身份信息
     */
    public EncodeData(String id, String orgId, String data, WeIdAuthentication weIdAuthentication) {
        this(id, orgId, data);
        this.weIdAuthentication = weIdAuthentication;
    }
}
