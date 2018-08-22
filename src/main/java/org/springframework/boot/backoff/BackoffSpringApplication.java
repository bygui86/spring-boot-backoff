/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.backoff;

import com.rabbit.samples.bootbackoff.SprinbBootBackoffApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.core.ReactorCoreAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;


/**
 * @author Dave Syer
 */
@SpringBootConfiguration
@Import({
		// LazyInitBeanFactoryPostProcessor.class,
		PropertyPlaceholderAutoConfiguration.class,
		ReactiveWebServerFactoryAutoConfiguration.class,
		CodecsAutoConfiguration.class,
		ErrorWebFluxAutoConfiguration.class,
		WebFluxAutoConfiguration.class,
		HttpHandlerAutoConfiguration.class,
		ConfigurationPropertiesAutoConfiguration.class,
		GsonAutoConfiguration.class,
		HttpMessageConvertersAutoConfiguration.class,
		ProjectInfoAutoConfiguration.class,
		ReactorCoreAutoConfiguration.class,
		ReactiveSecurityAutoConfiguration.class,
		RestTemplateAutoConfiguration.class,
		EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
		WebClientAutoConfiguration.class,
		EndpointAutoConfiguration.class,
		WebEndpointAutoConfiguration.class,
		HealthIndicatorAutoConfiguration.class,
		HealthEndpointAutoConfiguration.class,
		InfoEndpointAutoConfiguration.class,
		InfoContributorAutoConfiguration.class,
		DiskSpaceHealthIndicatorAutoConfiguration.class,
		ReactiveManagementContextAutoConfiguration.class,
		ManagementContextAutoConfiguration.class
})
public class BackoffSpringApplication {

	@Value("${backoff.health.status:OUT_OF_SERVICE}")
	private BackoffHealthStatusEnum healthStatus;

	private ConfigurableApplicationContext backoff;

	private Class<?> main;

	public BackoffSpringApplication() {

		// no-op
	}

	public BackoffSpringApplication(final Class<?> main) {

		this.main = main;
	}

	@Bean
	public HealthIndicator down() {

		switch (healthStatus) {
			case DOWN:
				return () -> Health.down().build();
			case UNKNOWN:
				return () -> Health.unknown().build();
			case OUT_OF_SERVICE:
				return () -> Health.outOfService().build();
			default:
				return () -> Health.outOfService().build();
		}
	}

	public static void main(final String[] args) throws Exception {

		SpringApplication.run(BackoffSpringApplication.class, args);
	}

	public static void run(final Class<SprinbBootBackoffApplication> main, final String... args) {

		new BackoffSpringApplication(main).run(args);
	}

	private void close() {

		if (backoff != null) {
			backoff.close();
			backoff = null;
		}
	}

	private void run(final String... args) {

		ConfigurableApplicationContext context = null;
		while (context == null) {
			try {
				context = new SpringApplicationBuilder(main).listeners(new CloseListener()).run(args);
			} catch (final Exception e) {
				context = null;
				// back off etc.
				backoff = SpringApplication.run(BackoffSpringApplication.class, args);
			}
		}

	}

	class CloseListener implements SmartApplicationListener {

		@Override
		public void onApplicationEvent(final ApplicationEvent event) {

			close();
		}

		@Override
		public int getOrder() {

			return 0;
		}

		@Override
		public boolean supportsEventType(final Class<? extends ApplicationEvent> eventType) {

			return ContextRefreshedEvent.class.isAssignableFrom(eventType);
		}

		@Override
		public boolean supportsSourceType(final Class<?> sourceType) {

			return true;
		}

	}

}