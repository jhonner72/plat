package com.fujixerox.aus.asset.app.dfc.credentials;

import com.fujixerox.aus.asset.app.component.ComponentTestAspect;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractCredentialManagerTest implements
        ComponentTestAspect.IComponentTest {

    public AbstractCredentialManagerTest() {
        super();
    }

    public static final int LENGTH = 20;

    public static final String TOKEN_PATTERN = "([0-9a-f]{2}){" + LENGTH + "}";

}
