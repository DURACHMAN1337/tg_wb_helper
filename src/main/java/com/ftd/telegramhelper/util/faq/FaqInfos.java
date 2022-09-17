package com.ftd.telegramhelper.util.faq;

import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class FaqInfos {

    private static final String FAQ_IMAGES_DIRECTORY = "images.faq/";

    public abstract class VaacumatorFaqInfos {
        // image infos:
        private static final String FIRST_STEP_IMAGE_NAME = "firstStep.jpg";
        private static final String SECOND_STEP_IMAGE_NAME = "secondStep.jpg";
        private static final String THIRD_STEP_IMAGE_NAME = "thirdStep.jpg";
        private static final String FOURTH_STEP_IMAGE_NAME = "fourthStep.jpg";

        private static final List<String> IMAGES = Arrays.asList(
                FIRST_STEP_IMAGE_NAME, SECOND_STEP_IMAGE_NAME,
                THIRD_STEP_IMAGE_NAME, FOURTH_STEP_IMAGE_NAME
        );

        // message infos:
        public static final String FIRST_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.message.firstStep";
        public static final String SECOND_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.message.secondStep";
        public static final String THIRD_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.message.thirdStep";
        public static final String FOURTH_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.message.fourthStep";

        public static final List<String> KNOWN_FAQ_MESSAGES_KEYS = Arrays.asList(
                FIRST_STEP_MESSAGE_KEY, SECOND_STEP_MESSAGE_KEY,
                THIRD_STEP_MESSAGE_KEY, FOURTH_STEP_MESSAGE_KEY
        );

        public abstract class StepImages {

            @Nullable
            public static InputStream loadImage(int stepNumber) {
                try {
                    return new ClassPathResource(
                            FAQ_IMAGES_DIRECTORY + getStepImageName(stepNumber)
                    ).getInputStream();
                } catch (IOException e) {
                    return null;
                }
            }

            @Nullable
            private static String getStepImageName(int stepNumber) {
                if (stepNumber > IMAGES.size() || stepNumber < 0) {
                    return null;
                }
                if (stepNumber > 0) {
                    stepNumber -= 1;
                }
                return IMAGES.get(stepNumber);
            }
        }
    }
}

