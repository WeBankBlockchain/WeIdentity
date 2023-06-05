

package com.webank.weid.protocol.cpt.old;

import java.util.List;
import lombok.Data;

//import com.github.reinert.jjschema.Attributes;

/**
 * the CPT for test.
 * @author Created by Junqi Zhang on 2019/4/3.
 */
@Data
//@Attributes(title = "test CPT", description = "Reserved CPT 11")
public class Cpt11 {

////    @Attributes(required = true, description = "CPT ID", minimum = 1)
    private Integer cptId;
////    @Attributes(required = true, description = "User ID")
    private String userId;
////    @Attributes(required = true, description = "User Name", maxLength = 30)
    private String userName;
////    @Attributes(required = true, description = "Registered Tags", minItems = 1)
    private List<String> tags;
////    @Attributes(required = true, description = "Gender", enums = {"MALE", "FEMALE"})
    private String gender;
}
