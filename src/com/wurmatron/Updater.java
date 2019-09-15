package com.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.stanjg.ptero4j.PteroAdminAPI;
import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.wurmatron.json.Config;
import com.wurmatron.json.ModpackUpdate;
import com.wurmatron.json.ModpackUpdate.Type;
import com.wurmatron.json.RequestGenerator;
import com.wurmatron.json.RequestGenerator.Status;
import com.wurmatron.json.ServerSFTPSettings;
import com.wurmatron.json.ServerStatus;
import com.wurmatron.json.Validation;
import com.wurmatron.utils.SFTPUtils;
import com.wurmatron.utils.curse.CurseHelper;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {

  public static String configSaveLocation = "Config.json";
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static Config config;
  public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool((Runtime.getRuntime().availableProcessors() - 1) > 0 ?
      Runtime.getRuntime().availableProcessors() : 1);
  // Api Access
  public static PteroUserAPI userAPI;
  public static PteroAdminAPI adminAPI;
  public static Validation restAPIVersion;

  public static void main(String[] args) throws Exception {
    config = Config.load(configSaveLocation);
    // Check for valid config / api connections
    userAPI = new PteroUserAPI(config.basePanelURL, new String(Base64.getDecoder().decode(config.userAPIKey)));
    adminAPI = new PteroAdminAPI(config.basePanelURL, new String(Base64.getDecoder().decode(config.adminAPIKey)));
    restAPIVersion = Status.getValidation();
    ServerStatus[] currentStatus = RequestGenerator.Status.getServerStatus();
    if (restAPIVersion == null) {
      System.out.println("Invalid Rest API");
      throw new InvalidKeyException("Invalid Rest URL / API Key");
    }
    if (currentStatus == null) {
      System.out.println("Server-Essentials Tracking Module must be enabled.");
      // TODO Possible SFTP to get Version
    }
    // Create Tracking Threads for each valid server entry on the panel
    for (Server server : adminAPI.getServersController().getAllServers()) {
      if (isValidServerForUpdater(server)) {
        ServerStatus status = getServerStatus(currentStatus, server);
        if (status != null) {
          System.out
              .println("Auto-Updater has been enabled for '" + server.getName() + " (" + server.getUuid() + ") on " + server.getNode().getName());
          // Check / Collect SFTP Login Info
          ServerSFTPSettings settings = config.getSFTPSettings(server);
          if (settings == null) {
            settings = ServerSFTPSettings.askForSettings(new ServerSFTPSettings());
            if (config.serverSFTPSettings == null) {
              config.serverSFTPSettings = new ArrayList<>();
            }
            config.serverSFTPSettings.add(settings);
            Config.save(configSaveLocation, config);
          }
          EXECUTOR.scheduleAtFixedRate(createServerUpdater(server), 0, config.updatePeriod, TimeUnit.MINUTES);
        } else {
          System.out.println("Unable to find server '" + server.getName() + " (" + server.getUuid() + ") on SE Rest API");
        }
      }
      break;
    }
  }

  private static boolean isValidServerForUpdater(Server server) {
    return server.getNestId() == 1; // Minecraft nest default is 1
  }

  private static ServerStatus getServerStatus(ServerStatus[] status, Server server) {
    for (ServerStatus track : status) {
      if (track.name.equals(server.getName()) || track.name.equalsIgnoreCase(server.getUuid())) {
        return track;
      }
    }
    return null;
  }

  private static ServerStatus getServerStatus(Server server) {
    return getServerStatus(RequestGenerator.Status.getServerStatus(), server);
  }

  private static Runnable createServerUpdater(final Server server) {
    return () -> {
      Thread.currentThread().setName(server.getName() + " Auto-Updater");
      ServerStatus status = getServerStatus(server);
      ServerSFTPSettings settings = config.getSFTPSettings(server);
      Session session = null;
      try {
        session = new JSch().getSession(settings.username, settings.url, settings.port);
        session.setPassword(settings.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(50000);
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        sftp.cd(settings.rootDir);
        ModpackUpdate update = SFTPUtils.getModpackUpdater(sftp);
        if (update.updateType == Type.TWITCH) {
          String newestVersion = CurseHelper.getNewestVersion(update.updateURL);
          System.out.printf("[%s]: %s", server.getName(), "Current Version: " + status.version + " | Latest Version: " + newestVersion + "\n");
          if (!status.version.equalsIgnoreCase(newestVersion)) {
            System.out.printf("[%s]: %s", server.getName(), "Update Found!");
          }
        }
      } catch (Exception e) {
        System.out.printf("[%s]: %s", server.getName(), e.getMessage());
      } finally {
        session.disconnect();
      }

    };
  }
}
