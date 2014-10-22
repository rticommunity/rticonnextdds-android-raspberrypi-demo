Before compiling or running the example, make sure the environment variable 
NDDSHOME is set to the directory where your version of RTI Connext is installed.

Run rtiddsgen with the options shown below. The RTI Connext Core 
Libraries and Utilities Getting Started Guide describes this process in detail. 
Follow the same procedure to generate the code and build the examples. 

Go to this link to follow Installation and Setup section to add RTI DDS libraries in your Android project https://github.com/rticommunity/rtishapesdemo-android/blob/master/ShapesDemo/README.md 

Just replace the step 4 "Generate the type support code for Java" with below one

rtiddsgen -ppDisable -language Java -package info.rti.tabsswipe -example armv7aAndroid2.3gcc4.8jdk BMP_pressure.idl
