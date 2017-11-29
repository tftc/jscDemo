package net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.concurrent.TimeUnit.MINUTES;

@Data
@Validated
@NoArgsConstructor
@ConfigurationProperties(RateLimitProperties.PREFIX)
public class RateLimitProperties {

  public static final String PREFIX = "ratelimit";

  private Policy defaultPolicy;

  @NotNull
  private Map<String, Policy> policies = Maps.newHashMap();

  private boolean behindProxy;

  private boolean enabled;

  public enum Server {
    ZUUL, GRPC
  }

  @NotNull
  private Server server = Server.GRPC;

  @NotNull
  @Value("${spring.application.name:rate-limit-application}")
  private String keyPrefix;

  @NotNull
  private Repository repository = Repository.IN_MEMORY;

  public enum Repository {
    REDIS, CONSUL, JPA, IN_MEMORY
  }

  public Optional<Policy> getPolicy(String key) {
    return Optional.ofNullable(policies.getOrDefault(key, defaultPolicy));
  }

  @Data
  @NoArgsConstructor
  public static class Policy {

    @NotNull
    private Long refreshInterval = MINUTES.toSeconds(1L);

    private Long limit;

    private Long quota;

    @NotNull
    private List<Type> type = Lists.newArrayList();

    public enum Type {
      ORIGIN, USER, URL
    }
  }
}