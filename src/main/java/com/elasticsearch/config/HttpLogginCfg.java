package com.elasticsearch.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.elasticsearch.commons.restlogger.RESTLoggingFilter;

@Configuration
public class HttpLogginCfg {

    @Value("#{'${rest.logging.filter.denyUrls}'.split(',')}")
    private List<String> denyUrlsList;

    @Value("${rest.logging.filter.enabled}")
    private boolean enableRestLogs;

    @Bean
    public FilterRegistrationBean getRestFilter() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RESTLoggingFilter(this.denyUrlsList, this.enableRestLogs));
        registration.addUrlPatterns("/*");
        registration.setName("requestFilter");

        return registration;
    }
}
