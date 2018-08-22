package com.rabbit.samples.bootbackoff.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@Configuration("backoffConfig")
public class BackoffConfig {

	static int COUNT = 0;

	@Value("${backoff.test.counting.threshold:10000}")
	int threshold;

	@Bean
	public CommandLineRunner runner() {

		log.debug("Missing cycles to stabilise the application: {}", (getThreshold() - COUNT));

		return args -> {
			if (COUNT++ < getThreshold()) {
				throw new IllegalStateException("Planned");
			}
		};
	}

}
