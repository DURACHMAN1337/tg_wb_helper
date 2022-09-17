package com.ftd.telegramhelper.telegramuser;

import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    @Autowired
    public TelegramUserService(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    public TelegramUser findBy(Long telegramUserId) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setTelegramId(telegramUserId);
        Optional<TelegramUser> one = telegramUserRepository.findOne(Example.of(telegramUser));
        return one.orElse(null);
    }

    public void save(TelegramUser telegramUser) {
        telegramUserRepository.save(telegramUser);
    }

    public TelegramUser createAndSaveFrom(User user, Long chatId) {
        TelegramUser telegramUser = new TelegramUser();

        telegramUser.setTelegramId(user.getId());
        telegramUser.setUsername(user.getUserName());
        telegramUser.setFirstName(user.getFirstName());
        telegramUser.setLastName(user.getLastName());
        telegramUser.setChatId(chatId);
        telegramUser.setState(UserStates.NEW);
        telegramUser.setId(UUID.randomUUID());

        save(telegramUser);

        return telegramUser;
    }
}
