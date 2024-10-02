package com.ticketing.review.common.config;

import com.ticketing.review.common.filter.GlobalAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<GlobalAuthFilter> globalAuthFilter() {
    FilterRegistrationBean<GlobalAuthFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new GlobalAuthFilter());
    bean.addUrlPatterns("/api/*");
    return bean;
  }
}
