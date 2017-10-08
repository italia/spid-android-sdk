package it.gov.spid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.logging.Logger;

import it.gov.spid.R;
import it.gov.spid.exception.SPIDDialogException;
import it.gov.spid.exception.SPIDException;
import it.gov.spid.exception.SPIDOperationCanceledException;
import it.gov.spid.utils.Utility;

public class WebDialog extends Dialog {
    static final boolean DISABLE_SSL_CHECK_FOR_TESTING = true;

    // width below which there are no extra margins
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    // width beyond which we're always using the MIN_SCALE_FACTOR
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    // height below which there are no extra margins
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    // height beyond which we're always using the MIN_SCALE_FACTOR
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;

    // the minimum scaling factor for the web dialog (50% of screen size)
    private static final double MIN_SCALE_FACTOR = 0.5;

    // translucent border around the webview
    private static final int BACKGROUND_GRAY = 0xCC000000;

    private static final int DEFAULT_THEME = R.style.spid_webview_theme;

    private String url;
    private OnCompleteListener onCompleteListener;
    private WebView webView;
    private ProgressDialog spinner;
    private ImageView crossImageView;
    private FrameLayout contentFrameLayout;
    private boolean listenerCalled = false;
    private boolean isDetached = false;
    private boolean isPageFinished = false;
    private static volatile int webDialogTheme = DEFAULT_THEME;

    //TODO redirect url e cancel url servono per chiudere l'activity quando l'utente viene redirecatato nella webview
    private final String REDIRECT_URL = "";
    private final String CANCEL_URL = "";

    //TODO questo può servire per specificare delle path da aggiungere all'authority
    private final String IDENTITY_PROVIDER_PATH = "";

    private final String serviceProviderLoginRequest;

    private final Logger logger = Logger.getLogger("WebDialog");

    protected static void initDefaultTheme(Context context) {
        if (context == null) {
            return;
        }

        ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        if (ai == null || ai.metaData == null) {
            return;
        }
    }

    public static WebDialog newInstance(Context context, String identityProviderAuthority, String serviceProviderLoginRequest, Bundle parameters, int theme, OnCompleteListener listener) {
        initDefaultTheme(context);

        return new WebDialog(context, identityProviderAuthority, serviceProviderLoginRequest, parameters, theme, listener);
    }

    public static int getWebDialogTheme() {
        return webDialogTheme;
    }

    public static void setWebDialogTheme(int theme) {
        webDialogTheme = (theme != 0) ? theme : DEFAULT_THEME;
    }

    /**
     * Interface that implements a listener to be called when the user's interaction with the
     * dialog completes, whether because the dialog finished successfully, or it was cancelled,
     * or an error was encountered.
     */
    public interface OnCompleteListener {
        /**
         * Called when the dialog completes.
         *
         * @param values on success, contains the values returned by the dialog
         * @param error  on an error, contains an exception describing the error
         */
        void onComplete(Bundle values, SPIDException error);
    }

    /**
     * Constructor which can be used to display a dialog with an already-constructed URL.
     *
     * @param context the context to use to display the dialog
     * @param url     the URL of the Web Dialog to display; no validation is done on this URL, but it should
     *                be a valid URL pointing to a SPID Web Dialog
     */
    protected WebDialog(Context context, String serviceProviderLoginRequest, String url) {
        this(context, url, serviceProviderLoginRequest, getWebDialogTheme());
    }

    /**
     * Constructor which can be used to display a dialog with an already-constructed URL and a custom theme.
     *
     * @param context the context to use to display the dialog
     * @param url     the URL of the Web Dialog to display; no validation is done on this URL, but it should
     *                be a valid URL pointing to a SPID Web Dialog
     * @param theme   identifier of a theme to pass to the Dialog class
     */
    private WebDialog(Context context, String url, String serviceProviderLoginRequest, int theme) {
        super(context, theme == 0 ? getWebDialogTheme() : theme);
        this.serviceProviderLoginRequest = serviceProviderLoginRequest;
        this.url = url;
    }

