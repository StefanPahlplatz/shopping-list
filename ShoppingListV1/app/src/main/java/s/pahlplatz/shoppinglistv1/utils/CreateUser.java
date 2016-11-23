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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.activities.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by Stefan on 23-11-2016.
 * <p>
 * Represents an asynchronous registration task used to create an account for the user.
 */

public class CreateUser extends AsyncTask<String, String, String>
{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Context ctx;
    private ProgressBar progressBar;
    private Button btn_CreateAccount;
    private Boolean isSuccess;
    private String response;

    public CreateUser(ArrayList<String> credentials, Context ctx, ProgressBar progressBar,
                      Button btn_CreateAccount)
    {
        username = credentials.get(0);
        password = credentials.get(1);
        firstName = credentials.get(2);
        lastName = credentials.get(3);
        this.ctx = ctx;
        this.progressBar = progressBar;
        this.btn_CreateAccount = btn_CreateAccount;
        isSuccess = false;
    }

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onPostExecute(String r)
    {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(ctx, r, Toast.LENGTH_SHORT).show();
        Log.d(TAG, r);

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
            btn_CreateAccount.setEnabled(true);
        }
    }

    @Override
    protected void onCancelled(String r)
    {
        progressBar.setVisibility(View.GONE);
        btn_CreateAccount.setEnabled(true);
        Toast.makeText(ctx, r, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params)
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
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(new Date());

                String query = "DECLARE @responseMessage NVARCHAR(250)\n" +
                        "EXEC dbo.uspAddUser\n" +
                        "          @pLogin = N'" + username + "',\n" +
                        "          @pPassword = N'" + password + "',\n" +
                        "          @pFirstName = N'" + firstName + "',\n" +
                        "          @pLastName = N'" + lastName + "',\n" +
                        "          @pRegDate = N'" + date + "',\n" +
                        "          @responseMessage=@responseMessage OUTPUT\n" +
                        "\n" +
                        "SELECT @responseMessage as N'@responseMessage'";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next())
                {
                    Log.i(TAG, "doInBackground: " + rs.getString(1));
                }
            }
        } catch (Exception ex)
        {
            Log.e(TAG, "doInBackground: Couldn't create account", ex);
            isSuccess = false;
            response = "Exceptions";
        }

        return response;
    }
}
