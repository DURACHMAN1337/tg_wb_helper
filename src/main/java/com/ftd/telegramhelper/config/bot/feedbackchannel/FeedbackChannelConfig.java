package com.ftd.telegramhelper.config.bot.feedbackchannel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackChannelConfig {

    @Value("${telegram-bot.feedback.channel.id}")
    private String channelId;

    @Value("${telegram-bot.feedback.channel.chat.id}")
    private String channelChatId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelChatId) {
        this.channelId = channelChatId;
    }

    public String getChannelChatId() {
        return channelChatId;
    }

    public void setChannelChatId(String channelChatId) {
        this.channelChatId = channelChatId;
    }
}
