
echo "Generating Type Support code from IDL \n"
rtiddsgen -replace -language C ShapeType.idl

echo " Building the source code \n"
make -f makefile_ShapeType_armv6vfphLinux3.xgcc4.7.2

echo "Runnign the appication, pass sudo password where needed\n"
sudo ./objs/armv6vfphLinux3.xgcc4.7.2/ShapeType_publisher


echo "Use Android DDS ShapesDemo app and subscribe to a RED Square\n"
echo "Alternatively on the other terminal run the subscriber applicatio \n"
echo "$.objs/armv6vfphLinux3.xgcc4.7.2/ShapeType_subscriber\n"



