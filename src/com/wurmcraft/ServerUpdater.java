package com.wurmcraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.stanjg.ptero4j.PteroAdminAPI;
import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.wurmcraft.json.Config;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerUpdater {

  public static final String SCRIPT_LINK = "https://raw.githubusercontent.com/Wurmcraft/Pterodactyl-Server-Updater/master/Updater.sh";

  public static Config config;
  public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
  public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  //  API Instances
  public static PteroAdminAPI adminAPI;
  public static PteroUserAPI userApi;

  public static void main(String[] args) {
    loadConfig();
    adminAPI = new PteroAdminAPI(config.panelURL, config.adminApiKey);
    userApi = new PteroUserAPI(config.panelURL, config.userApiKey);
    for (Server server : adminAPI.getServersController().getAllServers()) {
      executor.scheduleAtFixedRate(() -> {
        String[] info = getLoginInfo(server);
        JSch ssh = new JSch();
        try {
          Session session = ssh.getSession(info[2], info[0], Integer.parseInt(info[1]));
          session.setPassword(Base64.getDecoder().decode(info[3]));
          session.setConfig("StrictHostKeyChecking", "no"); // TODO Move to known-hosts
          session.connect(50000);
          new UpdateThreadHelper().run(server,userApi.getServersController().getServer(server.getLongId()), session, info[4]);
        } catch (JSchException e) {
          e.printStackTrace();
          System.out
              .println("Failed to login to " + info[0] + " @" + info[1] + " with user " + info[2]);
        }
      }, 0, 30, TimeUnit.MINUTES);
    }
  }

  private static void loadConfig() {
    try {
      config = gson.fromJson(new FileReader(new File("settings.json")), Config.class);
    } catch (FileNotFoundException e) {
      System.out.println("Error finding, settings.json");
      config = createNewConfig();
    }
  }

  private static Config createNewConfig() {
    // Collect Config Values
    // Panel
    Scanner sc = new Scanner(System.in);
    String panelURL = getUserInput(sc, "Enter the panel URL: ");
    String adminApiKey = getUserInput(sc, "Enter the Admin API Key: ");
    String userApiKey = getUserInput(sc, "Enter the User API Key: ");
    String modpackInfo = getUserInput(sc, "Enter the Modpack Info File (Local-Per-Server): ");
    // Write to File
    Config config = new Config(panelURL, adminApiKey, userApiKey, modpackInfo);
    saveConfig();
    return config;
  }

  private static void saveConfig() {
    try (FileWriter fw = new FileWriter("settings.json")) {
      fw.write(gson.toJson(config));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String getUserInput(Scanner sc, String msg) {
    System.out.print(msg);
    String input = sc.nextLine();
    System.out.println();
    return input;
  }

  // [0] == URL
  // [1] == Port
  // [2] == Username
  // [3] == Password
  // [4] == root dir
  private static String[] getLoginInfo(Server server) {
    if (config.serverLoginInfo.getOrDefault(server.getUuid(), new String[0]).length > 0) {
      return config.serverLoginInfo.get(server.getUuid());
    } else {
      Scanner sc = new Scanner(System.in);
      String url = getUserInput(sc,
          "Login URL for " + server.getName() + " (" + server.getUuid() + "): ");
      String port = getUserInput(sc, "Port: ");
      String username = getUserInput(sc, "User: ");
      String password = getUserInput(sc, "Password: ");
      String dir = getUserInput(sc, "Directory (without uuid): ");
      String[] info = new String[]{url, port, username, Base64.getEncoder().encodeToString(password.getBytes()), dir};
      config.serverLoginInfo.put(server.getUuid(), info);
      saveConfig();
      return info;
    }
  }
}
