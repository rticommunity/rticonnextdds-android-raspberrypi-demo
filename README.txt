rticonnextdds-android-raspberrypi-demo 
 For more details http://community.rti.com/howto/rti-connext-dds-android-raspberry-pi-demos

======================================
1. What problem does it solve:
  1. Systems integration via platform agnostics.
  2. Demonstrates systems integration and platform agnostics by linking together,
      * Different computing systems (CPU+OS) and,
      * Software application languages (C, C++, Java etc.,).
  3. To exchange various types of data (i.e., Values, Events, Alarms, Commands, Streams) coordinated between publishers and subscribers over LAN and/or WAN.

2. Visibility into system:
  * Publisher and Subscriber match analysis, debugging, host identification,
  * QoS offered and requested between publishers and subscribers, 
  * Data and metadata exchange and communication statics, middleware and application performance, system snapshot.
  
3. Demo architecture: 
  Various sensors (ultrasonic/barometric) and stepper motors connected with Raspberry Pi (ARM + Linux) exchanging data with discrete platforms (x86/Win, x86/Linux, ARM/Android) and application languages (Java, C, C++ and LabView). 

4. System software and configuration
  * RASPBIAN OS (Wheezy) - http://www.raspberrypi.org/downloads/.
  * RTI Connext DDS 5.1.0 Libraries for Raspberry Pi - http://community.rti.com/downloads/rti-connext-dds-raspberry-pi.
  * Raspberry Pi GPIO Setup - https://learn.adafruit.com/adafruits-raspberry-pi-lesson-4-gpio-setup/configuring-gpio.

5. Bill of materials
  * Barometric Temperature, Pressure and Altitude Sensor BMP180 or BMP085.
  * Ultrasonic sensor SRF05. 
  * 2 x Stepper motors 28BYJ-48.
  * Raspberry Pi Model B with 512MB RAM.
  * Google Nexus 7 tablet (UI might need adjustments for different screen sizes)
