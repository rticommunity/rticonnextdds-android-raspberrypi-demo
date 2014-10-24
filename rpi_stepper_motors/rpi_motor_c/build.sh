
echo "Generating Type Support code from IDL \n"
rtiddsgen -replace -language C Motor_control.idl
echo " Building the source code \n"
make -f makefile_Motor_control_armv6vfphLinux3.xgcc4.7.2
echo "Runnign the appication, pass sudo password where needed\n"
sudo ./objs/armv6vfphLinux3.xgcc4.7.2/Motor_control_subscriber
