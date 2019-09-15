package com.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stanjg.ptero4j.PteroAdminAPI;
import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.wurmatron.json.Config;
import com.wurmatron.json.RequestGenerator;
import com.wurmatron.json.RequestGenerator.Status;
import com.wurmatron.json.ServerStatus;
import com.wurmatron.json.Validation;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static Config config;
  public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool((Runtime.getRuntime().availableProcessors() - 1) > 0 ?
      Runtime.getRuntime().availableProcessors() : 1);
  // Api Access
  public static PteroUserAPI userAPI;
  public static PteroAdminAPI adminAPI;
  public static Validation restAPIVersion;

  public static void main(String[] args) throws Exception {
    String configLoc = "Config.json";
    config = Config.load(configLoc);
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
    }
    // Create Tracking Threads for each valid server entry on the panel
    for (Server server : adminAPI.getServersController().getAllServers()) {
      if (isValidServerForUpdater(server)) {
        ServerStatus status = getServerStatus(currentStatus, server);
        if (status != null) {
          System.out
              .println("Auto-Updater has been enabled for '" + server.getName() + " (" + server.getUuid() + ") on " + server.getNode().getName());
          EXECUTOR.scheduleAtFixedRate(createServerUpdater(server), 0, config.updatePeriod, TimeUnit.MINUTES);
        } else {
          System.out.println("Unable to find server '" + server.getName() + " (" + server.getUuid() + ") on SE Rest API");
        }
      }
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
      ServerStatus staus = getServerStatus(server);
      System.out.printf("[%s]: %s", server.getName(), "Current Version: " + staus.version + " | Latest Version: Unknown \n");
    };
  }
}
