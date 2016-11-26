package s.pahlplatz.shoppinglistv1.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.utils.AuthUser;

/**
 * Created by Stefan on 22-11-2016.
 *
 * Fragment to log in to the users personal account.
 */

public class FragmentLogin extends Fragment
{
    private static final String TAG = FragmentLogin.class.getSimpleName();

    private TextInputLayout et_Username, et_Password;
    private ProgressBar progressBar;
    private Button btn_SignIn;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        et_Username = (TextInputLayout) view.findViewById(R.id.login_til_username);
        et_Password = (TextInputLayout) view.findViewById(R.id.login_til_password);

        progressBar = (ProgressBar) view.findViewById(R.id.login_pb_progress);
        progressBar.setVisibility(View.GONE);

        // Login button
        btn_SignIn = (Button) view.findViewById(R.id.login_btn_login);
        btn_SignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        // Create new account
        Button btn_CreateAcc = (Button) view.findViewById(R.id.login_btn_create);
        btn_CreateAcc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Class fragmentClass = FragmentCreateAccount.class;
                Fragment fragment = null;
                try
                {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception ex)
                {
                    Log.e(TAG, "onClick: Couldn't create fragment instance", ex);
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.loginactivity_container, fragment)
                        .addToBackStack("loginFragment")
                        .commit();
            }
        });

        // Set appropriate button for password keyboard
        //noinspection ConstantConditions
        et_Password.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    login();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void login()
    {
        // Verify credentials
        if (et_Username.getEditText().getText().toString().isEmpty())
        {
            et_Username.getEditText().setError("Please enter your group name");
            et_Username.requestFocus();
        } else if (et_Password.getEditText().getText().toString().isEmpty())
        {
            et_Password.getEditText().setError("Please enter your password");
            et_Password.requestFocus();
        } else
        {
            progressBar.setVisibility(View.VISIBLE);
            btn_SignIn.setEnabled(false);

            // Start login procedure
            new AuthUser(et_Username.getEditText().getText().toString()
                    , et_Password.getEditText().getText().toString()
                    , getContext(), progressBar, btn_SignIn).execute();
        }
    }
}
