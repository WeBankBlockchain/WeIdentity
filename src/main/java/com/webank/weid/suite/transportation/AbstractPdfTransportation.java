

package com.webank.weid.suite.transportation;

import java.util.List;

import com.webank.weid.suite.api.transportation.inf.PdfTransportation;


public abstract class AbstractPdfTransportation
    extends AbstractTransportation
    implements PdfTransportation {

    @Override
    public PdfTransportation specify(List<String> verifierWeIdList) {
        this.setVerifier(verifierWeIdList);
        return this;
    }
}
