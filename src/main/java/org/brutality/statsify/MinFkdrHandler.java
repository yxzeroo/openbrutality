package org.brutality.statsify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.client.Minecraft;

public class MinFkdrHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String text = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "brutality";
    private static final String text2 = text + File.separator + "statsify.json";
    public int count = -1;
    public String text3 = "bws";
    public boolean flag = false;
    public boolean flag2 = true;
    public boolean flag3 = false;
    public boolean flag4 = true;
    public String text4 = "";
    public String text5 = "bracket_star_name_dot_fkdr";

    public void save() {
        File file = new File(text);
        if (!file.exists()) {
            file.mkdirs();
        }

        File file2 = new File(text2);

        try (FileWriter fileWriter = new FileWriter(file2)) {
            JsonObject json = new JsonObject();
            json.addProperty("minFkdr", this.count);
            json.addProperty("mode", this.text3);
            json.addProperty("tags", this.flag);
            json.addProperty("tablist", this.flag2);
            json.addProperty("urchin", this.flag3);
            json.addProperty("urchinkey", this.text4);
            json.addProperty("autowho", this.flag4);
            json.addProperty("tabformat", this.text5);
            gson.toJson((JsonElement)json, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(text2);
        if (!file.exists()) {
            this.save();
        } else {
            try {
                try (FileReader fileReader = new FileReader(file)) {
                    JsonObject json = gson.fromJson(fileReader, JsonObject.class);
                    if (json != null) {
                        this.count = json.has("minFkdr") ? json.get("minFkdr").getAsInt() : -1;
                        this.text3 = json.has("mode") ? json.get("mode").getAsString() : "bws";
                        this.flag = json.has("tags") ? json.get("tags").getAsBoolean() : false;
                        this.flag2 = json.has("tablist") ? json.get("tablist").getAsBoolean() : true;
                        this.flag3 = json.has("urchin") ? json.get("urchin").getAsBoolean() : false;
                        this.flag4 = json.has("autowho") ? json.get("autowho").getAsBoolean() : true;
                        this.text4 = json.has("urchinkey") ? json.get("urchinkey").getAsString() : "";
                        this.text5 = json.has("tabformat") ? json.get("tabformat").getAsString() : "bracket_star_name_dot_fkdr";
                        return;
                    }

                    this.save();
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.save();
            }
        }
    }

    public MinFkdrHandler() {
        this.load();
    }
}
