package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.SQLDatabase;

import java.sql.SQLException;

public class Schema1 extends Schema {

    private SQLDatabase db = SQLDatabase.inst();
    private String prefix;

    public void updateDatabase() {
        prefix = PluginConfig.inst().getPrefix();
        createSchemaTable();
        createItemsTable();
        createSignsTable();
    }

    private void createSchemaTable() {
        String schemaTable = prefix + "schema";
        try{
            db.prepareStatement("CREATE TABLE `" + schemaTable + "` (`version` TINYINT UNSIGNED);").execute();
            db.prepareStatement("INSERT INTO `" + schemaTable + "` VALUES(1);").execute();
        }catch(SQLException e) {
            NoirStore.debug().disable("Could not create schema table!", e);
        }
    }

    private void createItemsTable() {

        try {
            db.prepareStatement("CREATE TABLE `" + prefix + "items` (`item_id` INT UNSIGNED AUTO_INCREMENT, `item` VARCHAR(64), `data` VARCHAR(64), `amount` INT UNSIGNED, PRIMARY KEY(item_id));").execute();
        } catch (SQLException e) {
            NoirStore.debug().disable("Couldn't create items table!", e);
        }
    }

    private void createSignsTable() {
        try {
            db.prepareStatement("CREATE TABLE `" + prefix + "signs` (`x` INT, `y` INT, `z` INT, `world` VARCHAR(255), `item_id` INT UNSIGNED, PRIMARY KEY (`x`, `y`, `z`, `world`), FOREIGN KEY (`item_id`) REFERENCES `store_items`(`item_id`));").execute();
        }catch(SQLException e) {
            NoirStore.debug().disable("Couldn't create signs table!", e);
        }
    }
}
