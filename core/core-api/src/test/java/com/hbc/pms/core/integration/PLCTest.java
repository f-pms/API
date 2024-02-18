package com.hbc.pms.core.integration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class PLCTest {

  @Container
  private final GenericContainer<?> server =
      new GenericContainer<>(DockerImageName.parse("huybui479/plc_sim:latest_linux"))
          .withExposedPorts(102, 80)
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("datablocks.json"), "/data/datablocks.json");
}
