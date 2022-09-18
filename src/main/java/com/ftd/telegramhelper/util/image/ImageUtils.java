package com.ftd.telegramhelper.util.image;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public abstract class ImageUtils {

    @Nullable
    public static InputStream loadImage(String path) {
        if (StringUtils.hasText(path)) {
            try {
                return new ClassPathResource(path).getInputStream();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
