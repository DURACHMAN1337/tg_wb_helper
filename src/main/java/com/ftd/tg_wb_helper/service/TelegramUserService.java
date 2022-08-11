package com.ftd.tg_wb_helper.service;

import com.ftd.tg_wb_helper.model.entity.TelegramUser;
import com.ftd.tg_wb_helper.model.repo.TelegramUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramUserService {


    @Autowired
    private TelegramUserRepo telegramUserRepo;

    public TelegramUserService() {
    }

    public void delete(TelegramUser telegramUser) {
        telegramUserRepo.delete(telegramUser);
    }

    public TelegramUser findBy(Long telegramUserId) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setTelegramId(telegramUserId);
        Optional<TelegramUser> one = telegramUserRepo.findOne(Example.of(telegramUser));
        if (one.isPresent()){
            return one.get();
        }else {
            return null;
        }
    }


    public void deleteById(UUID id) {
        telegramUserRepo.deleteById(id);
    }

    public List<TelegramUser> getAll() {
        return telegramUserRepo.findAll();
    }

    public List<TelegramUser> getAllSort() {
        List<TelegramUser> list = telegramUserRepo.findAll();
        Collections.sort(list);
        return list;
    }

    public void save(TelegramUser telegramUser) {
        telegramUserRepo.save(telegramUser);
    }

    public void create(Long telegramId, String firstName, String lastName, String username, Long chatId) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setId(UUID.randomUUID());
        telegramUser.setTelegramId(telegramId);
        telegramUser.setUsername(username);
        telegramUser.setFirstName(firstName);
        telegramUser.setLastName(lastName);
        telegramUser.setChatId(chatId);
        telegramUserRepo.save(telegramUser);
    }
}
