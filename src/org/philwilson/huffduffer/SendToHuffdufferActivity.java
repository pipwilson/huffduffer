package org.philwilson.huffduffer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class SendToHuffdufferActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_huffduffer);
        
        Bundle extras = this.getIntent().getExtras();
        
        String url = extras.getString(Intent.EXTRA_TEXT); // URL
        String title = extras.getString(Intent.EXTRA_SUBJECT); // Page title 
        
        if (title == null) {
            title = url;
        }
        
        EditText urlEditText = (EditText)findViewById(R.id.send_to_huffduffer_url);
        urlEditText.setText(url);
        
        EditText titleEditText = (EditText)findViewById(R.id.send_to_huffduffer_title);
        titleEditText.setText(title);        
    }


}
