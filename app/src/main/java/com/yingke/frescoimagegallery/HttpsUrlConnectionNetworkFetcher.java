package com.yingke.frescoimagegallery;

import android.net.Uri;

import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.ProducerContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class HttpsUrlConnectionNetworkFetcher extends BaseNetworkFetcher<FetchState> {


    private static final int NUM_NETWORK_THREADS = 3;
    private static final int MAX_REDIRECTS = 5;

    public static final int HTTP_TEMPORARY_REDIRECT = 307;
    public static final int HTTP_PERMANENT_REDIRECT = 308;

    public static final int HTTP_DEFAULT_TIMEOUT = 30000;

    private int mHttpConnectionTimeout;

    private final ExecutorService mExecutorService;

    public HttpsUrlConnectionNetworkFetcher() {
        this(Executors.newFixedThreadPool(NUM_NETWORK_THREADS));
    }

    public HttpsUrlConnectionNetworkFetcher(int httpConnectionTimeout) {
        this(Executors.newFixedThreadPool(NUM_NETWORK_THREADS));
        mHttpConnectionTimeout = httpConnectionTimeout;
    }

    @VisibleForTesting
    HttpsUrlConnectionNetworkFetcher(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public FetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext context) {
        return new FetchState(consumer, context);
    }

    @Override
    public void fetch(final FetchState fetchState, final Callback callback) {
        final Future<?> future = mExecutorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        fetchSync(fetchState, callback);
                    }
                });
        fetchState.getContext().addCallbacks(
                new BaseProducerContextCallbacks() {
                    @Override
                    public void onCancellationRequested() {
                        if (future.cancel(false)) {
                            callback.onCancellation();
                        }
                    }
                });
    }

    @VisibleForTesting
    void fetchSync(FetchState fetchState, Callback callback) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = downloadFrom(fetchState.getUri(), MAX_REDIRECTS);

            if (connection != null) {
                is = connection.getInputStream();
                callback.onResponse(is, -1);
            }
        } catch (IOException e) {
            callback.onFailure(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing and ignore the IOException here
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private HttpURLConnection downloadFrom(Uri uri, int maxRedirects) throws IOException {

        // 增加https支持
        String scheme = uri.getScheme();
        if ("https".equals(scheme)) {
//            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            SSLContext sslContext = SSLContextUtils.getSSLContext();
            if (sslContext != null) {
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                HttpsURLConnection.setDefaultHostnameVerifier(SSLContextUtils.getHostnameVerifier());
//                httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
//                httpsURLConnection.setHostnameVerifier(SSLContextUtils.getHostnameVerifier());
            }
//            connection = httpsURLConnection;
        }
        HttpURLConnection connection = openConnectionTo(uri);
        connection.setConnectTimeout(mHttpConnectionTimeout);

        // 继续请求
        int responseCode = connection.getResponseCode();
        if (isHttpSuccess(responseCode)) {
            return connection;

        } else if (isHttpRedirect(responseCode)) {
            String nextUriString = connection.getHeaderField("Location");
            connection.disconnect();

            Uri nextUri = (nextUriString == null) ? null : Uri.parse(nextUriString);
            String originalScheme = uri.getScheme();

            if (maxRedirects > 0 && nextUri != null && !nextUri.getScheme().equals(originalScheme)) {
                return downloadFrom(nextUri, maxRedirects - 1);
            } else {
                String message = maxRedirects == 0
                        ? error("URL %s follows too many redirects", uri.toString())
                        : error("URL %s returned %d without a valid redirect", uri.toString(), responseCode);
                throw new IOException(message);
            }

        } else {
            connection.disconnect();
            throw new IOException(String
                    .format("Image URL %s returned HTTP code %d", uri.toString(), responseCode));
        }
    }

    @VisibleForTesting
    static HttpURLConnection openConnectionTo(Uri uri) throws IOException {
        URL url = UriUtil.uriToUrl(uri);
        return (HttpURLConnection) url.openConnection();
    }

    private static boolean isHttpSuccess(int responseCode) {
        return (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE);
    }

    private static boolean isHttpRedirect(int responseCode) {
        switch (responseCode) {
            case HttpURLConnection.HTTP_MULT_CHOICE:
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
            case HTTP_TEMPORARY_REDIRECT:
            case HTTP_PERMANENT_REDIRECT:
                return true;
            default:
                return false;
        }
    }

    private static String error(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

}
