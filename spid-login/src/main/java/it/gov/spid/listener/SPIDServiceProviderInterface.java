package it.gov.spid.listener;

import android.content.Context;

/**
 * Created by matteo on 07/10/17.
 */

public interface SPIDServiceProviderInterface {
    void requestServiceProviderLoginExchange(SPIDServiceProviderCallback callback, Context context);
    void sendIdentityProviderLoginExchangeResponse(SPIDServiceProviderCallback callback, Context context);
}