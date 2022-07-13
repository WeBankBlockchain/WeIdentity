

package com.webank.weid.service.impl.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * AMOP common args.
 * @author tonychen 2019年4月16日
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AmopCommonArgs extends AmopBaseMsgArgs {

    /**
     * 任意包体.
     */
    private String message;
}
