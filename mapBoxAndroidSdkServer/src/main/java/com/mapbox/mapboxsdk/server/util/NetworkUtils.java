/**
 * @author Brad Leege <bleege@gmail.com>
 * Created on 2/15/14 at 3:26 PM
 */

package com.mapbox.mapboxsdk.server.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mapbox.mapbox.sdk.shared.constants.MapboxConstants;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class NetworkUtils {
    static OkHttpClient client = new OkHttpClient();
    static Cache cache;
    static File cacheDir =
            new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static HttpURLConnection getHttpURLConnection(final URL url) {
        return getHttpURLConnection(url, null);
    }

    public static OkHttpClient getOkHttp(){
        if (cache == null){
            try {
                cache = getCache(cacheDir,10 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cache != null) {
            client.setCache(cache);
        }

        return client;
    }

    public static HttpURLConnection getHttpURLConnection(final URL url, final SSLSocketFactory sslSocketFactory) {



        /*if (cache == null){
            try {
                cache = getCache(cacheDir,1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cache != null) {
            client.setCache(cache);
        }
        if (sslSocketFactory != null) {
            client.setSslSocketFactory(sslSocketFactory);
        }*/

        HttpURLConnection connection = new OkUrlFactory(client).open(url);
        connection.setRequestProperty("User-Agent", MapboxConstants.USER_AGENT);
        return connection;
    }

    public static Cache getCache(final File cacheDir, final int maxSize) throws IOException {
        return new Cache(cacheDir, maxSize);
    }
}
