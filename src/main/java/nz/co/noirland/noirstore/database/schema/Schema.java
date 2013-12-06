package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class Schema {

    private static SortedMap<Integer, Schema> schemas = new TreeMap<Integer, Schema>();

    private static void putSchemas() {
        if(schemas.isEmpty()) {
            schemas.put(1, new Schema1());
        }
    }

    public static Map<Integer, Schema> getSchemas() {
        putSchemas();
        return schemas;
    }

    public static int getCurrentSchema() {
        putSchemas();
        return schemas.lastKey();
    }

    public static Schema getSchema(int version) {
        putSchemas();
        if(schemas.containsKey(version)) {
            return schemas.get(version);
        }else{
            NoirStore.inst().debug("Could not find schema version: " + version);
            return null;
        }
    }
    public abstract void updateDatabase();



}
