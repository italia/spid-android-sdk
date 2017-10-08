package it.gov.spid.utils;

import android.net.Uri;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by matteo on 08/10/17.
 */

public class Utility {
    private static final String URL_SCHEME = "https";

    public static Uri buildUri(String authority, String path, Bundle parameters) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME);
        builder.authority(authority);
        builder.path(path);
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                Object parameter = parameters.get(key);
                if (parameter instanceof String) {
                    builder.appendQueryParameter(key, (String) parameter);
                }
            }
        }
        return builder.build();
    }

    public static Bundle parseUrlQueryString(String queryString) {
        Bundle params = new Bundle();
        if (!isNullOrEmpty(queryString)) {
            String array[] = queryString.split("&");
            for (String parameter : array) {
                String keyValuePair[] = parameter.split("=");

                try {
                    if (keyValuePair.length == 2) {
                        params.putString(
                                URLDecoder.decode(keyValuePair[0], "UTF-8"),
                                URLDecoder.decode(keyValuePair[1], "UTF-8"));
                    } else if (keyValuePair.length == 1) {
                        params.putString(
                                URLDecoder.decode(keyValuePair[0], "UTF-8"),
                                "");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return params;
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0);
    }

}
