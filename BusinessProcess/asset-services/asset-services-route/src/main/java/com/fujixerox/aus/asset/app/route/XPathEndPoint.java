package com.fujixerox.aus.asset.app.route;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class XPathEndPoint extends SimpleEndPoint {

    private final String _xPath;

    public XPathEndPoint(String endPointId, String xPath) {
        super(endPointId);
        _xPath = xPath;
    }

    public String getXPath() {
        return _xPath;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof XPathEndPoint)) {
            return false;
        }

        if (!super.equals(other)) {
            return false;
        }

        XPathEndPoint that = (XPathEndPoint) other;

        if (_xPath == null) {
            return that._xPath == null;
        }
        return _xPath.equals(that._xPath);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (_xPath == null) {
            return 31 * result;
        }
        return 31 * result + _xPath.hashCode();
    }

}
