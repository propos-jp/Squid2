package jp.co.and_ex.squid2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import Graph.GraphFragment;
import Graph.GraphListener;
import jp.co.and_ex.squid2.db.ObserveData;
import jp.co.and_ex.squid2.list.ListViewFragment;
import jp.co.and_ex.squid2.map.MapViewFragment;
import jp.co.and_ex.squid2.observe.ObserveViewFragment;
import jp.co.and_ex.squid2.util.TabListener;


public class MainActivity extends Activity implements ListViewFragment.OnFragmentInteractionListener , GraphListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBarをGetしてTabModeをセット
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.list_tab_name))
                .setTabListener(new TabListener<ListViewFragment>(
                        this, getString(R.string.list_tab_tag), ListViewFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.map_tab_name))
                .setTabListener(new TabListener<MapViewFragment>(
                        this, getString(R.string.map_tab_tag), MapViewFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.observe_tab_name))
                .setTabListener(new TabListener<ObserveViewFragment>(
                        this, getString(R.string.observe_tab_tag), ObserveViewFragment.class)));

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

    @Override
     public void onFragmentInteraction(Integer id) {
        GraphFragment.show(getFragmentManager(),this,id);
     }

    @Override
    public void onCloseButtonClick() { GraphFragment.hide(getFragmentManager());}


}



