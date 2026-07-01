package com.tony.log4m.bots.core;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages per-chat rule keyword input sessions for Telegram bot interactions.
 * When a user clicks "create rule from bill", we record the mapping
 * from chatId -> billId and consume it on the next text message from that chat.
 */
@Component
public class RuleKeywordSessionManager {

    private final ConcurrentMap<Long, Long> pendingKeyword = new ConcurrentHashMap<>();

    public void startKeywordInput(Long chatId, Long billId) {
        if (chatId != null && billId != null) {
            pendingKeyword.put(chatId, billId);
        }
    }

    public boolean isAwaitingKeyword(Long chatId) {
        return chatId != null && pendingKeyword.containsKey(chatId);
    }

    public Long peekKeywordTarget(Long chatId) {
        return chatId == null ? null : pendingKeyword.get(chatId);
    }

    public Long consumeKeywordTarget(Long chatId) {
        return chatId == null ? null : pendingKeyword.remove(chatId);
    }

    public void cancel(Long chatId) {
        if (chatId != null) {
            pendingKeyword.remove(chatId);
        }
    }
}
