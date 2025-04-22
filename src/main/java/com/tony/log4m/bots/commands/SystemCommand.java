package com.tony.log4m.bots.commands;

import cn.hutool.core.io.FileUtil;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.core.MoneyBot;
import com.tony.log4m.bots.enums.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipFile;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemCommand implements CommandStrategy {

    private final DataSource dataSource;
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        switch (command) {
            case HELP -> {
                String help = """
                        新建规则: /rule_add/{规则名称}-{金额}-{1:支付，0:收入}-{分类}
                        规则示例: /rule_add/鸭肶-18-1-吔
                        """;
                return new SendMessage(chatId, help);
            }

            case RESET -> {
                return new SendMessage(chatId, "重置成功");
            }

            case EXPORT -> {
                // 获取备份文件
                File file = null;
                try {
                    file = backupMvDb();
                    SendDocument sendDocument = new SendDocument(chatId, file);
                    MoneyBot.bot.execute(sendDocument);
                } catch (Exception e) {
                    log.error("备份失败", e);
                    return new SendMessage(chatId, "备份失败");
                } finally {
                    FileUtil.del(file);
                }
                return new SendMessage(chatId, "备份成功");
            }

            default -> {
                return new SendMessage(chatId, "未识别的命令");
            }
        }
    }

    public File backupMvDb() throws Exception {
        // 1. 解析 JDBC URL，定位数据库文件前缀
        //    如：jdbc:h2:file:/data/mydb;DB_CLOSE_ON_EXIT=FALSE
        int p1 = datasourceUrl.indexOf("file:") + 5;
        int p2 = datasourceUrl.indexOf(';') > 0 ? datasourceUrl.indexOf(';') : datasourceUrl.length();
        String basePath = datasourceUrl.substring(p1, p2);

        // 2. 在 home 目录生成临时 ZIP
        Path zipPath = Paths.get(System.getProperty("user.home"), "log4m-backup.zip");
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             Statement stmt = conn.createStatement()) {
            // 在线压缩所有文件到 ZIP
            stmt.execute("BACKUP TO '" + zipPath.toAbsolutePath() + "' ");
        }

        // 3. 打开 ZIP，定位其中的 .mv.db 条目
        String dbFileName = Paths.get(basePath).getFileName().toString() + ".mv.db";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        File outFile = Paths.get(System.getProperty("user.home"), "log4m-backup-" + dateTime + ".mv.db").toFile();
        try (ZipFile zip = new ZipFile(zipPath.toFile());
             InputStream in = zip.getInputStream(zip.getEntry(dbFileName));
             OutputStream out = new FileOutputStream(outFile)) {

            byte[] buf = new byte[8 * 1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }

        // 4. 删除临时 ZIP
        Files.deleteIfExists(zipPath);

        return outFile;
    }


}
