package com.sensuscorp.device_management.api.model;


import io.hypersistence.tsid.TSID;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class SensorMonitoringOutput {

    private TSID id;
    private Double lastTemperature;
    private OffsetDateTime updateAt;
    private Boolean enabled;
}
