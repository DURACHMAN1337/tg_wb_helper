package com.ftd.telegramhelper.util.callback;

import com.ftd.telegramhelper.util.faq.FaqInfos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Callback {
    public static final String BACK = "back";
    public static final String START_CHATTING = "start_chatting";
    public static final String SUCCESS = "success";
    public static final String DENIED = "denied";
    public static final String FAKE = "fake";

    public abstract static class Faq {
        public static final List<String> KNOWN_FAQ_CALLBACKS = new ArrayList<>() {{
            addAll(Vaacumator.First.ALL_STEP_CALLBACKS);
            addAll(Vaacumator.Second.ALL_STEP_CALLBACKS);
    }};
        public abstract static class Vaacumator {
            public abstract static class First {
                public static final String STEP_1_CB = FaqInfos.VaacumatorFaqInfos.FirstFaqInfo.StepCallbacks.FIRST_STEP_CALLBACK;
                public static final String STEP_2_CB = FaqInfos.VaacumatorFaqInfos.FirstFaqInfo.StepCallbacks.SECOND_STEP_CALLBACK;
                public static final String STEP_3_CB = FaqInfos.VaacumatorFaqInfos.FirstFaqInfo.StepCallbacks.THIRD_STEP_CALLBACK;
                public static final String STEP_4_CB = FaqInfos.VaacumatorFaqInfos.FirstFaqInfo.StepCallbacks.FOURTH_STEP_CALLBACK;

                public static final List<String> ALL_STEP_CALLBACKS = Arrays.asList(STEP_1_CB, STEP_2_CB, STEP_3_CB, STEP_4_CB);
            }

            public abstract static class Second {
                public static final String STEP_1_CB = FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.FIRST_STEP_CALLBACK;
                public static final String STEP_2_CB = FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.SECOND_STEP_CALLBACK;
                public static final String STEP_3_CB = FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.THIRD_STEP_CALLBACK;
                public static final String STEP_4_CB = FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.FOURTH_STEP_CALLBACK;
                public static final String STEP_5_CB = FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.FIVE_STEP_CALLBACK;
                public static final String STEP_6_CB = FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.SIX_STEP_CALLBACK;

                public static final List<String> ALL_STEP_CALLBACKS = Arrays.asList(STEP_1_CB, STEP_2_CB, STEP_3_CB, STEP_4_CB, STEP_5_CB, STEP_6_CB);
            }
        }
    }
}
