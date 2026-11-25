package org.brutality.system;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

public final class SessionLeaseLock {
    private FileChannel fileChannel;
    private FileLock fileLock;
    private Path path;
    private Path path2;
    private String token;

    public synchronized void handlePath(Path path, String token) {
        if (token == null) {
            this.release();
        } else if (!token.equals(this.token)) {
            this.release();

            try {
                String str = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
                this.path2 = path.resolve(str);
                Files.createDirectories(this.path2);

                try (
                    FileChannel fileChannel = FileChannel.open(this.path2.resolve("coordination.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    FileLock fileLock = fileChannel.lock();
                ) {
                    this.path = Files.createFile(this.path2.resolve(ProcessHandle.current().pid() + "-" + UUID.randomUUID() + ".lease"));
                    this.fileChannel = FileChannel.open(this.path, StandardOpenOption.WRITE);
                    this.fileLock = this.fileChannel.lock();
                    this.token = token;
                }
            } catch (Exception e) {
                this.release();
            }
        }
    }

    public synchronized void handleString(String str, Runnable task) {
        if (str != null && str.equals(this.token) && this.fileLock != null) {
            Path path2 = this.path2;

            try (
                FileChannel fileChannel = FileChannel.open(path2.resolve("coordination.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock fileLock = fileChannel.lock();
            ) {
                this.release();
                boolean flag = false;

                try (DirectoryStream directoryStream = Files.newDirectoryStream(path2, "*.lease")) {
                    for (Path path : ((Iterable<Path>)(Object)(directoryStream))) {
                        boolean flag2 = false;

                        try (FileChannel fileChannel2 = FileChannel.open(path, StandardOpenOption.WRITE)) {
                            try (FileLock fileLock2 = fileChannel2.tryLock()) {
                                if (fileLock2 == null) {
                                    flag = true;
                                } else {
                                    flag2 = true;
                                }
                            } catch (OverlappingFileLockException e) {
                                flag = true;
                            }
                        }

                        if (flag2) {
                            Files.deleteIfExists(path);
                        }
                    }
                }

                if (!flag) {
                    task.run();
                }
            } catch (Exception e2) {
                this.release();
            }
        }
    }

    private void release() {
        try {
            if (this.fileLock != null) {
                this.fileLock.release();
            }
        } catch (Exception ignored) {
        }

        try {
            if (this.fileChannel != null) {
                this.fileChannel.close();
            }
        } catch (Exception ignored2) {
        }

        try {
            if (this.path != null) {
                Files.deleteIfExists(this.path);
            }
        } catch (Exception ignored3) {
        }

        this.fileLock = null;
        this.fileChannel = null;
        this.path = null;
        this.token = null;
    }
}
