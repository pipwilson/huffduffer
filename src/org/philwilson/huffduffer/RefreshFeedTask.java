package org.philwilson.huffduffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.philwilson.huffduffer.AtomFeedParser.Entry;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

public class RefreshFeedTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "HUFFDUFFER_REFRESH";
    private static ArrayList<String> titles = new ArrayList<String>();
    private Activity parentActivity;

    RefreshFeedTask(Activity a) {
        parentActivity = a;
    }

    @Override
    protected String doInBackground(String... urls) {
        Log.d(TAG, "in doInBackground");
        try {
            // returns null if successful
            return fetchFeed(urls[0]);
        } catch (FileNotFoundException fnfe) {
            Log.d(TAG, fnfe.getMessage());
            return fnfe.getMessage();
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getMessage());
            ioe.printStackTrace();
            return ioe.getMessage();
        } catch (XmlPullParserException xppe) {
            Log.d(TAG, xppe.getMessage());
            xppe.printStackTrace();
            return xppe.getMessage();
        }
    }

    // Retrieves a huffduffer feed and parses it
    private String fetchFeed(String urlString) throws XmlPullParserException, IOException {
        Log.d(TAG, "in fetchFeed");
        InputStream stream = null;

        // Instantiate the parser
        AtomFeedParser feedParser = new AtomFeedParser();
        List<Entry> entries = null;

        try {
            stream = downloadUrl(urlString);
            entries = feedParser.parse(stream);

            if (entries != null && entries.size() > 0) {
                setTitles(new ArrayList<String>(entries.size()));

                for (Entry entry : entries) {
                    getTitles().add(entry.title);
                }
                Log.d(TAG, "Everything has gone wonderfully.");

            }
        } catch (IOException ioe) {
            Log.e(TAG, "No network available");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // we always return a null String. onPostExecute checks the value of
        // 'titles'
        return null;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        Log.d(TAG, "downloading URL");
        if (Utils.isConnectedToNetwork(parentActivity)) {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // Set cookies in requests
            CookieManager cookieManager = CookieManager.getInstance();
            
            //String cookie = cookieManager.getCookie(urlString);
            /*
            if (cookie != null) {
                Log.d(TAG, cookie);
                // conn.setRequestProperty("Cookie", cookie);
            }
            */
            
            
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            /*
            // Get cookies from responses and save into the cookie manager
            List<String> cookieList = conn.getHeaderFields().get("Set-Cookie");
            if (cookieList != null) {
                for (String cookieTemp : cookieList) {
                    cookieManager.setCookie(conn.getURL().toString(), cookieTemp);
                    Log.d(TAG, cookieTemp);
                }
            }
            */

            return conn.getInputStream();
        } else {
            throw new IOException("No network connection");
        }

    }

    public static ArrayList<String> getTitles() {
        return titles;
    }

    public static void setTitles(ArrayList<String> titles) {
        RefreshFeedTask.titles = titles;
    }

    protected void onPostExecute(String result) {
        ((ItemListActivity) parentActivity).updateTitles();
    }

}