package com.tony.log4m.bots.core;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages per-chat remark input sessions for Telegram bot interactions.
 * When a user clicks the "remark" button for a bill, we record the mapping
 * from chatId -> billId and consume it on the next text message from that chat.
 */
@Component
public class RemarkSessionManager {

    private final ConcurrentMap<Long, Long> pendingRemark = new ConcurrentHashMap<>();

    public void startRemark(Long chatId, Long billId) {
        if (chatId != null && billId != null) {
            pendingRemark.put(chatId, billId);
        }
    }

    public boolean isAwaitingRemark(Long chatId) {
        return chatId != null && pendingRemark.containsKey(chatId);
    }

    public Long peekRemarkTarget(Long chatId) {
        return chatId == null ? null : pendingRemark.get(chatId);
    }

    public Long consumeRemarkTarget(Long chatId) {
        return chatId == null ? null : pendingRemark.remove(chatId);
    }

    public void cancel(Long chatId) {
        if (chatId != null) {
            pendingRemark.remove(chatId);
        }
    }
}
