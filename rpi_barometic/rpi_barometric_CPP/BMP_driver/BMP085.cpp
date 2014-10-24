#include "BMP085.hpp"
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <cstring>
#include <cstdio>
#include <cerrno>
#include <cmath>

using namespace std;

BMP085::BMP085(BMP085::OversamplingSetting o, string dev, int addr) :
		ok(true), err(""), oss(o), hiRes(true), testRef(false) {
	fd = open(dev.c_str(), O_RDWR);
	if (fd < 0) {
		ok = false;
		err = "open() fail: ";
		err += strerror(errno);
		return;
	}
	if (ioctl(fd, I2C_SLAVE, addr) < 0) {
		ok = false;
		err = "ioctl() fail: ";
		err += strerror(errno);
		return;
	}
	ac1 = readWord(0xaa);
	ac2 = readWord(0xac);
	ac3 = readWord(0xae);
	ac4 = (uint16_t) readWord(0xb0);
	ac5 = (uint16_t) readWord(0xb2);
	ac6 = (uint16_t) readWord(0xb4);
	b1 = readWord(0xb6);
	b2 = readWord(0xb8);
	mb = readWord(0xba);
	mc = readWord(0xbc);
	md = readWord(0xbe);
}

BMP085::~BMP085() {
	close(fd);
}

/* API */

double BMP085::getCelcius() {
	return getCelcius(getB5Value());
}

// Algorithm from sec. 3.5 of the datasheet.
BMP085::reading BMP085::getBoth() {
// Get uncompensated pressure.
	int ucPress;
	if (testRef)
		ucPress = 23843;
	else {
		if (i2c_smbus_write_byte_data(fd, CONTROL_REG, (oss << 6) + 0x34) != 0)
			throw BMP085::smbusIOException(
					"getBoth() write to control register", errno);
		unsigned int duration = oss * 5 + 5;
		if (oss == 3)
			duration += 5;
		millisleep(duration);
		int msb = i2c_smbus_read_byte_data(fd, DATA_REG);
		int lsb = i2c_smbus_read_byte_data(fd, DATA_REG + 1);
		int xlsb = 0;
		if (hiRes)
			xlsb = i2c_smbus_read_byte_data(fd, DATA_REG + 2);
		if (msb == -1 || lsb == -1 || xlsb == -1)
			throw BMP085::smbusIOException("getBoth() read data register",
					errno);
		ucPress = ((msb << 16) + (lsb << 8) + xlsb) >> (8 - oss);
	}
// Calculate true pressure.
	long b5 = getB5Value();
	long b6 = b5 - 4000;
	long x1 = (b2 * (b6 * b6 >> 12)) >> 11;
	long x2 = ac2 * b6 >> 11;
	long x3 = x1 + x2;
	long b3 = (((ac1 * 4 + x3) << oss) + 2) >> 2;
	x1 = ac3 * b6 >> 13;
	x2 = (b1 * (b6 * b6 >> 12)) >> 16;
	x3 = ((x1 + x2) + 2) >> 2;
	unsigned long b4 = ac4 * (unsigned long) (x3 + 32768) >> 15;
	unsigned long b7 = ((unsigned long) ucPress - b3) * (50000 >> oss);
	long p;
	if (b7 < 0x80000000)
		p = (b7 * 2) / b4;
	else
		p = (b7 / b4) * 2;
	long p8 = p >> 8;
	x1 = p8 * p8;
	x1 *= 3038;
	x1 >>= 16;
	x2 = (-7357 * p) >> 16;
	p += (x1 + x2 + 3791) >> 4;
// Return struct with temperature.
	BMP085::reading data = { getCelcius(b5), (double) p / 1000.0f };
	return data;
}

double BMP085::getRelativeAltitude(double pressure) {
// This formula is from sec. 3.6 of the data sheet.
	double x = pow(((double) pressure / 101.325), ALT_EXP);
	return (double) (44330.0 * (1.0 - x));
}

void BMP085::millisleep(unsigned int thousandths) {
	struct timespec duration;
	duration.tv_sec = thousandths / 1000;
	duration.tv_nsec = (thousandths % 1000) * 10000000;
	nanosleep((const struct timespec*) &duration, NULL);
}

/* Internal */

double BMP085::getCelcius(long b5) {
	long t = (b5 + 8) >> 4;
	return (double) t / 10.0f;
}

double BMP085::getB5Value() {
	if (testRef)
		return 2399;
	if (i2c_smbus_write_byte_data(fd, CONTROL_REG, TEMP_COMMAND) != 0)
		throw BMP085::smbusIOException("getB5Value() write to control register",
				errno);
	millisleep(5);
	long x1 = (readWord(DATA_REG) - ac6) * ac5 >> 15;
	long x2 = (mc << 11) / (x1 + md);
	return x1 + x2;
}

int16_t BMP085::readWord(int addr) {
	int word = i2c_smbus_read_word_data(fd, addr);
	if (word == -1) {
		char str[32];
		sprintf(str, "readWord() 0x%x", addr);
		throw BMP085::smbusIOException(str, errno);
	}
// The sensor is big-endian!
	return (int16_t)((word >> 8) + ((word & 0xff) << 8));
}

void BMP085::useReferenceValues() {
	oss = OSS_LOW;
	ac1 = 408;
	ac2 = -72;
	ac3 = -14383;
	ac4 = 32741;
	ac5 = 32757;
	ac6 = 23153;
	b1 = 6190;
	b2 = 4;
	mb = -32768;
	mc = -8711;
	md = 2868;
	testRef = true;
}

/* Class smbusIOException */

BMP085::smbusIOException::smbusIOException(const char* m, int err) :
		err(err), msg(m) {
}

const char* BMP085::smbusIOException::what() throw () {
	string m = msg;
	if (err) {
		m += ":\n";
		m += strerror(errno);
	}
	return m.c_str();
}
