package cr.clearcorp.odoo.saleorderclient.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cr.clearcorp.odoo.saleorderclient.R;
import cr.clearcorp.odoo.saleorderclient.models.Product;

public class ProductAdapter extends ArrayAdapter<Product> {

    private int resource;

    public ProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);

        }

        // Lookup view for data population
        ImageView imageViewProduct = (ImageView) convertView.findViewById(R.id.imageViewProduct);
        if (!product.getImageMedium().isEmpty()) {
            byte[] decodedString = Base64.decode(product.getImageMedium(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageViewProduct.setImageBitmap(decodedByte);
        }
        else {
            imageViewProduct.setImageResource(R.drawable.placeholder);
        }

        TextView textViewProduct = (TextView) convertView.findViewById(R.id.textViewProduct);
        textViewProduct.setText(product.toString());

        // Return the completed view to render on screen
        return convertView;
    }
}
