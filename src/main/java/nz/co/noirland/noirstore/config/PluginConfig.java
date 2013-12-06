package nz.co.noirland.noirstore.config;

public class PluginConfig extends Config {

    private static PluginConfig inst;

    private PluginConfig() {
        super("config.yml");
    }

    public static PluginConfig inst() {
        if(inst == null) {
            inst = new PluginConfig();
        }

        return inst;
    }

    // MySQL
    public String  getPrefix()   { return config.getString("noirstore.mysql.prefix", "store_"); }
    public String  getDatabase() { return config.getString("noirstore.mysql.database"); }
    public String  getUsername() { return config.getString("noirstore.mysql.username"); }
    public String  getPassword() { return config.getString("noirstore.mysql.password"); }
    public int     getPort()     { return config.getInt   ("noirstore.mysql.port", 3306); }
    public String  getHost()     { return config.getString("noirstore.mysql.host", "localhost"); }

    public boolean getDebug()    { return config.getBoolean("noirstore.debug", false);}
}
