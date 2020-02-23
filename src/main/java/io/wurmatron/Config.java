package io.wurmatron;

import io.wurmatron.utils.UserInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class Config {

  // Pterodactyl
  public String baseURL = "";
  public String userAuthKey = "";
  public String adminAuthKey = "";

  public Config(File file) {
    load(file);
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

  public void load(File file) {
    try {
      Updater.GSON.fromJson(new FileReader(file), Config.class);
    } catch (FileNotFoundException e) {
      System.out.println("Unable to load config file '" + file.getName() + "'");
      String configFile = UserInput.askAndGetInput("Enter the name of the config file: ");
      file = new File(configFile);
      Updater.configFile = file;
      if (file.exists()) {
        load(file);
      } else {
        System.out.println("Creating new config file @ '" + file.getAbsolutePath() + "'");
        try {
          file.createNewFile();
          save();
        } catch (IOException f) {
          f.printStackTrace();
          System.out.println("Unable to save config file!");
        }
      }
    }
  }
}
