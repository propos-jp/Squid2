package jp.co.and_ex.squid2.list;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jp.co.and_ex.squid2.db.ObserveData;
import jp.co.and_ex.squid2.db.ObserveDataProvider;
import jp.co.and_ex.squid2.list.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * interface.
 */
public class ListViewFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private SimpleCursorAdapter cursorAdapter;
    private OnFragmentInteractionListener mListener;
    List<Integer> pos_array;

    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loaderの初期化
        getLoaderManager().initLoader(0, null, this);

        // CursorAdapter をセット. フラグの部分は autoRequery はしないようにセットするので注意
        final String[] from = {ObserveData.KEY_OBSERVE_DATE};
        final int[] to = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, from, to, 0);

         setListAdapter(cursorAdapter);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {

            mListener.onFragmentInteraction(pos_array.get(position));
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Integer id);
    }

       @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Loaderの廃棄
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(getClass().getSimpleName(), "onCreateLoader called.");
        return new CursorLoader(getActivity(), ObserveDataProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(getClass().getSimpleName(), "onLoadFinished called.");

        // CursorLoader と CursorAdapter を使用する上での決まり文句
        Cursor old = cursorAdapter.swapCursor(cursor);
        if (old != null) {
            old.close();
        }
        if (cursor.getCount() == 0) {
            pos_array = null;
            return;
        }

        pos_array = new ArrayList<Integer>();
        if (cursor.moveToFirst()) {
            do {
                Integer i = cursor.getInt(0);

                pos_array.add(i);
            } while (cursor.moveToNext());
        }
    }



    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        Log.d(getClass().getSimpleName(), "onLoaderReset called.");

        // CursorLoader と CursorAdapter を使用する上での決まり文句
        cursorAdapter.swapCursor(null);
    }

}
