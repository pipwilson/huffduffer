package org.philwilson.huffduffer;

public class LoginHuffduffer /*extends AsyncTask<String, Void, Boolean> */ {
/*

	private String status = "";

	protected void onPostExecute(Boolean result) {
		Toast.makeText(new MainActivity(), status, Toast.LENGTH_SHORT).show();
	}

	protected Boolean doInBackground(String... args) {

		// http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://huffduffer.com/login");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("login[username]", "pip"));
			nameValuePairs.add(new BasicNameValuePair("login[password]", "huffduffer"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			// TextView txtView = (TextView)
			// findViewById(R.id.mainactivity_txtview);
			// txtView.setText(response.getStatusLine().getStatusCode());
			this.status = ((Integer) response.getStatusLine().getStatusCode()).toString();
			return true;

		}
        catch (ClientProtocolException e) {
			return false;
		}
        catch (IOException e) {
			return false;
		}
	}
	*/
}
