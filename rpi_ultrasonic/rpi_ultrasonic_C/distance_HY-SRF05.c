#include <wiringPi.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <errno.h>
#include <time.h>
#include <signal.h>

#define ROUNDS 10
#define ROUNDS_LIMIT 20
#define TRIGGER_PIN1 28
#define ECHO_PIN1 29

//#define TRIGGER_PIN2 18
//#define ECHO_PIN2 22

static int deadline = 0;
int partition(int *a, int p, int r);
int random_partition(int *a, int p, int r);
int random_select(int *a, int p, int r, int i);
int median(int *a, int l) {
	return random_select(a, 0, l, (l + 1) / 2);
}

int set_alarm(long timeout, void (*handler)(int)) {
	static timer_t *timer = NULL;
	if (!timer) {
		timer = malloc(sizeof(*timer));
		if (timer_create(CLOCK_MONOTONIC, NULL, &timer) == -1) {
			perror("timer_create");
		}
		struct sigaction action = { 0 };
		action.sa_handler = *handler;
		if (sigaction(SIGALRM, &action, NULL) == -1) {
			perror("sigaction");
		}
	}

	struct itimerspec nv = { 0 };
	struct itimerspec ov = { 0 };
	nv.it_value.tv_sec = 0;
	nv.it_value.tv_nsec = 1000L * timeout;
	if (timer_settime(timer, 0, &nv, &ov) == -1) {
		perror("timer_settime");
	}
}

void set_deadline(int unused) {
	deadline = (1 == 1);
}

int waitforpin(int pin, int bit, long timeout) {
	deadline = (1 == 0);
	struct timespec start, now;
	if (clock_gettime(CLOCK_MONOTONIC, &start) == -1) {
		perror("clock_gettime");
	}
	set_alarm(timeout, set_deadline);
	while (digitalRead(pin) != bit) {
		if (clock_gettime(CLOCK_MONOTONIC, &now) == -1) {
			perror("clock_gettime");
		}
		if (deadline == (1 == 1)) {
			return -1;
		}
	}
	return ((long) (now.tv_sec - start.tv_sec)) * 1000000L
			+ ((long) (now.tv_nsec - start.tv_nsec)) / 1000;
}

void fire(int pin) {
	struct timespec req;
	req.tv_sec = 0;
	req.tv_nsec = 1000L;
	digitalWrite(pin, LOW);
	usleep(1000L);
	digitalWrite(pin, HIGH);
	errno = 0;
	if (nanosleep(&req, NULL) == -1) {
		perror("nanosleep");
	}
	digitalWrite(pin, LOW);
}

int readSensor(int trig, int echo) {

	int pulsewidth;
	pinMode(trig, OUTPUT);
	pinMode(echo, INPUT);
	int elapse[ROUNDS];
	int count = 0;
	int i = 0;
	while (1) {
		fire(trig);
		if (waitforpin(echo, HIGH, 5000L) != -1) {
			pulsewidth = waitforpin(echo, LOW, 60000L);
			if (pulsewidth != -1) {
				elapse[count] = pulsewidth;
				++count;
			} else {
#ifdef DEBUG
				printf("echo timed out\n");
#endif
			}
		} else {
#ifdef DEBUG
			printf("sensor didn't fire\n");
#endif
		}
		++i;
		if (i > ROUNDS_LIMIT || (i > ROUNDS && count > ROUNDS / 2)) {
			break;
		}
	}
	if (count > 0) {
		return median(elapse, count);
	}
	return -1;
}

int partition(int *a, int p, int r) {
	int x = a[r], t;
	int i = p - 1, j;
	for (j = p; j < r; j++) {
		if (a[j] <= x) {
			i++;
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}
	t = a[i + 1];
	a[i + 1] = a[r];
	a[r] = t;
	return i + 1;
}

int random_partition(int *a, int p, int r) {
	int i = p + (int) ((r - p + 1) * rand() / (RAND_MAX + 1.0));
	int t = a[i];
	a[i] = a[r];
	a[r] = t;
	return partition(a, p, r);
}

int random_select(int *a, int p, int r, int i) {
	if (p == r) {
		return a[p];
	}
	int q = random_partition(a, p, r);
	int k = q - p + 1;
	if (i == k) {
		return a[q];
	} else if (i < k) {
		return random_select(a, p, q - 1, i);
	} else {
		return random_select(a, q + 1, r, i - k);
	}
}
