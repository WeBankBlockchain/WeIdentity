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

package com.webank.weid.service.impl.engine;

import java.util.List;

import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * @author tonychen 2019年6月25日
 *
 */
public interface AuthorityIssuerServiceEngine {

	public ResponseData<Boolean> addAuthorityIssuer(RegisterAuthorityIssuerArgs args);
	
	public ResponseData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args);
	
	public ResponseData<Boolean> isAuthorityIssuer(String address);
	
	public ResponseData<AuthorityIssuer> getAuthorityIssuerInfoNonAccValue(String weId);
	
	public List<String> getAuthorityIssuerAddressList(Integer index, Integer num);
	
	public ResponseData<Boolean> removeIssuer(String issuerType, String issuerAddress);
	
	public ResponseData<Boolean> isSpecificTypeIssuer(String issuerType, String address);
	
	public ResponseData<List<String>> getSpecificTypeIssuerList(String issuerType,Integer index,Integer num);
	
	public ResponseData<Boolean>  registerIssuerType(String issuerType);
	
	public ResponseData<Boolean> addIssuer(String issuerType, String issuerAddress);
}
