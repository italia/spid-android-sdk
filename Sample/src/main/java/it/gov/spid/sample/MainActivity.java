package it.gov.spid.sample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.gov.spid.listener.SPIDLoginListener;
import it.gov.spid.listener.SPIDServiceProviderCallback;
import it.gov.spid.listener.SPIDServiceProviderInterface;
import it.gov.spid.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginButton loginButton = findViewById(R.id.loginButton);

        //listener per il risultato finale
        loginButton.setSPIDLoginListener(new SPIDLoginListener() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Response: " + response);
            }

            @Override
            public void onFailure(String message) {
                System.out.println("Failure: " + message);
            }
        });

        //implementazione del provider per la comunicazione con il service provider
        loginButton.setSpidServiceProvider(new SPIDServiceProviderInterface() {
            //Prima request di scambio con il service provider della login request
            @Override
            public void requestServiceProviderLoginExchange(SPIDServiceProviderCallback callback, Context context) {
                callback.onServiceProviderResponse("Test response");
            }

            //request finale al service provider
            @Override
            public void sendIdentityProviderLoginExchangeResponse(SPIDServiceProviderCallback callback, Context context) {
                callback.onServiceProviderResponse("Test response");
            }
        });
    }
}
