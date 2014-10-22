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


package info.rti.tabsswipe.adapter;



import info.rti.tabsswipe.AltitudeFragment;
import info.rti.tabsswipe.PressureFragment;
import info.rti.tabsswipe.TemperatureFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class TabsPagerAdapter extends FragmentPagerAdapter {
	 
 
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		//Starting DDS subscriber as separate threaded task...
/*		ExecutorService service = Executors.newFixedThreadPool(4);
		 service.submit(new Runnable() {
		        @Override
				public void run() {
		        	String[] args = {}; // {"0", "2"};
		    		BMP_pressureSubscriber.main(args);		 
		    		
		        }
		    });*/
		//System.out.println("Pressure_high 0: " + mPressure_high);
 	 	// System.out.println("Pressure_low 0: " + mPressure_low);
	}

	@Override
	public Fragment getItem(int index) {
		
		switch (index) {
		case 0:
			return new TemperatureFragment();
		case 1:
	  		 return new PressureFragment();
		case 2:
			return new AltitudeFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}


}
