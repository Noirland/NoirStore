package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.database.queries.StoreQuery;
import nz.co.noirland.zephcore.database.Schema;

import java.sql.SQLException;

public class Schema3 implements Schema {

    @Override
    public void run() {
        try {
            new StoreQuery("ALTER TABLE `{PREFIX}_signs` DROP FOREIGN KEY `{PREFIX}_signs_ibfk_1`").execute();
            new StoreQuery("ALTER TABLE `{PREFIX}_items` CHANGE `item_id` `item_id` INT(10) UNSIGNED").execute();
            new StoreQuery("ALTER TABLE `{PREFIX}_signs` ADD CONSTRAINT `{PREFIX}_signs_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `{PREFIX}_items`(`item_id`)").execute();
            new StoreQuery("UPDATE `{PREFIX}_schema` SET `version` = 3").execute();
        } catch (SQLException e) {
            NoirStore.debug().disable("Unable to update database to schema 3!", e);
        }
    }
}
