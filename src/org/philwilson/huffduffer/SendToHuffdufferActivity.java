package org.philwilson.huffduffer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SendToHuffdufferActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_huffduffer);
        
        Bundle extras = this.getIntent().getExtras();
        
        String url = extras.getString(Intent.EXTRA_TEXT); // URL
        String title = extras.getString(Intent.EXTRA_SUBJECT); // Page title 
        
        if (title==null) {
            title = url;
        }
    }


}
