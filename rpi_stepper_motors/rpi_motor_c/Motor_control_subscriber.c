
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
 
/* Motor_control_subscriber.c

 A subscription example

 This file is derived from code automatically generated by the rtiddsgen 
 command:

 rtiddsgen -language C -example <arch> Motor_control.idl

 Example subscription of type MotorControl automatically generated by 
 'rtiddsgen'. To test them, follow these steps:

 (1) Compile this file and the example publication.

 (2) Start the subscription on the same domain used for RTI Data Distribution
 Service  with the command
 objs/<arch>/Motor_control_subscriber <domain_id> <sample_count>

 (3) Start the publication on the same domain used for RTI Data Distribution
 Service with the command
 objs/<arch>/Motor_control_publisher <domain_id> <sample_count>

 (4) [Optional] Specify the list of discovery initial peers and 
 multicast receive addresses via an environment variable or a file 
 (in the current working directory) called NDDS_DISCOVERY_PEERS. 

 You can run any number of publishers and subscribers programs, and can 
 add and remove them dynamically from the domain.


 Example:

 To run the example application on domain <domain_id>:

 On UNIX systems: 

 objs/<arch>/Motor_control_publisher <domain_id> 
 objs/<arch>/Motor_control_subscriber <domain_id> 

 On Windows systems:

 objs\<arch>\Motor_control_publisher <domain_id>  
 objs\<arch>\Motor_control_subscriber <domain_id>   


 modification history
 ------------ -------       
 */

#include <stdio.h>
#include <stdlib.h>
#include "ndds/ndds_c.h"
#include "Motor_control.h"
#include "Motor_controlSupport.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <stdint.h>
#include <pthread.h>
#include <semaphore.h>
#include <sys/mman.h>
#include <signal.h>
#include <unistd.h>

int volatile mDirection = 0;
int volatile mSpeed = 0;
int volatile mStart = 1;
int volatile mMotorId = 4;
int volatile mTimeSec_t = 3;
int mTimeSec = 3;

FILE *pin0_a;
FILE *pin1_a;
FILE *pin2_a;
FILE *pin3_a;

FILE *pin0_b;
FILE *pin1_b;
FILE *pin2_b;
FILE *pin3_b;

//struct timespec mts = { 0, 0 };

pthread_mutex_t mutexA;
pthread_mutex_t mutexB;

void  InitMutexA() {
	if(pthread_mutex_init(&mutexA, NULL) != 0) {
		 printf("\n mutexA init failed\n");
		        return 1;
	}
}
void InitMutexB() {
	if(pthread_mutex_init(&mutexB, NULL) != 0) {
		 printf("\n mutexB init failed\n");
				        return 1;
	}

}


 
/**************Motor Code Start************/

// General purpose error message
// A real system would probably have a better error handling method...
static void panic(char *message) {
	fprintf(stderr, "Fatal error: %s\n", message);
	exit(1);
}

#define MAX_LOGENTRIES 200000
static unsigned int logindex;
static struct timespec timestamps[MAX_LOGENTRIES];

static void logtimestamp(void) {
	clock_gettime(CLOCK_MONOTONIC, &timestamps[logindex++]);
	if (logindex > MAX_LOGENTRIES) {
		logindex = 0;
	}
}

static void dumptimestamps(int unused) {
	FILE *fp = fopen("timestamps0.txt", "w");
	int i;
	for (i = 0; i < logindex; i++) {
		if (timestamps[i].tv_sec > 0) {
			fprintf(fp, "%d.%09d\n", (int) timestamps[i].tv_sec,
					(int) timestamps[i].tv_nsec);
			// printf("Time:%d.%09d\n", (int) timestamps[i].tv_sec,
			//          (int) timestamps[i].tv_nsec);

		}
	}
	fclose(fp);
	exit(0);
}

