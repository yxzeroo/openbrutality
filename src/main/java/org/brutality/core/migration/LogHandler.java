package org.brutality.core.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LogHandler {
    private final File file;
    public final String text;
    private final Set<String> itemSet = new HashSet<>();

    public LogHandler(String str, File file) {
        this.text = str.toUpperCase();
        this.file = file;
        this.process();
    }

    public Set<String> getItemSet() {
        return this.itemSet;
    }

    private void process() {
        this.itemSet.clear();
        if (!this.file.exists()) {
            this.log("File not found: " + this.file.getAbsolutePath());
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
                String str;
                while ((str = reader.readLine()) != null) {
                    this.itemSet.add(str);
                }

                this.log("Names successfully read from " + this.file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String str) {
    }
}
