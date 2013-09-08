package org.philwilson.huffduffer;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.philwilson.huffduffer.AtomFeedParser.Entry;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    // HUFFDUFFER URL constants
	private static final String HUFFDUFFER_NEW_FILES_FEED = "http://huffduffer.com/new/atom";
    private static final String HUFFDUFFER_COLLECTIVE_FEED = "http://huffduffer.com/pip/collective/atom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getOverflowMenu();
    }

    // massive hack to get the action bar menu button to always appear, even if
    // the device has a hardware menu key
    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    // refresh generic new items feed, triggered by a menu item
    public boolean refreshHuffdufferNewItemsList(MenuItem menuItem) {
        new RefreshHuffdufferURLTask().execute(HUFFDUFFER_NEW_FILES_FEED);
        return true;
    }

    // refresh collective, triggered by a menu item
    public boolean refreshHuffdufferCollectiveList(MenuItem menuItem) {
        new RefreshHuffdufferURLTask().execute(HUFFDUFFER_COLLECTIVE_FEED);
        //Toast.makeText(MainActivity.this, getExternalFilesDir(null).getAbsolutePath(), Toast.LENGTH_SHORT).show();
        return true;
    }

    // handle select list item action from collective item list
    public boolean showItemDetail(MenuItem menuItem) {
        // TODO implement it!
        // Make sure we store the collective feed as an internal data structure when we retrieve
        // it and then just use index of menu as index into data structure.
        // Display Title, Description, Duration (if available) and button to Huffduff This.
        return false;
    }


    private class RefreshHuffdufferURLTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "HUFFDUFFER_REFRESH";
        private ArrayList<String> titles;

        private void log(String message) {
            String FILENAME = "huffduffer.log";

            try {
                File file = new File(getExternalFilesDir(null), FILENAME);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(message.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
            } catch (IOException ioe) {
            }


            Log.d(TAG, message);
        }

        @Override
        protected String doInBackground(String... urls) {
            log("in doInBackground");
            try {
                if (isConnectedToNetwork()) {
                    return loadXmlFromNetwork(urls[0]);
                } else {
                    Toast.makeText(MainActivity.this, "You need to be connected to the internet to do that.", Toast.LENGTH_SHORT).show();
                    return "no network";
                }

            } catch (FileNotFoundException fnfe) {
                log(fnfe.getMessage());
                return fnfe.getMessage();
            } catch (IOException ioe) {
                log(ioe.getMessage());
                ioe.printStackTrace();
                return ioe.getMessage();
            } catch (XmlPullParserException xppe) {
                log(xppe.getMessage());
                xppe.printStackTrace();
                return xppe.getMessage();
            }
        }

        private boolean isConnectedToNetwork() {
            log("checking network connectivity");
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                log("connected to network");
                return true;
            } else {
                log("not connected to network");
                return false;
            }
        }

        // Retrieves your huffduffer collective, parses it, and combines it with
        // HTML markup. Returns HTML string.
        private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            log("loading XML from network");
            InputStream stream = null;

            // Instantiate the parser
            AtomFeedParser feedParser = new AtomFeedParser();
            List<Entry> entries = null;

            try {
                stream = downloadUrl(urlString);
                entries = feedParser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            titles = new ArrayList<String>(entries.size());

            for (Entry entry : entries) {
                titles.add(entry.title);
            }
            Log.d(TAG, "Everything has gone wonderfully.");
            return null;
        }

        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }


        // update the listview with the data we got from huffduffer.com
        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.activity_main);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, R.id.label, titles);
            setListAdapter(adapter);
        }
    }
}
