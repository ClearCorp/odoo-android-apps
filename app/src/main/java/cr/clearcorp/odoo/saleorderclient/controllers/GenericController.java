package cr.clearcorp.odoo.saleorderclient.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

import static java.util.Arrays.asList;

public class GenericController {

    private static final String OBJECT_URL = "/xmlrpc/2/object";
    private static final String SEARCH = "search";
    private static final String READ = "read";

    public static ArrayList<HashMap<String, Object>> readAll(
            String url, String database, Integer uid, String password, String model, Object domain,
            HashMap<String, Object> fields) {


        ArrayList<HashMap<String, Object>> elements = new ArrayList<>();

        try {
            URL encoded_url = new URL(url + OBJECT_URL);
            XMLRPCClient client = new XMLRPCClient(encoded_url);

            Object [] ids = (Object [])client.call("execute_kw", database,
                    uid, password, model, SEARCH, domain);

            Integer[] object_ids = Arrays.copyOf(ids, ids.length, Integer[].class);

            Object [] objects = (Object[]) client.call("execute_kw", database, uid, password,
                    model, READ, asList(asList(object_ids)), fields);


            for (Object obj : objects) {
                elements.add((HashMap<String, Object>) obj);
            }

        } catch(XMLRPCServerException e) {
            // The server throw an error.
            e.printStackTrace();
        } catch(XMLRPCException e) {
            // An error occured in the client.
            e.printStackTrace();
        } catch(Exception e) {
            // Any other exception
            e.printStackTrace();
        }

        return elements;
    }
}
