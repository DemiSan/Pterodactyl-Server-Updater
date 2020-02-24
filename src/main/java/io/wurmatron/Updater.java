package io.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.stanjg.ptero4j.entities.objects.server.PowerAction;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.stanjg.ptero4j.entities.panel.user.UserServer;
import io.wurmatron.curse.CurseHelper;
import io.wurmatron.ptredactyl.Ptredactyl;
import io.wurmatron.rest.RequestGenerator;
import io.wurmatron.rest.Rest;
import io.wurmatron.rest.json.ServerStatus;
import io.wurmatron.sftp.SFTPController;
import io.wurmatron.sftp.json.Update;
import io.wurmatron.sftp.utils.SFTPUtils;
import io.wurmatron.utils.storage.ServerData;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(4);
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
    HashMap<ServerData, Session> sftpData = sftp.connect();
    for (ServerData data : sftpData.keySet()) {
      createUpdateThread(data, sftpData.get(data));
    }
  }

  public static void createUpdateThread(ServerData data, Session sftp) {
    EXECUTOR.scheduleAtFixedRate(() -> {
      String currentVersion = getCurrentVersion(data.restName);
      Thread.currentThread().setName(data.restName);
      System.out.printf("[%s] Current Modpack Version: " + currentVersion + "%n", Thread.currentThread().getName());
      ChannelSftp channel = null;
      try {
        channel =  (ChannelSftp) sftp.openChannel("sftp");
      } catch (Exception e) {
        e.printStackTrace();
      }
      Update update = GSON.fromJson(SFTPUtils.readFile("update.json", channel), Update.class);
      String curseID = update.curseID;
      System.out.println("[" + Thread.currentThread().getName() + "] Checking for update on " + curseID);
      String newestVersion = CurseHelper.getNewestVersion(curseID);
      System.out.println("[" + Thread.currentThread().getName() + "] Current: " + currentVersion + " Latest: " + newestVersion);
      if(!currentVersion.equals(newestVersion)) {
        System.out.println("[" + Thread.currentThread().getName() + "] Update Detected!");
      }
    }, 0, 30, TimeUnit.MINUTES);
  }

  private static String getCurrentVersion(String restName) {
    for (ServerStatus status : RequestGenerator.Status.getServerStatus()) {
      if (status.name.equals(restName)) {
        return status.version;
      }
    }
    return "";
  }

}