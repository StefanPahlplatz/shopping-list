package s.pahlplatz.shoppinglistv1.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
    private static final String TAG = FragmentAdd.class.getSimpleName();

    private Database db;
    private SwipeRefreshLayout swipeContainer;
    private ListView lv_Products;
    private Button btn_Add;
    private ArrayList<ArrayList> list;
    private ArrayList<String> productsNotInList;
    private AutoCompleteTextView actv_Product;
    private AddProductAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new Database(getContext().getResources().getString(R.string.ConnectionString)
                , getContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("userid", -1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Configure ListView
        lv_Products = (ListView) view.findViewById(R.id.add_product_lv_Products);
        lv_Products.setLongClickable(true);
        registerForContextMenu(lv_Products);
        new PopulateListView().execute(getContext());

        // Configure the SwipeRefreshLayout
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.add_product_swipeContainer);
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
        actv_Product = (AutoCompleteTextView) view.findViewById(R.id.add_product_autoCompleteTextView);
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
                            if (list.get(0).get(i).toString().toUpperCase()
                                    .contains(actv_Product.getText().toString().toUpperCase()))
                            {
                                // Add the item to the custom lists
                                customProducts.add(list.get(0).get(i).toString());
                                customCount.add((int) list.get(1).get(i));
                            }
                        }

                        lv_Products.setAdapter(new AddProductAdapter(customProducts, customCount
                                , getContext()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        // Fill the list
        new PopulateAutoComplete().execute(getContext());

        // Configure add button
        btn_Add = (Button) view.findViewById(R.id.add_product_btn_add);
        btn_Add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Input
                String productTemp = actv_Product.getText().toString();
                String product;

                // Make first letter capital
                try
                {
                    product = productTemp.substring(0, 1).toUpperCase() + productTemp.substring(1);
                } catch (Exception e)
                {
                    Toast.makeText(getActivity(), "Invalid name!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Exception while making first character capital", e);
                    return;
                }

                // Check if the product is already in the list
                for (int i = 0; i < list.get(0).size(); i++)
                {
                    if (list.get(0).get(i).toString().toUpperCase()
                            .contains(product.trim().toUpperCase()))
                    {
                        Toast.makeText(getActivity(), "Product is already in the list!"
                                , Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // If the product is already in the database
                if (productsNotInList.contains(product))
                {
                    db.updateIsInList(product);
                } else
                {
                    // Add product to the database
                    db.addProduct(product);
                }

                // Create custom lists
                ArrayList<String> customProducts = list.get(0);
                ArrayList<Integer> customCount = list.get(1);

                // Add new item
                customProducts.add(product);
                customCount.add(1);

                // Assign adapter
                AddProductAdapter allProductsAdapter = new AddProductAdapter(customProducts,
                        customCount, getContext());
                lv_Products.setAdapter(allProductsAdapter);

                Toast.makeText(getActivity(), "Added " + product + "!", Toast.LENGTH_SHORT).show();
                actv_Product.setText("");
            }
        });

        return view;
    }

    // Fill the ListView with data from the database
    private class PopulateListView extends AsyncTask<Context, Void, AddProductAdapter>
    {
        @SuppressWarnings("unchecked")
        protected AddProductAdapter doInBackground(Context... params)
        {
            // Get context from param
            Context ctx = params[0];

            // Get list from database
            list = db.getInfoAddProducts();

            // Pass the adapter to onPostExecute
            return new AddProductAdapter(list.get(0),list.get(1), ctx);
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
    private class PopulateAutoComplete extends AsyncTask<Context, Void, ArrayAdapter>
    {
        @SuppressWarnings("unchecked")
        protected ArrayAdapter doInBackground(Context... params)
        {
            try
            {
                // Get context from param
                Context ctx = params[0];

                // Get all products from database
                productsNotInList = db.getProductsNotInList();

                if (productsNotInList.size() == 0)
                    return null;
                else
                    return new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, productsNotInList);
            } catch (Exception ex)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayAdapter adapter)
        {
            if (adapter != null)
            {
                actv_Product.setAdapter(adapter);
            }
        }
    }
}
