package com.fujixerox.aus.asset.test.client.parser;

import java.io.InputStream;

import com.fujixerox.aus.asset.test.client.beans.IResultMessage;
import com.fujixerox.aus.asset.test.client.beans.InquiryXMLMessage;
import com.fujixerox.aus.asset.test.client.exceptions.ImageMarkException;

class InquiryResultDecoder extends ResultDecoder {

    private InquiryXMLMessage _inquiryResultMessage;

    public InquiryResultDecoder(InputStream stream) throws ImageMarkException {
        super(stream);
    }

    public IResultMessage getResultMessage() throws ImageMarkException {
        if (_inquiryResultMessage == null) {
            _inquiryResultMessage = new InquiryXMLMessage();
            extractInquiryResultCodes();
        }
        return _inquiryResultMessage;
    }

    private void extractInquiryResultCodes() throws ImageMarkException {
        if (findIdentifier("inquiryresult", true) == NOT_FOUND) {
            String response = getResult();
            if (response.length() > MAX_LOGGABLE_LENGTH) {
                response = response.substring(0, MAX_LOGGABLE_LENGTH - 1);
            }
            throw new ImageMarkException("Unexpected inquiry response: "
                    + response);
        }
        String code = getAttributeValue("code");
        if (stringHasValue(code)) {
            _inquiryResultMessage.setCode(getIntValue(code));
        }
        String subcode = getAttributeValue("subcode");
        if (stringHasValue(subcode)) {
            _inquiryResultMessage.setSubcode(getIntValue(subcode));
        }
    }
}
