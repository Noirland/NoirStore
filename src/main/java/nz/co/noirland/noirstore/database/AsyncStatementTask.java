package nz.co.noirland.noirstore.database;

import nz.co.noirland.noirstore.NoirStore;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AsyncStatementTask extends BukkitRunnable {

    private PreparedStatement statement;

    public AsyncStatementTask(PreparedStatement statement) {
        this.statement = statement;
    }

    @Override
    public void run() {
        try {
            statement.execute();
            NoirStore.debug().debug("Ran statement async: " + statement.toString());
            statement.close();
        } catch (SQLException e) {
            NoirStore.debug().debug("Could not execute statement " + statement.toString(), e);
        }
    }
}
