package com.ftd.telegramhelper.massmailing;

import java.io.File;

public interface MassMailingService {

    void sendMassMail(String message);

    void sendMassMail(String message, File photo);
}
