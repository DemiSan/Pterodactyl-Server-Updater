package io.wurmatron;

import io.wurmatron.utils.UserInput;
import io.wurmatron.utils.storage.ServerData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Config {

  // Pterodactyl
  public String panelBaseURL = "";
  public String userAuthKey = "";
  public String adminAuthKey = "";

  // Rest
  public String restBaseURL = "";
  public String restAuthToken = "";

  // Server Storage Data
  public List<ServerData> servers = new ArrayList<>();

  public Config() {
  }

  public void save() {
    String json = Updater.GSON.toJson(this);
    try {
      Files.write(Updater.configFile.toPath(), json.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Unable to save config file!");
    }
  }

  public static Config load(File file) {
    Updater.configFile = file;
    try {
      String lines = String.join("", Files.readAllLines(file.toPath()).toArray(new String[0]));
      return Updater.GSON.fromJson(lines, Config.class);
    } catch (IOException e) {
      System.out.println("Unable to load config file '" + file.getName() + "'");
      String configFile = UserInput
          .askAndGetInput("Enter the name of the config file (config.json): ");
      if (configFile == null || configFile.isEmpty()) {
        configFile = "config.json";
      }
      file = new File(configFile);
      Updater.configFile = file;
      if (file.exists()) {
        return load(file);
      } else {
        System.out.println("Creating new config file @ '" + file.getAbsolutePath() + "'");
        try {
          file.createNewFile();
          if (Updater.config == null) {
            Updater.config = new Config();
            Updater.configFile = file;
          }
          Updater.config.save();
        } catch (IOException f) {
          f.printStackTrace();
          System.out.println("Unable to save config file!");
        }
      }
    }
    return new Config();
  }
}
