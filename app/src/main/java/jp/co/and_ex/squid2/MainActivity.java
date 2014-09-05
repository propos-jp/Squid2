package jp.co.and_ex.squid2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import jp.co.and_ex.squid2.list.ListTabFragment;
import jp.co.and_ex.squid2.map.MapTabFragment;
import jp.co.and_ex.squid2.observe.ObserveTabFragment;
import jp.co.and_ex.squid2.util.TabListener;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBarをGetしてTabModeをセット
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.list_tab_name))
                .setTabListener(new TabListener<ListTabFragment>(
                        this, getString(R.string.list_tab_tag), ListTabFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.map_tab_name))
                .setTabListener(new TabListener<MapTabFragment>(
                        this, getString(R.string.map_tab_tag), MapTabFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.observe_tab_name))
                .setTabListener(new TabListener<ObserveTabFragment>(
                        this, getString(R.string.observe_tab_tag), ObserveTabFragment.class)));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            return rootView;
        }
    }
}
