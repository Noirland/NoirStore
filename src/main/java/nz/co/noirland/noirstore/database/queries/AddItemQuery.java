package nz.co.noirland.noirstore.database.queries;

public class AddItemQuery extends StoreQuery {

    private static final String QUERY = "INSERT INTO `{PREFIX}_items`(`item_id`, `item`,`data`,`amount`) VALUES(?, ?, ?, ?)";

    public AddItemQuery(int id, String material, String data, int count) {
        super(4, QUERY);
        setValue(1, id);
        setValue(2, material);
        setValue(3, data);
        setValue(4, count);
    }

}
