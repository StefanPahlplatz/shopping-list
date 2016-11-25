package s.pahlplatz.shoppinglistv1.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Stefan on 22-11-2016.
 *
 * Database class to interact with the SQL Database in a structured way.
 */

public class Database
{
    private static final String TAG = Database.class.getSimpleName();

    private String conString;
    private Integer userid;

    public Database(String conString, Integer userid)
    {
        this.conString = conString;
        this.userid = userid;
    }

    public void addProduct(String product)
    {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(new Date());

        String query = "INSERT INTO dbo.Products (UserID, Product, Amount, Date, Checked, IsInList) "
                + "VALUES (" + userid + ",'" + product + "',1,'" + date + "',0,1)";

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    public void removeProduct(String product)
    {
        String query = "DELETE FROM dbo.Products WHERE Product='" + product + "' AND UserID=" + userid + "";

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    public void updateCount(String product, boolean increment)
    {
        String update = increment ? "SET Amount=Amount+1" : "SET Amount=Amount-1 ";

        String query = "UPDATE dbo.Products " +
                update +
                "WHERE Product='" + product + "' " +
                "AND UserID=" + userid;

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    public void updateName(String product, String newName)
    {
        String query = "UPDATE dbo.Products " +
                "SET Product='" + newName + "' " +
                "WHERE Product='" + product + "' " +
                "AND UserID=" + userid + "";

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    public void updateIsInList(String product)
    {
        String query = "UPDATE dbo.Products " +
                "SET IsInList=(IsInList^1), Amount=1, Checked=0" +
                "WHERE Product='" + product + "' " +
                "AND UserID=" + userid;

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    public void updateCheckedState(String product)
    {
        String query = "UPDATE dbo.Products " +
                "SET Checked=(Checked^1) " +
                "WHERE Product='" + product + "' " +
                "AND UserID=" + userid;

        Thread myThread = new Thread(new ExecuteQuery(query));
        myThread.start();
    }

    // Return all items that are not in the list
    public ArrayList<String> getProductsNotInList()
    {
        ArrayList<String> products = new ArrayList<>();

        try
        {
            Connection con = new ConnectionClass().CONN(conString);

            if (con == null)
            {
                Log.e(TAG,"No internet connection");
            }
            else
            {
                String query = "SELECT Product " +
                        "FROM dbo.Products " +
                        "WHERE UserID=" + userid +
                        "AND IsInList = 0 " +
                        "ORDER BY Product";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next())
                {
                    products.add(rs.getString(1));
                }
            }

            if (con != null)
                con.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "getAutoCompleteItems: Error while retrieving items from database", ex);
        }

        return products;
    }

    // Returns all Products and Amount for active items for the current user
    public ArrayList<ArrayList> getInfoAddProducts()
    {
        // Create lists for products and count
        ArrayList<String> products = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();

        ArrayList<ArrayList> list = new ArrayList<>();

        // Add all lists to main list
        list.add(products);
        list.add(count);

        try
        {
            Connection con = new ConnectionClass().CONN(conString);

            if (con == null)
            {
                Log.e(TAG, "No internet connection");
            }
            else
            {
                String query = "SELECT Product, Amount " +
                        "FROM dbo.Products " +
                        "WHERE UserID=" + userid +
                        "AND IsInList=1 " +
                        "ORDER BY Product";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next())
                {
                    products.add(rs.getString(1));
                    count.add(rs.getInt(2));
                }
            }

            if (con != null)
                con.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }

        return list;
    }

    // Returns all Products and Amount for active items for the current user
    public ArrayList<String> getInfoAllProducts()
    {
        // Create lists for products and count
        ArrayList<String> products = new ArrayList<>();

        try
        {
            Connection con = new ConnectionClass().CONN(conString);

            if (con == null)
            {
                Log.e(TAG, "No internet connection");
            }
            else
            {
                String query = "SELECT Product " +
                        "FROM dbo.Products " +
                        "WHERE UserID=" + userid +
                        "ORDER BY Product";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next())
                {
                    products.add(rs.getString(1));
                }
            }

            if (con != null)
                con.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }

        return products;
    }

    // Returns all Products and Amount for active items for the current user
    public ArrayList<ArrayList> getInfoCheckList()
    {
        // Create lists for products and count
        ArrayList<String> products = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();
        ArrayList<Integer> checked = new ArrayList<>();

        ArrayList<ArrayList> list = new ArrayList<>();
        list.add(products);
        list.add(count);
        list.add(checked);

        try
        {
            Connection con = new ConnectionClass().CONN(conString);

            if (con == null)
            {
                Log.e(TAG, "No internet connection");
            }
            else
            {
                String query = "SELECT Product, Amount, Checked " +
                        "FROM dbo.Products " +
                        "WHERE UserID=" + userid +
                        "AND IsInList=1" +
                        "ORDER BY Product";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next())
                {
                    products.add(rs.getString(1));
                    count.add(rs.getInt(2));
                    checked.add(rs.getInt(3));
                }
            }

            if (con != null)
                con.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }

        return list;
    }

    private class ExecuteQuery implements Runnable
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
                Connection con = new ConnectionClass().CONN(conString);

                // Test internet
                if (con == null)
                {
                    Log.e(TAG, "No internet connection");
                }
                else
                {
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                    Log.d(TAG, "SQL operation successful: " + query);
                }

                if (con != null)
                    con.close();
            }
            catch (Exception ex)
            {
                Log.e(TAG, "run: Couldn't add product to server", ex);
            }
        }
    }
}