package com.ftd.telegramhelper.telegramuser;

import com.ftd.telegramhelper.config.bot.longpolling.LongPollingTelegramBotConfig;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final LongPollingTelegramBotConfig telegramBotConfig;

    @Autowired
    public TelegramUserService(
            TelegramUserRepository telegramUserRepository,
            LongPollingTelegramBotConfig telegramBotConfig
    ) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramBotConfig = telegramBotConfig;
    }

    public TelegramUser findBy(Long telegramUserId) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setTelegramId(telegramUserId);
        Optional<TelegramUser> one = telegramUserRepository.findOne(Example.of(telegramUser));
        return one.orElse(null);
    }

    public TelegramUser findBy(String username) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setUsername(username);
        Optional<TelegramUser> one = telegramUserRepository.findOne(Example.of(telegramUser));
        return one.orElse(null);
    }

    public TelegramUser findBy(User user) {
        return findBy(user.getUserName());
    }

    public List<TelegramUser> findAll(){
      return telegramUserRepository.findAll();
    }

    public TelegramUser save(TelegramUser telegramUser) {
        return telegramUserRepository.save(telegramUser);
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

        return save(telegramUser);
    }

    public boolean isMainAdmin(@Nullable User user) {
        return telegramBotConfig.isMainAdmin(user);
    }
}
