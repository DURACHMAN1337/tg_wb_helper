package com.ftd.tg_wb_helper.config;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@Configuration
@AllArgsConstructor
public class SpingConfig {

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource ret = new ReloadableResourceBundleMessageSource();
        ret.setBasenames("classpath:messages");
        ret.setDefaultEncoding("UTF-8");
        ret.setDefaultLocale(new Locale("ru"));
        return ret;
    }
}
