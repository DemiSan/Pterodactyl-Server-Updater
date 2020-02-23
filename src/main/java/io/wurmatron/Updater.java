package io.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.ptredactyl.Ptredactyl;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Updater {

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
  public static final Scanner SCANNER = new Scanner(System.in);

  // Config
  public static Config config;
  public static File configFile;

  // Instances
  public static Ptredactyl ptero;

  // 0 = Config File Location
  public static void main(String[] args) {
    // Load Config
    if (args.length == 0) {
      config = loadConfig("config.json");
    } else {
      config = loadConfig(args[0]);
    }
    ptero = new Ptredactyl(config.baseURL, config.userAuthKey, config.adminAuthKey);
    System.out.println("Pterodactyl API Loaded!");
  }

  public static Config loadConfig(String file) {
    return new Config(new File(file));
  }
}
