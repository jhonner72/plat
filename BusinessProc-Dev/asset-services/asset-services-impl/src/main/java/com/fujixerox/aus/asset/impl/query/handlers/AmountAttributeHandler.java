package com.fujixerox.aus.asset.impl.query.handlers;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class AmountAttributeHandler extends StringAttributeHandler {

    public AmountAttributeHandler() {
        super();
    }

    public AmountAttributeHandler(boolean wildCard, boolean range) {
        super(wildCard, range);
    }

    @Override
    public String getStringValue(IDfValue value) {
        String result = super.getStringValue(value);
        if (StringUtils.isBlank(result)) {
            Logger.debug("Blank value, returning 0");
            return "0";
        }
        BigDecimal amount = new BigDecimal(result);
        amount = amount.multiply(new BigDecimal(100));
        amount = amount.setScale(0, BigDecimal.ROUND_DOWN);
        return amount.toPlainString();
    }

}