    /**
     * Constructor which will construct the URL of the Web dialog based on the specified parameters.
     *
     * @param context    the context to use to display the dialog
     * @param parameters parameters which will be included as part of the URL
     * @param theme      identifier of a theme to pass to the Dialog class
     * @param listener the listener to notify, or null if no notification is desired
     */
    private WebDialog(Context context, String identityProviderAuthority, String serviceProviderLoginRequest, Bundle parameters, int theme, OnCompleteListener listener) {
        super(context, theme == 0 ? getWebDialogTheme() : theme);

        if (parameters == null) {
            parameters = new Bundle();
        }

        onCompleteListener = listener;
        this.serviceProviderLoginRequest = serviceProviderLoginRequest;

        Uri uri = Utility.buildUri(identityProviderAuthority, IDENTITY_PROVIDER_PATH, parameters);
        this.url = uri.toString();
    }

    /**
     * Sets the listener which will be notified when the dialog finishes.
     *
     * @param listener the listener to notify, or null if no notification is desired
     */
    public void setOnCompleteListener(OnCompleteListener listener) {
        onCompleteListener = listener;
    }

    /**
     * Gets the listener which will be notified when the dialog finishes.
     *
     * @return the listener, or null if none has been specified
     */
    public OnCompleteListener getOnCompleteListener() {
        return onCompleteListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancel();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void dismiss() {
        if (webView != null) {
            webView.stopLoading();
        }
        if (!isDetached) {
            if (spinner != null && spinner.isShowing()) {
                spinner.dismiss();
            }
        }
        super.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        resize();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDetachedFromWindow() {
        isDetached = true;
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        isDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = new ProgressDialog(getContext());
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getContext().getString(R.string.loading));
        // Stops people from accidently cancelling the login flow
        spinner.setCanceledOnTouchOutside(false);
        spinner.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cancel();
            }
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentFrameLayout = new FrameLayout(getContext());

        // First calculate how big the frame layout should be
        resize();
        getWindow().setGravity(Gravity.CENTER);

        // resize the dialog if the soft keyboard comes up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        /* Create the 'x' image, but don't add to the contentFrameLayout layout yet
         * at this point, we only need to know its drawable width and height
         * to place the webview
         */
        createCrossImage();

        if (this.url != null) {
            /* Now we know 'x' drawable width and height,
            * layout the webview and add it the contentFrameLayout layout
            */
            int crossWidth = crossImageView.getDrawable().getIntrinsicWidth();
            setUpWebView(crossWidth / 2 + 1);
        }

        /* Finally add the 'x' image to the contentFrameLayout layout and
        * add contentFrameLayout to the Dialog view
        */
        contentFrameLayout.addView(crossImageView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(contentFrameLayout);
    }

    protected Bundle parseResponseUri(String urlString) {
        Uri u = Uri.parse(urlString);

        Bundle b = Utility.parseUrlQueryString(u.getQuery());
        b.putAll(Utility.parseUrlQueryString(u.getFragment()));

        return b;
    }

    protected boolean isListenerCalled() {
        return listenerCalled;
    }

    protected boolean isPageFinished() {
        return isPageFinished;
    }

    protected WebView getWebView() {
        return webView;
    }

    public void resize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // always use the portrait dimensions to do the scaling calculations so we always get a portrait shaped
        // web dialog
        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        int dialogWidth = Math.min(
                getScaledSize(width, metrics.density, NO_PADDING_SCREEN_WIDTH, MAX_PADDING_SCREEN_WIDTH),
                metrics.widthPixels);
        int dialogHeight = Math.min(
                getScaledSize(height, metrics.density, NO_PADDING_SCREEN_HEIGHT, MAX_PADDING_SCREEN_HEIGHT),
                metrics.heightPixels);

        getWindow().setLayout(dialogWidth, dialogHeight);
    }

