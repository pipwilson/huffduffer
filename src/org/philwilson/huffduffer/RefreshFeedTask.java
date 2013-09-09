package org.philwilson.huffduffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.philwilson.huffduffer.AtomFeedParser.Entry;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class RefreshFeedTask extends AsyncTask<String, Void, String> {

	private static final String TAG = "HUFFDUFFER_REFRESH";
	private ArrayList<String> titles;
	private Activity parentActivity;

	RefreshFeedTask(Activity a) {
		parentActivity = a;
	}
	
	private void log(String message) {
		String FILENAME = "huffduffer.log";

		try {
			File file = new File(parentActivity.getExternalFilesDir(null), FILENAME);
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
			if (Utils.isConnectedToNetwork(parentActivity)) {
				return loadXmlFromNetwork(urls[0]);
			} else {
				Toast.makeText(parentActivity,
						"You need to be connected to the internet to do that.",
						Toast.LENGTH_SHORT).show();
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

	// Retrieves your huffduffer collective, parses it, and combines it with
	// HTML markup. Returns HTML string.
	private String loadXmlFromNetwork(String urlString)
			throws XmlPullParserException, IOException {
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
		parentActivity.setContentView(R.layout.activity_main);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				parentActivity, R.layout.list_item, R.id.label, titles);
		((ListActivity) parentActivity).setListAdapter(adapter);
	}
}