package com.hbc.core.api.domain;

import org.springframework.context.ApplicationEvent;

public record Message(boolean isConnected, double temperature, int voltage) {
}
