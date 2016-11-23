package s.pahlplatz.shoppinglistv1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText et_Username, et_Password;
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

        et_Username = (EditText) view.findViewById(R.id.login_et_username);
        et_Password = (EditText) view.findViewById(R.id.login_et_password);

        progressBar = (ProgressBar) view.findViewById(R.id.login_pb_progress);
        progressBar.setVisibility(View.GONE);

        // Login button
        btn_SignIn = (Button) view.findViewById(R.id.login_btn_login);
        btn_SignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Login();
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

        // Set appropriate button for password keyboard
        et_Password.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    Login();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void Login()
    {
        progressBar.setVisibility(View.VISIBLE);
        btn_SignIn.setEnabled(false);

        // Start login procedure
        new AuthUser(et_Username.getText().toString(), et_Password.getText().toString()
                , getContext(), progressBar, btn_SignIn).execute();
    }
}
