/**
 * Copyright 2014-2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.weid.service.impl.engine.fiscov3.callback;

import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marsli
 */
public class RawTxCallbackV3 extends TransactionCallback {

    private static final Logger logger = LoggerFactory
        .getLogger(RawTxCallbackV3.class);

    private CompletableFuture<TransactionReceipt> future;

    public RawTxCallbackV3(
        CompletableFuture<TransactionReceipt> future) {
        this.future = future;
    }


    @Override
    public void onResponse(TransactionReceipt receipt) {
        logger.info("onResponse receipt {}", receipt.getTransactionHash());
        future.complete(receipt);
    }
}
