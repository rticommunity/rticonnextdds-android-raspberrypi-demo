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



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

//public class MotorControlMainActivity extends ActionBarActivity {
public class MotorControlMainActivity extends Activity {

	public static String mMotor_id = "1";
	public static int mTime_sec = 4;
	public static String mDirection = "Clock";
	public static int mSpeed = 0;
	public static String mSpeed_s = "xxxxx";
	public static String mAction = "Start";
	
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
		if (MotorControlPublisher.Pub_sub_create_count==1) {
		ExecutorService service = Executors.newFixedThreadPool(4);
		service.submit(new Runnable() {
			@Override
			public void run() {
				String[] args = {"0"};// {"0", "0"};
				MotorControlPublisher.main(args);
			}
		});
		MotorControlPublisher.Pub_sub_create_count=0;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.motor_control_main, menu);

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
 	 
				MotorControlMainActivity.mMotor_id = spinner1.getSelectedItem()
						.toString();
				MotorControlMainActivity.mTime_sec = Integer.valueOf(spinner2
						.getSelectedItem().toString());
				 
				
				if(spinner3.getSelectedItem()
						.toString().equals("Clock"))
				{
					MotorControlMainActivity.mDirection="0";
				} 
				if(spinner3.getSelectedItem()
						.toString().equals("Anti"))
				{
					MotorControlMainActivity.mDirection="1";
				}
				if(spinner3.getSelectedItem()
						.toString().equals("Invrt"))
				{
					MotorControlMainActivity.mDirection="2";
				}
				
				if(spinner4.getSelectedItem()
						.toString().equals("Fast"))
				{
					MotorControlMainActivity.mSpeed=1;
				} 
				if (spinner4.getSelectedItem()
						.toString().equals("Medium")) {
					MotorControlMainActivity.mSpeed=4;
				} 
				if (spinner4.getSelectedItem()
						.toString().equals("Slow")) {
					MotorControlMainActivity.mSpeed=8;
				}
	 
				 System.out.println("MotorControlPublisher.mMotor_id "
				 + MotorControlMainActivity.mMotor_id);
				 System.out.println("MotorControlPublisher.mTime_sec "
				 + MotorControlMainActivity.mTime_sec);
				 System.out.println("MotorControlPublisher.mDirection"
				 + MotorControlMainActivity.mDirection);
				 System.out.println("otorControlPublisher.mSpeed"
						 + MotorControlMainActivity.mSpeed);

				final TextView mTextField = (TextView) findViewById(R.id.textView4);

				new CountDownTimer(MotorControlMainActivity.mTime_sec * 1000, 1000) {

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
						//MotorControlPublisher.flagBtn = false;
						mTextField.setText("               ");
						StartBtn.setEnabled(true);
						spinner1.setEnabled(true);
						spinner2.setEnabled(true);
						spinner3.setEnabled(true);
						spinner4.setEnabled(true);

						MotorControlMainActivity.mMotor_id =spinner1
								.getSelectedItem().toString();
						MotorControlMainActivity.mTime_sec = Integer
								.valueOf(spinner2.getSelectedItem().toString());
						
			 				
						if(spinner3.getSelectedItem().toString().equals("Clock")){
							MotorControlMainActivity.mDirection = "0";
						}
						
						if(spinner3.getSelectedItem().toString().equals("Anti")){
							MotorControlMainActivity.mDirection = "1";
						}
						
						if(spinner3.getSelectedItem().toString().equals("Invrt")){
							MotorControlMainActivity.mDirection = "2";
						}
						
		 
						if(spinner4.getSelectedItem()
								.toString().equals("Fast"))
						{
							MotorControlMainActivity.mSpeed=1;
						} 
						if (spinner4.getSelectedItem()
								.toString().equals("Medium")) {
							MotorControlMainActivity.mSpeed=4;
						} 
						if (spinner4.getSelectedItem()
								.toString().equals("Slow")) {
							MotorControlMainActivity.mSpeed=8;
						} 
			 
					}
				}.start();

			}
		});

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
}
