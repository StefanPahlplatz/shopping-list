package s.pahlplatz.shoppinglistv1.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Stefan on 22-11-2016.
 */

public class Database
{
    public void removeProduct(String product)
    {
        String query = "DELETE FROM dbo.Products WHERE Product='"+ product +"'";

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    public void adjustCount(String product, Integer newCount)
    {
        String query = "UPDATE dbo.Products SET Amount=" + newCount + "WHERE Product='" + product + "'";

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    // Return everything from dbo.AllProducts
    public ArrayList<String> getAllProducts()
    {
        ArrayList<String> products = new ArrayList<>();

        try
        {
            Connection con = new ConnectionClass().CONN();

            if (con == null)
            {
                Log.e(TAG,"No internet connection");
            }
            else
            {
                String query = "select * from dbo.AllProducts ORDER BY Product";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next())
                {
                    products.add(rs.getString(1));
                }
            }
            con.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }

        return products;
    }

    // Return everything from dbo.Products
    public ArrayList<ArrayList> getAllInfo()
    {
        // Create lists for every column
        ArrayList<String> products = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<Integer> checked = new ArrayList<>();

        ArrayList<ArrayList> list = new ArrayList<>();

        // Add all lists to main list
        list.add(products);
        list.add(count);
        list.add(date);
        list.add(checked);

        try
        {
            Connection con = new ConnectionClass().CONN();

            if (con == null)
            {
                Log.e(TAG, "No internet connection");
            }
            else
            {
                String query = "select * from dbo.Products ORDER BY Product";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next())
                {
                    products.add(rs.getString(2));
                    count.add(rs.getInt(3));
                    date.add(rs.getString(4));
                    checked.add(rs.getInt(5));
                }
            }
            con.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }

        return list;
    }

    class ExecuteQuery implements Runnable
    {
        String query;

        ExecuteQuery(String query)
        {
            this.query = query;
        }

        public void run()
        {
            try
            {
                Connection con = new ConnectionClass().CONN();

                // Test internet
                if (con == null)
                {
                    Log.e(TAG, "No internet connection");
                }
                else
                {
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                    Log.d(TAG, "SQL operation successful");
                }
                con.close();
            }
            catch (Exception ex)
            {
                Log.e(TAG, ex.toString());
            }
        }
    }
}