// Initialize a GPIO pin in Linux using the sysfs interface
FILE *init_gpio(int gpioport) {
	// Export the pin to the GPIO directory
	FILE *fp = fopen("/sys/class/gpio/export", "w");
	fprintf(fp, "%d", gpioport);
	//  printf("fp =%d\n",gpioport);
	fclose(fp);

	// Set the pin as an output
	char filename[256];
	sprintf(filename, "/sys/class/gpio/gpio%d/direction", gpioport);
	// printf("filename /sys/class/gpio/gpio%d/direction\n",gpioport);
	fp = fopen(filename, "w");
	if (!fp) {
		panic("Could not open gpio file");
	}
	fprintf(fp, "out");
	fclose(fp);

	// Open the value file and return a pointer to it.
	sprintf(filename, "/sys/class/gpio/gpio%d/value", gpioport);
	//printf(" filename /sys/class/gpio/gpio%d/value\n",gpioport);
	fp = fopen(filename, "w");
	if (!fp) {
		panic("Could not open gpio file");
	}
	return fp;
}

// Given a FP in the stepper struct, set the I/O pin
// to the specified value. Uses the sysfs GPIO interface.
void setiopin(FILE *fp, int val) {
	fprintf(fp, "%d\n", val);
	fflush(fp);
}

// Adds "delay" nanoseconds to timespecs and sleeps until that time
static void sleep_until(struct timespec *ts, int delay) {

	ts->tv_nsec += delay;
	if (ts->tv_nsec >= 1000 * 1000 * 1000) {
		ts->tv_nsec -= 1000 * 1000 * 1000;
		ts->tv_sec++;
	}
	clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, ts, NULL);
}

/*****
 * Demo program for running a stepper connected to the Raspberry PI
platform.
# Motor definitions
  motora = Motor(17,18,27,22)
  motorb = Motor(4,25,24,23)

  Physical pins 11, 12, 13, 15 for Motor A
  Physical pins 16, 18, 22, 7 for Motor B
********/

