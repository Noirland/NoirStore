package nz.co.noirland.noirstore.database;

public class DatabaseQueries {

    public static String GET_SCHEMA               = "SELECT `version` FROM `" + DatabaseTables.SCHEMA.toString() + "`";
    public static String SET_SCHEMA               = "UPDATE `" + DatabaseTables.SCHEMA.toString() + "` SET `version`= ?";
    public static String INSERT_ITEM              = "INSERT INTO `" + DatabaseTables.ITEMS.toString() + "` (`item`, `data`, `amount`) VALUES (?, ?, ?)";
    public static String GET_ITEM_BY_ID           = "SELECT * FROM `" + DatabaseTables.ITEMS.toString() + "` WHERE `item_id` = ?";
    public static String GET_ITEM_BY_ITEM         = "SELECT * FROM `" + DatabaseTables.ITEMS.toString() + "` WHERE `item` = ? AND `data` = ?";
    public static String UPDATE_ITEM_AMOUNT_BY_ID = "UPDATE `" + DatabaseTables.ITEMS.toString() + "` SET `amount` = ? WHERE `item_id` = ?";
    public static String INSERT_SIGN              = "INSERT INTO `" + DatabaseTables.SIGNS.toString() + "` VALUES(?,?,?,?,?,?)";
    public static String GET_SIGN_BY_LOCATION     = "SELECT * FROM `" + DatabaseTables.SIGNS.toString() + "` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?";
    public static String GET_AlL_SIGNS            = "SELECT * FROM `" + DatabaseTables.SIGNS.toString() + "`";
    public static String REMOVE_SIGN_BY_LOCATION  = "DELETE FROM `" + DatabaseTables.SIGNS.toString() + "` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?";


}