    /**
     * Returns a scaled size (either width or height) based on the parameters passed.
     * @param screenSize a pixel dimension of the screen (either width or height)
     * @param density density of the screen
     * @param noPaddingSize the size at which there's no padding for the dialog
     * @param maxPaddingSize the size at which to apply maximum padding for the dialog
     * @return a scaled size.
     */
    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        int scaledSize = (int) ((float) screenSize / density);
        double scaleFactor;
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = MIN_SCALE_FACTOR;
        } else {
            // between the noPadding and maxPadding widths, we take a linear reduction to go from 100%
            // of screen size down to MIN_SCALE_FACTOR
            scaleFactor = MIN_SCALE_FACTOR +
                    ((double) (maxPaddingSize - scaledSize))
                            / ((double) (maxPaddingSize - noPaddingSize))
                            * (1.0 - MIN_SCALE_FACTOR);
        }
        return (int) (screenSize * scaleFactor);
    }

    protected void sendSuccessToListener(Bundle values) {
        if (onCompleteListener != null && !listenerCalled) {
            listenerCalled = true;
            onCompleteListener.onComplete(values, null);
            dismiss();
        }
    }

    protected void sendErrorToListener(Throwable error) {
        if (onCompleteListener != null && !listenerCalled) {
            listenerCalled = true;
            SPIDException SPIDException;
            if (error instanceof SPIDException) {
                SPIDException = (SPIDException) error;
            } else {
                SPIDException = new SPIDException(error);
            }
            onCompleteListener.onComplete(null, SPIDException);
            dismiss();
        }
    }

    public void cancel() {
        if (onCompleteListener != null && !listenerCalled) {
            sendErrorToListener(new SPIDOperationCanceledException());
        }
    }

    private void createCrossImage() {
        crossImageView = new ImageView(getContext());
        // Dismiss the dialog when user click on the 'x'
        crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        Drawable crossDrawable = getContext().getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
        crossImageView.setImageDrawable(crossDrawable);
        /* 'x' should not be visible while webview is loading
         * make it visible only after webview has fully loaded
         */
        crossImageView.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        webView = new WebView(getContext().getApplicationContext()) {
            /* Prevent NPE on Motorola 2.2 devices
             * See https://groups.google.com/forum/?fromgroups=#!topic/android-developers/ktbwY2gtLKQ
             */
            @Override
            public void onWindowFocusChanged(boolean hasWindowFocus) {
                try {
                    super.onWindowFocusChanged(hasWindowFocus);
                } catch (NullPointerException e) {
                }
            }
        };
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setWebViewClient(new DialogWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!v.hasFocus())
                {
                    v.requestFocus();
                }
                return false;
            }
        });

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(webView);
        webViewContainer.setBackgroundColor(BACKGROUND_GRAY);
        contentFrameLayout.addView(webViewContainer);
    }

    private class DialogWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(WebDialog.this.REDIRECT_URL)) {
                Bundle values = parseResponseUri(url);

                //TODO read request parameters from value value.getString(key)
                return true;
            } else if (url.startsWith(WebDialog.this.CANCEL_URL)) {
                cancel();
                return true;
            }
            // launch non-dialog URLs in a full browser
            try {
                getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } catch (ActivityNotFoundException e) {
                return false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            sendErrorToListener(new SPIDDialogException(description, errorCode, failingUrl));
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (DISABLE_SSL_CHECK_FOR_TESTING) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);

                handler.cancel();
                sendErrorToListener(new SPIDDialogException(null, ERROR_FAILED_SSL_HANDSHAKE, null));
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            logger.info("Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            if (!isDetached) {
                spinner.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isDetached) {
                spinner.dismiss();
            }
            /*
             * Once web view is fully loaded, set the contentFrameLayout background to be transparent
             * and make visible the 'x' image.
             */
            contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
            webView.setVisibility(View.VISIBLE);
            crossImageView.setVisibility(View.VISIBLE);
            isPageFinished = true;
        }
    }
}
