package nz.co.noirland.noirstore.database;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.schema.Schema;

import java.sql.*;

public class SQLDatabase {

    private static SQLDatabase inst;
    private NoirStore plugin = NoirStore.inst();
    private Connection con;

    public static SQLDatabase inst() {
        if(inst == null) {
            inst = new SQLDatabase();
        }
        return inst;
    }

    private SQLDatabase() {
        PluginConfig config = PluginConfig.inst();
        String url = "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase();
        try {
            con = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            plugin.disable("Couldn't connect to database!", e);
        }
    }

    // -- QUERY FUNCTIONS -- //

    /**
     * Add an item to the database, using 0 as the amount of stock.
     * @return The item id of the item added
     */
    public int addItem(String material, String data) {
        try {
            PreparedStatement query = prepareStatement(DatabaseQueries.INSERT_ITEM);
            query.setString(1, material);
            query.setString(2, data);
            query.setInt(3, 0);
            query.execute();
            ResultSet key = query.getGeneratedKeys();
            key.first();
            return key.getInt(1);

        } catch (SQLException e) {
            plugin.debug("Could not add item " + material + " - " + data + " to database.", e);
            return -1;
        }
    }

    /**
     * Get the item id of an item
     * @param material name of the material
     * @param data material data
     * @return the item id of the item, or -1 if it does not exist in the database
     */
    public int getItemId(String material, String data) {

        PreparedStatement query = prepareStatement(DatabaseQueries.GET_ITEM_BY_ITEM);
        try {
            query.setString(1, material);
            query.setString(2, data);
            ResultSet res = query.executeQuery();
            res.last();
            if(res.getRow() == 1) {
                return res.getInt("item_id");
            }else if(res.getRow() > 1) {
                plugin.getLogger().warning("Multiple instances of " + material + " - " + data + " in database!");
                res.first();
                return res.getInt("item_id");
            }
            return -1;
        } catch (SQLException e) {
            plugin.debug("Failed to check for item " + material + " - " + data, e);
            return -1;
        }
    }

    public void updateItemAmount(int id, int newAmount) {

        PreparedStatement query = prepareStatement(DatabaseQueries.UPDATE_ITEM_AMOUNT_BY_ID);
        try{
            query.setInt(1, id);
            query.setInt(1, newAmount);
            query.execute();
        } catch(SQLException e) {
            plugin.debug("Could not update item amount", e);
        }
    }

    public int getItemAmount(int id) {
        PreparedStatement query = prepareStatement(DatabaseQueries.GET_ITEM_BY_ID);
        try{
            query.setInt(1, id);
            ResultSet res = query.executeQuery();
            getNumRows(res);
            res.first();
            return res.getInt("amount");
        } catch(SQLException e) {
            plugin.debug("Could not get item amount for id " + id, e);
            return 0;
        }
    }

    public boolean isTable(String table) {
        Statement statement;
        try {
            statement = con.createStatement();
        } catch (SQLException e) {
            plugin.debug("Could not create isTable statement", e);
            return false;
        }
        try {
            statement.executeQuery("SELECT * FROM " + table);
            return true; // Result can never be null, bad logic from earlier versions.
        } catch (SQLException e) {
            return false; // Query failed, table does not exist.
        }
    }

    // -- DATABASE FUNCTIONS -- //

    public PreparedStatement prepareStatement(String query) {
        try {
            if(query.startsWith("INSERT")) {
                return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            }
            return con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            plugin.disable("Could not create statement for database!", e);
            return null;
        }
    }

    public void disconnect() {
        try {
            con.close();
        } catch (SQLException e) {
            plugin.debug("Couldn't close connection to database.", e);
        }
    }

    public void checkSchema() {
        int version = getSchema();
        int latest = Schema.getCurrentSchema();
        if(version == latest) {
            return;
        }
        if(version > latest) {
            plugin.disable("Database schema is newer than this plugin version!");
        }

        for(int i = version + 1; i <= latest; i++) {
            Schema.getSchema(i).updateDatabase();
        }

    }

    private int getSchema() {
        try {
            if(isTable(DatabaseTables.SCHEMA.toString())) {
                ResultSet res = con.prepareStatement(DatabaseQueries.GET_SCHEMA).executeQuery();
                res.first();
                return res.getInt("version");
            }else{
                // SCHEMA table does not exist, tables not set up
                return 0;
            }
        } catch (SQLException e) {
            plugin.disable("Could not get database schema!", e);
            return 0;
        }
    }

    // -- UTILITY FUNCTIONS -- //

    public static int getNumRows(ResultSet rs) {
        try {
            rs.last();
            return rs.getRow();
        } catch (SQLException e) {
            NoirStore.inst().debug("Could not get number of rows of result set!", e);
            return -1;
        }
    }

}
