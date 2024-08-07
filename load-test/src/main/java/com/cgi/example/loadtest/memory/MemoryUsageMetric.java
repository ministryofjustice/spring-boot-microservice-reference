package com.cgi.example.loadtest.memory;

import lombok.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Value
public class MemoryUsageMetric {

    LocalDateTime collectionTime;
    BigInteger memoryUsedInBytes;

    public MemoryUsageMetric(String memoryUsedInBytes) {
        this.collectionTime = LocalDateTime.now();
        this.memoryUsedInBytes = new BigDecimal(memoryUsedInBytes).toBigInteger();
    }

    @Override
    public String toString() {
        return "MemoryUsageMetric{" +
                "collectionTime=" + collectionTime +
                ", memoryUsedInBytes=" + memoryUsedInBytes +
                '}';
    }
}
