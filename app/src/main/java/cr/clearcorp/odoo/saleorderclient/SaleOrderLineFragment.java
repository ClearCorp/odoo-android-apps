package cr.clearcorp.odoo.saleorderclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

import cr.clearcorp.odoo.saleorderclient.adapters.SaleOrderLineAdapter;
import cr.clearcorp.odoo.saleorderclient.controllers.CustomerController;
import cr.clearcorp.odoo.saleorderclient.controllers.PricelistController;
import cr.clearcorp.odoo.saleorderclient.models.Customer;
import cr.clearcorp.odoo.saleorderclient.models.Pricelist;
import cr.clearcorp.odoo.saleorderclient.models.Product;
import cr.clearcorp.odoo.saleorderclient.models.SaleOrderLine;
import cr.clearcorp.odoo.saleorderclient.models.UnitofMeasure;


public class SaleOrderLineFragment extends Fragment {

    private OnItemClickEditListener listener;
    private String database;
    private String password;
    private String url;
    private Integer uid;
    private TextView textViewTotal;
    private SearchableSpinner spinnerCustomer;
    private SearchableSpinner spinnerPricelist;
    private SaleOrderLineAdapter adapterLines;
    private ListView listViewLines;
    private RetrieveCustomersIdsTask customerTask;
    private RetrievePricelistIdsTask pricelistTask;

