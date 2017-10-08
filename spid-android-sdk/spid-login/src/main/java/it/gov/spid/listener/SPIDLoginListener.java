package it.gov.spid.listener;

/**
 * Created by matteo on 07/10/17.
 */

public interface SPIDLoginListener {
    void onSuccess(String response);
    void onFailure(String message);
}