package com.ftd.telegramhelper.util.message;

import com.vdurmont.emoji.EmojiParser;

import javax.validation.constraints.NotNull;

public enum Smiles {
    DIGIT_ONE(":one:"),
    DIGIT_TWO(":two:"),
    DIGIT_THREE(":three:"),
    DIGIT_FOUR(":four:"),
    DIGIT_FIVE(":five:"),
    DIGIT_SIX(":six:"),
    DIGIT_SEVEN(":seven:"),
    CREDIT_CARD(":credit_card:"),
    CATALOG(":blue_book:"),
    CART(":shopping_trolley: "),
    SEARCH(":mag:"),
    PROFILE(":bust_in_silhouette:"),
    QUESTION(":question:"),
    BLACK_CIRCLE(":black_circle:"),
    BLACK_PLUS_SIGN(":heavy_plus_sign:"),
    BLACK_MINUS_SIGN(":heavy_minus_sign:"),
    RED_X_SIGN(":x:"),
    BLACK_DOLLAR_SIGN(":heavy_dollar_sign:"),
    ARROW_RIGHT(":arrow_right:"),
    ARROW_LEFT(":arrow_left:"),
    ARROW_HEADING_UP(":arrow_heading_up:"),
    BACK(":back:");

    private final String code;

    Smiles(String code) {
        this.code = code;
    }

    public @NotNull String getUnicode() {
        return EmojiParser.parseToUnicode(code);
    }
}