package com.sensuscorp.device_management.api.controller;

import com.sensuscorp.device_management.api.model.SensorInput;
import com.sensuscorp.device_management.common.IdGenerator;
import com.sensuscorp.device_management.domain.model.Sensor;
import com.sensuscorp.device_management.domain.model.SensorId;
import com.sensuscorp.device_management.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SesorController {

    private final SensorRepository sensorRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sensor create(@RequestBody SensorInput input){
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enable(false)
                .build();

        return sensorRepository.save(sensor);
    }
}