void *Motor_B(void *delay_sec) {

	pthread_mutex_lock(&mutexB);

	fflush(stdout);

 	unsigned int delay = mSpeed * 1000000; // Note: Delay in ns i.e.,

	struct timespec ts;
	clock_gettime(CLOCK_MONOTONIC, &ts);
	//range of speed is .9mil sec to 9 mils


	time_t start_time;
	time_t current_time;

	start_time = time(NULL);
	current_time = time(NULL);
	printf("Start Motor_B for %d \n", mTimeSec_t);

	if (mDirection == 0) {
		/*Clock wise*/
		pin0_b = init_gpio(4);
		pin1_b = init_gpio(25);
		pin2_b = init_gpio(24);
		pin3_b = init_gpio(23);
	}
	if (mDirection == 1) {
		/*Anti-Clock wise*/
		pin0_b = init_gpio(23);
		pin1_b = init_gpio(24);
		pin2_b = init_gpio(25);
		pin3_b = init_gpio(4);
	}

	if (mDirection == 2) {
		/*Anti-Clock wise*/
		pin0_b = init_gpio(23);
		pin1_b = init_gpio(24);
		pin2_b = init_gpio(25);
		pin3_b = init_gpio(4);
	}

	if (mDirection == 3) {
		/*Clock wise*/
		pin0_b = init_gpio(4);
		pin1_b = init_gpio(25);
		pin2_b = init_gpio(24);
		pin3_b = init_gpio(23);
	}

/ * Few Temp variables to hold old valuse before entering in While loop*/
	int mDirection_t = mDirection;
	int mDuration_t = mTimeSec_t;
	int mSpeed_t = mSpeed;
 
	while (mStart == 0) {

		if (mMotorId == 1) {
			goto end;
		}
		if( (mDuration_t != mTimeSec_t) || (mSpeed_t != mSpeed) ) {
			goto end;
		}

		delay = mSpeed * 1000000;
 
		setiopin(pin0_b, 1);
		sleep_until(&ts, delay);

		setiopin(pin3_b, 0);
		sleep_until(&ts, delay);

		setiopin(pin1_b, 1);
		sleep_until(&ts, delay);

		setiopin(pin0_b, 0);
		sleep_until(&ts, delay);

		setiopin(pin2_b, 1);
		sleep_until(&ts, delay);

		setiopin(pin1_b, 0);
		sleep_until(&ts, delay);

		setiopin(pin3_b, 1);
		sleep_until(&ts, delay);

		setiopin(pin2_b, 0);
		sleep_until(&ts, delay);

		current_time = time(NULL);



	if (current_time <= start_time + mTimeSec_t) {
			continue;
		} else {
			goto end;
		}

		if (mMotorId == 2 || mMotorId == 3) {
			continue;
		} else {
			goto end;
		}

		if (mDirection_t == mDirection) {
			continue;
		} else {
			goto end;
		}

	}

	end:

	setiopin(pin0_b, 0);
	setiopin(pin1_b, 0);
	setiopin(pin2_b, 0);
	setiopin(pin3_b, 0);


	pthread_mutex_unlock(&mutexB);
	pthread_mutex_destroy(&mutexB);
//	pthread_exit(NULL);

 pthread_exit(Motor_B);
	return NULL;
}
 
 
void *Motor_A(void *delay_sec) {

	pthread_mutex_lock(&mutexA);

	fflush(stdout);

 	unsigned int delay = mSpeed * 1000000; // Note: Delay in ns i.e.,
	struct timespec ts;
	clock_gettime(CLOCK_MONOTONIC, &ts);

	time_t start_time;
	time_t current_time;

	start_time = time(NULL);
	current_time = time(NULL);
	printf("Start Motor_A for %d \n", mTimeSec);

	if (mDirection == 0) {
		/*Clock*/
		pin0_a = init_gpio(22);
		pin1_a = init_gpio(27);
		pin2_a = init_gpio(18);
		pin3_a = init_gpio(17);

	}

	if (mDirection == 1) {
		/*Anti Clock*/
		pin0_a = init_gpio(17);
		pin1_a = init_gpio(18);
		pin2_a = init_gpio(27);
		pin3_a = init_gpio(22);

	}

	if (mDirection == 2) {
		/*Clock*/
		pin0_a = init_gpio(22);
		pin1_a = init_gpio(27);
		pin2_a = init_gpio(18);
		pin3_a = init_gpio(17);

	}

	if (mDirection == 3) {
		/*Anti Clock*/
		pin0_a = init_gpio(17);
		pin1_a = init_gpio(18);
		pin2_a = init_gpio(27);
		pin3_a = init_gpio(22);

	}

	int mDirection_t = mDirection;
 	int mDuration_t = mTimeSec_t;
	int mSpeed_t = mSpeed;
	
	while (mStart == 0) {
		if (mMotorId == 2) {

			goto end;
		}
		if( (mDuration_t != mTimeSec_t) || (mSpeed_t != mSpeed) ) {
			goto end;
		}


		delay = mSpeed * 1000000;


		setiopin(pin0_a, 1);
		sleep_until(&ts, delay);

		setiopin(pin3_a, 0);
		sleep_until(&ts, delay);

		setiopin(pin1_a, 1);
		sleep_until(&ts, delay);

		setiopin(pin0_a, 0);
		sleep_until(&ts, delay);

		setiopin(pin2_a, 1);
		sleep_until(&ts, delay);

		setiopin(pin1_a, 0);
		sleep_until(&ts, delay);

		setiopin(pin3_a, 1);
		sleep_until(&ts, delay);

		setiopin(pin2_a, 0);
		sleep_until(&ts, delay);


		current_time = time(NULL);


		if (current_time <= start_time + mTimeSec_t) {
			continue;
		} else {
			goto end;
		}

		if (mMotorId == 1 || mMotorId == 3) {
			continue;
		} else {
			goto end;
		}

		if (mDirection_t == mDirection) {
			continue;
		} else {
			goto end;

		}

	}

	end:

	setiopin(pin0_a, 0);
	setiopin(pin1_a, 0);
	setiopin(pin2_a, 0);
	setiopin(pin3_a, 0);

	pthread_mutex_unlock(&mutexA);
	pthread_mutex_destroy(&mutexA);
	pthread_exit(Motor_A);
	//pthread_exit(NULL);


	return NULL;

}

