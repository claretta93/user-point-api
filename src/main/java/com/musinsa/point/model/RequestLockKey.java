package com.musinsa.point.model;

public class RequestLockKey {

    private static final String FORMAT = "requestLock.%d.%s.%s";

    public static String getKey(Long requestId, String requestedBy, PointStatus status) {
        return String.format(FORMAT, requestId, requestedBy, status.name());
    }
}
