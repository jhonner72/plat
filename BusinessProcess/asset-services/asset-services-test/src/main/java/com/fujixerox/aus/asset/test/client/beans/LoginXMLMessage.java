package com.fujixerox.aus.asset.test.client.beans;

public class LoginXMLMessage implements IResultMessage {

    private String _sessionName;

    private int _managerCode;

    private int _managerSubcode;

    private int _code;

    private int _subcode;

    private String _secToken;

    public LoginXMLMessage() {
        super();
    }

    public int getCode() {
        return _code;
    }

    public int getManagerCode() {
        return _managerCode;
    }

    public int getManagerSubcode() {
        return _managerSubcode;
    }

    public String getSecToken() {
        return _secToken;
    }

    public String getSessionName() {
        return _sessionName;
    }

    public int getSubcode() {
        return _subcode;
    }

    public void setCode(int code) {
        _code = code;
    }

    public void setManagerCode(int managerCode) {
        _managerCode = managerCode;
    }

    public void setManagerSubcode(int managerSubcode) {
        _managerSubcode = managerSubcode;
    }

    public void setSecToken(String secToken) {
        _secToken = secToken;
    }

    public void setSessionName(String sessionName) {
        _sessionName = sessionName;
    }

    public void setSubcode(int subcode) {
        _subcode = subcode;
    }

    private boolean hasError() {
        return (_code + _managerCode + _managerSubcode != 0) || (_subcode <= 0);
    }

    public String errorString() {
        if (hasError()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LoginManagerResult code: ");
            stringBuilder.append(_managerCode);
            stringBuilder.append(" subcode: ");
            stringBuilder.append(_managerSubcode);
            stringBuilder.append(", LoginResult code: ");
            stringBuilder.append(_code);
            stringBuilder.append(" subcode: ");
            stringBuilder.append(_subcode);
            return stringBuilder.toString();
        }
        return null;
    }

    public boolean isSecTokenInvalid() {
        return (hasError()) || (_secToken == null) || ("".equals(_secToken));
    }
}
