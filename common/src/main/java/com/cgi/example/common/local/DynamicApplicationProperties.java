package com.cgi.example.common.local;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class DynamicApplicationProperties {

    @JsonProperty("applicationPort")
    private Port applicationPort;

    @JsonProperty("managementPort")
    private Port managementPort;

    @JsonProperty("wireMockPort")
    private Port wireMockPort;

    @JsonProperty("mongoDBPort")
    private Port mongoDBPort;

    @JsonProperty("oAuth2Port")
    private Port oAuth2Port;
}
