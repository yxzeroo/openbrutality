package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.proxy.ProxyManager;

public class HttpUtil {
    public static final ListeningExecutorService field_180193_a = MoreExecutors.listeningDecorator(
        Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build())
    );
    private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    static final Logger logger = LogManager.getLogger();

    public static String buildPostString(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();

        for (Entry entry : data.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }

            try {
                sb.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (entry.getValue() != null) {
                sb.append('=');

                try {
                    sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static String postMap(URL url, Map<String, Object> data, boolean skipLoggingErrors) {
        return post(url, buildPostString(data), skipLoggingErrors);
    }

    private static String post(URL url, String content, boolean skipLoggingErrors) {
        try {
            Proxy proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
            if (ProxyManager.checkState() && ProxyManager.computeProxy() != null) {
                proxy = ProxyManager.computeProxy();
            } else if (proxy == null) {
                proxy = Proxy.NO_PROXY;
            }

            HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(content);
            dataOutputStream.flush();
            dataOutputStream.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();

            String str;
            while ((str = reader.readLine()) != null) {
                stringBuffer.append(str);
                stringBuffer.append('\r');
            }

            reader.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            if (!skipLoggingErrors) {
                logger.error("Could not post to " + url, (Throwable)e);
            }

            return "";
        }
    }

    public static ListenableFuture<Object> downloadResourcePack(
        final File saveFile, final String packUrl, final Map<String, String> p_180192_2_, final int maxSize, final IProgressUpdate p_180192_4_, final Proxy p_180192_5_
    ) {
        return (ListenableFuture<Object>)field_180193_a.submit(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream in = null;
                DataOutputStream dataOutputStream = null;
                if (p_180192_4_ != null) {
                    p_180192_4_.resetProgressAndMessage("Downloading Resource Pack");
                    p_180192_4_.displayLoadingString("Making Request...");
                }

                try {
                    byte[] bytes = new byte[4096];
                    URL url = new URL(packUrl);
                    conn = (HttpURLConnection)url.openConnection(p_180192_5_);
                    float f = 0.0F;
                    float size = p_180192_2_.entrySet().size();

                    for (Entry entry : p_180192_2_.entrySet()) {
                        conn.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                        if (p_180192_4_ != null) {
                            p_180192_4_.setLoadingProgress((int)(++f / size * 100.0F));
                        }
                    }

                    in = conn.getInputStream();
                    size = conn.getContentLength();
                    int contentLength = conn.getContentLength();
                    if (p_180192_4_ != null) {
                        p_180192_4_.displayLoadingString(String.format("Downloading file (%.2f MB)...", size / 1000.0F / 1000.0F));
                    }

                    if (saveFile.exists()) {
                        long len = saveFile.length();
                        if (len == contentLength) {
                            if (p_180192_4_ != null) {
                                p_180192_4_.setDoneWorking();
                            }

                            return;
                        }

                        HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + contentLength + " vs our " + len + ").");
                        FileUtils.deleteQuietly(saveFile);
                    } else if (saveFile.getParentFile() != null) {
                        saveFile.getParentFile().mkdirs();
                    }

                    dataOutputStream = new DataOutputStream(new FileOutputStream(saveFile));
                    if (maxSize > 0 && size > maxSize) {
                        if (p_180192_4_ != null) {
                            p_180192_4_.setDoneWorking();
                        }

                        throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + maxSize + ")");
                    }

                    int n = 0;

                    while (true) {
                        if ((n = in.read(bytes)) < 0) {
                            if (p_180192_4_ == null) {
                                return;
                            }

                            p_180192_4_.setDoneWorking();
                            return;
                        }

                        f += n;
                        if (p_180192_4_ != null) {
                            p_180192_4_.setLoadingProgress((int)(f / size * 100.0F));
                        }

                        if (maxSize > 0 && f > maxSize) {
                            if (p_180192_4_ != null) {
                                p_180192_4_.setDoneWorking();
                            }

                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + maxSize + ")");
                        }

                        if (Thread.interrupted()) {
                            HttpUtil.logger.error("INTERRUPTED");
                            if (p_180192_4_ != null) {
                                p_180192_4_.setDoneWorking();
                            }
                            break;
                        }

                        dataOutputStream.write(bytes, 0, n);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (conn != null) {
                        InputStream errorStream = conn.getErrorStream();

                        try {
                            HttpUtil.logger.error(IOUtils.toString(errorStream));
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }

                    if (p_180192_4_ != null) {
                        p_180192_4_.setDoneWorking();
                        return;
                    }

                    return;
                } finally {
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly((OutputStream)dataOutputStream);
                }
            }
        });
    }

    public static int getSuitableLanPort() throws IOException {
        ServerSocket serverSocket = null;
        int n = -1;

        try {
            serverSocket = new ServerSocket(0);
            n = serverSocket.getLocalPort();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException ignored) {
            }
        }

        return n;
    }

    public static String get(URL url) throws IOException {
        Proxy NO_PROXY = Proxy.NO_PROXY;
        if (ProxyManager.checkState() && ProxyManager.computeProxy() != null) {
            NO_PROXY = ProxyManager.computeProxy();
        }

        HttpURLConnection conn = (HttpURLConnection)url.openConnection(NO_PROXY);
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
            sb.append('\r');
        }

        reader.close();
        return sb.toString();
    }
}
