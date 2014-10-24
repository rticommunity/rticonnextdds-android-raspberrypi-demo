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

package com.rti.motorcontrolpub;

import java.io.Serializable;

import com.rti.dds.cdr.CdrHelper;
import com.rti.dds.infrastructure.Copyable;

public class MotorControl implements Copyable, Serializable {

	public String motor_id = ""; /* maximum length = (128) */
	public int time_sec = 0;
	public String direction = ""; /* maximum length = (5) */
	public int speed = 0;
	public String action = ""; /* maximum length = (5) */

	public MotorControl() {

	}

	public MotorControl(MotorControl other) {

		this();
		copy_from(other);
	}

	public static Object create() {
		MotorControl self;
		self = new MotorControl();

		self.clear();

		return self;
	}

	public void clear() {

		motor_id = "";

		time_sec = 0;

		direction = "";

		speed = 0;

		action = "";

	}

	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		MotorControl otherObj = (MotorControl) o;

		if (!motor_id.equals(otherObj.motor_id)) {
			return false;
		}

		if (time_sec != otherObj.time_sec) {
			return false;
		}

		if (!direction.equals(otherObj.direction)) {
			return false;
		}

		if (speed != otherObj.speed) {
			return false;
		}

		if (!action.equals(otherObj.action)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int __result = 0;

		__result += motor_id.hashCode();

		__result += (int) time_sec;

		__result += direction.hashCode();

		__result += (int) speed;

		__result += action.hashCode();

		return __result;
	}

	/**
	 * This is the implementation of the <code>Copyable</code> interface. This
	 * method will perform a deep copy of <code>src</code> This method could be
	 * placed into <code>MotorControlTypeSupport</code> rather than here by
	 * using the <code>-noCopyable</code> option to rtiddsgen.
	 * 
	 * @param src
	 *            The Object which contains the data to be copied.
	 * @return Returns <code>this</code>.
	 * @exception NullPointerException
	 *                If <code>src</code> is null.
	 * @exception ClassCastException
	 *                If <code>src</code> is not the same type as
	 *                <code>this</code>.
	 * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
	 */
	public Object copy_from(Object src) {

		MotorControl typedSrc = (MotorControl) src;
		MotorControl typedDst = this;

		typedDst.motor_id = typedSrc.motor_id;

		typedDst.time_sec = typedSrc.time_sec;

		typedDst.direction = typedSrc.direction;

		typedDst.speed = typedSrc.speed;

		typedDst.action = typedSrc.action;

		return this;
	}

	public String toString() {
		return toString("", 0);
	}

	public String toString(String desc, int indent) {
		StringBuffer strBuffer = new StringBuffer();

		if (desc != null) {
			CdrHelper.printIndent(strBuffer, indent);
			strBuffer.append(desc).append(":\n");
		}

		CdrHelper.printIndent(strBuffer, indent + 1);
		strBuffer.append("motor_id: ").append(motor_id).append("\n");

		CdrHelper.printIndent(strBuffer, indent + 1);
		strBuffer.append("time_sec: ").append(time_sec).append("\n");

		CdrHelper.printIndent(strBuffer, indent + 1);
		strBuffer.append("direction: ").append(direction).append("\n");

		CdrHelper.printIndent(strBuffer, indent + 1);
		strBuffer.append("speed: ").append(speed).append("\n");

		CdrHelper.printIndent(strBuffer, indent + 1);
		strBuffer.append("action: ").append(action).append("\n");

		return strBuffer.toString();
	}

}
