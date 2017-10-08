package it.gov.spid.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import it.gov.spid.IdentityProviders;
import it.gov.spid.R;
import it.gov.spid.adapter.IdentityProviderAdapter;
import it.gov.spid.dialog.WebDialog;
import it.gov.spid.exception.SPIDException;
import it.gov.spid.listener.SPIDLoginListener;
import it.gov.spid.listener.SPIDServiceProviderCallback;
import it.gov.spid.listener.SPIDServiceProviderInterface;

/**
 * Created by matteo on 07/10/17.
 */

//TODO il loginButton dovrebbe implementare le linee guida di design del bottone spid, quali il colore
public class LoginButton extends android.support.v7.widget.AppCompatButton {
    //lister per gli utilizzatori del LoginButton
    private OnClickListener externalOnClickListener;

    //listener usato dalla libreria per chiamare il serviceProvider e identityProvider
    private final OnClickListener internalOnClickListener = new LoginClickListener();

    //Listener sul web dialog
    private WebDialogOnCompleteListener webDialogOnCompleteListener = new WebDialogOnCompleteListener();

    //Provider per l'implementazione delle comunicazioni con il server provider
    private SPIDServiceProviderInterface spidServiceProvider = new SPIDServiceProviderInterface() {
        @Override
        public void requestServiceProviderLoginExchange(SPIDServiceProviderCallback callback, Context context) {
            callback.onServiceProviderError("Please setSpidServiceProvider on LoginButton ");
        }

        @Override
        public void sendIdentityProviderLoginExchangeResponse(SPIDServiceProviderCallback callback, Context context) {
            callback.onServiceProviderError("Please setSpidServiceProvider on LoginButton ");
        }
    };

    //Listener sul risultato finale del login button
    private SPIDLoginListener spidLoginListener = new SPIDLoginListener() {
        @Override
        public void onSuccess(String response) {
            Toast.makeText(getContext(), "Success: " + response, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(String message) {
            Toast.makeText(getContext(), "Failure: " + message, Toast.LENGTH_SHORT).show();
        }
    };

    public LoginButton(Context context) {
        super(context);
        configureButton(context, null);
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureButton(context, attrs);
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configureButton(context, attrs);
    }

    public void setSpidServiceProvider(SPIDServiceProviderInterface spidServiceProvider) {
        this.spidServiceProvider = spidServiceProvider;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.externalOnClickListener = l;
    }

    protected void configureButton(Context context, AttributeSet attrs) {
        if(isInEditMode()) {
            return;
        }

        this.configureOnClickLister();

        setText(getResources().getText(R.string.login_with_spid));

        setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_spid_ico_circle_bb_bg), null , null , null);
    }

    private void configureOnClickLister() {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internalOnClickListener != null) {
                    internalOnClickListener.onClick(v);
                } else if(externalOnClickListener != null){
                    externalOnClickListener.onClick(v);
                }
            }
        });
    }

    protected void callExternalOnClickListener(final View v) {
        if (externalOnClickListener != null) {
            externalOnClickListener.onClick(v);
        }
    }

    protected Activity getActivity() {
        Context context = getContext();
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (context instanceof Activity) {
            return (Activity) context;
        }

        throw new RuntimeException("Unable to get Activity.");
    }

    public void setSPIDLoginListener(SPIDLoginListener spidLoginListener) {
        this.spidLoginListener = spidLoginListener;
    }

    protected class LoginClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            callExternalOnClickListener(v);
            initiateLogin();
        }

        private void initiateLogin() {
            IdentityProviderAdapter adapter = new IdentityProviderAdapter(IdentityProviders.values());
            final MaterialDialog dialog = new MaterialDialog.Builder(LoginButton.this.getContext()).title(R.string.auth_provider_dialog_title
            ).adapter(adapter, null).show();

            adapter.setOnClickListener(new IdentityProviderAdapter.OnClickListener() {
                @Override
                public void onItemClicked(int position, View v, final IdentityProviders provider) {
                    dialog.dismiss();

                    spidServiceProvider.requestServiceProviderLoginExchange(new SPIDServiceProviderCallback() {
                        @Override
                        public void onServiceProviderResponse(String response) {
                            //TODO send request to identity provider
                            //Potrebbe essere necessario mandare parametri opzionali. In questo caso al posto di parameters null mandare un bundle con
                            //i valore corretti
                            WebDialog.newInstance(LoginButton.this.getContext(), provider.getAuthority(), response, /*parameters =*/ null, R.style.spid_webview_theme, webDialogOnCompleteListener).show();
                        }

                        @Override
                        public void onServiceProviderError(String response) {
                            spidLoginListener.onFailure(response);
                        }
                    }, getContext());
                }
            });
        }
    }

    protected class WebDialogOnCompleteListener implements WebDialog.OnCompleteListener {
        @Override
        public void onComplete(Bundle values, SPIDException error) {
            if(error != null) {
                spidLoginListener.onFailure(error.toString());
                return;
            }

            final MaterialDialog dialog = new MaterialDialog.Builder(LoginButton.this.getContext()).build();
            spidServiceProvider.sendIdentityProviderLoginExchangeResponse(new SPIDServiceProviderCallback() {
                @Override
                public void onServiceProviderResponse(String response) {
                    dialog.dismiss();
                    spidLoginListener.onSuccess("Success");
                }

                @Override
                public void onServiceProviderError(String response) {
                    dialog.dismiss();
                    spidLoginListener.onFailure(response);
                }
            }, getContext());
        }
    }
}
