package com.ftd.tg_wb_helper.model.repo;

import com.ftd.tg_wb_helper.model.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TelegramUserRepo  extends JpaRepository<TelegramUser, UUID> {
}
