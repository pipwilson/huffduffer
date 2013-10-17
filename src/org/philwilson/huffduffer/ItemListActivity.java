package org.philwilson.huffduffer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class ItemListActivity extends FragmentActivity implements ItemListFragment.Callbacks {

    private static final String HUFFDUFFER_POPULAR_FILES_FEED = "http://huffduffer.com/popular/atom";
    
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        new RefreshFeedTask(this).execute(HUFFDUFFER_POPULAR_FILES_FEED);
        new LoginTask(this).execute();
        this.setTitle(R.string.title_item_list);
        
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getSupportFragmentManager().findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean showSettingsScreen(MenuItem menuItem) {
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }
    
    public void updateTitles() {
        ItemListFragment itemListFragment = (ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list);
        itemListFragment.setListAdapter(new ArrayAdapter<AtomFeedParser.Entry>(itemListFragment.getActivity(),
                android.R.layout.simple_list_item_activated_1, android.R.id.text1, AtomFeedParser.ITEMS));        
    }
    
    // https://developer.android.com/guide/topics/ui/menus.html
    
    // refresh generic new items feed, triggered by a menu item
    public boolean refreshHuffdufferNewItemsList(MenuItem menuItem) {
        // TODO do we need to deal with the return from doInBackground()?
        new RefreshFeedTask(this).execute(HUFFDUFFER_POPULAR_FILES_FEED);           
        return true;
    }
    
}
