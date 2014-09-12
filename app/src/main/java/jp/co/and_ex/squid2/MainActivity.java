package jp.co.and_ex.squid2;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import Graph.GraphFragment;
import Graph.GraphListener;
import jp.co.and_ex.squid2.list.ListViewFragment;
import jp.co.and_ex.squid2.map.MapViewFragment;
import jp.co.and_ex.squid2.observe.ObserveViewFragment;
import jp.co.and_ex.squid2.util.TabListener;

import com.actionbarsherlock.app.SherlockActivity;


public class MainActivity extends SherlockActivity implements ListViewFragment.OnFragmentInteractionListener , GraphListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBarをGetしてTabModeをセット
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setTheme(R.style.Sherlock___Theme_DarkActionBar);
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
     public void onFragmentInteraction(Integer id) {
        GraphFragment.show(getFragmentManager(),this,id);
     }

    @Override
    public void onCloseButtonClick() { GraphFragment.hide(getFragmentManager());}


}



