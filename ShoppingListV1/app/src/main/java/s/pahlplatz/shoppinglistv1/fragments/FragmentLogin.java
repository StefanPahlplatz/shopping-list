package s.pahlplatz.shoppinglistv1.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.activities.MainActivity;
import s.pahlplatz.shoppinglistv1.utils.ConnectionClass;

/**
 * Created by Stefan on 22-11-2016.
 * <p>
 * Fragment to log in to the users personal account.
 */

public class FragmentLogin extends Fragment
{
    private static final String TAG = FragmentLogin.class.getSimpleName();

    // UI references.
    private EditText et_Username, et_Password;
    private View view_Login;
    private ProgressBar pbbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        et_Username = (EditText) view.findViewById(R.id.login_et_username);
        et_Password = (EditText) view.findViewById(R.id.login_et_password);
        view_Login = view.findViewById(R.id.login_layout);

        pbbar = (ProgressBar) view.findViewById(R.id.login_pb_progress);
        pbbar.setVisibility(View.GONE);

        // Set appropriate button for password keyboard
        et_Password.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    DoLogin doLogin = new DoLogin();
                    doLogin.execute("");
                    return true;
                }
                return false;
            }
        });

        // Login button
        Button btn_SignIn = (Button) view.findViewById(R.id.login_btn_login);
        btn_SignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
            }
        });

        // Create new account
        Button btn_CreateAcc = (Button) view.findViewById(R.id.login_btn_create);
        btn_CreateAcc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: GO TO NEW FRAGMENT TO CREATE AN ACCOUNT
            }
        });

        return view;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DoLogin extends AsyncTask<String, String, String>
    {
        String z = "";
        Boolean isSuccess = false;

        // Store credentials
        String username = et_Username.getText().toString();
        String password = et_Password.getText().toString();

        // Make the progressbar visible
        @Override
        protected void onPreExecute()
        {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            // Make the progressbar invisible
            pbbar.setVisibility(View.GONE);

            // Display message from doInBackground
            Toast.makeText(getContext(), r, Toast.LENGTH_SHORT).show();

            Log.d(TAG, r);

            if (isSuccess)
            {
                // Create main activity
                Intent mainIntent = new Intent(getContext(), MainActivity.class);

                // Prevent backwards navigation
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start login activity
                startActivity(mainIntent);

                // Stop this activity
                getActivity().finish();
            }
        }

        @Override
        protected void onCancelled(String r)
        {
            // Make the progressbar invisible
            pbbar.setVisibility(View.GONE);

            // Display message from doInBackground
            Toast.makeText(getContext(), r, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            // Check if the username and password are valid
            if (username.trim().equals("") || password.trim().equals(""))
            {
                z = "Please enter User Id and Password";
            } else
            {
                try
                {
                    ConnectionClass con = new ConnectionClass();
                    Connection connection = con.CONN(getContext().getResources().getString(R.string.ConnectionString));

                    if (connection == null)
                    {
                        z = "Error in connection with SQL server";
                    } else
                    {
                        String query = "DECLARE	@responseMessage nvarchar(250) EXEC	dbo.uspLogin @pLoginName = N'" + username + "', @pPassword = N'" + password + "', @responseMessage = @responseMessage OUTPUT SELECT @responseMessage as N'@responseMessage'";

                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next())
                        {
                            z = rs.getString(1);
                            Log.d(TAG, "Server response: " + z);

                            switch (z)
                            {
                                case "Invalid login":
                                    isSuccess = false;
                                    continue;
                                case "Incorrect password":
                                    isSuccess = false;
                                    break;
                                case "User successfully logged in":
                                    SharedPreferences sp = getContext().getSharedPreferences("Preferences", 0);
                                    sp.edit().putBoolean("logged_in", true).apply();
                                    isSuccess = true;
                                    break;
                            }
                        }
                    }
                } catch (Exception ex)
                {
                    Log.e("Exception", ex.toString());
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }
}
