package cr.clearcorp.odoo.saleorderclient.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import cr.clearcorp.odoo.saleorderclient.models.Customer;
import cr.clearcorp.odoo.saleorderclient.models.Pricelist;

import static java.util.Arrays.asList;

public class CustomerController {

    private static final String MODEL = "res.partner";

    public static ArrayList<Customer> readAllCustomers(String url, String database, Integer uid, String password) {

        ArrayList<HashMap<String, Object>> elements = GenericController.readAll(url, database, uid,
                password, MODEL, asList(asList(asList("customer", "=", true))),
                new HashMap() {{
                    put("fields", asList("id", "full_app_name", "property_product_pricelist", "property_payment_term"));
                }});

        ArrayList<Customer> customers = new ArrayList<>();
        for (final HashMap<String, Object> object : elements) {
            Object[] pricelistArray = (Object[]) object.get("property_product_pricelist");
            Integer pricelistId = (Integer) pricelistArray[0];
            String pricelistName = (String) pricelistArray[1];

            Integer paymentTermId = 1;
            try {
                Object[] paymentTermArray = (Object[]) object.get("property_payment_term");
                paymentTermId = (Integer) paymentTermArray[0];
            }
            catch (Exception e){
                e.printStackTrace();
            }

            Customer customer = new Customer((Integer)object.get("id"), (String)object.get("full_app_name"), paymentTermId);
            customer.setPricelist(new Pricelist(pricelistId, pricelistName));
            customers.add(customer);
        }

        return customers;
    }
}
