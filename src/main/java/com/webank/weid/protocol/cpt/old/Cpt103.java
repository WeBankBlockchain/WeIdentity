

package com.webank.weid.protocol.cpt.old;

import com.webank.weid.protocol.base.Challenge;
import lombok.Data;

/**
 * Credential for authentication. The answer to meet the challenge. We package the answer into a
 * Credential(CPT104) so the verifier can verify this answer.
 *
 * @author Created by Junqi Zhang on 2019/4/9.
 */
@Data
//@Attributes(title = "Authentication Answer", description = "Answer to meet the challenge")
public class Cpt103 {

//    @Attributes(required = true, description = "The entity's weidentity did")
    private String id;
//    @Attributes(required = true, description = "The challenge")
    private Challenge challenge;
//    @Attributes(required = true, description = "The proof")
    private String proof;
}
