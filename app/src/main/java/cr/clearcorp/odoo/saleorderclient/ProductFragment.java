package cr.clearcorp.odoo.saleorderclient;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;

import cr.clearcorp.odoo.saleorderclient.adapters.ProductAdapter;
import cr.clearcorp.odoo.saleorderclient.controllers.PricelistController;
import cr.clearcorp.odoo.saleorderclient.controllers.ProductController;
import cr.clearcorp.odoo.saleorderclient.models.Customer;
import cr.clearcorp.odoo.saleorderclient.models.Pricelist;
import cr.clearcorp.odoo.saleorderclient.models.Product;

public class ProductFragment extends Fragment {

    private OnItemClickListener listener;
    private String database;
    private String password;
    private String url;
    private Integer uid;
    private GridView productGrid;
    private RetrieveProductIdsTask productTask;
    private ComputePriceTask priceTask;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        this.database = bundle.getString("database");
        this.password = bundle.getString("password");
        this.url = bundle.getString("url");
        this.uid = bundle.getInt("uid", 0);
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        this.productGrid = (GridView) view.findViewById(R.id.gridViewProduct);
        productTask = new RetrieveProductIdsTask(url, database, uid, password);
        productTask.execute();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            productTask.cancel(true);
            priceTask.cancel(true);
        }
        catch (Exception e) {
            Log.d("ProductFragement", "Object not instantiated");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProductFragment.OnItemClickListener");
        }
    }

    private void LoadViewProducts(ArrayList<Product> products) {
        ProductAdapter adapter;
        adapter = new ProductAdapter(getContext(), R.layout.product, products);
        productGrid.setAdapter(adapter);
        productGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                View activity_view = getActivity().findViewById(R.id.SaleCoordinatorLayout);

                Spinner spinnerCustomer = (Spinner) getActivity().findViewById(R.id.spinnerCustomer);
                Customer customer = (Customer) spinnerCustomer.getSelectedItem();
                if (customer.getId() == 0) {
                    Snackbar.make(activity_view, R.string.error_no_customer, Snackbar.LENGTH_SHORT).show();
                } else {
                    Spinner spinnerPricelist = (Spinner) getActivity().findViewById(R.id.spinnerPricelist);
                    Pricelist pricelist = (Pricelist) spinnerPricelist.getSelectedItem();
                    if (pricelist.getId() == 0) {
                        Snackbar.make(activity_view, R.string.error_no_pricelist, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Product product = (Product) parent.getAdapter().getItem(position);
                        priceTask = new ComputePriceTask(ProductFragment.this.url,
                                ProductFragment.this.database, ProductFragment.this.uid,
                                ProductFragment.this.password, product, pricelist.getId(), 1.0);
                        priceTask.execute();
                    }
                }
            }
        });

    }


    public interface OnItemClickListener {
        public void OnItemClicked(Product product, Double price);
    }

    private class RetrieveProductIdsTask extends AsyncTask<Void, Void, ArrayList<Product>> {

        private String database;
        private Integer uid;
        private String password;
        private String url;

        public RetrieveProductIdsTask(String url, String database, Integer uid, String password) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
        }

        @Override
        protected ArrayList<Product> doInBackground(Void... params) {
            return LoadProducts();
        }

        @Override
        protected void onPostExecute(ArrayList<Product> result) {
            LoadViewProducts(result);
        }

        private ArrayList<Product> LoadProducts(){
            return ProductController.readAllProducts(this.url, this.database, this.uid, this.password);
        }
    }

    private class ComputePriceTask extends AsyncTask<Void, Void, Double> {

        private String database;
        private Integer uid;
        private String password;
        private String url;
        Product product;
        Integer pricelistId;
        Double quantity;

        public ComputePriceTask(String url, String database, Integer uid, String password,
                                Product product, Integer pricelistId, Double quantity) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
            this.product = product;
            this.pricelistId = pricelistId;
            this.quantity = quantity;
        }

        @Override
        protected Double doInBackground(Void... params) {
            return LoadPrice();
        }

        @Override
        protected void onPostExecute(Double result) {
            listener.OnItemClicked(this.product, result);
        }

        private Double LoadPrice(){
            return PricelistController.getPrice(this.url, this.database, this.uid, this.password,
                    this.product.getId(), this.pricelistId, this.quantity);
        }
    }
}
