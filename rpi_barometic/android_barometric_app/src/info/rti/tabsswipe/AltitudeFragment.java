/*****************************************************************************/
/*         (c) Copyright, Real-Time Innovations, All rights reserved.        */
/*                                                                           */
/*         Permission to modify and use for internal purposes granted.       */
/* This software is provided "as is", without warranty, express or implied.  */
/*                                                                           */
/*****************************************************************************/

/*******************************************************************************
 (c) 2005-2014 Copyright, Real-Time Innovations, Inc.  All rights reserved.
 RTI grants Licensee a license to use, modify, compile, and create derivative
 works of the Software.  Licensee has the right to distribute object form only
 for use with RTI products.  The Software is provided "as is", with no warranty
 of any type, including any warranty for fitness for any purpose. RTI is under
 no obligation to maintain or support the Software.  RTI shall not be liable for
 any incidental or consequential damages arising out of the use or inability to
 use the software.
 ******************************************************************************/

package info.rti.tabsswipe;

import info.rti.example.liveTemp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class AltitudeFragment extends Fragment implements OnClickListener {

	private static final String TIME = "H:mm:ss";
	private static final String[] ITEMS = { "A", "B", "C", "D", "E", "F" };

	private final int[] COLORS = { Color.RED, Color.YELLOW, Color.CYAN,
			Color.GREEN, };

	private static final int[] THRESHOLD_VALUES = { 550, 150, -2000 };
	private double mYAxisMin = -2200;
	private double mYAxisMax = 600;
	private static final int[] THRESHOLD_COLORS = { Color.RED, Color.GREEN,
		Color.YELLOW };
	private static final String[] THRESHOLD_LABELS = { "Very high altitude",
		"Normal altitude", "Very low altitude" };

	private static final int TEN_SEC = 10000;
	private static final int TWO_SEC = 2000;
	private View mViewZoomIn3;
	private View mViewZoomOut3;
	private View mViewZoomReset3;
	private GraphicalView mChartView;
	private XYSeriesRenderer[] mThresholdRenderers;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset mDataset;
	private HashMap<String, TimeSeries> mSeries;
	private TimeSeries[] mThresholds;
	private ArrayList<String> mItems;

	private double mZoomLevel = 1;
	private double mLastItemChange;
	private int mItemIndex = 5;
	private int mYAxisPadding = 9;

	private final CountDownTimer mTimer = new CountDownTimer(15 * 60 * 1000, 10) {
		@Override
		public void onTick(final long millisUntilFinished) {
			addValue();

		}

		@Override
		public void onFinish() {
		}
	};

	private final ZoomListener mZoomListener3 = new ZoomListener() {
		@Override
		public void zoomReset() {
			mZoomLevel = 1;
			scrollGraph(new Date().getTime());
		}

		@Override
		public void zoomApplied(final ZoomEvent event) {
			if (event.isZoomIn()) {
				mZoomLevel /= 2;
			} else {
				mZoomLevel *= 2;
			}
			scrollGraph(new Date().getTime());
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItems = new ArrayList<String>();
		mSeries = new HashMap<String, TimeSeries>();
		mDataset = new XYMultipleSeriesDataset();
		mRenderer = new XYMultipleSeriesRenderer();

		mRenderer.setAxisTitleTextSize(24);
		mRenderer.setChartTitleTextSize(28);
		mRenderer.setLabelsTextSize(22);
		mRenderer.setLegendTextSize(22);
		mRenderer.setPointSize(8f);
		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setYLabelsAlign(Align.CENTER);

		mRenderer.setChartTitle("Live Altitude from RaspberryPi ("
				+ BMP_pressureSubscriber.mId + ") Barometric Sensor (BMP085) "
				+ "\nat Temperature " + BMP_pressureSubscriber.mTemperature
				+ " Celsius and Pressure " + BMP_pressureSubscriber.mPressure
				+ " kPa");

		mRenderer.setXTitle("In Real Time...");
		mRenderer.setYTitle("Altitude (meters)");
		mRenderer.setLabelsColor(Color.LTGRAY);
		mRenderer.setAxesColor(Color.LTGRAY);
		mRenderer.setGridColor(Color.rgb(136, 136, 136));
		mRenderer.setBackgroundColor(Color.BLACK);

		mRenderer.setApplyBackgroundColor(true);

		mRenderer.setMargins(new int[] { 60, 60, 60, 60 });

		mRenderer.setFitLegend(true);
		mRenderer.setShowGrid(true);

		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(true);
		mRenderer.setExternalZoomEnabled(true);

		mRenderer.setAntialiasing(true);
		mRenderer.setInScroll(true);

		mLastItemChange = new Date().getTime();
		mItemIndex = 5;// Math.abs(RAND.nextInt(ITEMS.length));

		mThresholds = new TimeSeries[3];
		mThresholdRenderers = new XYSeriesRenderer[3];

		for (int i = 0; i < THRESHOLD_COLORS.length; i++) {
			mThresholdRenderers[i] = new XYSeriesRenderer();
			mThresholdRenderers[i].setColor(THRESHOLD_COLORS[i]);
			mThresholdRenderers[i].setLineWidth(3);

			mThresholds[i] = new TimeSeries(THRESHOLD_LABELS[i]);
			final long now = new Date().getTime();
			mThresholds[i].add(new Date(now - 1000 * 60 * 10),
					THRESHOLD_VALUES[i]);
			mThresholds[i].add(new Date(now + 1000 * 60 * 10),
					THRESHOLD_VALUES[i]);

			mDataset.addSeries(mThresholds[i]);
			mRenderer.addSeriesRenderer(mThresholdRenderers[i]);
		}

	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		if (Configuration.ORIENTATION_PORTRAIT == getResources()
				.getConfiguration().orientation) {
			mYAxisPadding = 9;
			mRenderer.setYLabels(30);
		}

		final LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.fragment_altitude, container, false);

		mChartView = ChartFactory.getTimeChartView(getActivity(), mDataset,
				mRenderer, TIME);
		mChartView.addZoomListener(mZoomListener3, true, false);
		view.addView(mChartView, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		return view;
	}

	// @Override
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mViewZoomIn3 = getActivity().findViewById(R.id.zoom_in3);
		mViewZoomOut3 = getActivity().findViewById(R.id.zoom_out3);
		mViewZoomReset3 = getActivity().findViewById(R.id.zoom_reset3);
		mViewZoomIn3.setOnClickListener(this);
		mViewZoomOut3.setOnClickListener(this);
		mViewZoomReset3.setOnClickListener(this);

		mTimer.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (null != mTimer) {
			mTimer.cancel();
		}
	}

	private void addValue() {

		// Subscriber to Temprature Value here

		DecimalFormat newFormat = new DecimalFormat("#.###");
		double mAltitude_t = Double.valueOf(newFormat
				.format(BMP_pressureSubscriber.mAltitude));

		final double value = mAltitude_t;
		/*
		 * System.out.println("TemperatureFragment:mId: " + mId);
		 * System.out.println("TemperatureFragment:mTemperature: " +
		 * mTemperature); System.out.println("TemperatureFragment:mPressure: " +
		 * mPressure); System.out.println("TemperatureFragment:mAltitude: " +
		 * mAltitude);
		 */
		mRenderer.setChartTitle("Live Altitude from RaspberryPi ("
				+ BMP_pressureSubscriber.mId + ") Barometric Sensor (BMP085) "
				+ "\nat Temperature " + BMP_pressureSubscriber.mTemperature
				+ " Celsius and Pressure " + BMP_pressureSubscriber.mPressure
				+ " kPa");

		if (mYAxisMin > value)
			mYAxisMin = value;
		if (mYAxisMax < value)
			mYAxisMax = value;

		final Date now = new Date();
		final long time = now.getTime();

		if (time - mLastItemChange > 10000) {
			mLastItemChange = time;
			mItemIndex = 5; // Math.abs(RAND.nextInt(ITEMS.length));
		}

		final String item = ITEMS[mItemIndex];
		final int color = COLORS[2]; // w.r.t number of colors
		final int lastItemIndex = mItems.lastIndexOf(item);
		mItems.add(item);

		if (lastItemIndex > -1) {
			boolean otherItemBetween = false;
			for (int i = lastItemIndex + 1; i < mItems.size(); i++) {
				if (!item.equals(mItems.get(i))) {
					otherItemBetween = true;
					break;
				}
			}
			if (otherItemBetween) {
				addSeries(null, now, value, item, color);
			} else {
				mSeries.get(item).add(now, value);
			}
		} else {
			addSeries(item, now, value, item, color);
		}

		mChartView.repaint();
		scrollGraph(time);

	}

	private void addSeries(final String title, final Date time,
			final double value, final String item, final int color) {
		for (int i = 0; i < THRESHOLD_COLORS.length; i++) {
			mThresholds[i].add(new Date(time.getTime() + 1000 * 60 * 5),
					THRESHOLD_VALUES[i]);
		}

		final TimeSeries series = new TimeSeries(title);
		series.add(time, value);
		mSeries.put(item, series);
		mDataset.addSeries(series);
		mRenderer.addSeriesRenderer(getSeriesRenderer(color));
	}

	private void scrollGraph(final long time) {
		final double[] limits = new double[] { time - TEN_SEC * mZoomLevel,
				time + TWO_SEC * mZoomLevel, mYAxisMin - mYAxisPadding,
				mYAxisMax + mYAxisPadding };
		mRenderer.setRange(limits);
	}

	private XYSeriesRenderer getSeriesRenderer(final int color) {
		final XYSeriesRenderer r = new XYSeriesRenderer();
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(22);
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(color);
		r.setFillPoints(true);
		r.setLineWidth(4);
		return r;
	}

	@Override
	public void onClick(final View v) {

		switch (v.getId()) {
		case R.id.zoom_in3:
			mChartView.zoomIn();
			break;

		case R.id.zoom_out3:
			mChartView.zoomOut();
			break;

		case R.id.zoom_reset3:
			mChartView.zoomReset();
			break;

		default:
			break;
		}

	}

}
