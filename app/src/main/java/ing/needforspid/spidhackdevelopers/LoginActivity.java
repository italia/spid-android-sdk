package ing.needforspid.spidhackdevelopers;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.webkit.WebView;
import android.webkit.WebSettings;

import android.content.Intent;
import android.content.Context;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    JavaScriptInterface JSInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://10.0.2.2:8080/spid/spidLogin.jsp");

        JSInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(JSInterface, "JSInterface");

    }

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @android.webkit.JavascriptInterface
        public void changeActivityOk()
        {
            Intent i = new Intent(LoginActivity.this, OKActivity.class);
            startActivity(i);
            finish();
        }

        @android.webkit.JavascriptInterface
        public void changeActivityKo()
        {
            Intent i = new Intent(LoginActivity.this, KOActivity.class);
            startActivity(i);
            finish();
        }
    }
}

