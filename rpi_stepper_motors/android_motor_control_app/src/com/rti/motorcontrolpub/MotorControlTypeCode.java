
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package com.rti.motorcontrolpub;
        
import com.rti.dds.typecode.*;


public class MotorControlTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[5];

        sm[i]=new StructMember("motor_id",false,(short)-1,true,(TypeCode)new TypeCode(TCKind.TK_STRING,128),0,false); i++;
        sm[i]=new StructMember("time_sec",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG,1,false); i++;
        sm[i]=new StructMember("direction",false,(short)-1,false,(TypeCode)new TypeCode(TCKind.TK_STRING,5),2,false); i++;
        sm[i]=new StructMember("speed",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG,3,false); i++;
        sm[i]=new StructMember("action",false,(short)-1,false,(TypeCode)new TypeCode(TCKind.TK_STRING,5),4,false); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("MotorControl",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
