package jp.co.and_ex.squid2.list;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.and_ex.squid2.R;
import jp.co.and_ex.squid2.util.BaseFragment;


/**
 * Created by obata on 2014/09/05.
 */
public class ListTabFragment extends BaseFragment {
    @Override
  public View onCreateView(
    LayoutInflater inflater,
    ViewGroup container,
    Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_list, container, false);
  }
}