/* Delete all entities */
static int subscriber_shutdown(DDS_DomainParticipant *participant) {
	DDS_ReturnCode_t retcode;
	int status = 0;

	if (participant != NULL) {
		retcode = DDS_DomainParticipant_delete_contained_entities(participant);
		if (retcode != DDS_RETCODE_OK) {
			printf("delete_contained_entities error %d\n", retcode);
			status = -1;
		}

		retcode = DDS_DomainParticipantFactory_delete_participant(
				DDS_TheParticipantFactory, participant);
		if (retcode != DDS_RETCODE_OK) {
			printf("delete_participant error %d\n", retcode);
			status = -1;
		}
	}

	/* RTI Data Distribution Service provides the finalize_instance() method on
	 domain participant factory for users who want to release memory used
	 by the participant factory. Uncomment the following block of code for
	 clean destruction of the singleton. */
	/*
	 retcode = DDS_DomainParticipantFactory_finalize_instance();
	 if (retcode != DDS_RETCODE_OK) {
	 printf("finalize_instance error %d\n", retcode);
	 status = -1;
	 }
	 */

	return status;
}

static int subscriber_main(int domainId, int sample_count) {
	DDS_DomainParticipant *participant = NULL;
	DDS_Subscriber *subscriber = NULL;
	DDS_Topic *topic = NULL;
	struct DDS_DataReaderListener reader_listener =
			DDS_DataReaderListener_INITIALIZER;
	DDS_DataReader *reader = NULL;
	DDS_ReturnCode_t retcode;
	const char *type_name = NULL;
	int count = 0;
 

	DDS_StatusCondition *status_condition;
	DDS_WaitSet *waitset = NULL;
	MotorControlDataReader *waitsets_reader = NULL;
	struct DDS_Duration_t timeout = { 4, 0 };
	struct DDS_Duration_t poll_period = { 4, 0 };

	/* To customize participant QoS, use 
	 the configuration file USER_QOS_PROFILES.xml */
	participant = DDS_DomainParticipantFactory_create_participant(
			DDS_TheParticipantFactory, domainId, &DDS_PARTICIPANT_QOS_DEFAULT,
			NULL /* listener */, DDS_STATUS_MASK_NONE);
	if (participant == NULL) {
		printf("create_participant error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	/* To customize subscriber QoS, use 
	 the configuration file USER_QOS_PROFILES.xml */
	subscriber = DDS_DomainParticipant_create_subscriber(participant,
			&DDS_SUBSCRIBER_QOS_DEFAULT, NULL /* listener */,
			DDS_STATUS_MASK_NONE);
	if (subscriber == NULL) {
		printf("create_subscriber error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	/* Register the type before creating the topic */
	type_name = MotorControlTypeSupport_get_type_name();
	retcode = MotorControlTypeSupport_register_type(participant, type_name);
	if (retcode != DDS_RETCODE_OK) {
		printf("register_type error %d\n", retcode);
		subscriber_shutdown(participant);
		return -1;
	}

	/* To customize topic QoS, use 
	 the configuration file USER_QOS_PROFILES.xml */
	topic = DDS_DomainParticipant_create_topic(participant,
			"Example MotorControl", type_name, &DDS_TOPIC_QOS_DEFAULT,
			NULL /* listener */, DDS_STATUS_MASK_NONE);
	if (topic == NULL) {
		printf("create_topic error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	/* To customize data reader QoS, use 
	 the configuration file USER_QOS_PROFILES.xml */
	reader = DDS_Subscriber_create_datareader(subscriber,
			DDS_Topic_as_topicdescription(topic), &DDS_DATAREADER_QOS_DEFAULT,
			NULL, DDS_STATUS_MASK_NONE);
	if (reader == NULL) {
		printf("create_datareader error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	status_condition = DDS_Entity_get_statuscondition((DDS_Entity*) reader);
	if (status_condition == NULL) {
		printf("get_statuscondition error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	// Since a single status condition can match many statuses,
	// enable only those we're interested in.
	retcode = DDS_StatusCondition_set_enabled_statuses(status_condition,
			DDS_DATA_AVAILABLE_STATUS);
	if (retcode != DDS_RETCODE_OK) {
		printf("set_enabled_statuses error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	// Create WaitSet, and attach conditions
	waitset = DDS_WaitSet_new();
	if (waitset == NULL) {
		printf("create waitset error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	retcode = DDS_WaitSet_attach_condition(waitset,
			(DDS_Condition*) status_condition);
	if (retcode != DDS_RETCODE_OK) {
		printf("attach_condition error\n");
		subscriber_shutdown(participant);
		return -1;
	}

	// Get narrowed datareader
	waitsets_reader = MotorControlDataReader_narrow(reader);
	if (waitsets_reader == NULL) {
		printf("DataReader narrow error\n");
		return -1;
	}


	int rcA;
	int rcB;


	/* Main loop */
	for (count = 0; (sample_count == 0) || (count < sample_count); ++count) {
		struct DDS_ConditionSeq active_conditions = DDS_SEQUENCE_INITIALIZER;
		int i, j;

		retcode = DDS_WaitSet_wait(waitset, &active_conditions, &timeout);
		if (retcode == DDS_RETCODE_TIMEOUT) {
			printf("wait timed out\n");
			count += 2;
			continue;
		} else if (retcode != DDS_RETCODE_OK) {
			printf("wait returned error: %d\n", retcode);
			break;
		}

		printf("got %d active conditions\n",
				DDS_ConditionSeq_get_length(&active_conditions));

		for (i = 0; i < DDS_ConditionSeq_get_length(&active_conditions); ++i) {
			if (DDS_ConditionSeq_get(&active_conditions, i)
					== (DDS_Condition*) status_condition) {
				// A status condition triggered--see which ones
				DDS_StatusMask triggeredmask;
				triggeredmask = DDS_Entity_get_status_changes(
						(DDS_Entity*) waitsets_reader);

				if (triggeredmask & DDS_DATA_AVAILABLE_STATUS) {
					struct MotorControlSeq data_seq = DDS_SEQUENCE_INITIALIZER;
					struct DDS_SampleInfoSeq info_seq = DDS_SEQUENCE_INITIALIZER;

					retcode = MotorControlDataReader_take(waitsets_reader,
							&data_seq, &info_seq, DDS_LENGTH_UNLIMITED,
							DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE,
							DDS_ANY_INSTANCE_STATE);
					if (retcode != DDS_RETCODE_OK) {
						printf("take error %d\n", retcode);
						break;
					}

					for (j = 0; j < MotorControlSeq_get_length(&data_seq);
							++j) {
						if (!DDS_SampleInfoSeq_get_reference(&info_seq, j)->valid_data) {
							printf("Got metadata\n");
							continue;
						}

						struct MotorControl *sample_t =
								MotorControlSeq_get_reference(&data_seq, j);

						printf("%s %d %s %d %s\n", sample_t->motor_id,
								sample_t->time_sec, sample_t->direction,
								sample_t->speed, sample_t->action);
			 
						mMotorId = atoi(sample_t->motor_id);
						mTimeSec_t = mTimeSec = sample_t->time_sec;

						mDirection = atoi(sample_t->direction);
						mSpeed = sample_t->speed;
						mStart = atoi(sample_t->action);



						printf(
								"Selection: Motor %d for sec %d, direction=%d, Speed=%d,  mStart=%d\n",
								mMotorId, mTimeSec, mDirection, mSpeed, mStart);

			 			if (mMotorId == 1) {
							if (mDirection == 2) {
								//make inwards to Anti-clock
								mDirection = 1;
							}
							if (mDirection == 3) {
								//make outwards to clock
								mDirection = 0;
							}
							pthread_t motor_a;


							InitMutexA();


							rcA = pthread_create(&motor_a, NULL, Motor_A,
									&mTimeSec);
							if (rcA) {
								printf(
										"ERROR; return code from pthread_create() is %d\n",
										rcA);
								exit(-1);
							}
							// pthread_join(motor_a, NULL);
							pthread_detach(motor_a);

						}

						if (mMotorId == 2) {
							if (mDirection == 2) {
								mDirection = 1;
							}
							if (mDirection == 3) {
								//make outwards to clock
								mDirection = 0;
							}
							pthread_t motor_b;
							InitMutexB();


							rcB = pthread_create(&motor_b, NULL, Motor_B,
									&mTimeSec);
							if (rcB) {
								printf(
										"ERROR; return code from pthread_create() is %d\n",
										rcB);
								exit(-1);
							}

							// pthread_join(motor_b, NULL);
							pthread_detach(motor_b);


						}

						if (mMotorId == 3)

						{
							InitMutexA();
							InitMutexB();
							pthread_t motor_a, motor_b;



							rcA = pthread_create(&motor_a, NULL, Motor_A,
									&mTimeSec);
							if (rcA) {
								printf(
										"ERROR; return code from pthread_create() is %d\n",
										rcA);
								exit(-1);
							}

							rcB = pthread_create(&motor_b, NULL, Motor_B,
									&mTimeSec);
							if (rcB) {
								printf(
										"ERROR; return code from pthread_create() is %d\n",
										rcB);
								exit(-1);
							}

							pthread_detach(motor_a);
							pthread_detach(motor_b);
							//pthread_join(motor_a, NULL);
							//pthread_join(motor_b, NULL);

						}
						/*
						 MotorControlTypeSupport_print_data(
						 MotorControlSeq_get_reference(&data_seq, j));

						 */
						continue;
					}

					retcode = MotorControlDataReader_return_loan(
							waitsets_reader, &data_seq, &info_seq);
					if (retcode != DDS_RETCODE_OK) {
						printf("return loan error %d\n", retcode);
					}

				}

			}
		}
	}

	pthread_exit(Motor_A);
	pthread_exit(Motor_B);
	/* Delete all entities */
	retcode = DDS_WaitSet_delete(waitset);
	if (retcode != DDS_RETCODE_OK) {
		printf("delete waitset error %d\n", retcode);
	}

	/* Cleanup and delete all entities */
	return subscriber_shutdown(participant);
}

#if defined(RTI_WINCE)
int wmain(int argc, wchar_t** argv)
{
	int domainId = 0;
	int sample_count = 0; /* infinite loop */

	if (argc >= 2) {
		domainId = _wtoi(argv[1]);
	}
	if (argc >= 3) {
		sample_count = _wtoi(argv[2]);
	}

	/* Uncomment this to turn on additional logging
	 NDDS_Config_Logger_set_verbosity_by_category(
	 NDDS_Config_Logger_get_instance(),
	 NDDS_CONFIG_LOG_CATEGORY_API, 
	 NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
	 */

	return subscriber_main(domainId, sample_count);
}
#elif !(defined(RTI_VXWORKS) && !defined(__RTP__)) && !defined(RTI_PSOS)
int main(int argc, char *argv[]) {
	int domainId = 0;
	int sample_count = 0; /* infinite loop */

	if (argc >= 2) {
		domainId = atoi(argv[1]);
	}
	if (argc >= 3) {
		sample_count = atoi(argv[2]);
	}

	/* Uncomment this to turn on additional logging
	 NDDS_Config_Logger_set_verbosity_by_category(
	 NDDS_Config_Logger_get_instance(),
	 NDDS_CONFIG_LOG_CATEGORY_API, 
	 NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
	 */

	return subscriber_main(domainId, sample_count);
}
#endif

#ifdef RTI_VX653
const unsigned char* __ctype = NULL;

void usrAppInit ()
{
#ifdef  USER_APPL_INIT
	USER_APPL_INIT; /* for backwards compatibility */
#endif

	/* add application specific code here */
	taskSpawn("sub", RTI_OSAPI_THREAD_PRIORITY_NORMAL, 0x8, 0x150000, (FUNCPTR)subscriber_main, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

}
#endif