    public SaleOrderLineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        this.database = bundle.getString("database");
        this.password = bundle.getString("password");
        this.url = bundle.getString("url");
        this.uid = bundle.getInt("uid", 0);
        View view = inflater.inflate(R.layout.fragment_sale_line, container, false);
        this.textViewTotal = (TextView) view.findViewById(R.id.textViewTotal);
        this.spinnerCustomer = (SearchableSpinner) view.findViewById(R.id.spinnerCustomer);
        this.spinnerCustomer.setTitle(getResources().getString(R.string.prompt_customer_no_selection));
        this.spinnerCustomer.setPositiveButton(getResources().getString(R.string.ok));
        this.spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = (Customer) SaleOrderLineFragment.this.spinnerCustomer.getItemAtPosition(position);
                Integer counter = 0;
                boolean flag = false;
                while (counter < SaleOrderLineFragment.this.spinnerPricelist.getCount()) {
                    Pricelist pricelist = (Pricelist) SaleOrderLineFragment.this.spinnerPricelist.getItemAtPosition(counter);
                    if (customer.getPricelist() != null) {
                        if (pricelist.getId() == customer.getPricelist().getId()) {
                            flag = true;
                            break;
                        }
                    }
                    counter += 1;
                }
                if (flag) {
                    SaleOrderLineFragment.this.spinnerPricelist.setSelection(counter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("SaleOrderLineFragment", "Nothing selected");
            }
        });
        this.spinnerPricelist = (SearchableSpinner) view.findViewById(R.id.spinnerPricelist);
        this.spinnerPricelist.setTitle(getResources().getString(R.string.prompt_pricelist_no_selection));
        this.spinnerPricelist.setPositiveButton(getResources().getString(R.string.ok));
        listViewLines = (ListView) view.findViewById(R.id.listViewLines);
        adapterLines = new SaleOrderLineAdapter(getContext(), R.layout.sale_line, new ArrayList<SaleOrderLine>());
        this.listViewLines.setAdapter(adapterLines);
        this.listViewLines.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SaleOrderLine line = (SaleOrderLine) parent.getAdapter().getItem(position);
                listener.OnItemEditClicked(line, position);
            }
        });
        customerTask = new RetrieveCustomersIdsTask(url, database, uid, password);
        customerTask.execute();
        pricelistTask = new RetrievePricelistIdsTask(url, database, uid, password);
        pricelistTask.execute();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        customerTask.cancel(true);
        pricelistTask.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickEditListener) {
            listener = (OnItemClickEditListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement SaleOrderLineFragment.OnItemClickEditListener");
        }
    }


    private void LoadtextViewCustomer(ArrayList<Customer> customers) {
        ArrayAdapter<Customer> adapter;
        customers.add(0, new Customer(0, getResources().getString(R.string.prompt_customer_no_selection), 0));
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, customers);
        spinnerCustomer.setAdapter(adapter);
    }

    private void LoadtextViewPricelist(ArrayList<Pricelist> pricelists) {
        ArrayAdapter<Pricelist> adapter;
        pricelists.add(0, new Pricelist(0, getResources().getString(R.string.prompt_pricelist_no_selection)));
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, pricelists);
        spinnerPricelist.setAdapter(adapter);
    }

    private void addProduct(Product product, Double price){
        boolean flag = false;
        Integer count = 0;
        while (count < this.adapterLines.getCount()) {
            if (this.adapterLines.getItem(count).getProduct().getId() == product.getId()){
                this.adapterLines.getItem(count).setQuantity(this.adapterLines.getItem(count).getQuantity() + 1);
                flag = true;
                break;
            }
            count += 1;
        }

        if (!flag){
            this.adapterLines.add(new SaleOrderLine(1.0, product, product.getUom(), price));
        }
        updateTotal();
    }

    private void updateTotal(){
        Double total = 0.0;
        Integer count = 0;
        while (count < this.adapterLines.getCount()) {
            SaleOrderLine line = this.adapterLines.getItem(count);
            total = total + (line.getQuantity() * line.getPrice());
            count += 1;
        }
        this.textViewTotal.setText(String.format(getResources().getString(R.string.prompt_total) + ": " + "%.2f", total));
    }

    public void LoadNewProduct(Product product, Double price) {
        addProduct(product, price);
        this.adapterLines.notifyDataSetChanged();
        Log.d("New Product add", product.toString());
    }

    public void UpdateAdapter(Double qty, Double price, UnitofMeasure uom, Integer position) {
        SaleOrderLine line = this.adapterLines.getItem(position);
        line.setQuantity(qty);
        line.setPrice(price);
        line.setUom(uom);
        this.adapterLines.notifyDataSetChanged();
    }

    public void DeleteAdapter(Integer position) {
        SaleOrderLine line = this.adapterLines.getItem(position);
        this.adapterLines.remove(line);
        this.adapterLines.notifyDataSetChanged();
    }

    public void ClearAdapter() {
        this.adapterLines.clear();
        this.adapterLines.notifyDataSetChanged();
    }

    public void ClearCustomer() {
        this.spinnerCustomer.setSelection(0);
    }

    public void ClearPricelist() {
        this.spinnerPricelist.setSelection(0);
    }

    public void ClearTotal() {
        this.textViewTotal.setText("");
    }

    public Customer getCustomer() {
        return  (Customer) this.spinnerCustomer.getSelectedItem();
    }

    public Pricelist getPricelist() {
        return (Pricelist) this.spinnerPricelist.getSelectedItem();
    }

    public ArrayList<SaleOrderLine> getSaleOrderLines() {
        ArrayList<SaleOrderLine> lines = new ArrayList<>();
        Integer counter = 0;
        while (counter < this.adapterLines.getCount()){
            lines.add(this.adapterLines.getItem(counter));
            counter += 1;
        }
        return lines;
    }

    public interface OnItemClickEditListener {
        public void OnItemEditClicked(SaleOrderLine line, Integer position);
    }

    private class RetrieveCustomersIdsTask extends AsyncTask<Void, Void, ArrayList<Customer>> {

        private String database;
        private Integer uid;
        private String password;
        private String url;

        public RetrieveCustomersIdsTask(String url, String database, Integer uid, String password) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
        }

        @Override
        protected ArrayList<Customer> doInBackground(Void... params) {
            return LoadCustomers();
        }

        @Override
        protected void onPostExecute(ArrayList<Customer> result) {
            LoadtextViewCustomer(result);
        }

        private ArrayList<Customer> LoadCustomers(){
            return CustomerController.readAllCustomers(this.url, this.database, this.uid, this.password);
        }
    }

    private class RetrievePricelistIdsTask extends AsyncTask<Void, Void, ArrayList<Pricelist>> {

        private String database;
        private Integer uid;
        private String password;
        private String url;

        public RetrievePricelistIdsTask(String url, String database, Integer uid, String password) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
        }

        @Override
        protected ArrayList<Pricelist> doInBackground(Void... params) {
            return LoadPricelists();
        }

        @Override
        protected void onPostExecute(ArrayList<Pricelist> result) {
            LoadtextViewPricelist(result);
        }

        private ArrayList<Pricelist> LoadPricelists(){
            return PricelistController.readAllPricelists(this.url, this.database, this.uid, this.password);
        }
    }
}
