package com.ticketing.review.infrastructure.configuration;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

  @Bean
  public RedisScript<Boolean> saveAvgRatingScript() throws IOException {
    Resource scriptSource = new ClassPathResource("scripts/saveAvgRating.lua");
    return RedisScript.of(scriptSource, Boolean.class);
  }

}
