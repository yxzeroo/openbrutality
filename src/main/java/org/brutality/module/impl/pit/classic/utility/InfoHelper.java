package org.brutality.module.impl.pit.classic.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class InfoHelper implements AutoCloseable {
    private final Logger logger = Logger.getAnonymousLogger();
    private final FileHandler fileHandler;
    private static final long timestamp = -8439279259652980736L;

    @Override
    public void close() {
        this.logger.removeHandler(this.fileHandler);
        this.fileHandler.close();
    }

    public InfoHelper(Path path) throws IOException {
        Path path2 = path.resolve("Brutality/logs").toAbsolutePath();
        Files.createDirectories(path2);
        String str = path2.toString().replace("%", "%%") + "/classic-vanish-debug-%g.log";
        this.fileHandler = new FileHandler(str, (int)timestamp, 3, true);
        this.logger.setUseParentHandlers(false);
        this.logger.setLevel(Level.INFO);
        this.fileHandler.setEncoding("UTF-8");
        this.fileHandler.setFormatter(new Formatter() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

            @Override
            public String format(LogRecord logRecord) {
                String str2 = this.dateTimeFormatter.format(Instant.ofEpochMilli(logRecord.getMillis()));
                String message = logRecord.getMessage();
                String str3 = System.lineSeparator();
                String str4 = message;
                String str5 = str2;
                return "[" + str5 + "] " + str4 + str3;
            }
        });
        this.logger.addHandler(this.fileHandler);
    }

    public void info(String str, Object... objs) {
        StringBuilder sb = new StringBuilder();
        int n = 0;

        for (Object obj : objs) {
            int n2 = str.indexOf("{}", n);
            if (n2 < 0) {
                break;
            }

            sb.append(str, n, n2).append(obj);
            n = n2 + 2;
        }

        this.logger.info(sb.append(str.substring(n)).toString());
    }
}
