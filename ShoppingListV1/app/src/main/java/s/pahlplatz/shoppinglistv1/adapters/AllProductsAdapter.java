package s.pahlplatz.shoppinglistv1.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.utils.Database;

/**
 * Created by Stefan on 22-11-2016.
 * <p>
 * Custom array adapter to display all items that the user ever entered
 */

public class AllProductsAdapter extends BaseAdapter implements ListAdapter
{
    private final Context ctx;
    private final Database db;
    private ArrayList<String> products;             // All products
    private ArrayList<String> productsInCurrent;    // Products that are in the 'current' list

    public AllProductsAdapter(ArrayList<String> products, Context ctx)
    {
        this.products = products;
        this.ctx = ctx;

        db = new Database(ctx.getResources().getString(R.string.ConnectionString));

        // TODO: GET PRODUCTSINCURRENT LIST FROM DATABASE
        // productsInCurrent = db.
    }

    @Override
    public int getCount()
    {
        return products.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return products.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_allproducts, null);
        }

        // Product name
        TextView tv_Product = (TextView) convertView.findViewById(R.id.list_item);
        tv_Product.setText(products.get(position));

        final Button btn_Add = (Button) convertView.findViewById(R.id.btn_AddItem);
        final RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rl_allproducts_item);

        // If the product is in the current list
        if (productsInCurrent.contains(products.get(position)))
        {
            rl.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAlreadyInList));
            btn_Add.setVisibility(View.INVISIBLE);
        } else
        {
            rl.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorNotInList));
            btn_Add.setVisibility(View.VISIBLE);

            btn_Add.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    rl.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAlreadyInList));
                    btn_Add.setVisibility(View.INVISIBLE);
                    db.addProduct(products.get(position));
                }
            });
        }

        return convertView;
    }
}
