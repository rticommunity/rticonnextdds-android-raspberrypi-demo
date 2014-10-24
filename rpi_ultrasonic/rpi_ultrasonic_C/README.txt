
Before compiling or running the example, make sure the environment variable 
NDDSHOME is set to the directory where your version of RTI Connext is installed.

Run rtiddsgen with the options shown below. The RTI Connext Core 
Libraries and Utilities Getting Started Guide describes this process in detail. 

Follow the same procedure to generate the code and build the examples. As we already have ShapeType_publisher.c (application) and makefile_BMP_temp_pressure_armv6vfphLinux3.xgcc4.7 (source build file). We only need to generate DDS Type Support files using rtiddsgen. 


rtiddsgen -replace -language C Mouse_data.idl
