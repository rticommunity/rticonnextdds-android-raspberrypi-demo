/*******************************************************************************
 (c) 2005-2014 Copyright, Real-Time Innovations, Inc.  All rights reserved.
 RTI grants Licensee a license to use, modify, compile, and create derivative
 works of the Software.  Licensee has the right to distribute object form only
 for use with RTI products.  The Software is provided "as is", with no warranty
 of any type, including any warranty for fitness for any purpose. RTI is under
 no obligation to maintain or support the Software.  RTI shall not be liable for
 any incidental or consequential damages arising out of the use or inability to
 use the software.
 ******************************************************************************//
*****************************************************************************/
/*         (c) Copyright, Real-Time Innovations, All rights reserved.        */
/*                                                                           */
/*         Permission to modify and use for internal purposes granted.       */
/* This software is provided "as is", without warranty, express or implied.  */
/*                                                                           */
/*****************************************************************************/

package info.rti.tabsswipe;

/* BMP_pressureSubscriber.java

 A publication of data of type BMP_pressure

 This file is derived from code automatically generated by the rtiddsgen 
 command:

 rtiddsgen -language java -example <arch> .idl

 Example publication of type BMP_pressure automatically generated by 
 'rtiddsgen' To test them follow these steps:

 (1) Compile this file and the example subscription.

 (2) Start the subscription on the same domain used for with the command
 java BMP_pressureSubscriber <domain_id> <sample_count>

 (3) Start the publication with the command
 java BMP_pressurePublisher <domain_id> <sample_count>

 (4) [Optional] Specify the list of discovery initial peers and 
 multicast receive addresses via an environment variable or a file 
 (in the current working directory) called NDDS_DISCOVERY_PEERS. 

 You can run any number of publishers and subscribers programs, and can 
 add and remove them dynamically from the domain.


 Example:

 To run the example application on domain <domain_id>:

 Ensure that $(NDDSHOME)/lib/<arch> is on the dynamic library path for
 Java.                       

 On UNIX systems: 
 add $(NDDSHOME)/lib/<arch> to the 'LD_LIBRARY_PATH' environment
 variable

 On Windows systems:
 add %NDDSHOME%\lib\<arch> to the 'Path' environment variable


 Run the Java applications:

 java -Djava.ext.dirs=$NDDSHOME/class BMP_pressurePublisher <domain_id>

 java -Djava.ext.dirs=$NDDSHOME/class BMP_pressureSubscriber <domain_id>  


 modification history
 ------------ -------   
 */

import org.w3c.dom.NodeList;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

// ===========================================================================

public class BMP_pressureSubscriber { // -----------------------------------------------------------------------
	// Public Methods
	// -----------------------------------------------------------------------
	public static String mId = "XXXXX";
	public static double mTemperature = 0.0;
	public static double mPressure = 0.0;
	public static double mAltitude = 0.0;

	public static int mPressure_high = 120;
	public static int mPressure_low = 10;

	public static int Pub_sub_create_count = 1;
	public static int XML_parser_create_count = 1;

	public final static String URL = "http://87.82.193.136:8080/dds/rest1/applications/LED_Demo/participants/LEDs/subscribers/TAPSubscriber/datareaders/TAPReader";

	// XML node keys
	public final static String KEY_PARENT = "sampleData"; // parent node
															// //sampleData
	public final static String KEY_ID = "id"; // parent node //sampleData
	public final static String KEY_TEMPRATURE = "Temperature";
	public final static String KEY_PRESSURE = "Pressure";
	public final static String KEY_ALTITUDE = "Altitude";

