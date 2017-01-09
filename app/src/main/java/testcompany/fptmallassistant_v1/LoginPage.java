package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;

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
                // add login if necessary

                // Then launch the fucking activity
                Intent intentModeOne = new Intent(LoginPage.this, ChatWindow.class);
                startActivity(intentModeOne);
            }
        });

    }
}
