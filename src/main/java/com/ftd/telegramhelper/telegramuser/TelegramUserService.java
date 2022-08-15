package com.ftd.telegramhelper.telegramuser;

import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
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

    public void delete(TelegramUser telegramUser) {
        telegramUserRepository.delete(telegramUser);
    }

    public void deleteBy(UUID id) {
        telegramUserRepository.deleteById(id);
    }

    public List<TelegramUser> getAll() {
        return telegramUserRepository.findAll();
    }

    public List<TelegramUser> getAllSort(String sortBy) {
        return telegramUserRepository.findAll(
                Sort.by(sortBy)
        );
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
}
