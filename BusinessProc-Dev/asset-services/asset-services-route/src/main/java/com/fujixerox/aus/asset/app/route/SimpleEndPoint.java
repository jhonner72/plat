package com.fujixerox.aus.asset.app.route;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SimpleEndPoint {

    private final String _endPointId;

    public SimpleEndPoint(String endPointId) {
        _endPointId = endPointId;
    }

    public final String getId() {
        return _endPointId;
    }

    public String getName() {
        return String.format("direct:%s", getId());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SimpleEndPoint)) {
            return false;
        }

        SimpleEndPoint that = (SimpleEndPoint) other;

        if (_endPointId == null) {
            return that._endPointId == null;
        }
        return _endPointId.equals(that._endPointId);
    }

    @Override
    public int hashCode() {
        if (_endPointId == null) {
            return 0;
        }
        return _endPointId.hashCode();
    }

}
