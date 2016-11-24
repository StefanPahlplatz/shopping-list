package s.pahlplatz.shoppinglistv1.fragments;

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

import java.util.ArrayList;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.utils.CreateUser;

/**
 * Created by Stefan on 23-11-2016.
 * <p>
 * Fragment for the registration of new accounts
 */

public class FragmentCreateAccount extends Fragment
{
    private static final String TAG = FragmentLogin.class.getSimpleName();

    private EditText et_Username, et_Password, et_PasswordConfirm, et_FirstName, et_LastName;
    private ProgressBar progressBar;
    private Button btn_CreateAccount;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        // TODO: EditText added is not a TextInputEditText. Please switch to using that class instead.
        et_Username = (EditText) view.findViewById(R.id.createaccount_et_username);
        et_Password = (EditText) view.findViewById(R.id.createaccount_et_password);
        et_PasswordConfirm = (EditText) view.findViewById(R.id.createaccount_et_passwordconfirm);
        et_FirstName = (EditText) view.findViewById(R.id.createaccount_et_firstname);
        et_LastName = (EditText) view.findViewById(R.id.createaccount_et_lastname);
        btn_CreateAccount = (Button) view.findViewById(R.id.createaccount_btn_create);
        progressBar = (ProgressBar) view.findViewById(R.id.createaccount_pb_progress);

        // Hide progressbar
        progressBar.setVisibility(View.GONE);

        // Make the last EditText go button create the account too
        et_LastName.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_NULL)
                    Log.i(TAG, "onEditorAction: Handle login action from here too!");
                return false;
            }
        });

        // Button click
        btn_CreateAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createAccount();
            }
        });

        return view;
    }

    private void createAccount()
    {
        // Check if the input is valid
        if (et_Username.getText().toString().isEmpty())
        {
            et_Username.setError("Please enter a username");
            et_Username.requestFocus();
        } else if (et_Password.getText().toString().isEmpty())
        {

            et_Password.setError("Please enter a password");
            et_Password.requestFocus();
        } else if (et_PasswordConfirm.getText().toString().isEmpty())
        {
            et_PasswordConfirm.setError("Please confirm your password");
            et_PasswordConfirm.requestFocus();
        } else if (!et_Password.getText().toString().equals(et_PasswordConfirm.getText().toString()))
        {
            et_PasswordConfirm.setError("Passwords do not match");
            et_PasswordConfirm.requestFocus();
        } else if (et_FirstName.getText().toString().isEmpty())
        {
            et_FirstName.setError("Please enter your first name");
            et_FirstName.requestFocus();
        } else if (et_LastName.getText().toString().isEmpty())
        {
            et_LastName.setError("Please enter your last name");
            et_LastName.requestFocus();
        } else
        {
            progressBar.setVisibility(View.VISIBLE);
            btn_CreateAccount.setEnabled(false);

            ArrayList<String> credentials = new ArrayList<>();
            credentials.add(et_Username.getText().toString());
            credentials.add(et_Password.getText().toString());
            credentials.add(et_FirstName.getText().toString());
            credentials.add(et_LastName.getText().toString());

            // Start account creation
            new CreateUser(credentials, getContext(), progressBar, btn_CreateAccount).execute();
        }
    }
}
