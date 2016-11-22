package s.pahlplatz.shoppinglistv1.utils;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Stefan on 22-11-2016.
 *
 * Class to help the database make a connection by creating the connection here and then
 * passing it to the database class.
 */

class ConnectionClass
{
    private static final String TAG = ConnectionClass.class.getSimpleName();

    @SuppressLint("NewApi")
    Connection CONN(String conString)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            conn = DriverManager.getConnection(conString);
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }

        return conn;
    }
}
