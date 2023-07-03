package com.musinsa.point.model;

public class RequestLockKey {

    private static final String format = "requestLock.%d.%s.%s";

    public static String getKey(Long requestId, String requestedBy, PointStatus status) {
        return String.format(format, requestId, requestedBy, status.name());
    }
}
