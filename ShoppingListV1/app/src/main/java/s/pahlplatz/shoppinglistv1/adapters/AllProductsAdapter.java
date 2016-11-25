package s.pahlplatz.shoppinglistv1.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
    private ArrayList<String> allproducts;
    private ArrayList<String> productsInList;

    public AllProductsAdapter(ArrayList<String> allproducts, ArrayList<String> productsInList, Context ctx)
    {
        this.allproducts = allproducts;
        this.productsInList = productsInList;
        this.ctx = ctx;

        SharedPreferences sharedPref = ctx.getSharedPreferences("pahlplatz.s", Context.MODE_PRIVATE);
        db = new Database(ctx.getResources().getString(R.string.ConnectionString)
                , sharedPref.getInt("userid", -1));
    }

    @Override
    public int getCount()
    {
        return allproducts.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return allproducts.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_allproducts, null);
        }

        // TODO: Only products that are not in the list show up

        // Product name
        TextView tv_Product = (TextView) convertView.findViewById(R.id.list_item);
        tv_Product.setText(allproducts.get(position));

        final Button btn_Add = (Button) convertView.findViewById(R.id.btn_AddItem);
        final RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rl_allproducts_item);

        // If the product is in the current list
        if (productsInList.contains(allproducts.get(position)))
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
                    db.updateIsInList(allproducts.get(position));
                }
            });
        }

        return convertView;
    }
}
