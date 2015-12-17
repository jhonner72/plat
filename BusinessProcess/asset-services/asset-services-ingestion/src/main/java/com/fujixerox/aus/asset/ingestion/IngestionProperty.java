package com.fujixerox.aus.asset.ingestion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IngestionProperty {

    String name() default "";

    String def() default "";

    boolean mandatory() default true;

}
