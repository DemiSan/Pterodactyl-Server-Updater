package com.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
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
import com.wurmatron.json.ServerSSHSettings;
import com.wurmatron.json.ServerStatus;
import com.wurmatron.json.Validation;
import com.wurmatron.utils.SFTPUtils;
import com.wurmatron.utils.curse.CurseHelper;
import java.io.InputStream;
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
          // Check / Collect SSH Login Info
          ServerSSHSettings sshSettings = config.getSHHSettings(server);
          if (sshSettings == null) {
            sshSettings = ServerSSHSettings.askForSettings(null);
            if (config.serverSSHSettings == null) {
              config.serverSSHSettings = new ArrayList<>();
            }
            config.serverSSHSettings.add(sshSettings);
            Config.save(configSaveLocation, config);
          }
          // Check / Collect SFTP Login Info
          ServerSFTPSettings sftpSettings = config.getSFTPSettings(server);
          if (sftpSettings == null) {
            sftpSettings = ServerSFTPSettings.askForSettings(null);
            if (config.serverSFTPSettings == null) {
              config.serverSFTPSettings = new ArrayList<>();
            }
            config.serverSFTPSettings.add(sftpSettings);
            Config.save(configSaveLocation, config);
          }
          System.out
              .println("Auto-Updater has been enabled for '" + server.getName() + " (" + server.getUuid() + ") on " + server.getNode().getName());
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
      ServerSSHSettings sshSettings = config.getSHHSettings(server);
      ServerSFTPSettings sftpSettings = config.getSFTPSettings(server);
      Session sftpSession = null;
      Session sshSession = null;
      try {
        // SFTP
        sftpSession = new JSch().getSession(sftpSettings.username, sftpSettings.url, sftpSettings.port);
        sftpSession.setPassword(sftpSettings.password);
        sftpSession.setConfig("StrictHostKeyChecking", "no");
        sftpSession.connect(50000);
        ChannelSftp sftp = (ChannelSftp) sftpSession.openChannel("sftp");
        try {
          sftp.connect();
          System.out.printf("[%s]: %s", server.getName(), "Connect to SFTP\n");
        } catch (Exception e) {
          System.out.printf("[%s]: %s", server.getName(), "Failed to connect to SFTP\n");
        }
        sftp.cd(sftpSettings.rootDir);
        ModpackUpdate update = SFTPUtils.getModpackUpdater(sftp);
        sftp.disconnect();
        // SSH
        sshSession = new JSch().getSession(sshSettings.username, sshSettings.host, sshSettings.port);
        sshSession.setPassword(sshSettings.password);  // No Key Support
        sshSession.setConfig("StrictHostKeyChecking", "no");
        sshSession.connect(50000);
        ChannelExec ssh = (ChannelExec) sshSession.openChannel("exec");
        ssh.setErrStream(System.out);
        ssh.setInputStream(null);
        try {
          ssh.connect();
          System.out.printf("[%s]: %s", server.getName(), "Connect to SSH\n");
        } catch (Exception e) {
          System.out.printf("[%s]: %s", server.getName(), "Failed to connect to SSH\n");
        }
        if (update.updateType == Type.TWITCH) {
          String newestVersion = CurseHelper.getNewestVersion(update.updateURL);
          System.out.printf("[%s]: %s", server.getName(), "Current Version: " + status.version + " | Latest Version: " + newestVersion + "\n");
          if (!status.version.equalsIgnoreCase(newestVersion)) {
            String serverUpdateURL = CurseHelper.getServerDownloadLink(update, newestVersion);
            System.out.printf("[%s]: %s", server.getName(), "Downloading " + serverUpdateURL + " to " + sshSettings.rootDir);
            ssh.setCommand("wget " + serverUpdateURL + "-O " + sshSettings.rootDir + (newestVersion + ".zip"));
          }
        }
        ssh.disconnect();
      } catch (Exception e) {
        System.out.printf("[%s]: %s", server.getName(), e.getMessage());
      } finally {
        sftpSession.disconnect();
        sshSession.disconnect();
      }
    };
  }
}
