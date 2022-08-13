package com.ftd.telegramhelper.config.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Configuration
public class SpringConfig {

    private final RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public SpringConfig(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource ret = new ReloadableResourceBundleMessageSource();
        ret.setBasenames("classpath:messages");
        ret.setDefaultEncoding("UTF-8");
        ret.setDefaultLocale(new Locale("ru"));
        return ret;
    }

    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }
}
