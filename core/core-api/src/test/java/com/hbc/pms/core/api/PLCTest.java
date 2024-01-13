package com.hbc.pms.core.api;

import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.api.AreaType;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class PLCTest {

  @Container
  private final GenericContainer<?> server = new GenericContainer<>(
        DockerImageName.parse("huybui479/plc_sim:latest_linux"))
      .withExposedPorts(102, 80)
      .withCopyFileToContainer(
          MountableFile.forClasspathResource("datablocks.json"),
          "/data/datablocks.json"
      );


}
