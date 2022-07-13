

package com.webank.weid.protocol.cpt.old;

//import com.github.reinert.jjschema.Attributes;
import lombok.Data;

/**
 * the CPT for test.
 * @author Created by Junqi Zhang on 2019/4/3.
 */
@Data
//@Attributes(title = "test CPT", description = "Reserved CPT 11Salt")
public class Cpt11Salt {

////    @Attributes(required = true, description = "CPT ID", minimum = 1)
    String cptId;

////    @Attributes(required = true, description = "User ID")
    String userId;

////    @Attributes(required = true, description = "User Name", maxLength = 30)
    String userName;
}
