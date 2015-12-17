package com.fujixerox.aus.asset.impl.query.handlers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
abstract class AbstractEnumAttributeHandler extends StringAttributeHandler {

    AbstractEnumAttributeHandler(boolean range) {
        super(false, range);
    }

    Interval getInterval(List<String> values, String low, String high) {
        if (isEqualBoundaries(low, high)) {
            Logger.debug("Log ''{0}'' and high ''{1}'' are equal", low, high);
            int index = values.indexOf(low);
            if (index == -1) {
                Logger
                        .debug(
                                "Unable to find low ''{0}'' in {1}, returning null interval",
                                low, values);
                return null;
            }
            return new Interval(index, index, false);
        }

        if (!isRange()) {
            Logger.debug("Range is not supported");
            int lowIndex = values.indexOf(low);
            int highIndex = values.indexOf(high);
            if (lowIndex == -1 && highIndex == -1) {
                Logger.debug(
                        "Unable to find low ''{0}'' and high ''{1}'' in {2}",
                        low, high, values);
                return null;
            }
            if (lowIndex == -1) {
                Logger.debug("Unable to find low ''{0}'' in {1}", low, values);
                return new Interval(highIndex, highIndex, false);
            }
            if (highIndex == -1) {
                Logger
                        .debug("Unable to find high ''{0}'' in {1}", high,
                                values);
                return new Interval(lowIndex, lowIndex, false);
            }
            return new Interval(lowIndex, highIndex, false);
        }

        int lowIndex = -1;
        int highIndex = -1;

        if (StringUtils.isNotEmpty(low)) {
            int index = values.indexOf(low);
            if (index > -1) {
                lowIndex = index;
            } else {
                Logger.debug("Unable to find low ''{0}'' in {1}", low, values);
            }
        } else {
            Logger.debug("Low ''{0}'' is empty", low);
        }

        if (StringUtils.isNotEmpty(high)) {
            int index = values.indexOf(high);
            if (index > -1) {
                highIndex = index;
            } else {
                Logger
                        .debug("Unable to find high ''{0}'' in {1}", high,
                                values);
            }
        } else {
            Logger.debug("High ''{0}'' is empty", high);
        }

        if (lowIndex == -1 && highIndex == -1) {
            Logger.debug("Unable to find low ''{0}'' and high ''{1}'' in {2}",
                    low, high, values);
            return null;
        }
        if (lowIndex == -1) {
            Logger.debug("Unable to find low ''{0}'' in {1}", low, values);
            return new Interval(0, highIndex, true);
        }
        if (highIndex == -1) {
            Logger.debug("Unable to find high ''{0}'' in {1}", high, values);
            return new Interval(lowIndex, values.size() - 1, true);
        }
        return new Interval(lowIndex, highIndex, true);
    }

}
