package com.fujixerox.aus.asset.app.route;

import org.apache.camel.test.junit4.CamelTestSupport;

import com.fujixerox.aus.asset.app.component.ComponentTestAspect;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractCamelTest extends CamelTestSupport implements
        ComponentTestAspect.IComponentTest {

    public AbstractCamelTest() {
        super();
    }

}
