

package com.webank.weid.protocol.base;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.inf.JsonSerializer;

/**
 * Created by Junqi Zhang on 2019/4/10.
 */
@Setter
@Getter
public class PolicyAndChallenge implements JsonSerializer {

    /**
     * the serialVersionUID.
     */
    private static final long serialVersionUID = -7730049255207201464L;

    private PresentationPolicyE presentationPolicyE;

    private Challenge challenge;
}

