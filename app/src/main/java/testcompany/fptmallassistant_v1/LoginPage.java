package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Cleofas.villarin on 1/6/2017.
 */
public class LoginPage extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // full screen here, NG behavior observed if declared in manifest
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loginpage);

        // Handle the button clicks
        final Button login_button = (Button)findViewById(R.id.login_button);

        // and listener
        login_button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!isInternetConnected())
                    return;

                // add login if necessary

                // Then launch the fucking activity
                Intent intentModeOne = new Intent(LoginPage.this, ChatWindow.class);
                startActivity(intentModeOne);
            }
        });

    }

    private boolean isInternetConnected()
    {
        // check whether internet connection is available
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }
}
