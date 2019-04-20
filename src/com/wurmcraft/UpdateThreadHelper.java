package com.wurmcraft;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.stanjg.ptero4j.entities.panel.user.UserServer;
import com.wurmcraft.curse.CurseHelper;
import com.wurmcraft.curse.json.ProjectData;
import com.wurmcraft.curse.json.ProjectData.ModFile;
import com.wurmcraft.json.ModpackUpdate;
import com.wurmcraft.json.ModpackUpdate.Type;
import com.wurmcraft.utils.URLUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateThreadHelper {

  private Session session;
  public Server server;
  public UserServer userServer;
  public String rootDir;
  public ModpackUpdate update;
  // Updated after first check
  public String currentVersion = "";
  public String newestVersion = "";
  public String updateLink = "";

  public void run(Server server, UserServer user, Session session, String rootDir) {
    this.server = server;
    this.userServer = user;
    this.session = session;
    this.rootDir = rootDir + "/" + server.getUuid() + "/";
    if (hasUpdate()) {
      print("Current Version: " + currentVersion + " Newest Version: " + newestVersion);
      if (canUpdateServerEasy()) {
        print("Updating...");
        updateServer();
      } else {
        // TODO Fix this Mess
        print("Update scheduled");
        ServerUpdater.executor.schedule(() -> {
          userServer.sendCommand("bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
              + ") / Restarting in 10min!");
          print("Update scheduled in 10min");
          ServerUpdater.executor.schedule(() -> {
            userServer.sendCommand("bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                + ") / Restarting in 5min!");
            print("Update scheduled in 5min");
            ServerUpdater.executor.schedule(() -> {
              userServer.sendCommand("bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                  + ") / Restarting in 3min!");
              print("Update scheduled in 3min");
              ServerUpdater.executor.schedule(() -> {
                userServer.sendCommand("bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                    + ") / Restarting in 2min!");
                print("Update scheduled in 2min");
                ServerUpdater.executor.schedule(() -> {
                  userServer.sendCommand(
                      "bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                          + ") / Restarting in 1min!");
                  print("Update scheduled in 1min");
                  ServerUpdater.executor.schedule(() -> {
                    userServer.sendCommand(
                        "bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                            + ") / Restarting in 30sec!");
                    print("Update scheduled in 30sec");
                    ServerUpdater.executor.schedule(() -> {
                      userServer.sendCommand(
                          "bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                              + ") / Restarting in 15sec!");
                      print("Update scheduled in 15sec");
                      ServerUpdater.executor.schedule(() -> {
                        userServer.sendCommand(
                            "bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion
                                + ") / Restarting in 5sec!");
                        print("Update scheduled in 5sec");
                        ServerUpdater.executor.schedule(() -> {
                          userServer.sendCommand(
                              "bc &4[&cWurm&bCraft&4] &6Server Updating (" + newestVersion + ")");
                          print("Updating...");
                          ServerUpdater.executor.schedule(() -> {
                            userServer.sendCommand("save-all");
                            userServer.stop();
                            updateServer();
                          }, 10, TimeUnit.SECONDS);
                        }, 5, TimeUnit.SECONDS);
                      }, 5, TimeUnit.SECONDS);
                    }, 15, TimeUnit.SECONDS);
                  }, 30, TimeUnit.SECONDS);
                }, 1, TimeUnit.MINUTES);
              }, 1, TimeUnit.MINUTES);
            }, 2, TimeUnit.MINUTES);
          }, 5, TimeUnit.MINUTES);
        }, 0, TimeUnit.MILLISECONDS);
      }
    }
  }

  // Update does not affect much
  private boolean canUpdateServerEasy() {
    return userServer.getPowerState().getValue().equalsIgnoreCase("OFF") || userServer
        .getPowerState().getValue().equalsIgnoreCase("STOPPING");
  }

  private void updateServer() {
    String backupDir = rootDir + "Updater-" + currentVersion + "/";
    String fileName = updateLink.substring(updateLink.lastIndexOf('/') + 1);
    runCommand("mkdir " + backupDir);
    runCommand("wget " + ServerUpdater.SCRIPT_LINK);
    runCommand("mv Updater.sh " + rootDir);
    runCommand("chmod +x " + rootDir + "Updater.sh ");
    runCommand("wget " + updateLink);
    runCommand("mv " + fileName + " " + newestVersion + ".zip");
    fileName = newestVersion + ".zip";
    runCommand("mv " + fileName + " " + rootDir);
    print(runCommand(
        "/" + rootDir + "Updater.sh " + rootDir + fileName + " " + backupDir + " " + currentVersion + " "
            + newestVersion));
  }

  // Copied from https://stackoverflow.com/questions/2405885/run-a-command-over-ssh-with-jsch
  private String runCommand(String command) {
    StringBuilder outputBuffer = new StringBuilder();
    try {
      Channel channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);
      InputStream commandOutput = channel.getInputStream();
      channel.connect();
      int readByte = commandOutput.read();
      while (readByte != 0xffffffff) {
        outputBuffer.append((char) readByte);
        readByte = commandOutput.read();
      }
      channel.disconnect();
    } catch (IOException | JSchException e) {
      print(e.getLocalizedMessage());
      return null;
    }
    return outputBuffer.toString();
  }

  public boolean hasUpdate() {
    try {
      ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
      sftp.connect();
      sftp.cd(rootDir);
      update = getModpackUpdater(sftp);
      // Read Modpack Version from Server-Essentials.cfg
      String modpackVersion = getModpackVersion(sftp);
      currentVersion = modpackVersion;
      if (update.updateType.equals(Type.TWITCH)) {
        return checkTwitchUpdate(update, modpackVersion);
      }
    } catch (Exception e) {
      print(e.getLocalizedMessage());
    }
    return false;
  }

  private boolean checkTwitchUpdate(ModpackUpdate update, String serverVersion) {
    String twitchCurrentVersion = getNewestVersion(update.updateURL);
    newestVersion = twitchCurrentVersion;
    if (!twitchCurrentVersion.equalsIgnoreCase(serverVersion)) {
      return true;
    }
    return false;
  }

  private String getNewestVersion(String curseID) {
    ProjectData data = CurseHelper.loadProjectData(Long.parseLong(curseID));
    if (data.latestFiles.size() > 0) {
      // Check for Beta
      for (ModFile file : data.latestFiles) {
        if (file.releaseType.equalsIgnoreCase("BETA")) {
          updateLink = getServerDownloadLink(data);
          return collectFileNameToVersion(file.fileName, file.gameVersion[0]);
        }
      }
      // Check for Release
      for (ModFile file : data.latestFiles) {
        if (file.releaseType.equalsIgnoreCase("RELEASE")) {
          updateLink = getServerDownloadLink(data);
          return collectFileNameToVersion(file.fileName, file.gameVersion[0]);
        }
      }
    }
    return "";
  }

  private String getServerDownloadLink(ProjectData data) {
    String page = URLUtils.toString("https://minecraft.curseforge.com/projects/" + data.slug);
    String download = "";
    for (String line : page.split("\n")) {
      if (line.contains("data-action=\"server-pack-download\"")) {
        download = line;
      }
    }
    download = download.replaceAll(" ", "");
    download = download.substring(download.indexOf("href"), download.indexOf("download\""));
    download = download.substring(download.indexOf("files"));
    download = download.replaceAll("/", "").replaceAll("files", "");
    return "https://minecraft.curseforge.com/projects/enigmatica2expert/files/" + download
        + "/download";
  }

  private String collectFileNameToVersion(String fileName, String mcVersion) {
    Pattern pattern = Pattern.compile(".\\.(.*)");
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.find()) {
      String versionUnfiltered = matcher.group();
      versionUnfiltered = versionUnfiltered.replaceAll(".zip", "");
      versionUnfiltered = versionUnfiltered.replaceAll(mcVersion, "");
      return versionUnfiltered;
    }
    return "";
  }

  private String getModpackVersion(ChannelSftp sftp) throws SftpException, IOException {
    sftp.cd("config");
    BufferedReader bis = new BufferedReader(
        new InputStreamReader(sftp.get("ServerEssentials.cfg")));
    String line;
    String modpackVersion = "";
    while ((line = bis.readLine()) != null) {
      if (line.contains("S:modpackVersion=")) {
        modpackVersion = line.replaceAll("S:modpackVersion=", "").replaceAll(" ", "");
      }
    }
    return modpackVersion;
  }

  private void print(String msg) {
    System.out.println(server.getName() + ": " + msg);
  }

  private ModpackUpdate getModpackUpdater(ChannelSftp sftp) {
    try (InputStream is = sftp.get("update.json")) {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      StringBuilder builder = new StringBuilder();
      while ((line = br.readLine()) != null) {
        builder.append(line);
      }
      return ServerUpdater.gson.fromJson(builder.toString(), ModpackUpdate.class);
    } catch (Exception e) {
      print(e.getLocalizedMessage());
    }
    return null;
  }
}
