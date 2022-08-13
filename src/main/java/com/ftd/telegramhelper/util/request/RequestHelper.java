package com.ftd.telegramhelper.util.request;

import com.ftd.telegramhelper.config.bot.longpolling.LongPollingTelegramBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class RequestHelper {

    private final ApplicationContext applicationContext;
    private final RestTemplate restTemplate;

    private static final String GET_UPDATES_URL = "https://api.telegram.org/bot%s/getUpdates?chat_id=%s";

    @Autowired
    public RequestHelper(
            ApplicationContext applicationContext,
            RestTemplate restTemplate
    ) {
        this.applicationContext = applicationContext;
        this.restTemplate = restTemplate;
    }

    public List<Update> getUpdates(String chatId) {
        String url = String.format(GET_UPDATES_URL, getBotToken(), chatId);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Update>>() {
                }
        ).getBody();
    }

    @NotNull
    private String getBotToken() {
        return applicationContext.getBean(LongPollingTelegramBotConfig.class).getBotToken();
    }
}
