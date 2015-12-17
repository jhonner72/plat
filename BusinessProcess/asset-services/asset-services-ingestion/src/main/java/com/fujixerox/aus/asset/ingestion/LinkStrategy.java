package com.fujixerox.aus.asset.ingestion;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public enum LinkStrategy {

    SIMPLE("none"), TRICKLE("trickle"), TFS("tfs");

    private final String _name;

    LinkStrategy(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public static LinkStrategy of(String name) {
        for (LinkStrategy strategy : LinkStrategy.values()) {
            if (strategy._name.equals(name)) {
                return strategy;
            }
        }
        return null;
    }

}
