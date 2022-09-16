

package com.webank.weid.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BlockChainDto {

    /**
     * object or list.
     *
     * @return type Enum
     */
    public BindTypeEnum bindType();

    enum BindTypeEnum {
        List, Object;
    }
}
