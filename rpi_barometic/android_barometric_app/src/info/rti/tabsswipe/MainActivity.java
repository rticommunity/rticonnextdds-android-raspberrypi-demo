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
import info.rti.tabsswipe.adapter.TabsPagerAdapter;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.renderscript.Element;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
 
 
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener { 
 	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
 
	// Tab titles
	private String[] tabs = {"Temperature", "Pressure", "Altitude"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	 
		setContentView(R.layout.activity_main);
		 
		//Starting DDS subscriber as separate threaded task...
		if (BMP_pressureSubscriber.Pub_sub_create_count==1) {
		ExecutorService service = Executors.newFixedThreadPool(4);
		 service.submit(new Runnable() {
		        @Override
				public void run() {
		        	String[] args = {}; // {"0", "2"};
		    		BMP_pressureSubscriber.main(args);		 
		        }
		    });
		 BMP_pressureSubscriber.Pub_sub_create_count=0;
		}
		 

		///XML PARSER///
	    
	    

		    //XML PARSER END///
		// code for Web DDS HTTP POST //

/*
		XMLOUT:<SampleSeq><Sample><readSampleInfo><sourceTimestamp><sec>1415638373</sec><nanosec>916437998</nanosec>
		</sourceTimestamp><validData>true</validData>
		<instanceHandle>246,135,170,109,151,3,0,147,171,133,73,148,248,75,53,214</instanceHandle>
		<instanceState>DDS_ALIVE_INSTANCE_STATE</instanceState><sampleState>DDS_NOT_READ_SAMPLE_STATE</sampleState>
		<viewState>DDS_NOT_NEW_VIEW_STATE</viewState></readSampleInfo>
		<sampleData>
		<id>RTI UK</id>
		<Temperature>20.400000</Temperature>
		<Pressure>98.692000</Pressure>
		<Altitude>221.552431</Altitude>
		</sampleData>
		</Sample>
		</SampleSeq>
	*/	
 		
/*		new CountDownTimer(1000,100) {
			public void onTick(long millisUntilFinished) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
				try {
					  URL url = new URL("http://87.82.193.136:8080/dds/rest1/applications/LED_Demo/participants/LEDs/subscribers/TAPSubscriber/datareaders/TAPReader");
					  HttpURLConnection con = (HttpURLConnection) url
					    .openConnection();
					  readStream(con.getInputStream());
					  } catch (Exception e) {
					  e.printStackTrace();
					}
					}
				});// end thread
				t.start();
			}

			public void onFinish() {
				
			}
		}.start();*/
		
	/*	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

			}
		});// end thread
		t.start();
*/

		
		// End code for WebDDS HTTP POST //

		//System.out.println("Pressure_high 0: " + mPressure_high);
 	 	// System.out.println("Pressure_low 0: " + mPressure_low);
		
		
		
		// Initialization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
	 
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {	
	
	
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
 
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
 
		
	}

}
