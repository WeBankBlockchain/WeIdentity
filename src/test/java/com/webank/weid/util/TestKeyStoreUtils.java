/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.util;

import com.webank.weid.util.keystore.P12KeyStore;
import com.webank.weid.util.keystore.PEMKeyStore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author tangxianjie
 * @date 2022/09/11
 */
public class TestKeyStoreUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestKeyStoreUtils.class);

    @Test
    public void testStorePem() throws Exception {

        PEMKeyStore.storeKeyPairWithPemFormat("0xed6fa6b25315ae63b9044a58989ce20c53a87b3c8bfb1b57f064adc42984f5f9", "storePem.pem", "secp256k1");
    }

    @Test
    public void testStoreP12() {
        P12KeyStore.storeKeyPairWithP12Format("0xb1fe02c4313e4dec3b489cd054e0e3f224b3ea8e33424f573f6122efa56c44ee","1234",
                "storeP12.p12","secp256k1", "ECDSAWITHSHA1");
    }
}
