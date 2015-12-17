package com.fujixerox.aus.asset.model.beans;

import com.fujixerox.aus.asset.api.beans.tuples.Pair;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SessionInfo extends Pair<SessionInfo, String, String> {

    public SessionInfo() {
        this(null, null);
    }

    public SessionInfo(String session, String application) {
        super(session, application);
    }

    public String getSession() {
        return getFirst();
    }

    public String getApplication(String application) {
        return getSecond();
    }

}
