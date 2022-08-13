package com.ftd.telegramhelper.feedback.feedbackchannel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ChannelChat")
public class FeedbackChannel {

    @Id
    @Column(name = "CHANNEL_CHAT_ID", nullable = false, unique = true)
    private String channelChatId;

    public String getChannelChatId() {
        return channelChatId;
    }

    public void setChannelChatId(String channelChatId) {
        this.channelChatId = channelChatId;
    }
}
