package com.sensuscorp.device_management;

import com.sensuscorp.device_management.common.IdGenerator;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;


class IdGeneratorTest {
    @Test
    void idGeratorUUID(){
        TSID tsid = IdGenerator.generateTSID();

        System.out.println(tsid);
    }
}