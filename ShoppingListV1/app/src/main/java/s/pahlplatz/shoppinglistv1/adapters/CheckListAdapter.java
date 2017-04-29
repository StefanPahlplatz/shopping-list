package s.pahlplatz.shoppinglistv1.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.utils.Database;

/**
 * Created by Stefan on 25-11-2016.
 * <p>
 * Adapter for checking your items
 */

public class CheckListAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = CheckListAdapter.class.getSimpleName();

    private ArrayList<String> products = new ArrayList<>();
    private ArrayList<Integer> count = new ArrayList<>();
    private ArrayList<Integer> checked = new ArrayList<>();
    private Context ctx;
    private Database db;

    public CheckListAdapter(ArrayList<String> products, ArrayList<Integer> count
            , ArrayList<Integer> checked, Context ctx) {
        this.products = products;
        this.count = count;
        this.checked = checked;
        this.ctx = ctx;

        db = new Database(ctx.getResources().getString(R.string.ConnectionString)
                , ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("userid", -1));
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int pos) {
        return products.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview_checklist, null);
        }

        // Display product
        TextView listItemText = (TextView) view.findViewById(R.id.list_item_all);
        listItemText.setText(products.get(position));

        // Display count
        TextView listItemCount = (TextView) view.findViewById(R.id.list_item_count_final);
        listItemCount.setText(String.format(count.get(position).toString()));

        final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.all_products_relativelayout);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_Gottem);

        checkBox.setChecked(checked.get(position) == 1);
        relativeLayout.setBackgroundColor(checkBox.isChecked()
                ? ContextCompat.getColor(ctx, R.color.colorAlreadyInList)
                : ContextCompat.getColor(ctx, R.color.colorNotInList));

        // Clicking anywhere in the relativelayout will trigger the checkbox click
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateState(position, relativeLayout, checkBox);
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
                updateState(position, relativeLayout, checkBox);
            }
        });
        listItemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
                updateState(position, relativeLayout, checkBox);
            }
        });
        listItemCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
                updateState(position, relativeLayout, checkBox);
            }
        });

        return view;
    }

    private void updateState(int position, RelativeLayout relativeLayout, CheckBox checkBox) {
        // Update checked state in server
        db.updateCheckedState(products.get(position));
        checked.set(position, checked.get(position) == 1 ? 0 : 1);

        // Update background colour
        relativeLayout.setBackgroundColor(checkBox.isChecked()
                ? ContextCompat.getColor(ctx, R.color.colorAlreadyInList)
                : ContextCompat.getColor(ctx, R.color.colorNotInList));
    }

    public ArrayList<Integer> getSelected() {
        ArrayList<Integer> retList = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.get(i) == 1) {
                retList.add(i);
            }
        }
        return retList;
    }
}
