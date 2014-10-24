/* Simple, standalone class representing a Bosch Sensortec
 * BMP085 Digital Pressure sensor. */

#ifndef CLASS_BMP085
#define CLASS_BMP085

#include <string>
#include <exception>
#include <inttypes.h>

class BMP085 {
public:
	// Compound type for returning data, see getBoth().
	typedef struct {
		double celcius;
		double kPa;
	} reading;
	// See sec. 3.3 of the datasheet for the significance 
	// of the oversampling setting.
	enum OversamplingSetting {
		OSS_LOW = 0, OSS_STANDARD = 1, OSS_HIGH = 2, OSS_ULTRAHIGH = 3
	};
	// If 'ok' is false, check 'err'.  This should be
	// done after the object is constructed.
	bool ok;
	std::string err;
	// You can change 'oss' and 'hiRes' any time; the latter is on by default
	// and means we use 19 bits of resolution for the pressure reading instead
	// of 16 (see, eg. datasheet sec. 4.5). 
	OversamplingSetting oss;
	bool hiRes;
	/* Constructor args (all optional): 
	 1) Oversampling setting.
	 2) I2C device node.
	 3) I2C address of the BMP085.
	 Raspberry Pi Model B rev. 1 (256 MB RAM) boards will need to specify
	 "/dev/i2c-0" as the device node.  The default I2C address (0x77) is
	 used by Adafruit's breakout board for the BMP085. */
	BMP085(OversamplingSetting = OSS_STANDARD, std::string = "/dev/i2c-1", int =
			0x77);
	// getCelcius() returns the temperature in °C.  The sensor has
	// a resolution of 1/10th a degree.
	double getCelcius();
	// Since it is necessary to read the temperatature in order to
	// calculate the pressure, getBoth() does both.
	reading getBoth();
	~BMP085();
	// Given a pressure in kPa, such as returned from getBoth(),
	// getRelativeAltitude() returns an altitude based on a mean
	// sea level pressure of 101.325 kPa.
	static double getRelativeAltitude(double kPa);
	// The arg to millisleep() is a number of milliseconds.
	static void millisleep(unsigned int);
	// These are thrown if an i2c_smbus read or write call fails.
	// (I haven't seen this happen).
	class smbusIOException: public std::exception {
	public:
		const char* what() throw ();
		const int err;
		smbusIOException(const char*, int);
	private:
		const char *msg;
	};
protected:
	static const int CONTROL_REG = 0xf4;
	static const int DATA_REG = 0xf6;
	static const int TEMP_COMMAND = 0x2e;
	static const double ALT_EXP = 1.0 / 5.255;
	int fd;
	int16_t ac1;
	int16_t ac2;
	int16_t ac3;
	uint16_t ac4;
	uint16_t ac5;
	uint16_t ac6;
	int16_t b1;
	int16_t b2;
	int16_t mb;
	int16_t mc;
	int16_t md;
	bool testRef;
	double getCelcius(long);
	double getB5Value();
	int16_t readWord(int addr);
	// Used in testing, see pg. 13 of the datasheet.
	void useReferenceValues();
private:
	/* This could be a sort of singleton, or static factory based
	 on dev node + I2C address.  Instead, just forbid copies, etc.
	 by leaving these private and undefined. */
	BMP085(const BMP085&);
	BMP085& operator=(const BMP085&);
};

#endif
