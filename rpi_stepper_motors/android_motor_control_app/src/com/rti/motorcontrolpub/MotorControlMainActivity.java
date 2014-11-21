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

package com.rti.motorcontrolpub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.camera.simplemjpeg.MjpegActivity;
import com.camera.simplemjpeg.MjpegView;

//public class MotorControlMainActivity extends ActionBarActivity {
public class MotorControlMainActivity extends Activity {

	public static String mMotor_id = "1";
	public static int mTime_sec = 4;
	public static String mDirection = "Clock";
	public static int mSpeed = 0;
	public static String mSpeed_s = "xxxxx";
	public static String mAction = "Start";

	// public MjpegView mv1 = null;
	public static String URL;
	public static MjpegView mv = null;
	// public String URL=null;

	private int width = 320;
	private int height = 240;

	private int ip_ad1 = 87;
	private int ip_ad2 = 82;
	private int ip_ad3 = 193;
	private int ip_ad4 = 136;
	private int ip_port = 8081;
	private String ip_command = "?action=stream";

	private boolean suspending = false;

	final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_motor_control_main);

		/* Uncomment this to turn on additional logging */
		/*
		 * Logger.get_instance().set_verbosity_by_category(
		 * LogCategory.NDDS_CONFIG_LOG_CATEGORY_API,
		 * LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
		 */
		// Log.i(TAG, "calling publisherMain");
		// Starting DDS subscriber as separate threaded task...
		if (MotorControlPublisher.Pub_sub_create_count == 1) {
			ExecutorService service = Executors.newFixedThreadPool(4);
			service.submit(new Runnable() {
				@Override
				public void run() {
					String[] args = { "0" };// {"0", "0"};
					MotorControlPublisher.main(args);
				}
			});
			MotorControlPublisher.Pub_sub_create_count = 0;

		}

		final Button StartBtn = (Button) findViewById(R.id.btnSubmit);
		StartBtn.setOnClickListener(new OnClickListener() {
			final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
			final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
			final Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
			final Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);

			// Do click handling here
			public void onClick(View view) {
				MotorControlPublisher.flagBtn = true;
				spinner1.setEnabled(true);
				spinner2.setEnabled(true);
				spinner3.setEnabled(true);
				spinner4.setEnabled(true);

				if (spinner1.getSelectedItem().toString().equals("Green")) {
					MotorControlMainActivity.mMotor_id = "1";
				}
				if (spinner1.getSelectedItem().toString().equals("Yellow")) {
					MotorControlMainActivity.mMotor_id = "2";
				}
				if (spinner1.getSelectedItem().toString().equals("Both")) {
					MotorControlMainActivity.mMotor_id = "3";
				}

				MotorControlMainActivity.mTime_sec = Integer.valueOf(spinner2
						.getSelectedItem().toString());

				if (spinner3.getSelectedItem().toString().equals("Clock")) {
					MotorControlMainActivity.mDirection = "0";
				}
				if (spinner3.getSelectedItem().toString().equals("Anti")) {
					MotorControlMainActivity.mDirection = "1";
				}
				if (spinner3.getSelectedItem().toString().equals("Invrt")) {
					MotorControlMainActivity.mDirection = "2";
				}
				if (spinner3.getSelectedItem().toString().equals("Outvt")) {
					MotorControlMainActivity.mDirection = "3";
				}

				if (spinner4.getSelectedItem().toString().equals("Fastest")) {
					MotorControlMainActivity.mSpeed = 1;
				}
				if (spinner4.getSelectedItem().toString().equals("Faster")) {
					MotorControlMainActivity.mSpeed = 2;
				}
				if (spinner4.getSelectedItem().toString().equals("Fast")) {
					MotorControlMainActivity.mSpeed = 3;
				}
				if (spinner4.getSelectedItem().toString().equals("Slow")) {
					MotorControlMainActivity.mSpeed = 4;
				}
				if (spinner4.getSelectedItem().toString().equals("Slower")) {
					MotorControlMainActivity.mSpeed = 6;
				}
				if (spinner4.getSelectedItem().toString().equals("Slowest")) {
					MotorControlMainActivity.mSpeed = 8;
				}

				System.out.println("MotorControlPublisher.mMotor_id "
						+ MotorControlMainActivity.mMotor_id);
				System.out.println("MotorControlPublisher.mTime_sec "
						+ MotorControlMainActivity.mTime_sec);
				System.out.println("MotorControlPublisher.mDirection"
						+ MotorControlMainActivity.mDirection);
				System.out.println("otorControlPublisher.mSpeed"
						+ MotorControlMainActivity.mSpeed);

				// code for Web DDS HTTP POST //

				final DefaultHttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(
						ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
				httpClient.getParams().getParameter("http.protocol.version");

 Thread t = new Thread(new Runnable() {
	@Override
	public void run() {
		// replace with your url
		StringEntity se = null;
		HttpPost httppost = new HttpPost(
				"http://87.82.193.136:8080/dds/rest1/types");
		se = null;

		httppost = new HttpPost(
				"http://87.82.193.136:8080/dds/rest1/applications/LED_Demo/participants/LEDs/publishers/MyPublisher/datawriters/MyMCWriter");
		try {
			se = new StringEntity(
					"<MotorControl><motor_id>"
							+ MotorControlMainActivity.mMotor_id
							+ "</motor_id><time_sec>"
							+ MotorControlMainActivity.mTime_sec
							+ "</time_sec><direction>"
							+ MotorControlMainActivity.mDirection
							+ "</direction><speed>"
							+ MotorControlMainActivity.mSpeed
							+ "</speed><action>0</action></MotorControl>",
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("POST: " + se);
		se.setContentType("application/webdds+xml");
		 //httppost.addHeader("Cache-Control", "max-age=0");
		httppost.addHeader("Cache-Control", "no-cache");
		 
		httppost.setEntity(se);
		try {
			httpClient.execute(httppost);
		}

		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
});
 t.start();

				final TextView mTextField = (TextView) findViewById(R.id.textView4);

				new CountDownTimer(MotorControlMainActivity.mTime_sec * 1000,
						1000) {

					public void onTick(long millisUntilFinished) {
						StartBtn.setEnabled(false);
						spinner1.setEnabled(false);
						spinner2.setEnabled(false);
						spinner3.setEnabled(false);
						spinner4.setEnabled(false);

						mTextField.setText("Finishing in: "
								+ millisUntilFinished / 1000 + " secs");
					}

					public void onFinish() {
						// MotorControlPublisher.flagBtn = false;
						mTextField.setText("               ");
						StartBtn.setEnabled(true);
						spinner1.setEnabled(true);
						spinner2.setEnabled(true);
						spinner3.setEnabled(true);
						spinner4.setEnabled(true);

						if (spinner1.getSelectedItem().toString()
								.equals("Green")) {
							MotorControlMainActivity.mMotor_id = "1";
						}
						if (spinner1.getSelectedItem().toString()
								.equals("Yellow")) {
							MotorControlMainActivity.mMotor_id = "2";
						}
						if (spinner1.getSelectedItem().toString()
								.equals("Both")) {
							MotorControlMainActivity.mMotor_id = "3";
						}

						MotorControlMainActivity.mTime_sec = Integer
								.valueOf(spinner2.getSelectedItem().toString());

						if (spinner3.getSelectedItem().toString()
								.equals("Clock")) {
							MotorControlMainActivity.mDirection = "0";
						}

						if (spinner3.getSelectedItem().toString()
								.equals("Anti")) {
							MotorControlMainActivity.mDirection = "1";
						}

						if (spinner3.getSelectedItem().toString()
								.equals("Invrt")) {
							MotorControlMainActivity.mDirection = "2";
						}

						if (spinner3.getSelectedItem().toString()
								.equals("Outvt")) {
							MotorControlMainActivity.mDirection = "3";
						}

						if (spinner4.getSelectedItem().toString()
								.equals("Fastest")) {
							MotorControlMainActivity.mSpeed = 1;
						}
						if (spinner4.getSelectedItem().toString()
								.equals("Faster")) {
							MotorControlMainActivity.mSpeed = 2;
						}
						if (spinner4.getSelectedItem().toString()
								.equals("Fast")) {
							MotorControlMainActivity.mSpeed = 3;
						}
						if (spinner4.getSelectedItem().toString()
								.equals("Slow")) {
							MotorControlMainActivity.mSpeed = 4;
						}
						if (spinner4.getSelectedItem().toString()
								.equals("Slower")) {
							MotorControlMainActivity.mSpeed = 6;
						}
						if (spinner4.getSelectedItem().toString()
								.equals("Slowest")) {
							MotorControlMainActivity.mSpeed = 8;
						}

					}
				}.start();

			}
		});

		/* Integration code for WebCam */

		SharedPreferences preferences = getSharedPreferences("SAVED_VALUES",
				MODE_PRIVATE);
		width = preferences.getInt("width", width);
		height = preferences.getInt("height", height);
		ip_ad1 = preferences.getInt("ip_ad1", ip_ad1);
		ip_ad2 = preferences.getInt("ip_ad2", ip_ad2);
		ip_ad3 = preferences.getInt("ip_ad3", ip_ad3);
		ip_ad4 = preferences.getInt("ip_ad4", ip_ad4);
		ip_port = preferences.getInt("ip_port", ip_port);
		ip_command = preferences.getString("ip_command", ip_command);

		StringBuilder sb = new StringBuilder();
		String s_http = "http://";
		String s_dot = ".";
		String s_colon = ":";
		String s_slash = "/";
		sb.append(s_http);
		sb.append(ip_ad1);
		sb.append(s_dot);
		sb.append(ip_ad2);
		sb.append(s_dot);
		sb.append(ip_ad3);
		sb.append(s_dot);
		sb.append(ip_ad4);
		sb.append(s_colon);
		sb.append(ip_port);
		sb.append(s_slash);
		sb.append(ip_command);
		URL = new String(sb);

		mv = (MjpegView) findViewById(R.id.mv);
		if (mv != null) {
			mv.setResolution(width, height);
		}

		// setTitle(R.string.title_connecting);
		/* (e.g. x.new A() where x is an instance of MjpegActivity). */
		MjpegActivity x = new MjpegActivity();

		x.new DoRead().execute(URL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.motor_control_main, menu);

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
		return suspending;

	}

	public void onResume() {
		// if(DEBUG) Log.d(TAG,"onResume()");
		super.onResume();
		if (mv != null) {
			if (suspending) {
				MjpegActivity x = new MjpegActivity();
				// (e.g. x.new A() where x is an instance of MjpegActivity).
				x.new DoRead().execute(URL);

				suspending = false;
			}
		}

	}

	public void onStart() {
		super.onStart();
	}

	public void onPause() {
	      super.onPause();
		if (mv != null) {
			if (mv.isStreaming()) {
				mv.stopPlayback();
				suspending = true;
			}
		}
	}

	public void onStop() {
 		super.onStop();
	}

	public void onDestroy() {
	 	if (mv != null) {
			mv.freeCameraMemory();
		}

		super.onDestroy();
	}

}
