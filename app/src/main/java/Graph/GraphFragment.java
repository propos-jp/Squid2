package Graph;



import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import jp.co.and_ex.squid2.R;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GraphFragment extends DialogFragment {

    private static final String TAG = GraphFragment.class.getSimpleName();

    public static final String ARG_PARAM1 = "data_id";

    private Integer id;

    private GraphListener graphListener = null;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        FrameLayout layout = (FrameLayout)view.findViewById(R.id.graphView);

        super.onCreate(savedInstanceState);

        XYSeries series = this.makeSeries();
        XYMultipleSeriesDataset dataset = this.makeDataset(series);
        XYMultipleSeriesRenderer renderer = this.makeRenderer();
        setChartSettings(renderer, "Scatter chart", "X", "Y", -10, 30, -10, 51, Color.GRAY, Color.LTGRAY);
        GraphicalView graph = this.makeGraph(getActivity(), dataset, renderer);


        layout.addView(graph);

        Button buttonOne = (Button) view.findViewById(R.id.CloseButton);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (graphListener != null){
                    graphListener.onCloseButtonClick();
                }
            }
        });


        return view;
    }


XYSeries makeSeries() {

        XYSeries series = new XYSeries("TEST");
        double x[] = new double[10];
        double y[] = new double[10];

        for (int i = 0; i < 10; i++) {
            x[i] = i;
            y[i] = i * i;
        }

        for (int i = 0; i < 10; i++) {
            series.add(x[i], y[i]);
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
        r.setColor( Color.BLUE);
        r.setPointStyle(PointStyle.CIRCLE);
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
}
