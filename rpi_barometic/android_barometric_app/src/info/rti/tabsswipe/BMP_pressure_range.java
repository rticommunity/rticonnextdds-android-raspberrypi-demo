/*****************************************************************************/
/*         (c) Copyright, Real-Time Innovations, All rights reserved.        */
/*                                                                           */
/*         Permission to modify and use for internal purposes granted.       */
/* This software is provided "as is", without warranty, express or implied.  */
/*                                                                           */
/*****************************************************************************/

/*******************************************************************************
 (c) 2005-2014 Copyright, Real-Time Innovations, Inc.  All rights reserved.
 RTI grants Licensee a license to use, modify, compile, and create derivative
 works of the Software.  Licensee has the right to distribute object form only
 for use with RTI products.  The Software is provided "as is", with no warranty
 of any type, including any warranty for fitness for any purpose. RTI is under
 no obligation to maintain or support the Software.  RTI shall not be liable for
 any incidental or consequential damages arising out of the use or inability to
 use the software.
 ******************************************************************************/

/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package info.rti.tabsswipe;
        

import java.io.Serializable;

import com.rti.dds.cdr.CdrHelper;
import com.rti.dds.infrastructure.Copyable;


public class BMP_pressure_range implements Copyable, Serializable
{

    public String id1 = ""; /* maximum length = (64) */
    public double Pressure_high = 0;
    public double Pressure_low = 0;


    public BMP_pressure_range() {

    }


    public BMP_pressure_range(BMP_pressure_range other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        BMP_pressure_range self;
        self = new BMP_pressure_range();
         
        self.clear();
        
        return self;
    }

    public void clear() {
        
        id1 = "";
            
        Pressure_high = 0;
            
        Pressure_low = 0;
            
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        BMP_pressure_range otherObj = (BMP_pressure_range)o;



        if(!id1.equals(otherObj.id1)) {
            return false;
        }
            
        if(Pressure_high != otherObj.Pressure_high) {
            return false;
        }
            
        if(Pressure_low != otherObj.Pressure_low) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += id1.hashCode();
                
        __result += (int)Pressure_high;
                
        __result += (int)Pressure_low;
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>BMP_pressure_rangeTypeSupport</code>
     * rather than here by using the <code>-noCopyable</code> option
     * to rtiddsgen.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>this</code>.
     * @exception NullPointerException If <code>src</code> is null.
     * @exception ClassCastException If <code>src</code> is not the 
     * same type as <code>this</code>.
     * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
     */
    public Object copy_from(Object src) {
        

        BMP_pressure_range typedSrc = (BMP_pressure_range) src;
        BMP_pressure_range typedDst = this;

        typedDst.id1 = typedSrc.id1;
            
        typedDst.Pressure_high = typedSrc.Pressure_high;
            
        typedDst.Pressure_low = typedSrc.Pressure_low;
            
        return this;
    }


    
    public String toString(){
        return toString("", 0);
    }
        
    
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        
                        
        
        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }
        
        
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("id1: ").append(id1).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("Pressure_high: ").append(Pressure_high).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("Pressure_low: ").append(Pressure_low).append("\n");
            
        return strBuffer.toString();
    }
    
}

