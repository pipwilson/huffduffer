package org.philwilson.huffduffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class LoginTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "HUFFDUFFER_LOGIN";
    private Activity parentActivity;
    private boolean isLoggedIn = false;
    
    public LoginTask(Activity a) {
        parentActivity = a;
    }

    protected void onPostExecute(Boolean result) {
        if (!isLoggedIn) {
            Toast.makeText(parentActivity, "Could not log you in", Toast.LENGTH_SHORT).show();
        }
        // new
        // RefreshFeedTask(parentActivity).execute(HUFFDUFFER_COLLECTIVE_FEED);
    }

    protected Boolean doInBackground(String... args) {

        if (Utils.isConnectedToNetwork(parentActivity)) {
            // http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://huffduffer.com/login");
            try {
                // get the saved username and password
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(parentActivity);
                String username = sharedPref.getString("username", "");
                String password = sharedPref.getString("password", "");
                
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("login[username]", username));                
                nameValuePairs.add(new BasicNameValuePair("login[password]", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // TODO: is this enough?
                int responseStatus = response.getStatusLine().getStatusCode();
                if (responseStatus < 400) {
                    Log.d(TAG, "Logged in OK, status code was " + ((Integer) responseStatus).toString());
                    isLoggedIn = true;
                }

            } catch (ClientProtocolException cpe) {
                Log.e(TAG, cpe.getMessage());
                return isLoggedIn;
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
                return isLoggedIn;
            }
        }

        return isLoggedIn;

    }
}
