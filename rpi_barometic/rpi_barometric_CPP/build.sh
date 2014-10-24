
echo "Generating Type Support code from IDL \n"
rtiddsgen -replace -language C++ BMP_temp_pressure.idl
echo " Building the source code \n"
make -f makefile_BMP_temp_pressure_armv6vfphLinux3.xgcc4.7.2
echo "Runnign the appication, pass sudo password where needed\n"
./objs/armv6vfphLinux3.xgcc4.7.2/BMP_temp_pressure_publisher
