

package com.webank.weid.protocol.cpt.old;

import com.webank.weid.protocol.base.Credential;
import java.util.List;
import lombok.Data;

/**
 * Multiple signature to a Credential.
 *
 * @author chaoxinhu 2019.8
 */

@Data
//@Attributes(title = "Embedded Signature", description = "Embedded Signature object for multi-sign")
public class Cpt106 {

//    @Attributes(required = true, description = "Original credential list to be signed")
    List<Credential> credentialList;
}
