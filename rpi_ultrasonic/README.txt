Demo : Ultrasonic sensor data (object distance)
  Using one topic
   1. DDS shapes “Square”
   
                  struct ShapeType {
                      string<128> color; //@key //@ID 0
                      long x; //@ID 1
                      long y; //@ID 2
                      long shapesize; //@ID 3
                  };
                  
Ultrasonic sensor data (object distance)
  Real-time visualization on Android application
  Reading data using Listener,
  Default QoS "BuiltinQosLibExp::Generic.StrictReliable.LowLatency"
