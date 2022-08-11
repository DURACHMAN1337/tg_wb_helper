package com.ftd.tg_wb_helper.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TelegramUser extends BaseUuidEntity implements Comparable<TelegramUser> {

    @Column(name = "telegram_id")
    Long telegramId;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "username")
    String username;

    @Column(name = "chat_id")
    Long chatId;


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
