package org.opendroidphp.app.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

import org.opendroidphp.R;
import org.opendroidphp.app.adapter.NavDrawerListAdapter;
import org.opendroidphp.app.model.NavDrawerItem;

import java.util.ArrayList;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class ActivityConsole extends RoboSherlockFragmentActivity {

    @InjectView(R.id.drawer_layout)
    private DrawerLayout mDrawerLayout;
    @InjectView(R.id.list_slidermenu)
    private ListView mDrawerList;
    @InjectResource(R.array.nav_drawer_items)
    private String[] navMenuTitles;

    private ActionBarHelper mActionBar;
    private SherlockActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerLayout.setDrawerListener(new RoboDrawerListener());
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        for (String menuTitle : navMenuTitles) {
            navDrawerItems.add(new NavDrawerItem(menuTitle));
        }
        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(this, navDrawerItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mActionBar = createActionBarHelper();
        mActionBar.init();

        mDrawerToggle = new SherlockActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer_light,
                R.string.drawer_open,
                R.string.drawer_close
        );

        // is it good to call syncState here ?
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            initializeView(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                return true;
            case R.id.web_admin:
                return true;
            case R.id.sql_admin:
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), Preferences.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
////        if (getBoolean("enable_server_on_app_startup", false)) {
////            startService(new Intent(this, ServerService.class));
////        }
//        //new ConnectionListenerTask().execute();
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Create a compatible helper that will manipulate the action bar if
     * available.
     */
    private ActionBarHelper createActionBarHelper() {
        return new ActionBarHelper();
    }


    protected Fragment getFragmentById(final int fragmentCode) {

        switch (fragmentCode) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MySqlFragment();
            case 2:
                return new ExtensionFragment();
            case 3:
            case 4:
                return new AboutFragment();
            default:
                return null;
        }
    }

    private void initializeView(int fragmentCode) {
        Fragment fragment = getFragmentById(fragmentCode);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.
                    beginTransaction().
                    replace(R.id.frame_container, fragment).
                    commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(fragmentCode, true);
            mDrawerList.setSelection(fragmentCode);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    /**
     * This list item click listener implements very simple view switching by
     * changing the primary content text. The drawer is closed when a selection
     * is made.
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //mContent.setText(Shakespeare.DIALOGUE[position]);
            //mActionBar.setTitle(Shakespeare.TITLES[position]);
            Toast.makeText(getApplicationContext(), "Position: " + position, Toast.LENGTH_LONG).show();
            initializeView(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /**
     * A drawer listener can be used to respond to drawer events such as
     * becoming fully opened or closed. You should always prefer to perform
     * expensive operations such as drastic relayout when no animation is
     * currently in progress, either before or after the drawer animates.
     * <p/>
     * When using ActionBarDrawerToggle, all DrawerLayout listener methods
     * should be forwarded if the ActionBarDrawerToggle is not used as the
     * DrawerLayout listener directly.
     */
    private class RoboDrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
            mActionBar.onDrawerOpened();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
            mActionBar.onDrawerClosed();
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    private class ActionBarHelper {
        private final ActionBar mActionBar;
        private CharSequence mDrawerTitle;
        private CharSequence mTitle;

        private ActionBarHelper() {
            mActionBar = getSupportActionBar();
        }

        public void init() {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mTitle = mDrawerTitle = getTitle();
        }

        /**
         * When the drawer is closed we restore the action bar state reflecting
         * the specific contents in view.
         */
        public void onDrawerClosed() {
            mActionBar.setTitle(mTitle);
        }

        /**
         * When the drawer is open we set the action bar to a generic title. The
         * action bar should only contain data relevant at the top level of the
         * nav hierarchy represented by the drawer, as the rest of your content
         * will be dimmed down and non-interactive.
         */
        public void onDrawerOpened() {
            mActionBar.setTitle(mDrawerTitle);
        }

        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }
}
