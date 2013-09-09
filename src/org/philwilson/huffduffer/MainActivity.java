package org.philwilson.huffduffer;

import java.lang.reflect.Field;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

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
        new RefreshFeedTask(this).execute(HUFFDUFFER_NEW_FILES_FEED);
        return true;
    }

    // refresh collective, triggered by a menu item
    public boolean refreshHuffdufferCollectiveList(MenuItem menuItem) {
        new RefreshFeedTask(this).execute(HUFFDUFFER_COLLECTIVE_FEED);
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

}
