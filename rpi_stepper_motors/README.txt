Demo: Stepper motors command and control via Android app

Using one topic,

 1. To send command parameters 
 
        struct MotorControl{
               string<128> motor_id; //@key //@ID 0
               long time_sec ; //@ID 1
               string<5> direction; //@ID 2 //clock, anti, inwards and outwards
               long speed; //@ID 3
               string <5> action; //@ID 4 //start or stop
        };
        
  Reading commands using Wait-Set,

  Default QoS "BuiltinQosLibExp::Generic.StrictReliable.LowLatency"
