package s.pahlplatz.shoppinglistv1.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.utils.Database;

/**
 * Created by Stefan on 22-11-2016.
 * <p>
 * Custom array adapter for FragmentAdd
 */

public class AddProductAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = AddProductAdapter.class.getSimpleName();

    private final Database db;

    private ArrayList<String> list;
    private ArrayList<Integer> count;
    private Context ctx;

    public AddProductAdapter(ArrayList<String> products, ArrayList<Integer> count, Context ctx) {
        this.list = products;
        this.count = count;
        this.ctx = ctx;

        db = new Database(ctx.getResources().getString(R.string.ConnectionString)
                , ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("userid", -1));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview_item, null);
        }

        // Display product
        TextView tv_Product = (TextView) view.findViewById(R.id.list_item_string);
        tv_Product.setText(list.get(position));

        // Display Count
        TextView tv_Count = (TextView) view.findViewById(R.id.list_item_count);
        tv_Count.setText(String.format(count.get(position).toString()));

        // Assign buttons
        Button btn_Delete = (Button) view.findViewById(R.id.delete_btn);
        Button btn_Add = (Button) view.findViewById(R.id.add_btn);

        // Decrease count / remove item
        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count.get(position) == 1) {
                    DialogInterface.OnClickListener dialogClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        // Remove from server
                                        db.updateIsInList(list.get(position));

                                        // Remove from client
                                        list.remove(position);
                                        count.remove(position);

                                        notifyDataSetChanged();
                                    }
                                }
                            };

                    // Show Yes/No dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("Are you sure you want to remove " + list.get(position)
                            + "?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    // Adjust value in server
                    db.updateCount(list.get(position), false);

                    // Adjust value in client
                    count.set(position, count.get(position) - 1);
                }
                notifyDataSetChanged();
            }
        });

        // Increment count
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adjust value in server
                db.updateCount(list.get(position), true);

                // Adjust value in client
                count.set(position, count.get(position) + 1);

                notifyDataSetChanged();
            }
        });

        return view;
    }
}
