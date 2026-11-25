package org.brutality.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    public String name2;
    public String description2;
    public String text;
    public List<String> entries = new ArrayList<>();

    public Command(String name2, String description2, String text, String... parts) {
        this.name2 = name2;
        this.description2 = description2;
        this.text = text;
        this.entries = Arrays.asList(parts);
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public void setDescription(String description2) {
        this.description2 = description2;
    }

    public String getName() {
        return this.name2;
    }

    public String getText() {
        return this.text;
    }

    public void setName(String name2) {
        this.name2 = name2;
    }

    public List<String> computeList(String[] parts) {
        return null;
    }

    public String getDescription() {
        return this.description2;
    }

    public abstract void onCommand(String[] args, String command);

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getEntries() {
        return this.entries;
    }
}
