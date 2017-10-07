package it.gov.spid.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import it.gov.spid.IdentityProviders;
import it.gov.spid.R;
import it.gov.spid.adapter.IdentityProviderAdapter;
import it.gov.spid.listener.SPIDLoginListener;

/**
 * Created by matteo on 07/10/17.
 */

public class LoginButton extends android.support.v7.widget.AppCompatButton{
    private OnClickListener internalOnClickListener;
    private OnClickListener externalOnClickListener;
    private SPIDLoginListener spidLoginListener = new SPIDLoginListener() {
        @Override
        public void onSuccess() {}

        @Override
        public void onFailure(String message) {}
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

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.externalOnClickListener = l;
    }

    protected void configureButton(Context context, AttributeSet attrs) {
        if(isInEditMode()) {
            return;
        }

        this.internalOnClickListener = new LoginClickListener();

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
            final MaterialDialog dialog = new MaterialDialog.Builder(LoginButton.this.getContext()).adapter(adapter, null).show();

            adapter.setOnClickListener(new IdentityProviderAdapter.OnClickListener() {
                @Override
                public void onItemClicked(int position, View v, IdentityProviders provider) {
                    dialog.dismiss();
                }
            });
        }
    }
}
