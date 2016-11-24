package s.pahlplatz.shoppinglistv1.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.activities.MainActivity;

/**
 * Created by Stefan on 23-11-2016.
 *
 * Represents an asynchronous login task used to authenticate
 * the user.
 */

public class AuthUser extends AsyncTask<String, String, String>
{
    private static final String TAG = AuthUser.class.getSimpleName();

    private String response = "";
    private Boolean isSuccess = false;

    private String username;
    private String password;
    private Context ctx;
    private ProgressBar progressBar;
    private Button btn_Login;

    public AuthUser(String username, String password, Context ctx, ProgressBar progressBar,
                    Button btn_Login)
    {
        this.username = username;
        this.password = password;
        this.ctx = ctx;
        this.progressBar = progressBar;
        this.btn_Login = btn_Login;
    }

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onPostExecute(String r)
    {
        progressBar.setVisibility(View.GONE);

        if (isSuccess)
        {
            // Create main activity
            Intent mainIntent = new Intent(ctx, MainActivity.class);

            // Prevent backwards navigation
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start login activity
            ctx.startActivity(mainIntent);
        } else
        {
            btn_Login.setEnabled(true);
        }
    }

    @Override
    protected void onCancelled(String r)
    {
        progressBar.setVisibility(View.GONE);
        btn_Login.setEnabled(true);
        Toast.makeText(ctx, r, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params)
    {
        // Check if the username and password are valid
        if (username.trim().equals("") || password.trim().equals(""))
        {
            response = "Please enter username and password first";
        } else
        {
            try
            {
                ConnectionClass con = new ConnectionClass();
                Connection connection = con.CONN(ctx.getResources().getString(R.string.ConnectionString));

                if (connection == null)
                {
                    response = "Error in connection with SQL server";
                } else
                {
                    // Query to run the Login procedure in the server
                    String query = "DECLARE	@responseMessage nvarchar(250)" +
                            "EXEC dbo.uspLogin " +
                            "@pLoginName = N'" + username + "'," +
                            "@pPassword = N'" + password + "'," +
                            "@responseMessage = @responseMessage " +
                            "OUTPUT SELECT @responseMessage as N'@responseMessage'";

                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next())
                    {
                        response = rs.getString(1);

                        switch (response)
                        {
                            case "Invalid login":
                            case "Incorrect password":
                                isSuccess = false;
                                break;
                            default:
                                ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                        .edit()
                                        .putInt("userid", rs.getInt(1))
                                        .apply();
                                isSuccess = true;

                                Log.d(TAG, "doInBackground() returned: " + rs.getInt(1));
                                break;
                        }
                    }
                }
            } catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: Couldn't log in", ex);
                isSuccess = false;
                response = "Exceptions";
            }
        }
        return response;
    }
}

