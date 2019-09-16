package com.wurmatron.json;

import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.wurmatron.utils.FileUtils;
import com.wurmatron.Updater;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class Config {

  // Panel
  public String basePanelURL;
  public String userAPIKey;
  public String adminAPIKey;
  // Rest API
  public String restURL;
  public String restAuth;
  // Program Settings
  public int updatePeriod;
  // Server SSH / SFTP Settings
  public List<ServerSSHSettings> serverSSHSettings;
  public List<ServerSFTPSettings> serverSFTPSettings;

  public static Config createNewConfig(String config) {
    Config cfg = new Config();
    askUserForConfigValues(cfg);
    save(config, cfg);
    return cfg;
  }

  private static void askUserForConfigValues(Config config) {
    Scanner sc = new Scanner(System.in);
    if (config == null) {
      config = new Config();
    }
    for (Field field : config.getClass().getDeclaredFields()) {
      System.out.print("Enter the value for '" + field.getName() + "': ");
      try {
        if (field.getName().endsWith("Key")) {
          field.set(config, Base64.getEncoder().encodeToString(sc.nextLine().getBytes()));
        } else if (field.getName().endsWith("Period")) {
          while (sc.hasNextLine()) {
            try {
              field.set(config, Integer.parseInt(sc.next()));
              break;
            } catch (NumberFormatException e) {
              System.out.println("Invalid Number");
              System.out.print("Enter the value for '" + field.getName() + "': ");
            }
          }
        } else if (!field.getName().endsWith("Settings")) {
          field.set(config, sc.nextLine().replaceAll(" ", ""));
          System.out.print("\n");
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

  }

  public static void save(String configLoc, Config config) {
    File file = new File(configLoc);
    if (!file.exists()) {
      try {
        boolean created = file.createNewFile();
        if (!created) {
          System.out.println("Failed to create config file!");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      Files.write(file.toPath(), Updater.GSON.toJson(config).getBytes(), StandardOpenOption.CREATE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Config load(String config) {
    File file = new File(config);
    if (file.exists()) {
      return Updater.GSON.fromJson(String.join("", FileUtils.readFile(file)), Config.class);
    } else {
      return Config.createNewConfig(config);
    }
  }

  public ServerSSHSettings getSHHSettings(Server server) {
    if (serverSSHSettings != null && !serverSSHSettings.isEmpty()) {
      for (ServerSSHSettings settings : serverSSHSettings) {
        if (server.getUuid().equalsIgnoreCase(settings.serverUUID)) {
          return settings;
        }
      }
    }
    return null;
  }

  public ServerSFTPSettings getSFTPSettings(Server server) {
    if (serverSFTPSettings != null && !serverSFTPSettings.isEmpty()) {
      for (ServerSFTPSettings settings : serverSFTPSettings) {
        if (server.getUuid().equalsIgnoreCase(settings.serverUUID)) {
          return settings;
        }
      }
    }
    return null;
  }

}
