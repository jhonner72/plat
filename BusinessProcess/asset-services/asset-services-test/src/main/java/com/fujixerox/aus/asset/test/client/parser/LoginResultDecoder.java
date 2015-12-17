package com.fujixerox.aus.asset.test.client.parser;

import java.io.InputStream;

import com.fujixerox.aus.asset.test.client.beans.IResultMessage;
import com.fujixerox.aus.asset.test.client.beans.LoginXMLMessage;
import com.fujixerox.aus.asset.test.client.exceptions.ImageMarkException;
import com.fujixerox.aus.asset.test.client.exceptions.ImageMarkLoginException;

class LoginResultDecoder extends ResultDecoder {

    private LoginXMLMessage _loginResultMessage;

    public LoginResultDecoder(InputStream stream) throws ImageMarkException {
        super(stream);
    }

    public IResultMessage getResultMessage() throws ImageMarkLoginException {
        if (_loginResultMessage == null) {
            _loginResultMessage = new LoginXMLMessage();
            extractLoginManagerResult();
            extractLoginResult();
        }
        return _loginResultMessage;
    }

    private void extractLoginManagerResult() throws ImageMarkLoginException {
        if (findIdentifier("loginmanagerresult", true) == NOT_FOUND) {
            String response = getResult();
            if (response.length() > MAX_LOGGABLE_LENGTH) {
                response = response.substring(0, MAX_LOGGABLE_LENGTH - 1);
            }
            throw new ImageMarkLoginException("Unexpected login response: "
                    + response);
        }
        String sessionName = getAttributeValue("sessionname");
        if (stringHasValue(sessionName)) {
            _loginResultMessage.setSessionName(sessionName);
        }
        String managerCode = getAttributeValue("code");
        if (stringHasValue(managerCode)) {
            _loginResultMessage.setManagerCode(getIntValue(managerCode));
        }
        String managerSubcode = getAttributeValue("subcode");
        if (stringHasValue(managerSubcode)) {
            _loginResultMessage.setManagerSubcode(getIntValue(managerSubcode));
        }
    }

    private void extractLoginResult() {
        if (findIdentifier("loginresult", true) == NOT_FOUND) {
            return;
        }
        String code = getAttributeValue("code");
        if (stringHasValue(code)) {
            _loginResultMessage.setCode(getIntValue(code));
        }
        String subcode = getAttributeValue("subcode");
        if (stringHasValue(subcode)) {
            _loginResultMessage.setSubcode(getIntValue(subcode));
        }
        String sectoken = getAttributeValue("sectoken");
        if (stringHasValue(sectoken)) {
            _loginResultMessage.setSecToken(sectoken);
        }
    }

}
