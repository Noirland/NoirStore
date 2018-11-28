package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.database.queries.StoreQuery;
import nz.co.noirland.zephcore.database.Schema;

import java.sql.SQLException;

public class Schema4 implements Schema {

    @Override
    public void run() {
        try {
            new StoreQuery("ALTER TABLE `NoirStore`.`{PREFIX}_signs` \n"
                    + "DROP FOREIGN KEY `{PREFIX}_signs_ibfk_1`;").execute();

            new StoreQuery("ALTER TABLE `NoirStore`.`{PREFIX}_signs` \n"
                    + "ADD COLUMN `price` DECIMAL(10, 2) NULL AFTER `sell`,\n"
                    + "CHANGE COLUMN `item_id` `item_id` VARCHAR(255) NULL,\n"
                    + "DROP INDEX `{PREFIX}_signs_ibfk_1`;").execute();

            new StoreQuery("DROP TABLE `NoirStore`.`{PREFIX}_items`;").execute();

            new StoreQuery("UPDATE `{PREFIX}_schema` SET `version` = 4").execute();
        } catch (SQLException e) {
            NoirStore.debug().disable("Unable to update database to schema 4!", e);
        }
    }
}
