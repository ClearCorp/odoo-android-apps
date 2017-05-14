package cr.clearcorp.odoo.saleorderclient;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import cr.clearcorp.odoo.saleorderclient.controllers.UnitofMeasureController;
import cr.clearcorp.odoo.saleorderclient.models.UnitofMeasure;


public class SaleOrderLineEditFragment extends Fragment {

    private OnActionListener listener;
    private String database;
    private String password;
    private String url;
    private Integer uid;
    private Integer position;
    private Integer product_id;
    private Integer uomId;
    private String uomName;
    private EditText editTextSaleQtyEdit;
    private Spinner spinnerUoMEdit;
    private TextView textViewSaleProductEdit;
    private EditText editTextSalePriceEdit;
    private TextView textViewSalePriceTotalEdit;
    private Button buttonSaleSaveEdit;
    private Button buttonSaleCancelEdit;
    private Button buttonSaleDeleteEdit;
    private RetrieveUnitofMeasureIdsTask unitofMeasureTask;

    public SaleOrderLineEditFragment() {
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
        this.position = bundle.getInt("position", 0);
        this.uomId = bundle.getInt("uom_id");
        this.uomName = bundle.getString("uom_name");
        View view = inflater.inflate(R.layout.fragment_sale_line_edit, container, false);

        this.textViewSaleProductEdit = (TextView) view.findViewById(R.id.textViewSaleProductEdit);
        this.textViewSaleProductEdit.setText(bundle.getString("product"));
        this.product_id = bundle.getInt("product_id");

        Double qty = bundle.getDouble("qty", 0.0);
        Double price = bundle.getDouble("price", 0.0);
        Double total = qty * price;

        if (qty % 1 == 0) {
            this.editTextSaleQtyEdit = (EditText) view.findViewById(R.id.editTextSaleQtyEdit);
            this.editTextSaleQtyEdit.setText(String.format("%d", qty.intValue()));
        }
        else {
            this.editTextSaleQtyEdit = (EditText) view.findViewById(R.id.editTextSaleQtyEdit);
            this.editTextSaleQtyEdit.setText(String.format("%.2f", qty));
        }

        this.spinnerUoMEdit = (Spinner) view.findViewById(R.id.spinnerUoMEdit);

        if (price % 1 == 0) {
            this.editTextSalePriceEdit = (EditText) view.findViewById(R.id.editTextSalePriceEdit);
            this.editTextSalePriceEdit.setText(String.format("%d", price.intValue()));
        }
        else {
            this.editTextSalePriceEdit = (EditText) view.findViewById(R.id.editTextSalePriceEdit);
            this.editTextSalePriceEdit.setText(String.format("%.2f", price));
        }

        this.textViewSalePriceTotalEdit = (TextView) view.findViewById(R.id.textViewSalePriceTotalEdit);
        this.textViewSalePriceTotalEdit.setText(String.format("%.2f", total));

        this.buttonSaleCancelEdit = (Button) view.findViewById(R.id.buttonSaleCancelEdit);
        this.buttonSaleCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnActionCancel();
            }
        });

        this.buttonSaleSaveEdit = (Button) view.findViewById(R.id.buttonSaleSaveEdit);
        this.buttonSaleSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double qty = Double.parseDouble(SaleOrderLineEditFragment.this.editTextSaleQtyEdit.getText().toString());
                    Double price = Double.parseDouble(SaleOrderLineEditFragment.this.editTextSalePriceEdit.getText().toString());
                    listener.OnActionSave(qty, price, (UnitofMeasure) SaleOrderLineEditFragment.this.spinnerUoMEdit.getSelectedItem(),
                            SaleOrderLineEditFragment.this.position);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.buttonSaleDeleteEdit = (Button) view.findViewById(R.id.buttonSaleDeleteEdit);
        this.buttonSaleDeleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.OnActionDelete(SaleOrderLineEditFragment.this.position);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        unitofMeasureTask = new RetrieveUnitofMeasureIdsTask(url, database, uid, password, new UnitofMeasure(this.uomId, this.uomName));
        unitofMeasureTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        unitofMeasureTask.cancel(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            listener = (OnActionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement SaleOrderLineEditFragment.OnActionListener");
        }
    }

    public void LoadSpinnerUnitofMeasure(ArrayList<UnitofMeasure> unitofMeasures) {
        ArrayAdapter<UnitofMeasure> adapter;
        unitofMeasures.add(0, new UnitofMeasure(this.uomId, this.uomName));
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, unitofMeasures);
        this.spinnerUoMEdit.setAdapter(adapter);
    }

    public interface OnActionListener {
        public void OnActionCancel();
        public void OnActionSave(Double qty, Double price, UnitofMeasure uom, Integer position);
        public void OnActionDelete(Integer position);
    }

    private class RetrieveUnitofMeasureIdsTask extends AsyncTask<Void, Void, ArrayList<UnitofMeasure>> {

        private String database;
        private Integer uid;
        private String password;
        private String url;
        private UnitofMeasure uoM;

        public RetrieveUnitofMeasureIdsTask(String url, String database, Integer uid, String password, UnitofMeasure uoM) {
            this.database = database;
            this.uid = uid;
            this.password = password;
            this.url = url;
            this.uoM = uoM;
        }

        @Override
        protected ArrayList<UnitofMeasure> doInBackground(Void... params) {
            return LoadUnitofMeasures();
        }

        @Override
        protected void onPostExecute(ArrayList<UnitofMeasure> result) {
            LoadSpinnerUnitofMeasure(result);
        }

        private ArrayList<UnitofMeasure> LoadUnitofMeasures(){
            return UnitofMeasureController.readAllUoMFromCategory(this.url, this.database, this.uid, this.password, this.uoM);
        }
    }

}
