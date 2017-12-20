package it.gov.spid.listener.impl;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import it.gov.spid.listener.SPIDServiceProviderCallback;
import it.gov.spid.listener.SPIDServiceProviderInterface;

/**
 * Created by matteo on 08/10/17.
 */

//TODO questa classe è pensata per dare la possibilità agli utenti che usano https://github.com/italia/spid-passport o simili di
//avere una classe più semplice per implementare la comunicazione fra service provider e client
//Le chiamate volley eseguite sono solo a titolo di esempio
public class DefaultSPIDServiceProviderInterface implements SPIDServiceProviderInterface {
    private String uri;

    public DefaultSPIDServiceProviderInterface(String uri) {
        this.uri = uri;
    }

    @Override
    public void requestServiceProviderLoginExchange(final SPIDServiceProviderCallback callback, final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onServiceProviderResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onServiceProviderError(error.getMessage());
                    }
                });
        queue.add(stringRequest);
    }

    @Override
    public void sendIdentityProviderLoginExchangeResponse(final SPIDServiceProviderCallback callback, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onServiceProviderResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onServiceProviderError(error.getMessage());
                    }
                });
        queue.add(stringRequest);
    }
}