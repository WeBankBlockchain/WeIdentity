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
     * 通讯Id.
     */
    private String amopId;
   
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
   
    private EncodeData(String id, String amopId, String data) {
        this.id = id;
        this.amopId = amopId;
        this.data = data;
    }
    
    /**
     * 构建编解码对象.
     * @param amopId 协议所属机构
     * @param id 数据编号
     * @param data 需要编解码数据
     * @param verifiers 协议数据指定用户
     */
    public EncodeData(
        String id,
        String amopId,
        String data,
        List<String> verifiers
    ) {
        this(id, amopId, data);
        this.verifiers = verifiers;
    }
    
    /**
     * 构建编解码对象.
     * @param amopId 协议所属机构
     * @param id 数据编号
     * @param data 需要编解码数据
     * @param weIdAuthentication 解码身份信息
     */
    public EncodeData(
        String id, 
        String amopId, 
        String data, 
        WeIdAuthentication weIdAuthentication
    ) {
        this(id, amopId, data);
        this.weIdAuthentication = weIdAuthentication;
    }
}
