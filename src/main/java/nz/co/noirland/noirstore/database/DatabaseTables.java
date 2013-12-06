package nz.co.noirland.noirstore.database;

import nz.co.noirland.noirstore.config.PluginConfig;



public enum DatabaseTables {
    SCHEMA("schema"),
    ITEMS("items"),
    SIGNS("signs");

    private String name;

    private DatabaseTables(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return PluginConfig.inst().getPrefix() + name;
    }
}
