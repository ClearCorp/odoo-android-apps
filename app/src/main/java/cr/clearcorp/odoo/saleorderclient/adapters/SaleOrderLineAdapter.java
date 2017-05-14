package cr.clearcorp.odoo.saleorderclient.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cr.clearcorp.odoo.saleorderclient.R;
import cr.clearcorp.odoo.saleorderclient.models.Product;
import cr.clearcorp.odoo.saleorderclient.models.SaleOrderLine;

public class SaleOrderLineAdapter extends ArrayAdapter<SaleOrderLine> {

    private int resource;

    public SaleOrderLineAdapter(Context context, int resource, List<SaleOrderLine> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        final SaleOrderLine line = getItem(position);
        final ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lookup view for data population
        viewHolder.textViewSaleQty = (TextView) convertView.findViewById(R.id.textViewSaleQty);
        viewHolder.textViewSaleQty.setText(String.format("%.2f", line.getQuantity()));

        viewHolder.textViewSaleProduct = (TextView) convertView.findViewById(R.id.textViewSaleProduct);
        viewHolder.textViewSaleProduct.setText(line.getProduct().toString());

        viewHolder.textViewSaleUom = (TextView) convertView.findViewById((R.id.textViewSaleUom));
        viewHolder.textViewSaleUom.setText(line.getUom().toString());

        viewHolder.textViewSalePrice = (TextView) convertView.findViewById(R.id.textViewSalePrice);
        viewHolder.textViewSalePrice.setText(String.format("%.2f", line.getPrice()));

        viewHolder.textViewSalePriceTotal = (TextView) convertView.findViewById(R.id.textViewSalePriceTotal);
        viewHolder.textViewSalePriceTotal.setText(String.format("%.2f", line.getPrice() * line.getQuantity()));

        // Return the completed view to render on screen
        return convertView;

    }

    static class ViewHolder {
        protected TextView textViewSaleQty;
        protected TextView textViewSaleProduct;
        protected TextView textViewSaleUom;
        protected TextView textViewSalePrice;
        protected TextView textViewSalePriceTotal;

    }

}
