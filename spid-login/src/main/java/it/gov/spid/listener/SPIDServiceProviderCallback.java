package it.gov.spid.listener;

/**
 * Created by matteo on 07/10/17.
 */

public interface SPIDServiceProviderCallback {
    public void onServiceProviderResponse(String response);
    public void onServiceProviderError(String response);
}