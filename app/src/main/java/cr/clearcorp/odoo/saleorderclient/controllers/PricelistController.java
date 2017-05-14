package cr.clearcorp.odoo.saleorderclient.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cr.clearcorp.odoo.saleorderclient.models.Pricelist;

import static java.util.Arrays.asList;

public class PricelistController {

    private static final String MODEL = "product.pricelist";

    public static ArrayList<Pricelist> readAllPricelists(String url, String database, Integer uid, String password) {

        ArrayList<HashMap<String, Object>> elements = GenericController.readAll(url, database, uid,
                password, MODEL, asList(asList(asList("type", "=", "sale"))),
                new HashMap() {{
                    put("fields", asList("id", "name", "currency_id"));
                }});

        ArrayList<Pricelist> pricelists = new ArrayList<>();
        for (final HashMap<String, Object> object : elements){
            Object[] currency = (Object[]) object.get("currency_id");
            String currencyName = (String) currency[1];
            pricelists.add(new Pricelist((Integer)object.get("id"), (String)object.get("name") + " (" + currencyName + ")"));
        }

        return pricelists;
    }

    public static Double getPrice(String url, String database, Integer uid, String password, Integer productId, Integer pricelistId, Double quantity){
        try {
            HashMap<String, Object> result = GenericController.callMethodHash(
                    url, database, uid, password, MODEL, "price_get_string_key", asList((Object) pricelistId, (Object) productId, (Object) quantity));
            return (Double) result.get(String.valueOf(pricelistId));
        }
        catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }
}
