package me.alpha432.oyvey.manager;

import com.google.gson.*;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.settings.EnumConverter;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.traits.Jsonable;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static me.alpha432.oyvey.util.traits.Util.mc;

public class ConfigManager {
    //options config
    public static final String CONFIG_FOLDER_NAME = "ThunderHackRecode";
    public static final File MAIN_FOLDER = new File(mc.runDirectory, CONFIG_FOLDER_NAME);

    private static final Path OYVEY_PATH = FabricLoader.getInstance().getGameDir().resolve("oyvey");
    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();
    private final List<Jsonable> jsonables = List.of(CrackedClient.friendManager, CrackedClient.moduleManager, CrackedClient.commandManager);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        String str;
        switch (setting.getType()) {
            case "Boolean" -> {
                setting.setValue(element.getAsBoolean());
            }
            case "Double" -> {
                setting.setValue(element.getAsDouble());
            }
            case "Float" -> {
                setting.setValue(element.getAsFloat());
            }
            case "Integer" -> {
                setting.setValue(element.getAsInt());
            }
            case "String" -> {
                str = element.getAsString();
                setting.setValue(str.replace("_", " "));
            }
            case "Bind" -> {
                setting.setValue(new Bind(element.getAsInt()));//new Bind.BindConverter().doBackward(element)    //new Bind(element.getAsInt())
            }
            case "Enum" -> {
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
                } catch (Exception exception) {
                }
            }
            default -> {
                CrackedClient.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
            }
        }
    }

    public void load() {
        if (!OYVEY_PATH.toFile().exists()) OYVEY_PATH.toFile().mkdirs();
        for (Jsonable jsonable : jsonables) {
            try {
                String read = Files.readString(OYVEY_PATH.resolve(jsonable.getFileName()));
                jsonable.fromJson(JsonParser.parseString(read));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (!OYVEY_PATH.toFile().exists()) OYVEY_PATH.toFile().mkdirs();
        for (Jsonable jsonable : jsonables) {
            try {
                JsonElement json = jsonable.toJson();
                Files.writeString(OYVEY_PATH.resolve(jsonable.getFileName()), gson.toJson(json));
            } catch (Throwable e) {
            }
        }
    }

    public List<String> getCloudConfigs() {
        List<String> list = new ArrayList<>();
        try {
            URL url = new URL("https://miserver.com/cloudConfigs.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                list.add(inputLine.trim());
        } catch (Exception ignored) {
        }
        return list;
    }

    public void loadCloud(String name) {
        Command.sendMessage("Downloading..");
        try (BufferedInputStream in = new BufferedInputStream(new URL("https://miserver/configs/" + name + ".json").openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(new File(MAIN_FOLDER, name + ".json"))) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            Command.sendMessage("Downloaded!");
            //load(name);//abrir con el nombre
        } catch (Exception e) {
           Command.sendMessage("There was an error downloading! Maybe the name is wrong?");
        }
    }

    public List<String> getConfigList() {
        if (!MAIN_FOLDER.exists()) return null;
        List<String> list = new ArrayList<>();

        if (MAIN_FOLDER.listFiles() != null) {
            for (File file : Arrays.stream(Objects.requireNonNull(MAIN_FOLDER.listFiles())).filter(f -> f.getName().endsWith(".json")).collect(Collectors.toList())) {
                list.add(file.getName().replace(".json", ""));
            }
        }
        return list;
    }
}