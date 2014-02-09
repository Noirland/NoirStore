package nz.co.noirland.noirstore.database;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.TradeItem;
import nz.co.noirland.noirstore.TradeSign;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.schema.Schema;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.ArrayList;

public class SQLDatabase {

    private static SQLDatabase inst;
    private NoirStore plugin = NoirStore.inst();
    private String url;
    private Connection con;

    public static SQLDatabase inst() {
        if(inst == null) {
            inst = new SQLDatabase();
        }
        return inst;
    }

    private SQLDatabase() {
        PluginConfig config = PluginConfig.inst();
        url = "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase();
        openConnection();
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
            query.setInt(1, newAmount);
            query.setInt(2, id);
            runStatementAsync(query);
        } catch(SQLException e) {
            plugin.debug("Could not update item amount", e);
        }
    }

    public int getItemAmount(int id) {
        PreparedStatement query = prepareStatement(DatabaseQueries.GET_ITEM_BY_ID);
        try{
            query.setInt(1, id);
            ResultSet res = query.executeQuery();
            res.first();
            return res.getInt("amount");
        } catch(SQLException e) {
            plugin.debug("Could not get item amount for id " + id, e);
            return 0;
        }
    }

    public boolean isTable(String table) {
        try {
            prepareStatement("SELECT * FROM " + table).executeQuery();
            return true; // Result can never be null, bad logic from earlier versions.
        } catch (SQLException e) {
            return false; // Query failed, table does not exist.
        }
    }

    public void addSign(TradeSign sign) {

        Location loc = sign.getLocation();

        PreparedStatement query = prepareStatement(DatabaseQueries.INSERT_SIGN);
        try {
            query.setInt(1, loc.getBlockX());
            query.setInt(2, loc.getBlockY());
            query.setInt(3, loc.getBlockZ());
            query.setString(4, loc.getWorld().getName());
            query.setInt(5, sign.getItem().getId());
            query.setInt(6, sign.getSellAmount());

            runStatementAsync(query);

        }catch(SQLException e) {
            plugin.debug("Could not insert sign!", e);
        }
    }

    public void removeSign(TradeSign sign) {
        Location loc = sign.getLocation();

        PreparedStatement query = prepareStatement(DatabaseQueries.REMOVE_SIGN_BY_LOCATION);
        try {
            query.setInt(1, loc.getBlockX());
            query.setInt(2, loc.getBlockY());
            query.setInt(3, loc.getBlockZ());
            query.setString(4, loc.getWorld().getName());

            runStatementAsync(query);
        }catch(SQLException e) {
            plugin.debug("Could not delete sign at " + loc.toString(), e);
        }
    }

    public ArrayList<TradeSign> loadSigns() {

        ArrayList<TradeSign> signs = new ArrayList<TradeSign>();

        PreparedStatement query = prepareStatement(DatabaseQueries.GET_AlL_SIGNS);
        try {
            ResultSet res = query.executeQuery();

            while(res.next()) {
                TradeItem item = plugin.getTradeItem(res.getInt("item_id"));
                if(item == null) {
                    plugin.debug("sign at row " + res.getRow() + " ID is nonexistant!");
                    continue;
                }
                World world = plugin.getServer().getWorld(res.getString("world"));
                Location loc = new Location(world, res.getInt("x"), res.getInt("y"),res.getInt("z"));
                TradeSign sign = new TradeSign(item, res.getInt("sell"), loc);
                plugin.addTradeSign(sign, false);
            }
        } catch (SQLException e) {
            plugin.disable("Could not get signs from database!", e);
        }


        return signs;
    }

    // -- DATABASE FUNCTIONS -- //

    public PreparedStatement prepareStatement(String query) {
        try {
            if(con.isClosed()) {
                openConnection();
            }

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
                ResultSet res = prepareStatement(DatabaseQueries.GET_SCHEMA).executeQuery();
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

    private void openConnection() {
        try {
            PluginConfig config = PluginConfig.inst();
            con = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            plugin.disable("Couldn't connect to database!", e);
        }
    }

    public void runStatementAsync(PreparedStatement statement) {
        new AsyncStatementTask(statement).runTaskAsynchronously(plugin);
    }
}
