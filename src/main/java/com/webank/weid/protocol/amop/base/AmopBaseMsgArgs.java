/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.protocol.amop.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.webank.weid.annoation.BlockChainDto;
import com.webank.weid.annoation.BlockChainDto.BindTypeEnum;
import com.webank.weid.protocol.base.Version;
import com.webank.weid.protocol.inf.IArgs;

/**
 * Created by junqizhang on 01/06/2017.
 */
@Data
@BlockChainDto(bindType = BindTypeEnum.Object)
@EqualsAndHashCode(callSuper = false)
public class AmopBaseMsgArgs implements IArgs {

    /**
     * sdk functions version.
     */
    protected Version version;

    /**
     * 消息id，用于链上链下消息去重.
     */
    protected String messageId;

    /**
     * 来源AmopId.
     */
    protected String fromAmopId;

    /**
     * 目的AmopId.
     */
    protected String toAmopId;
    
    /**
     * 业务类型.
     */
    protected String serviceType;
    
    /**
     * 通道编号.
     */
    protected String channelId;
}
