package bennett.dave.autocollect;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import okhttp3.OkHttpClient;

public class Login extends AppCompatActivity {
    private Button loginButton;
    private Button requestButton;
    private GoogleUserCredentialProvider provider;
    private WebView web;
    private EditText textCode;
    private OkHttpClient httpClient;
    private SharedPreferences pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new LoginListener());
        requestButton = (Button) findViewById(R.id.tokenButton);
        requestButton.setVisibility(View.INVISIBLE);
        requestButton.setOnClickListener(new TokenRequest());
        textCode = (EditText) findViewById(R.id.keyText);
        textCode.setVisibility(View.INVISIBLE);
        pf = PreferenceManager.getDefaultSharedPreferences(this);
        httpClient = new OkHttpClient();
        String token = pf.getString("token",null);
        if(token != null)
        {
            PokeController.init(this);
            startNewActivity();

        }

    }

    public void startNewActivity(){

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();

    }

    public void webView(String url)
    {
        Dialog auth_dialog;
        auth_dialog = new Dialog(Login.this);
        auth_dialog.setContentView(R.layout.web_layout);
        web = (WebView)auth_dialog.findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(url);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        auth_dialog.show();
        auth_dialog.setTitle("Authorization");
        auth_dialog.setCancelable(true);

    }

    class LoginListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            try {
                provider = new GoogleUserCredentialProvider(httpClient);
                webView(GoogleUserCredentialProvider.LOGIN_URL);
                requestButton.setVisibility(View.VISIBLE);
                textCode.setVisibility(View.VISIBLE);
            } catch (LoginFailedException e) {
                new Alert("Login Issues","Problem Logging in").buildAlert(getApplicationContext());
                e.printStackTrace();
            } catch (RemoteServerException e) {
                new Alert("Server Issues","Problem Communicating with the Server").buildAlert(getApplicationContext());
                e.printStackTrace();
            }

        }
    }

    class TokenRequest implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        provider.login(textCode.getText().toString());
                        SharedPreferences.Editor editor = pf.edit();
                        editor.putString("token",provider.getRefreshToken());
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PokeController.init(getApplicationContext());
                            }
                        });
                        startNewActivity();
                    } catch (LoginFailedException e) {
                        new Alert("Login Issues","Problem Logging in").buildAlert(getApplicationContext());
                        e.printStackTrace();
                    } catch (RemoteServerException e) {
                        new Alert("Server Issues","Problem Communicating with the Server").buildAlert(getApplicationContext());
                        e.printStackTrace();
                    }

                }
            });
            t.start();


        }
    }

}
