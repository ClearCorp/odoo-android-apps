package cr.clearcorp.odoo.saleorderclient.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import cr.clearcorp.odoo.saleorderclient.models.UnitofMeasure;

import static java.util.Arrays.asList;

public class UnitofMeasureController {

    private static final String MODEL = "product.uom";

    public static ArrayList<UnitofMeasure> readAllUoMFromCategory(String url, String database, Integer uid, String password, UnitofMeasure uoM) {

        ArrayList<HashMap<String, Object>> elements = GenericController.readAll(url, database, uid,
                password, MODEL, asList(asList(asList("id", "=", uoM.getId()))),
                new HashMap() {{
                    put("fields", asList("id", "name", "category_id"));
                }});

        HashMap<String, Object>  uom = elements.get(0);
        Object[] categoryobj = (Object[]) uom.get("category_id");
        Integer category = (Integer) categoryobj[0];

        ArrayList<HashMap<String, Object>> categoryElements = GenericController.readAll(url, database, uid,
                password, MODEL, asList(asList(asList("id", "!=", uoM.getId()), asList("category_id", "=", category))),
                new HashMap() {{
                    put("fields", asList("id", "name"));
                }});


        ArrayList<UnitofMeasure> unitofMeasures = new ArrayList<>();
        for (final HashMap<String, Object> object : categoryElements){
            unitofMeasures.add(new UnitofMeasure((Integer) object.get("id"), (String) object.get("name")));
        }

        return unitofMeasures;
    }
}
