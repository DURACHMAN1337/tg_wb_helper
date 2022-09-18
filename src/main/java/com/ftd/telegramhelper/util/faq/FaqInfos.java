package com.ftd.telegramhelper.util.faq;

import com.ftd.telegramhelper.util.image.ImageUtils;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public abstract class FaqInfos {
    private static final String FAQ_IMAGES_DIRECTORY = "image/faq/";

    public abstract static class VaacumatorFaqInfos {
        private static final String VAACUMATOR_FAQ_IMAGES_DIRECTORY = FAQ_IMAGES_DIRECTORY + "vaacumator/";

        public abstract static class FirstFaqInfo {
            public static final int STEPS_COUNT = 4;
            private static final String VAACUMATOR_FIRST_FAQ_IMAGES_DIRECTORY = VAACUMATOR_FAQ_IMAGES_DIRECTORY + "first/";

            public abstract static class StepCallbacks {
                public static final String FIRST_STEP_CALLBACK = "vaacumator_faq_1_step_1_cb";
                public static final String SECOND_STEP_CALLBACK = "vaacumator_faq_1_step_2_cb";
                public static final String THIRD_STEP_CALLBACK = "vaacumator_faq_1_step_3_cb";
                public static final String FOURTH_STEP_CALLBACK = "vaacumator_faq_1_step_4_cb";
            }

            public abstract static class StepMessages {
                public static final String FIRST_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.first.message.firstStep";
                public static final String SECOND_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.first.message.secondStep";
                public static final String THIRD_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.first.message.thirdStep";
                public static final String FOURTH_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.first.message.fourthStep";

                public static final List<String> KNOWN_FAQ_MESSAGES_KEYS = Arrays.asList(
                        FIRST_STEP_MESSAGE_KEY, SECOND_STEP_MESSAGE_KEY,
                        THIRD_STEP_MESSAGE_KEY, FOURTH_STEP_MESSAGE_KEY
                );
            }

            public abstract static class StepImages {
                private static final String FIRST_STEP_IMAGE_NAME = "firstStep.jpg";
                private static final String SECOND_STEP_IMAGE_NAME = "secondStep.jpg";
                private static final String THIRD_STEP_IMAGE_NAME = "thirdStep.jpg";
                private static final String FOURTH_STEP_IMAGE_NAME = "fourthStep.jpg";

                private static final List<String> IMAGES = Arrays.asList(
                        FIRST_STEP_IMAGE_NAME, SECOND_STEP_IMAGE_NAME,
                        THIRD_STEP_IMAGE_NAME, FOURTH_STEP_IMAGE_NAME
                );

                @Nullable
                public static InputStream loadStepImage(int stepNumber) {
                    return ImageUtils.loadImage(VAACUMATOR_FIRST_FAQ_IMAGES_DIRECTORY + getStepImageName(stepNumber));
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

        public abstract static class SecondFaqInfo {
            public static final int STEPS_COUNT = 6;
            private static final String VAACUMATOR_SECOND_FAQ_IMAGES_DIRECTORY = VAACUMATOR_FAQ_IMAGES_DIRECTORY + "second/";

            public abstract static class StepCallbacks {
                public static final String FIRST_STEP_CALLBACK = "vaacumator_faq_2_step_1_cb";
                public static final String SECOND_STEP_CALLBACK = "vaacumator_faq_2_step_2_cb";
                public static final String THIRD_STEP_CALLBACK = "vaacumator_faq_2_step_3_cb";
                public static final String FOURTH_STEP_CALLBACK = "vaacumator_faq_2_step_4_cb";
                public static final String FIVE_STEP_CALLBACK = "vaacumator_faq_2_step_5_cb";
                public static final String SIX_STEP_CALLBACK = "vaacumator_faq_2_step_6_cb";
            }

            public abstract static class StepMessages {
                public static final String FIRST_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.second.message.firstStep";
                public static final String SECOND_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.second.message.secondStep";
                public static final String THIRD_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.second.message.thirdStep";
                public static final String FOURTH_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.second.message.fourthStep";
                public static final String FIVE_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.second.message.fiveStep";
                public static final String SIX_STEP_MESSAGE_KEY = "ftd.telegram_helper.faq.vaacumator.second.message.sixStep";

                public static final List<String> KNOWN_FAQ_MESSAGES_KEYS = Arrays.asList(
                        FIRST_STEP_MESSAGE_KEY, SECOND_STEP_MESSAGE_KEY,
                        THIRD_STEP_MESSAGE_KEY, FOURTH_STEP_MESSAGE_KEY,
                        FIVE_STEP_MESSAGE_KEY, SIX_STEP_MESSAGE_KEY
                );
            }

            public abstract static class StepImages {
                private static final String FIRST_STEP_IMAGE_NAME = "firstStep.jpg";
                private static final String SECOND_STEP_IMAGE_NAME = "secondStep.jpg";
                private static final String THIRD_STEP_IMAGE_NAME = "thirdStep.jpg";
                private static final String FOURTH_STEP_IMAGE_NAME = "fourthStep.jpg";
                private static final String FIVE_STEP_IMAGE_NAME = "fiveStep.jpg";
                private static final String SIX_STEP_IMAGE_NAME = "sixStep.jpg";

                private static final List<String> IMAGES = Arrays.asList(
                        FIRST_STEP_IMAGE_NAME, SECOND_STEP_IMAGE_NAME,
                        THIRD_STEP_IMAGE_NAME, FOURTH_STEP_IMAGE_NAME,
                        FIVE_STEP_IMAGE_NAME, SIX_STEP_IMAGE_NAME
                );

                @Nullable
                public static InputStream loadStepImage(int stepNumber) {
                    return ImageUtils.loadImage(VAACUMATOR_SECOND_FAQ_IMAGES_DIRECTORY + getStepImageName(stepNumber));
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
}

