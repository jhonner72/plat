package com.fujixerox.aus.asset.impl.constants;

import com.fujixerox.aus.asset.api.constants.IResponseCode;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class ResponseConstants {

    private ResponseConstants() {
        super();
    }

    public static class AbstractResponseCode implements IResponseCode {

        private final int _code;

        private final int _subCode;

        public AbstractResponseCode(int code, int subCode) {
            _code = code;
            _subCode = subCode;
        }

        @Override
        public int getCode() {
            return _code;
        }

        @Override
        public int getSubCode() {
            return _subCode;
        }

    }

    public static final class GenericCode extends AbstractResponseCode {

        public static final GenericCode SUCCESS = new GenericCode(0, 0);

        public static final GenericCode SYNTAX_ERROR = new GenericCode(1, 1);

        public static final GenericCode INVALID_TOKEN = new GenericCode(1, 2);

        public GenericCode(int code, int subCode) {
            super(code, subCode);
        }
    }

    public static final class LoginManagerCode extends AbstractResponseCode {

        public LoginManagerCode(int code, int subCode) {
            super(code, subCode);
        }

        public static final class LoginCode extends AbstractResponseCode {

            public static final LoginCode SUCCESS = new LoginCode(0, 0);

            public static final LoginCode LOGIN_FAILED = new LoginCode(21, 1);

            public static final LoginCode ALREADY_LOGGEDIN = new LoginCode(21,
                    4);

            public static final LoginCode RESOURCE_UNAVAILABLE = new LoginCode(
                    21, 5);

            public LoginCode(int code, int subCode) {
                super(code, subCode);
            }

        }

    }

}
