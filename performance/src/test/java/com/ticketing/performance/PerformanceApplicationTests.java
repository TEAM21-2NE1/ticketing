package com.ticketing.performance;

import com.ticketing.performance.application.scheduler.SeatScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PerformanceApplicationTests {

	@Autowired
	SeatScheduler scheduler;

	@Test
	void contextLoads() {
		scheduler.run();
	}

}
