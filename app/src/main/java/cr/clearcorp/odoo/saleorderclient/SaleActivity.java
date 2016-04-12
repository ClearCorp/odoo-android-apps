package cr.clearcorp.odoo.saleorderclient;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cr.clearcorp.odoo.saleorderclient.controllers.SaleOrderController;
import cr.clearcorp.odoo.saleorderclient.models.Product;
import cr.clearcorp.odoo.saleorderclient.models.SaleOrder;
import cr.clearcorp.odoo.saleorderclient.models.SaleOrderLine;

public class SaleActivity extends AppCompatActivity implements ProductFragment.OnItemClickListener, SaleOrderLineFragment.OnItemClickEditListener,
SaleOrderLineEditFragment.OnActionListener {

    private String database;
    private String password;
    private String url;
    private Integer uid;
    private boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        Intent intent = getIntent();
        this.database = intent.getStringExtra("database");
        this.password = intent.getStringExtra("password");
        this.url = intent.getStringExtra("url");
        this.uid = intent.getIntExtra("uid", 0);

        this.editing = false;

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.main_sale_activity) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SaleOrderLineFragment saleOrderLineFragment = new SaleOrderLineFragment();
            ProductFragment productFragment = new ProductFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            saleOrderLineFragment.setArguments(getIntent().getExtras());
            productFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'main_sale_activity' Layout
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_sale_activity, productFragment, "ProductFragment");
            transaction.add(R.id.main_sale_activity, saleOrderLineFragment, "SaleOrderLineFragment");
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.create_new_cash:
                CreateNewCashConfirm();
                return true;
            case R.id.create_new_credit:
                CreateNewCreditConfirm();
                return true;
            case R.id.clear_sale:
                FragmentManager fragmentManager = getSupportFragmentManager();
                SaleOrderLineFragment saleOrderLineFragment = (SaleOrderLineFragment) fragmentManager.findFragmentByTag("SaleOrderLineFragment");
                saleOrderLineFragment.ClearAdapter();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void CreateNewCashConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                CreateNewCash();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d("Cancel", "Cancel");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void CreateNewCash() {
        try {
            SaleOrder so = new SaleOrder();

            FragmentManager fragmentManager = getSupportFragmentManager();
            SaleOrderLineFragment saleOrderLineFragment = (SaleOrderLineFragment) fragmentManager.findFragmentByTag("SaleOrderLineFragment");

            so.setCustomer(saleOrderLineFragment.getCustomer());
            so.setPricelist(saleOrderLineFragment.getPricelist());
            so.setLines(saleOrderLineFragment.getSaleOrderLines());

            if (so.getLines().isEmpty() || so.getCustomer().getId() == 0 || so.getPricelist().getId() == 0){
                Snackbar.make(this.findViewById(R.id.SaleCoordinatorLayout), R.string.error_no_lines_no_pricelist_no_customer,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                CreateSaleOrderTask saleOrderTask = new CreateSaleOrderTask(this.url, this.database, this.uid, this.password, so, 1);
                saleOrderTask.execute();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CreateNewCreditConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                CreateNewCredit();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d("Cancel", "Cancel");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void CreateNewCredit() {
        try {
            SaleOrder so = new SaleOrder();

            FragmentManager fragmentManager = getSupportFragmentManager();
            SaleOrderLineFragment saleOrderLineFragment = (SaleOrderLineFragment) fragmentManager.findFragmentByTag("SaleOrderLineFragment");

            so.setCustomer(saleOrderLineFragment.getCustomer());
            so.setPricelist(saleOrderLineFragment.getPricelist());
            so.setLines(saleOrderLineFragment.getSaleOrderLines());

            if (so.getLines().isEmpty() || so.getCustomer().getId() == 0 || so.getPricelist().getId() == 0){
                Snackbar.make(this.findViewById(R.id.SaleCoordinatorLayout), R.string.error_no_lines_no_pricelist_no_customer,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                CreateSaleOrderTask saleOrderTask = new CreateSaleOrderTask(this.url, this.database, this.uid, this.password, so, 3);
                saleOrderTask.execute();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    protected void onStart(){
        super.onStart();
        RetrieveWarehouseIdsTask warehouseTask = new RetrieveWarehouseIdsTask(url, database, uid, password);
        warehouseTask.execute();
    }



    private void LoadtextViewWarehouse(ArrayList<Warehouse> warehouses) {
        ArrayAdapter<Warehouse> adapter;
        adapter = new ArrayAdapter<>(SaleActivity.this, android.R.layout.simple_spinner_dropdown_item, warehouses);
        spinnerWarehouse.setAdapter(adapter);
    }*/

    private void LoadNewSaleOrderId(String saleOrderName) {
        Log.d("SaleActivity", saleOrderName);
        Intent intent = new Intent(this, SaleConfirmationActivity.class);
        intent.putExtra("orderName", saleOrderName);
        startActivity(intent);
    }

    @Override
    public void OnItemClicked(Product product, Double price) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SaleOrderLineFragment fragment = (SaleOrderLineFragment) fragmentManager.findFragmentByTag("SaleOrderLineFragment");
        fragment.LoadNewProduct(product, price);
        Log.d("SaleOrderLineFragment", "Product added");
    }

    @Override
    public void OnItemEditClicked(SaleOrderLine line, Integer positon) {
        if (!this.editing) {
            this.editing = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProductFragment fragment = (ProductFragment) fragmentManager.findFragmentByTag("ProductFragment");

            SaleOrderLineEditFragment saleOrderLineEditFragment = new SaleOrderLineEditFragment();
            Bundle bundle = new Bundle();
            bundle.putAll(getIntent().getExtras());
            bundle.putInt("position", positon);
            bundle.putDouble("qty", line.getQuantity());
            bundle.putInt("product_id", line.getProduct().getId());
            bundle.putString("product", line.getProduct().toString());
            bundle.putInt("uom", line.getUom().getId());
            bundle.putDouble("price", line.getPrice());
            saleOrderLineEditFragment.setArguments(bundle);


            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragment);
            transaction.add(R.id.main_sale_activity, saleOrderLineEditFragment, "SaleOrderLineEditFragment");
            transaction.commit();
        }
        else {
            Snackbar.make(this.findViewById(R.id.SaleCoordinatorLayout), R.string.error_already_editing,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void OnActionCancel() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ProductFragment productFragment = (ProductFragment) fragmentManager.findFragmentByTag("ProductFragment");
        SaleOrderLineEditFragment saleOrderLineEditFragment = (SaleOrderLineEditFragment) fragmentManager.findFragmentByTag("SaleOrderLineEditFragment");

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(saleOrderLineEditFragment);
        transaction.show(productFragment);
        transaction.commit();
        this.editing = false;
    }

    @Override
    public void OnActionSave(Double qty, Double price, Integer uomId, Integer position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SaleOrderLineFragment saleOrderLineFragment = (SaleOrderLineFragment) fragmentManager.findFragmentByTag("SaleOrderLineFragment");
        ProductFragment productFragment = (ProductFragment) fragmentManager.findFragmentByTag("ProductFragment");
        SaleOrderLineEditFragment saleOrderLineEditFragment = (SaleOrderLineEditFragment) fragmentManager.findFragmentByTag("SaleOrderLineEditFragment");

        saleOrderLineFragment.UpdateAdapter(qty, price, uomId, position);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(saleOrderLineEditFragment);
        transaction.show(productFragment);
        transaction.commit();
        this.editing = false;
    }

    @Override
    public void OnActionDelete(Integer position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SaleOrderLineFragment saleOrderLineFragment = (SaleOrderLineFragment) fragmentManager.findFragmentByTag("SaleOrderLineFragment");
        ProductFragment productFragment = (ProductFragment) fragmentManager.findFragmentByTag("ProductFragment");
        SaleOrderLineEditFragment saleOrderLineEditFragment = (SaleOrderLineEditFragment) fragmentManager.findFragmentByTag("SaleOrderLineEditFragment");

        saleOrderLineFragment.DeleteAdapter(position);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(saleOrderLineEditFragment);
        transaction.show(productFragment);
        transaction.commit();
        this.editing = false;
    }

    private class CreateSaleOrderTask extends AsyncTask<Void, Void, String> {

        private String database;
        private Integer uid;
        private String password;
        private String url;
        private SaleOrder saleOrder;
        private Integer type;

        public CreateSaleOrderTask(String url, String database, Integer uid, String password, SaleOrder saleOrder, Integer type) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
            this.saleOrder = saleOrder;
            this.type = type;
        }

        @Override
        protected String doInBackground(Void... params) {
            return CreateSaleOrder();
        }

        @Override
        protected void onPostExecute(String result) {
            LoadNewSaleOrderId(result);
        }

        private String CreateSaleOrder(){
            Integer saleOrderId = SaleOrderController.createSaleOrder(this.url, this.database, this.uid, this.password, this.saleOrder, this.type);
            return SaleOrderController.readName(this.url, this.database, this.uid, this.password, saleOrderId);
        }
    }

    /*private class RetrieveWarehouseIdsTask extends AsyncTask<Void, Void, ArrayList<Warehouse>> {

        private String database;
        private Integer uid;
        private String password;
        private String url;

        public RetrieveWarehouseIdsTask(String url, String database, Integer uid, String password) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
        }

        @Override
        protected ArrayList<Warehouse> doInBackground(Void... params) {
            return LoadWarehouses();
        }

        @Override
        protected void onPostExecute(ArrayList<Warehouse> result) {
            LoadtextViewWarehouse(result);
        }

        private ArrayList<Warehouse> LoadWarehouses(){
            return WarehouseController.readAllWarehouses(this.url, this.database, this.uid, this.password);
        }
    }*/
}
