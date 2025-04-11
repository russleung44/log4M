package com.tony.log4m.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.tony.log4m.external.tutu.TutuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileHandler {

    private final TutuService tutuService;


    /**
     * 处理文件消息
     */
    public void handle(TelegramBot bot, Message message) {
        Document document = message.document();
        Long userId = message.from().id();

        try {
            processDocument(bot, document, userId);
        } catch (IOException e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            bot.execute(new SendMessage(message.chat().id(), "文件处理失败，请重试"));
        } catch (Exception e) {
            log.error("文件解析异常: {}", e.getMessage(), e);
            bot.execute(new SendMessage(message.chat().id(), "文件格式错误"));
        }
    }

    private void processDocument(TelegramBot bot, Document document, Long userId) throws IOException {
        String fileId = document.fileId();
        String fileName = document.fileName();

        // 获取文件对象
        GetFileResponse fileResponse = bot.execute(new GetFile(fileId));
        String filePath = bot.getFullFilePath(fileResponse.file());

        log.info("开始处理文件: {} ({})", fileName, filePath);
        tutuService.read(filePath, userId);
    }


}