package com.cgi.example.petstore.config;

import com.cgi.example.petstore.logging.mdc.AddUniqueRequestIdToMappedDiagnosticContextAndResponse;
import com.cgi.example.petstore.logging.mdc.AddUsernameToMappedDiagnosticContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  public FilterRegistrationBean<AddUniqueRequestIdToMappedDiagnosticContextAndResponse>
      addRequestIdToLoggingFilter(
          @Autowired AddUniqueRequestIdToMappedDiagnosticContextAndResponse filter) {

    FilterRegistrationBean<AddUniqueRequestIdToMappedDiagnosticContextAndResponse>
        registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(filter);
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return registrationBean;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new AddUsernameToMappedDiagnosticContext());
  }
}
