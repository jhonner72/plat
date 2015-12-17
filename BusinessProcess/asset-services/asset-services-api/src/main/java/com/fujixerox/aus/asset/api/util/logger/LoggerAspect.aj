package com.fujixerox.aus.asset.api.util.logger;

import org.aspectj.lang.Signature;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public aspect LoggerAspect {

    pointcut logging(): call(static * Logger.*(..)) && !within(Logger) && !within(LoggerAspect);

    before(): logging() {
        Signature signature = thisEnclosingJoinPointStaticPart.getSignature();
        Logger.setCaller(String.format("%s.%s", signature.getDeclaringTypeName(), signature.getName()));
    }

    after(): logging() {
        Logger.removeCaller();
    }

}
