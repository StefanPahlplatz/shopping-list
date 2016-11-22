package s.pahlplatz.shoppinglistv1.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.fragments.FragmentLogin;

public class LoginActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Load fragment
        if (savedInstanceState == null)
        {
            Fragment fragment = null;
            Class fragmentClass;
            fragmentClass = FragmentLogin.class;

            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.loginactivity_container, fragment).commit();
        }
    }
}
