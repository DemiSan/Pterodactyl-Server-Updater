package io.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.ptredactyl.Ptredactyl;
import io.wurmatron.rest.Rest;
import io.wurmatron.sftp.SFTPController;
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
  public static Rest rest;
  public static SFTPController sftp;

  // 0 = Config File Location
  public static void main(String[] args) {
    // Load Config
    if (args.length == 0) {
      config = Config.load(new File("config.json"));
    } else {
      config = Config.load(new File(args[0]));
    }
    ptero = new Ptredactyl(config.panelBaseURL, config.userAuthKey, config.adminAuthKey);
    System.out.println("Pterodactyl API Loaded!");
    rest = new Rest(config.restBaseURL, config.restAuthToken);
    sftp = new SFTPController(config.servers);
    System.out.println("SFTP Loaded!");
    System.out.println("Loading Finished!");
  }
}