	public static void main(String[] args) {
		// --- Get domain ID --- //
		int domainId = 0;
		if (args.length >= 1) {
			domainId = Integer.valueOf(args[0]).intValue();
		}

		// -- Get max loop count; 0 means infinite loop --- //
		int sampleCount = 0;
		if (args.length >= 2) {
			sampleCount = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Uncomment this to turn on additional logging
		 * Logger.get_instance().set_verbosity_by_category(
		 * LogCategory.NDDS_CONFIG_LOG_CATEGORY_API,
		 * LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
		 */

		// --- Run --- //
		subscriberMain(domainId, sampleCount);
	}

	// -----------------------------------------------------------------------
	// Private Methods
	// -----------------------------------------------------------------------

	// --- Constructors: -----------------------------------------------------

	private BMP_pressureSubscriber() {
		super();
	}

	// -----------------------------------------------------------------------

	private static void subscriberMain(int domainId, int sampleCount) {

		DomainParticipant participant = null;
		Subscriber subscriber = null;
		Subscriber subscriber2 = null;
		Topic topic = null;
		Topic topic2 = null;
		BMP_pressureDataReader reader = null;
		BMP_pressure_rangeDataReader reader2 = null;

		try {

			// --- Create participant --- //

			/*
			 * To customize participant QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */

			DomainParticipantQos dpqos = new DomainParticipantQos();
			DomainParticipantFactory.get_instance()
					.get_default_participant_qos(dpqos);

			// insert relevant field changes here

			participant = DomainParticipantFactory.TheParticipantFactory
					.create_participant(domainId, dpqos, null /* listener */,
							StatusKind.STATUS_MASK_NONE);
			if (participant == null) {
				System.err.println("create_participant error\n");
				return;
			}

			// --- Create subscriber --- //

			/*
			 * To customize subscriber QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */

			subscriber = participant.create_subscriber(
					DomainParticipant.SUBSCRIBER_QOS_DEFAULT,
					null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (subscriber == null) {
				System.err.println("create_subscriber error\n");
				return;
			}
			// xxxxxxxxxxxxxxxx
			subscriber2 = participant.create_subscriber(
					DomainParticipant.SUBSCRIBER_QOS_DEFAULT,
					null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (subscriber2 == null) {
				System.err.println("create_subscriber2 error\n");
				return;
			}
			// xxxxxxxxxxxxxxxxxxx
			// --- Create topic --- //

			/* Register type before creating topic */
			String typeName = BMP_pressureTypeSupport.get_type_name();
			BMP_pressureTypeSupport.register_type(participant, typeName);

			/*
			 * To customize topic QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */

			topic = participant.create_topic("Example BMP_pressure", typeName,
					DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (topic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			// xxxxxxxxxxxxxxxx
			/* Register type before creating topic */
			String typeName2 = BMP_pressure_rangeTypeSupport.get_type_name();
			BMP_pressure_rangeTypeSupport.register_type(participant, typeName2);

			/*
			 * To customize topic QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */

			topic2 = participant.create_topic("Example BMP_pressure_range",
					typeName2, DomainParticipant.TOPIC_QOS_DEFAULT,
					null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (topic2 == null) {
				System.err.println("create_topic2 error\n");
				return;
			}

			// xxxxxxxxxxxxxxxxx

			// --- Create reader --- //

			/*
			 * To customize data reader QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */
			reader = (BMP_pressureDataReader) subscriber.create_datareader(
					topic, Subscriber.DATAREADER_QOS_DEFAULT, null,
					StatusKind.STATUS_MASK_NONE);
			if (reader == null) {
				System.err.println("create_datareader error\n");
				return;
			}

			// xxxxxxxxxx
			// --- Create reader --- //

			/*
			 * To customize data reader QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */
			reader2 = (BMP_pressure_rangeDataReader) subscriber2
					.create_datareader(topic2,
							Subscriber.DATAREADER_QOS_DEFAULT, null,
							StatusKind.STATUS_MASK_NONE);
			if (reader2 == null) {
				System.err.println("create_datareader2 error\n");
				return;
			}

			/*
			 * If you want to change the DataReader's QoS programmatically
			 * rather than using the XML file, you will need to add the
			 * following lines to your code and comment out the
			 * create_datareader call above.
			 * 
			 * In this case, we reduce the liveliness timeout period to trigger
			 * the StatusCondition DDS_LIVELINESS_CHANGED_STATUS
			 */

			/*
			 * DataReaderQos datareader_qos = new DataReaderQos();
			 * subscriber.get_default_datareader_qos(datareader_qos);
			 * 
			 * datareader_qos.liveliness.lease_duration.sec = 2;
			 * datareader_qos.liveliness.lease_duration.nanosec = 0;
			 * 
			 * reader = (waitsetsDataReader) subscriber.create_datareader(
			 * topic, datareader_qos, null, StatusKind.STATUS_MASK_NONE); if
			 * (reader == null) {
			 * System.err.println("create_datareader error\n"); return; }
			 */

			/*
			 * Create read condition --------------------- Note that the Read
			 * Conditions are dependent on both incoming data as well as sample
			 * state. Thus, this method has more overhead than adding a
			 * DDS_DATA_AVAILABLE_STATUS StatusCondition. We show it here purely
			 * for reference
			 */
			ReadCondition read_condition = reader.create_readcondition(
					SampleStateKind.NOT_READ_SAMPLE_STATE,
					ViewStateKind.ANY_VIEW_STATE,
					InstanceStateKind.ANY_INSTANCE_STATE);

			ReadCondition read_condition2 = reader2.create_readcondition(
					SampleStateKind.NOT_READ_SAMPLE_STATE,
					ViewStateKind.ANY_VIEW_STATE,
					InstanceStateKind.ANY_INSTANCE_STATE);

			/*
			 * Set enabled statuses -------------------- Now that we have the
			 * Status Condition, we are going to enable the statuses we are
			 * interested in: DDS_SUBSCRIPTION_MATCHED_STATUS and
			 * DDS_LIVELINESS_CHANGED_STATUS.
			 */
			/*
			 * status_condition.set_enabled_statuses(
			 * StatusKind.SUBSCRIPTION_MATCHED_STATUS |
			 * StatusKind.LIVELINESS_CHANGED_STATUS);
			 * 
			 * status_condition2.set_enabled_statuses(
			 * StatusKind.SUBSCRIPTION_MATCHED_STATUS |
			 * StatusKind.LIVELINESS_CHANGED_STATUS);
			 */

			/*
			 * Create and attach conditions to the WaitSet
			 * ------------------------------------------- Finally, we create
			 * the WaitSet and attach both the Read Conditions and the Status
			 * Condition to it.
			 */
			WaitSet waitset = new WaitSet();
			waitset.attach_condition(read_condition);
			waitset.attach_condition(read_condition2);

			// WaitSet waitset2 = new WaitSet();
			// waitset2.attach_condition(read_condition2);
			// waitset2.attach_condition(status_condition2);

			// --- Wait for data --- //

			// Duration_t duration = new Duration_t(1, 500000000);
			Duration_t duration = new Duration_t(0, 10000000);

			for (int count = 0; (sampleCount == 0) || (count < sampleCount); ++count) {
     			ConditionSeq active_conditions = new ConditionSeq();
				// ConditionSeq active_conditions2 = new ConditionSeq();
				try {
					/*
					 * wait() blocks execution of the thread until one or more
					 * attached Conditions become true, or until a
					 * user-specified timeout expires.
					 */
					waitset.wait(active_conditions, duration);
					// waitset.wait(active_conditions2, duration);
				} catch (RETCODE_TIMEOUT e) {
					/* We get to timeout if no conditions were triggered */
					System.out.print("Wait timed out!! No conditions were triggered\n");
					
					XMLParser parser = new XMLParser();
					String xml = parser.getXmlFromUrl(URL); // getting
															// XML
					// System.out.println("XMLOUT:" + xml);
					org.w3c.dom.Document doc = parser
							.getDomElement(xml); // getting DOM
													// element
					NodeList nl = doc
							.getElementsByTagName(KEY_PARENT);

					// looping through all item nodes <item>
					for (int i1 = 0; i1 < nl.getLength(); i1++) {
						org.w3c.dom.Element e1 = (org.w3c.dom.Element) nl
								.item(i1);
						BMP_pressureSubscriber.mId = parser
								.getValue(e1, KEY_ID);
						BMP_pressureSubscriber.mTemperature = Double
								.parseDouble(parser.getValue(e1,
										KEY_TEMPRATURE)
										.toString());  
						BMP_pressureSubscriber.mPressure = Double
								.parseDouble(parser.getValue(e1,
										KEY_PRESSURE)
										.toString());  
						BMP_pressureSubscriber.mAltitude = Double
								.parseDouble(parser.getValue(e1,
										KEY_ALTITUDE)
										.toString());  

					/*	System.out.println("id:"
								+ BMP_pressureSubscriber.mId);
						System.out
								.println("temprature:"
										+ BMP_pressureSubscriber.mTemperature);
						System.out
								.println("pressure:"
										+ BMP_pressureSubscriber.mPressure);
						System.out
								.println("altitude:"
										+ BMP_pressureSubscriber.mAltitude);*/
					}

					
					continue;
				}
				/* Get the number of active conditions */
				// System.out.print("Got" + active_conditions.size() +
				// " active conditions\n");

				for (int i = 0; i < active_conditions.size(); ++i) {
					/*
					 * Now we compare the current condition with the Status
					 * Conditions and the Read Conditions previously defined. If
					 * they match, we print the condition that was triggered.
					 */

					/* Compare with Read Conditions */
					if (active_conditions.get(i) == read_condition) {
						/*
						 * Current conditions match our conditions to read data,
						 * so we can read data just like we would do in any
						 * other example.
						 */
						BMP_pressureSeq data_seq = new BMP_pressureSeq();
						SampleInfoSeq info_seq = new SampleInfoSeq();

						/*
						 * You may want to call take_w_condition() or
						 * read_w_condition() on the Data Reader. This way you
						 * will use the same status masks that were set on the
						 * Read Condition. This is just a suggestion, you can
						 * always use read() or take() like in any other
						 * example.
						 */
						reader.read_w_condition(data_seq, info_seq,
								ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
								read_condition);

						for (int j = 0; j < info_seq.size(); ++j) {
							if (!((SampleInfo) info_seq.get(j)).valid_data) {
								// System.out.print("Got metadata\n");
								continue;
							}
							count++;
							BMP_pressure data = (BMP_pressure) data_seq.get(j);
							// System.out.println("ID: " + data.id);
							mId = data.id;
							// System.out.println("Temperature: " +
							// data.Temperature);
							mTemperature = data.Temperature;
							// System.out.println("Pressure: " + data.Pressure);
							mPressure = data.Pressure;
							// System.out.println("Altitude: " + data.Altitude);
							mAltitude = data.Altitude;

						}
						reader.return_loan(data_seq, info_seq);
					} else if (active_conditions.get(i) == read_condition2) {
						/*
						 * Current conditions match our conditions to read data,
						 * so we can read data just like we would do in any
						 * other example.
						 */
						BMP_pressure_rangeSeq data_seq = new BMP_pressure_rangeSeq();
						SampleInfoSeq info_seq2 = new SampleInfoSeq();

						/*
						 * You may want to call take_w_condition() or
						 * read_w_condition() on the Data Reader. This way you
						 * will use the same status masks that were set on the
						 * Read Condition. This is just a suggestion, you can
						 * always use read() or take() like in any other
						 * example.
						 */
						reader2.read_w_condition(data_seq, info_seq2,
								ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
								read_condition2);

						for (int j = 0; j < info_seq2.size(); ++j) {
							if (!((SampleInfo) info_seq2.get(j)).valid_data) {
								// System.out.print("Got metadata\n");
								continue;
							}
							count++;
							BMP_pressure_range data = (BMP_pressure_range) data_seq
									.get(j);

							System.out.println("ID: " + data.id1);
							mId = data.id1;

							System.out.println("Pressure_high: "
									+ data.Pressure_high);
							mPressure_high = (int) data.Pressure_high;

							System.out.println("Pressure_low: "
									+ data.Pressure_low);
							mPressure_low = (int) data.Pressure_low;

						}
						reader2.return_loan(data_seq, info_seq2);
					}
				}

			}
		} finally {

			// --- Shutdown --- //

			if (participant != null) {
				participant.delete_contained_entities();

				DomainParticipantFactory.TheParticipantFactory
						.delete_participant(participant);
			}
			/*
			 * RTI Connext provides the finalize_instance() method for users who
			 * want to release memory used by the participant factory singleton.
			 * Uncomment the following block of code for clean destruction of
			 * the participant factory singleton.
			 */
			DomainParticipantFactory.finalize_instance();
		}
	}

}
