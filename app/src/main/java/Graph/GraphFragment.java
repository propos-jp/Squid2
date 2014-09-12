package Graph;



import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import jp.co.and_ex.squid2.R;
import jp.co.and_ex.squid2.db.ObserveData;
import jp.co.and_ex.squid2.db.ObserveDataContract;
import jp.co.and_ex.squid2.db.ObserveDataProvider;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class GraphFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String TAG = GraphFragment.class.getSimpleName();

    public static final String ARG_PARAM1 = "data_id";

    private Integer db_id;

    private GraphListener graphListener = null;

    private List<ObserveData> mData;

    double maxX = 0;
    double minX = 0;
    double maxY =0;
    double minY =0;

    public static final void show(FragmentManager manager,GraphListener graphListener, int data_id) {

        GraphFragment dialog = new GraphFragment();
        dialog.graphListener = graphListener;
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, data_id);
        dialog.setArguments(args);
        dialog.show(manager, GraphFragment.class.getSimpleName());
    }

    public static final void hide(FragmentManager manager) {

        Fragment temp = manager.findFragmentByTag(TAG);
        if (temp instanceof GraphFragment) {
            GraphFragment dialog = (GraphFragment) temp;
            dialog.graphListener = null;
            dialog.dismiss();
        }
    }

    public GraphFragment() {
        // Required empty public constructor
    }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
       Dialog dialog = new Dialog(getActivity(), R.style.MyDialogTheme);
       return dialog;
   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             db_id = getArguments().getInt(ARG_PARAM1);

        }
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setTitle("観測データ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        view.setBackgroundColor(Color.argb(0, 0, 0, 0));

        super.onCreate(savedInstanceState);



        Button buttonOne = (Button) view.findViewById(R.id.CloseButton);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (graphListener != null){
                    graphListener.onCloseButtonClick();
                }
            }
        });
        view.setBackground(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }


        XYSeries makeSeries(String data) {

            XYSeries series = new XYSeries("観測データ");
            try{
                if(data != null && data.length() > 0) {

                String[] lines = data.split(Pattern.quote("\r\n"));
                Log.d("line count ",new Integer(lines.length).toString());
                Double y0 = 0.0;
                for (Integer i = 0;i < lines.length; i++){
                    Log.d("line",i.toString());
                    Log.d("data", lines[i]);
                    String values[] = lines[i].split(",");
                    if (values != null && values.length == 3){
                        Double x,y;
                        String ondo = values[1];
                        String depth = values[2];

                        if (depth == null || ondo == null){
                            continue;
                        }
                        x = Double.valueOf(ondo.trim());
                        y = Double.valueOf(depth.trim());
                        x = 0.1123  *x - 25.664;
                        if (i == 0){
                            y0 = y;
                        }
                        y = 0.4841* (y -y0);
                        if (x > maxX){
                            maxX = x;
                        }
                        if (x < minX){
                            minX = x;
                        }
                        if (y > maxY){
                            maxY = y;
                        }
                        if (y < minY){
                            minY = y;
                        }

                        Log.d("水温",x.toString());
                        Log.d("深度",y.toString());
                        series.add(x, y);
                    }
                }

            }
            }catch (Exception e){

            }
            return series;

        }

    XYMultipleSeriesDataset makeDataset(XYSeries series) {

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        return dataset;
    }

    XYMultipleSeriesRenderer makeRenderer() {

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();

        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        renderer.setMargins(new int[] { 20, 30, 15, 20 });


        r.setColor( Color.YELLOW);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    GraphicalView makeGraph(Context context, XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {

        GraphicalView graph = ChartFactory.getScatterChartView(context, dataset, renderer);
        return graph;
    }


    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    public void  setGraphListener(GraphListener listener)
    {
        this.graphListener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(0);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        Uri uri = Uri.parse(ObserveDataProvider.CONTENT_URI + "/" + db_id);
        return new CursorLoader(this.getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.getCount() == 0) {
            mData = null;
            return;
        }
        mData = new ArrayList<ObserveData>();
        if (cursor.moveToFirst()) {
            do {
                ObserveData data = new ObserveData();
                data.setId(cursor.getInt(ObserveDataContract.FIELD_ORDER._ID.ordinal()));
                data.setGlobal_id(cursor.getString((ObserveDataContract.FIELD_ORDER.GLOBAL_ID.ordinal())));
                data.setObserve_date(cursor.getString(ObserveDataContract.FIELD_ORDER.OBSERVE_DATE.ordinal()));
                data.setLatitude(cursor.getDouble(ObserveDataContract.FIELD_ORDER.LATITUDE.ordinal()));
                data.setLongitude(cursor.getDouble(ObserveDataContract.FIELD_ORDER.LONGITUDE.ordinal()));
                data.setData(cursor.getString(ObserveDataContract.FIELD_ORDER.DATA.ordinal()));
                mData.add(data);
            } while (cursor.moveToNext());
        }

        viewSet();
    }

    private void viewSet() {
       ObserveData data = mData.get(0);


        TextView view2 = (TextView) getView().findViewById(R.id.textObserveDate);
        view2.setText(data.getObserve_date());
        TextView view3 = (TextView) getView().findViewById(R.id.textLatitude);
        view3.setText(Double.toString(data.getLatitude()));
        TextView view4 = (TextView) getView().findViewById(R.id.textLongtude);
        view4.setText(Double.toString(data.getLongitude()));

        FrameLayout layout = (FrameLayout)getView().findViewById(R.id.graphView);
        XYSeries series = this.makeSeries(data.getData());
        XYMultipleSeriesDataset dataset = this.makeDataset(series);
        XYMultipleSeriesRenderer renderer = this.makeRenderer();
        setChartSettings(renderer, "観測データ表示", "水温", "深度", minX * 1.1, maxX * 1.1, maxY * 1.1, minY * 1.1, Color.GRAY, Color.LTGRAY);
        GraphicalView graph = this.makeGraph(getActivity(), dataset, renderer);


        layout.addView(graph);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
