package com.cgi.example.common.local;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
record Port(@Getter Integer port, String modifiedBy, Instant modifiedAt) {

}

