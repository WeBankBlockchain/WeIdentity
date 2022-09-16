

package com.webank.weid.suite.transportation.pdf.protocol;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.suite.transportation.json.protocol.JsonBaseData;


@Getter
@Setter
public class PdfAttributeInfo extends JsonBaseData {

    /**
     * PDF模板id.
     */
    private String templateId;


}
