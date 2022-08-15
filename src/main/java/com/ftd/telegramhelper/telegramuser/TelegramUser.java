package com.ftd.telegramhelper.telegramuser;

import com.ftd.telegramhelper.util.state.UserStates;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class TelegramUser implements Comparable<TelegramUser> {
    @Id
    private UUID id;

    @Column(name = "TELEGRAM_ID")
    private Long telegramId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "CHAT_ID")
    private Long chatId;

    @Column(name = "feedback_message_id")
    private String feedbackMessageId;

    @Column(name = "state")
    private UserStates state;

    public String getFeedbackMessageId() {
        return feedbackMessageId;
    }

    public void setFeedbackMessageId(String feedbackMessageId) {
        this.feedbackMessageId = feedbackMessageId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStates getState() {
        return state;
    }

    public void setState(UserStates state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "TelegramUser{" +
                "telegramId=" + telegramId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int compareTo(TelegramUser telegramUser) {
        return telegramUser.getTelegramId().compareTo(this.getTelegramId());
    }
}
