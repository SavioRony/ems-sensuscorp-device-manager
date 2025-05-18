package com.sensuscorp.device_management.api.controller;

import com.sensuscorp.device_management.api.client.SensorMonitoringClient;
import com.sensuscorp.device_management.api.model.*;
import com.sensuscorp.device_management.common.IdGenerator;
import com.sensuscorp.device_management.domain.model.Sensor;
import com.sensuscorp.device_management.domain.model.SensorId;
import com.sensuscorp.device_management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorRepository sensorRepository;
    private final SensorMonitoringClient sensorMonitoringClient;

    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable){
       Page<Sensor> sensors = sensorRepository.findAll(pageable);
       return sensors.map(this::convertToModel);
    }

    @GetMapping("/{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(sensor);
    }

    @GetMapping("/{sensorId}/detail")
    public SensorDetailOutput getOneWithDetail(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        SensorMonitoringOutput monitoring = sensorMonitoringClient.getDetail(sensorId);
        return SensorDetailOutput.builder()
                .sensor(convertToModel(sensor))
                .monitoring(monitoring)
                .build();
    }


    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId){
        SensorId sensorId1 = new SensorId(sensorId);
        existsById(sensorId1);
        sensorRepository.deleteById(sensorId1);
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    @DeleteMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.OK)
    public SensorOutput disable(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(false);
        SensorOutput sensorOutput = convertToModel(sensorRepository.save(sensor));
        sensorMonitoringClient.disableMonitoring(sensorId);
        return sensorOutput;
    }

    @PutMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.OK)
    public SensorOutput enabledSensor(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(true);
        SensorOutput sensorOutput = convertToModel(sensorRepository.save(sensor));
        sensorMonitoringClient.enableMonitoring(sensorId);
        return sensorOutput;
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
                .enabled(false)
                .build();

        Sensor sensorSaved = sensorRepository.save(sensor);
        return convertToModel(sensorSaved);
    }

    @PutMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput update(@PathVariable TSID sensorId, @RequestBody SensorInputUpdate input){

        existsById(new SensorId(sensorId));

        Sensor sensorUpdate = Sensor.builder()
                .id(new SensorId(sensorId))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(input.getEnabled())
                .build();

        return convertToModel(sensorRepository.save(sensorUpdate));
    }

    private void existsById(SensorId sensorId) {
        boolean exists = sensorRepository.existsById(sensorId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private SensorOutput convertToModel(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue())
                .name(sensor.getName())
                .ip(sensor.getIp())
                .location(sensor.getLocation())
                .protocol(sensor.getProtocol())
                .model(sensor.getModel())
                .enabled(sensor.getEnabled())
                .build();
    }
}
