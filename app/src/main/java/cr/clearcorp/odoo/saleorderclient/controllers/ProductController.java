package cr.clearcorp.odoo.saleorderclient.controllers;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cr.clearcorp.odoo.saleorderclient.models.Product;
import cr.clearcorp.odoo.saleorderclient.models.UnitofMeasure;

import static java.util.Arrays.asList;

public class ProductController {

    private static final String MODEL = "product.product";

    public static ArrayList<Product> readAllProducts(String url, String database, Integer uid, String password) {

        ArrayList<HashMap<String, Object>> elements = GenericController.readAll(url, database, uid,
                password, MODEL, asList(asList(asList("sale_ok", "=", true))),
                new HashMap() {{
                    put("fields", asList("id", "name", "code", "image_medium", "uom_id", "taxes_id"));
                }});

        ArrayList<Product> products = new ArrayList<>();
        for (final HashMap<String, Object> object : elements){
            Integer id = (Integer) object.get("id");
            String name = (String) object.get("name");
            String code = "";
            try {
                code = (String) object.get("code");
            }
            catch (Exception e) {
                Log.e("Code", "Product has no code");
            }
            String imageMedium = "";
            try {
                imageMedium = (String) object.get("image_medium");
            }
            catch (Exception e) {
                Log.e("Image", "Product has no image");
            }

            Integer uomId;
            String uomName;
            try {
                Object[] uom = (Object[]) object.get("uom_id");
                uomId = (Integer) uom[0];
                uomName = (String) uom[1];
            }
            catch (Exception e){
                Log.d("UoM", "Invalid UoM not loading product");
                continue;
            }

            ArrayList<Integer> taxes_ids = new ArrayList<>();
            try {
                Object[] taxes = (Object[]) object.get("taxes_id");
                for (Object tax : taxes){
                    taxes_ids.add((Integer) tax);
                }
            }
            catch (Exception e){
                Log.d("Taxes", "Invalid taxes");
                continue;
            }

            Product product = new Product(id, name, code, imageMedium, new UnitofMeasure(uomId, uomName));
            product.setTaxes(taxes_ids);
            products.add(product);
        }

        return products;
    }
}
