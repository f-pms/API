package com.hbc.pms.core.api.support.error;

import lombok.RequiredArgsConstructor;

public class PlcConnectionException extends RuntimeException {
    public PlcConnectionException(String message) {
        super(message);
    }
}
