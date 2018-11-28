package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.database.queries.StoreQuery;
import nz.co.noirland.zephcore.database.Schema;

import java.sql.SQLException;

public class Schema1 implements Schema {

    @Override
    public void run() {
        try {
            createItemsTable();
            createSignsTable();
            createSchemaTable();
        } catch (SQLException e) {
            NoirStore.debug().disable("Could not update database to schema 1!", e);
        }
    }

    private void createSchemaTable() throws SQLException {
            new StoreQuery("CREATE TABLE `{PREFIX}_schema` (`version` TINYINT UNSIGNED);").execute();
            new StoreQuery("INSERT INTO `{PREFIX}_schema` VALUES(1);").execute();
    }

    private void createItemsTable() throws SQLException {
        new StoreQuery("CREATE TABLE `{PREFIX}_items` (" +
                "`item_id` INT UNSIGNED AUTO_INCREMENT, " +
                "`item` VARCHAR(64), `data` VARCHAR(64), " +
                "`amount` INT UNSIGNED, " +
                "PRIMARY KEY(item_id));")
                .execute();
    }

    private void createSignsTable() throws SQLException {
        new StoreQuery("CREATE TABLE `{PREFIX}_signs` (`x` INT, " +
                "`y` INT, " +
                "`z` INT, " +
                "`world` VARCHAR(32), " +
                "`item_id` INT UNSIGNED, " +
                "PRIMARY KEY (`x`, `y`, `z`, `world`), " +
                "FOREIGN KEY (`item_id`) REFERENCES `{PREFIX}_items`(`item_id`))")
                .execute();
    }
}
