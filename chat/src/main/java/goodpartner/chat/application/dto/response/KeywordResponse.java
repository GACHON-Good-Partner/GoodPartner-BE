package goodpartner.chat.application.dto.response;

import goodpartner.chat.entity.Keyword;

public record KeywordResponse(
        String keyWord,
        String url
) {
    public static KeywordResponse of(Keyword keyword) {
        return new KeywordResponse(keyword.getKeyword(), keyword.getUrl());
    }
}
