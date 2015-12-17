package com.fujixerox.aus.asset.impl.processor.fallback;

import java.util.Map;

import com.fujixerox.aus.asset.api.processor.IRequestProcessor;
import com.fujixerox.aus.asset.impl.constants.ResponseConstants;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class FallBackProcessor implements IRequestProcessor<String, String> {

    public FallBackProcessor() {
        super();
    }

    @Override
    public String process(String request, Map headers) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\"?>");
        if (request == null) {
            return stringBuilder.toString();
        }
        if (request.contains("<loginmanager")) {
            stringBuilder.append("\n");
            stringBuilder.append("<loginmanagerresult code=\"");
            stringBuilder.append(ResponseConstants.GenericCode.SYNTAX_ERROR
                    .getCode());
            stringBuilder.append("\" subcode=\"");
            stringBuilder.append(ResponseConstants.GenericCode.SYNTAX_ERROR
                    .getSubCode());
            stringBuilder.append("\"/>");
        } else if (request.contains("<inquiry")) {
            stringBuilder.append("\n");
            stringBuilder.append("<inquiryresult code=\"");
            stringBuilder.append(ResponseConstants.GenericCode.SYNTAX_ERROR
                    .getCode());
            stringBuilder.append("\" subcode=\"");
            stringBuilder.append(ResponseConstants.GenericCode.SYNTAX_ERROR
                    .getSubCode());
            stringBuilder.append("\"/>");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean canProcess(String request) {
        return true;
    }

    @Override
    public Class<String> getRequestClass() {
        return String.class;
    }

    @Override
    public Class<String> getResponseClass() {
        return String.class;
    }

}
