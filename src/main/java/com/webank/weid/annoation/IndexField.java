

package com.webank.weid.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexField {

    /**
     * the file name.
     * @return name
     */
    String name() default "";

    /**
     * convert null to ''.
     * @return ''
     */ 
    String nullAs() default "";

    /**
     * start from 0.
     * @return index
     */
    int index();
}
