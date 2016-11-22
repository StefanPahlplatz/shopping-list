package s.pahlplatz.shoppinglistv1.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.adapters.AllProductsAdapter;
import s.pahlplatz.shoppinglistv1.utils.Database;

/**
 * Created by Stefan on 22-11-2016.
 * <p>
 * Fragment for showing all the products that the user ever entered.
 */

public class FragmentAll extends Fragment
{
    private static final String TAG = FragmentAll.class.getSimpleName();

    private ArrayList<String> products;
    private SwipeRefreshLayout swipeContainer;
    private ListView lv_Products;
    private Database db;
    private AllProductsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_all_products, container, false);

        SharedPreferences sharedPref = getContext().getSharedPreferences("pahlplatz.s", Context.MODE_PRIVATE);
        db = new Database(getContext().getResources().getString(R.string.ConnectionString), sharedPref.getInt("userid", -1));

        lv_Products = (ListView) view.findViewById(R.id.lv_Products);
        lv_Products.setLongClickable(true);
        registerForContextMenu(lv_Products);
        new PopulateListView().execute(getContext());

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new PopulateListView().execute(getContext());
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId() == R.id.lv_Products)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.setHeaderTitle(products.get(info.position));

            String[] menuItems = {"Hernoemen", "Verwijderen"};

            for (int i = 0; i < menuItems.length; i++)
            {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int menuItemIndex = item.getItemId();
        String[] menuItems = {"Rename", "Delete"};
        String menuItemName = menuItems[menuItemIndex];
        final String listItemName = products.get(info.position);

        switch (menuItemIndex)
        {
            // Rename
            case 0:
                Log.d(TAG, "Selected Rename, item: " + listItemName);

                // Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(menuItemName);

                // Set up the input
                final EditText input = new EditText(getContext());
                input.setText(listItemName);

                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String m_Text = input.getText().toString();

                        // Check if new name is already in the current list
                        for (int i = 0; i < lv_Products.getCount(); i++)
                        {
                            if (input.toString().toUpperCase().equals(listItemName.toUpperCase()))
                            {
                                // Product is already in main list
                                Toast.makeText(getActivity(), "Product is already in the list!"
                                        , Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Update name in server
                        db.updateName(listItemName, m_Text);

                        // Update name in client
                        new PopulateListView().execute(getContext()); //TODO: Updating after renaming is very slow, just update locally

                        // Product renamed!
                        Toast.makeText(getActivity(), "Product hernoemd!", Toast.LENGTH_SHORT).show();

                        //TODO: What if the changed name is already in the main list?
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            // Verwijderen
            case 1:
                Log.d(TAG, "Selected Delete");

                // Remove item from client
                products.remove(menuItemIndex);
                lv_Products.setAdapter(new AllProductsAdapter(products, getContext()));

                // Remove item from server
                db.removeProduct(listItemName);

                Toast.makeText(getActivity(), "Product deleted!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    private class PopulateListView extends AsyncTask<Context, Void, AllProductsAdapter>
    {
        protected AllProductsAdapter doInBackground(Context... params)
        {
            // Get context from param
            Context context = params[0];

            // Get all products
            products = db.getAllProducts();

            // Pass the caa to onPostExecute
            return new AllProductsAdapter(products, context);
        }

        protected void onPostExecute(AllProductsAdapter param)
        {
            // Assign the adapter
            lv_Products.setAdapter(param);

            adapter = param;

            if (swipeContainer.isRefreshing())
            {
                swipeContainer.setRefreshing(false);
            }
        }
    }
}
