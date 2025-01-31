/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.config;

import static org.junit.jupiter.api.Assertions.*;

import io.airbyte.commons.version.AirbyteVersion;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EnvConfigsTest {

  private Map<String, String> envMap;
  private EnvConfigs config;

  @BeforeEach
  void setUp() {
    envMap = new HashMap<>();
    config = new EnvConfigs(envMap);
  }

  @Test
  void ensureGetEnvBehavior() {
    assertNull(System.getenv("MY_RANDOM_VAR_1234"));
  }

  @Test
  void testAirbyteRole() {
    envMap.put(EnvConfigs.AIRBYTE_ROLE, null);
    assertNull(config.getAirbyteRole());

    envMap.put(EnvConfigs.AIRBYTE_ROLE, "dev");
    assertEquals("dev", config.getAirbyteRole());
  }

  @Test
  void testAirbyteVersion() {
    envMap.put(EnvConfigs.AIRBYTE_VERSION, null);
    assertThrows(IllegalArgumentException.class, () -> config.getAirbyteVersion());

    envMap.put(EnvConfigs.AIRBYTE_VERSION, "dev");
    assertEquals(new AirbyteVersion("dev"), config.getAirbyteVersion());
  }

  @Test
  void testWorkspaceRoot() {
    envMap.put(EnvConfigs.WORKSPACE_ROOT, null);
    assertThrows(IllegalArgumentException.class, () -> config.getWorkspaceRoot());

    envMap.put(EnvConfigs.WORKSPACE_ROOT, "abc/def");
    assertEquals(Paths.get("abc/def"), config.getWorkspaceRoot());
  }

  @Test
  void testLocalRoot() {
    envMap.put(EnvConfigs.LOCAL_ROOT, null);
    assertThrows(IllegalArgumentException.class, () -> config.getLocalRoot());

    envMap.put(EnvConfigs.LOCAL_ROOT, "abc/def");
    assertEquals(Paths.get("abc/def"), config.getLocalRoot());
  }

  @Test
  void testConfigRoot() {
    envMap.put(EnvConfigs.CONFIG_ROOT, null);
    assertThrows(IllegalArgumentException.class, () -> config.getConfigRoot());

    envMap.put(EnvConfigs.CONFIG_ROOT, "a/b");
    assertEquals(Paths.get("a/b"), config.getConfigRoot());
  }

  @Test
  void testGetDatabaseUser() {
    envMap.put(EnvConfigs.DATABASE_USER, null);
    assertThrows(IllegalArgumentException.class, () -> config.getDatabaseUser());

    envMap.put(EnvConfigs.DATABASE_USER, "user");
    assertEquals("user", config.getDatabaseUser());
  }

  @Test
  void testGetDatabasePassword() {
    envMap.put(EnvConfigs.DATABASE_PASSWORD, null);
    assertThrows(IllegalArgumentException.class, () -> config.getDatabasePassword());

    envMap.put(EnvConfigs.DATABASE_PASSWORD, "password");
    assertEquals("password", config.getDatabasePassword());
  }

  @Test
  void testGetDatabaseUrl() {
    envMap.put(EnvConfigs.DATABASE_URL, null);
    assertThrows(IllegalArgumentException.class, () -> config.getDatabaseUrl());

    envMap.put(EnvConfigs.DATABASE_URL, "url");
    assertEquals("url", config.getDatabaseUrl());
  }

  @Test
  void testGetWorkspaceDockerMount() {
    envMap.put(EnvConfigs.WORKSPACE_DOCKER_MOUNT, null);
    envMap.put(EnvConfigs.WORKSPACE_ROOT, "abc/def");
    assertEquals("abc/def", config.getWorkspaceDockerMount());

    envMap.put(EnvConfigs.WORKSPACE_DOCKER_MOUNT, "root");
    envMap.put(EnvConfigs.WORKSPACE_ROOT, "abc/def");
    assertEquals("root", config.getWorkspaceDockerMount());

    envMap.put(EnvConfigs.WORKSPACE_DOCKER_MOUNT, null);
    envMap.put(EnvConfigs.WORKSPACE_ROOT, null);
    assertThrows(IllegalArgumentException.class, () -> config.getWorkspaceDockerMount());
  }

  @Test
  void testGetLocalDockerMount() {
    envMap.put(EnvConfigs.LOCAL_DOCKER_MOUNT, null);
    envMap.put(EnvConfigs.LOCAL_ROOT, "abc/def");
    assertEquals("abc/def", config.getLocalDockerMount());

    envMap.put(EnvConfigs.LOCAL_DOCKER_MOUNT, "root");
    envMap.put(EnvConfigs.LOCAL_ROOT, "abc/def");
    assertEquals("root", config.getLocalDockerMount());

    envMap.put(EnvConfigs.LOCAL_DOCKER_MOUNT, null);
    envMap.put(EnvConfigs.LOCAL_ROOT, null);
    assertThrows(IllegalArgumentException.class, () -> config.getLocalDockerMount());
  }

  @Test
  void testDockerNetwork() {
    envMap.put(EnvConfigs.DOCKER_NETWORK, null);
    assertEquals("host", config.getDockerNetwork());

    envMap.put(EnvConfigs.DOCKER_NETWORK, "abc");
    assertEquals("abc", config.getDockerNetwork());
  }

  @Test
  void testTrackingStrategy() {
    envMap.put(EnvConfigs.TRACKING_STRATEGY, null);
    assertEquals(Configs.TrackingStrategy.LOGGING, config.getTrackingStrategy());

    envMap.put(EnvConfigs.TRACKING_STRATEGY, "abc");
    assertEquals(Configs.TrackingStrategy.LOGGING, config.getTrackingStrategy());

    envMap.put(EnvConfigs.TRACKING_STRATEGY, "logging");
    assertEquals(Configs.TrackingStrategy.LOGGING, config.getTrackingStrategy());

    envMap.put(EnvConfigs.TRACKING_STRATEGY, "segment");
    assertEquals(Configs.TrackingStrategy.SEGMENT, config.getTrackingStrategy());

    envMap.put(EnvConfigs.TRACKING_STRATEGY, "LOGGING");
    assertEquals(Configs.TrackingStrategy.LOGGING, config.getTrackingStrategy());
  }

  @Test
  void testworkerKubeTolerations() {
    envMap.put(EnvConfigs.JOB_KUBE_TOLERATIONS, null);
    assertEquals(config.getJobKubeTolerations(), List.of());

    envMap.put(EnvConfigs.JOB_KUBE_TOLERATIONS, ";;;");
    assertEquals(config.getJobKubeTolerations(), List.of());

    envMap.put(EnvConfigs.JOB_KUBE_TOLERATIONS, "key=k,value=v;");
    assertEquals(config.getJobKubeTolerations(), List.of());

    envMap.put(EnvConfigs.JOB_KUBE_TOLERATIONS, "key=airbyte-server,operator=Exists,effect=NoSchedule");
    assertEquals(config.getJobKubeTolerations(), List.of(new TolerationPOJO("airbyte-server", "NoSchedule", null, "Exists")));

    envMap.put(EnvConfigs.JOB_KUBE_TOLERATIONS, "key=airbyte-server,operator=Equals,value=true,effect=NoSchedule");
    assertEquals(config.getJobKubeTolerations(), List.of(new TolerationPOJO("airbyte-server", "NoSchedule", "true", "Equals")));

    envMap.put(EnvConfigs.JOB_KUBE_TOLERATIONS,
        "key=airbyte-server,operator=Exists,effect=NoSchedule;key=airbyte-server,operator=Equals,value=true,effect=NoSchedule");
    assertEquals(config.getJobKubeTolerations(), List.of(
        new TolerationPOJO("airbyte-server", "NoSchedule", null, "Exists"),
        new TolerationPOJO("airbyte-server", "NoSchedule", "true", "Equals")));
  }

  @Test
  void testJobKubeNodeSelectors() {
    envMap.put(EnvConfigs.JOB_KUBE_NODE_SELECTORS, null);
    assertFalse(config.getJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.JOB_KUBE_NODE_SELECTORS, ",,,");
    assertFalse(config.getJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.JOB_KUBE_NODE_SELECTORS, "key=k,,;$%&^#");
    assertEquals(config.getJobKubeNodeSelectors().get(), Map.of("key", "k"));

    envMap.put(EnvConfigs.JOB_KUBE_NODE_SELECTORS, "one=two");
    assertEquals(config.getJobKubeNodeSelectors().get(), Map.of("one", "two"));

    envMap.put(EnvConfigs.JOB_KUBE_NODE_SELECTORS, "airbyte=server,something=nothing");
    assertEquals(config.getJobKubeNodeSelectors().get(), Map.of("airbyte", "server", "something", "nothing"));
  }

  @Test
  void testSpecKubeNodeSelectors() {
    envMap.put(EnvConfigs.SPEC_JOB_KUBE_NODE_SELECTORS, null);
    assertFalse(config.getSpecJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.SPEC_JOB_KUBE_NODE_SELECTORS, ",,,");
    assertFalse(config.getSpecJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.SPEC_JOB_KUBE_NODE_SELECTORS, "key=k,,;$%&^#");
    assertEquals(config.getSpecJobKubeNodeSelectors().get(), Map.of("key", "k"));

    envMap.put(EnvConfigs.SPEC_JOB_KUBE_NODE_SELECTORS, "one=two");
    assertEquals(config.getSpecJobKubeNodeSelectors().get(), Map.of("one", "two"));

    envMap.put(EnvConfigs.SPEC_JOB_KUBE_NODE_SELECTORS, "airbyte=server,something=nothing");
    assertEquals(config.getSpecJobKubeNodeSelectors().get(), Map.of("airbyte", "server", "something", "nothing"));
  }

  @Test
  void testCheckKubeNodeSelectors() {
    envMap.put(EnvConfigs.CHECK_JOB_KUBE_NODE_SELECTORS, null);
    assertFalse(config.getCheckJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.CHECK_JOB_KUBE_NODE_SELECTORS, ",,,");
    assertFalse(config.getCheckJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.CHECK_JOB_KUBE_NODE_SELECTORS, "key=k,,;$%&^#");
    assertEquals(config.getCheckJobKubeNodeSelectors().get(), Map.of("key", "k"));

    envMap.put(EnvConfigs.CHECK_JOB_KUBE_NODE_SELECTORS, "one=two");
    assertEquals(config.getCheckJobKubeNodeSelectors().get(), Map.of("one", "two"));

    envMap.put(EnvConfigs.CHECK_JOB_KUBE_NODE_SELECTORS, "airbyte=server,something=nothing");
    assertEquals(config.getCheckJobKubeNodeSelectors().get(), Map.of("airbyte", "server", "something", "nothing"));
  }

  @Test
  void testDiscoverKubeNodeSelectors() {
    envMap.put(EnvConfigs.DISCOVER_JOB_KUBE_NODE_SELECTORS, null);
    assertFalse(config.getDiscoverJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.DISCOVER_JOB_KUBE_NODE_SELECTORS, ",,,");
    assertFalse(config.getDiscoverJobKubeNodeSelectors().isPresent());

    envMap.put(EnvConfigs.DISCOVER_JOB_KUBE_NODE_SELECTORS, "key=k,,;$%&^#");
    assertEquals(config.getDiscoverJobKubeNodeSelectors().get(), Map.of("key", "k"));

    envMap.put(EnvConfigs.DISCOVER_JOB_KUBE_NODE_SELECTORS, "one=two");
    assertEquals(config.getDiscoverJobKubeNodeSelectors().get(), Map.of("one", "two"));

    envMap.put(EnvConfigs.DISCOVER_JOB_KUBE_NODE_SELECTORS, "airbyte=server,something=nothing");
    assertEquals(config.getDiscoverJobKubeNodeSelectors().get(), Map.of("airbyte", "server", "something", "nothing"));
  }

  @Nested
  @DisplayName("CheckJobResourceSettings")
  public class CheckJobResourceSettings {

    @Test
    @DisplayName("should default to JobMainCpuRequest if not set")
    void testCpuRequestDefaultToJobMainCpuRequest() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_CPU_REQUEST, null);
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_CPU_REQUEST, "1");
      assertEquals("1", config.getCheckJobMainContainerCpuRequest());
    }

    @Test
    @DisplayName("checkJobCpuRequest should take precedent if set")
    void testCheckJobCpuRequestTakePrecedentIfSet() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_CPU_REQUEST, "1");
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_CPU_REQUEST, "2");
      assertEquals("1", config.getCheckJobMainContainerCpuRequest());
    }

    @Test
    @DisplayName("should default to JobMainCpuLimit if not set")
    void testCpuLimitDefaultToJobMainCpuLimit() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_CPU_LIMIT, null);
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_CPU_LIMIT, "1");
      assertEquals("1", config.getCheckJobMainContainerCpuLimit());
    }

    @Test
    @DisplayName("checkJobCpuLimit should take precedent if set")
    void testCheckJobCpuLimitTakePrecedentIfSet() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_CPU_LIMIT, "1");
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_CPU_LIMIT, "2");
      assertEquals("1", config.getCheckJobMainContainerCpuLimit());
    }

    @Test
    @DisplayName("should default to JobMainMemoryRequest if not set")
    void testMemoryRequestDefaultToJobMainMemoryRequest() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_MEMORY_REQUEST, null);
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_MEMORY_REQUEST, "1");
      assertEquals("1", config.getCheckJobMainContainerMemoryRequest());
    }

    @Test
    @DisplayName("checkJobMemoryRequest should take precedent if set")
    void testCheckJobMemoryRequestTakePrecedentIfSet() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_MEMORY_REQUEST, "1");
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_MEMORY_REQUEST, "2");
      assertEquals("1", config.getCheckJobMainContainerMemoryRequest());
    }

    @Test
    @DisplayName("should default to JobMainMemoryLimit if not set")
    void testMemoryLimitDefaultToJobMainMemoryLimit() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_MEMORY_LIMIT, null);
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_MEMORY_LIMIT, "1");
      assertEquals("1", config.getCheckJobMainContainerMemoryLimit());
    }

    @Test
    @DisplayName("checkJobMemoryLimit should take precedent if set")
    void testCheckJobMemoryLimitTakePrecedentIfSet() {
      envMap.put(EnvConfigs.CHECK_JOB_MAIN_CONTAINER_MEMORY_LIMIT, "1");
      envMap.put(EnvConfigs.JOB_MAIN_CONTAINER_MEMORY_LIMIT, "2");
      assertEquals("1", config.getCheckJobMainContainerMemoryLimit());
    }

  }

  @Test
  void testEmptyEnvMapRetrieval() {
    assertEquals(Map.of(), config.getJobDefaultEnvMap());
  }

  @Test
  void testEnvMapRetrieval() {
    envMap.put(EnvConfigs.JOB_DEFAULT_ENV_PREFIX + "ENV1", "VAL1");
    envMap.put(EnvConfigs.JOB_DEFAULT_ENV_PREFIX + "ENV2", "VAL\"2WithQuotesand$ymbols");

    final var expected = Map.of("ENV1", "VAL1", "ENV2", "VAL\"2WithQuotesand$ymbols");
    assertEquals(expected, config.getJobDefaultEnvMap());
  }

}
