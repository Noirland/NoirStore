package nz.co.noirland.noirstore.database.queries;

public class InsertItemQuery extends StoreQuery {

    private static final String QUERY = "INSERT INTO `{PREFIX}_items`(`id`, `item`,`data`,`amount`) VALUES(?, ?, ?, ?)";

    public InsertItemQuery(int id, String material, String data, int count) {
        super(4, QUERY);
        setValue(1, id);
        setValue(2, material);
        setValue(3, data);
        setValue(4, count);
    }

}
