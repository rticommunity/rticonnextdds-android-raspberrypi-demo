Demo: Barometric sensor data (Temperature, Pressure and Altitude) and pressure alarm range configuration

Using two topics for,

1. Data exchange
      struct BMP_pressure {
           string id; //@key
           double Temperature; //in C
           double Pressure; //in kPa
           double Altitude; //in meter   
      };

2. Alarm range parameter configuration
      struct BMP_pressure_range {
           string id1; //@key
           double Pressure_high; //in kPa 
           double Pressure_low; //in kPa
      };
      
Real-time data visualization on Android application
  Reading data using Wait-Set
  Default QoS "BuiltinQosLibExp::Generic.StrictReliable.LowLatency"

Pressure Range (high and low) alarm configuration
  Reading data using Polling
  Default QoS "BuiltinQosLibExp::Pattern.Event"
