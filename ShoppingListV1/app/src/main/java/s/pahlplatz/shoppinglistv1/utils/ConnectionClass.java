package s.pahlplatz.shoppinglistv1.utils;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;

import static android.content.ContentValues.TAG;

/**
 * Created by Stefan on 22-11-2016.
 */

public class ConnectionClass
{
    //private final String connectionString = Resources.getSystem().getString(R.string.ConnectionString);

    @SuppressLint("NewApi")
    public Connection CONN()
    {
        String connectionString = "jdbc:jtds:sqlserver://ShoppingDB.mssql.somee.com;databaseName=ShoppingDB;user=stefan314_SQLLogin_1;password=eigscrfomx;";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            conn = DriverManager.getConnection(connectionString);
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }

        return conn;
    }
}
