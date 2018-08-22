package org.springframework.boot.backoff;

import lombok.Getter;


@Getter
public enum BackoffHealthStatusEnum {

	DOWN("DOWN"),
	OUT_OF_SERVICE("OUT_OF_SERVICE"),
	UNKNOWN("UNKNOWN");

	String value;

	BackoffHealthStatusEnum(final String value) {

		this.value = value;
	}

}
