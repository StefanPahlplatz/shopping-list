package s.pahlplatz.shoppinglistv1.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import java.util.ArrayList;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.adapters.AddProductAdapter;
import s.pahlplatz.shoppinglistv1.utils.Database;

/**
 * Created by Stefan on 22-11-2016.
 *
 * Fragment for adding products to the list and adjusting the amount for each product.
 */

public class FragmentAdd extends Fragment
{
    private Database db;

    private SwipeRefreshLayout swipeContainer;
    private ListView lv_Products;
    private ArrayList<ArrayList> list;
    private AutoCompleteTextView actv_Product;
    private AddProductAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences sharedPref = getContext().getSharedPreferences("pahlplatz.s", Context.MODE_PRIVATE);
        db = new Database(getContext().getResources().getString(R.string.ConnectionString), sharedPref.getInt("userid", -1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Configure ListView
        lv_Products = (ListView) view.findViewById(R.id.lv_Products);
        lv_Products.setLongClickable(true);
        registerForContextMenu(lv_Products);
        new PopulateListView().execute(getContext());

        // Configure the SwipeRefreshLayout
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new PopulateListView().execute(getContext());
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Configure AutoCompleteTextView
        actv_Product = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        actv_Product.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            // Show the results matching the word in the AutoCompleteTextView
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (actv_Product.getText().toString().isEmpty())
                {
                    lv_Products.setAdapter(adapter);
                }
                else
                {
                    // Create custom lists
                    ArrayList<String> customProducts = new ArrayList<>();
                    ArrayList<Integer> customCount = new ArrayList<>();

                    // Loop through all product names
                    if(list != null)
                    {
                        for (int i = 0; i < list.get(0).size(); i++)
                        {
                            // If the list contains the current autocomplete text
                            if (list.get(0).get(i).toString().toUpperCase().contains(actv_Product.getText().toString().toUpperCase()))
                            {
                                // Add the item to the custom lists
                                customProducts.add(list.get(0).get(i).toString());
                                customCount.add((int) list.get(1).get(i));
                            }
                        }

                        lv_Products.setAdapter(new AddProductAdapter(customProducts, customCount, getContext()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        new PopulateAutoComplete().execute();

        return view;
    }

    // Fill the ListView with data from the database
    private class PopulateListView extends AsyncTask<Context, Void, AddProductAdapter>
    {
        @SuppressWarnings("unchecked")
        protected AddProductAdapter doInBackground(Context... params)
        {
            // Get context from param
            Context context = params[0];

            // Get list from database
            list = db.getAllInfo();

            // Pass the adapter to onPostExecute
            return new AddProductAdapter(list.get(0),list.get(1), context);
        }

        protected void onPostExecute(AddProductAdapter param)
        {
            // Assign the adapter
            adapter = param;
            lv_Products.setAdapter(adapter);

            if(swipeContainer.isRefreshing())
            {
                swipeContainer.setRefreshing(false);
            }
        }
    }

    // Assign adapter for the AutoCompleteTextView
    private class PopulateAutoComplete extends AsyncTask<Void, Void, ArrayAdapter>
    {
        private PopulateAutoComplete()
        {
            super();
        }

        @SuppressWarnings("unchecked")
        protected ArrayAdapter doInBackground(Void... params)
        {
            // Get all products from database
            ArrayList<String> allProducts = db.getAllProducts();

            {
                // Trim results
                for (int i = 0; i < allProducts.size(); i++)
                {
                    allProducts.set(i, allProducts.get(i).trim());
                }
            }

            return new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, allProducts);
        }

        @Override
        protected void onPostExecute(ArrayAdapter adapter)
        {
            actv_Product.setAdapter(adapter);
        }
    }
}
