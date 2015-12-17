package com.fujixerox.aus.asset.test.client.beans;

public class InquiryXMLMessage implements IResultMessage {

    private int _code;

    private int _subcode;

    private String _secToken;

    public InquiryXMLMessage() {
        super();
    }

    public int getCode() {
        return _code;
    }

    public String getSecToken() {
        return _secToken;
    }

    public int getSubcode() {
        return _subcode;
    }

    public void setCode(int code) {
        _code = code;
    }

    public void setSecToken(String secToken) {
        _secToken = secToken;
    }

    public void setSubcode(int subcode) {
        _subcode = subcode;
    }

    private boolean hasError() {
        return _code + _subcode != 0;
    }

    public String errorString() {
        if (hasError()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("code: ");
            stringBuilder.append(_code);
            stringBuilder.append(", subcode: ");
            stringBuilder.append(_subcode);
            return stringBuilder.toString();
        }
        return null;
    }

    public boolean isSecTokenInvalid() {
        return (_code == 1) && (_subcode == 2);
    }
}
