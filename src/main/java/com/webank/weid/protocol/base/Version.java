

package com.webank.weid.protocol.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Junqi Zhang on 2019/4/10.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Version {

    private Integer version;
}
