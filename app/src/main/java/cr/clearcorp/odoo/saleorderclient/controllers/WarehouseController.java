package cr.clearcorp.odoo.saleorderclient.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cr.clearcorp.odoo.saleorderclient.models.Warehouse;

import static java.util.Arrays.asList;

public class WarehouseController {
    private static final String MODEL = "stock.warehouse";

    public static ArrayList<Warehouse> readAllWarehouses(String url, String database, Integer uid, String password) {

        ArrayList<HashMap<String, Object>> elements = GenericController.readAll(url, database, uid,
                password, MODEL, asList(Collections.emptyList()),
                new HashMap() {{
                    put("fields", asList("id", "name"));
                }});

        ArrayList<Warehouse> warehouses = new ArrayList<>();
        for (final HashMap<String, Object> object : elements){
            warehouses.add(new Warehouse((Integer)object.get("id"), (String)object.get("name")));
        }

        return warehouses;
    }

}
