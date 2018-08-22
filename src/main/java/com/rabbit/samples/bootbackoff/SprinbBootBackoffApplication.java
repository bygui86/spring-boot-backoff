package com.rabbit.samples.bootbackoff;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.backoff.BackoffSpringApplication;


/**
 * A convenience class for launching a Spring Boot app with retry and backoff if it fails.
 * While the main app is failing the backoff brings up a web endpoint on /actuator/info and /actuator/health.
 * The health endpoint will show a default status of "OUT_OF_SERVICE" until the main application context comes up successfully.
 * <p>
 * The status is configurable through the application property "backoff.health.status" with following values:
 * . DOWN
 * . OUT_OF_SERVICE
 * . UNKNOWN
 */
@SpringBootApplication(scanBasePackages = {"com.rabbit.samples.bootbackoff"})
// PLEASE NOTE: There is a difference between using @SpringBootApplication and following others
// . @SpringBootConfiguration
// . @EnableAutoConfiguration
// . @ComponentScan(basePackages = {"com.rabbit.samples.bootbackoff"})
public class SprinbBootBackoffApplication {

	public static void main(final String[] args) {

		// Classic SpringApplication runner
		// SpringApplication.run(SpringBootBackoffApplication.class, args);

		// Just use BackoffSpringApplication to run the main method.
		BackoffSpringApplication.run(SprinbBootBackoffApplication.class, args);
	}

}
