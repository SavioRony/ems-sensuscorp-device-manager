package com.sensuscorp.device_management.api.controller;

import com.sensuscorp.device_management.api.model.SensorInput;
import com.sensuscorp.device_management.api.model.SensorOutput;
import com.sensuscorp.device_management.common.IdGenerator;
import com.sensuscorp.device_management.domain.model.Sensor;
import com.sensuscorp.device_management.domain.model.SensorId;
import com.sensuscorp.device_management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SesorController {

    private final SensorRepository sensorRepository;

    @GetMapping("/{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(sensor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input){
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enable(false)
                .build();

        Sensor sensorSaved = sensorRepository.save(sensor);
        return convertToModel(sensorSaved);
    }

    private SensorOutput convertToModel(Sensor sensorSaved) {
        return SensorOutput.builder()
                .id(sensorSaved.getId().getValue())
                .name(sensorSaved.getName())
                .ip(sensorSaved.getIp())
                .location(sensorSaved.getLocation())
                .protocol(sensorSaved.getProtocol())
                .model(sensorSaved.getModel())
                .enable(sensorSaved.getEnable())
                .build();
    }
}
