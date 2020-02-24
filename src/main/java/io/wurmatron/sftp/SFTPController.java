package io.wurmatron.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import io.wurmatron.utils.ServerLinker;
import io.wurmatron.utils.storage.ServerData;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class SFTPController {

  public List<ServerData> serverData;

  public SFTPController(List<ServerData> serverData) {
    if (serverData != null && !serverData.isEmpty() && serverData.get(0).sftp != null) {
      this.serverData = serverData;
    } else {
      ServerLinker.addSFTPData(ServerLinker.linkRestIDToSFTP());
    }
  }

  public HashMap<ServerData, Session> connect() {
    HashMap<ServerData, Session> sftpData = new HashMap<>();
    for (ServerData data : serverData) {
      try {
        Session sftp = new JSch()
            .getSession(data.sftp.username, data.sftp.ip, Integer.parseInt(data.sftp.port));
        java.util.Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        sftp.setConfig(config);
        sftp.setPassword(data.sftp.password);
        sftp.connect(50000);
        sftpData.put(data, sftp);
        System.out.println("Connected to " + data.restName + " (" + data.panelUUID + ")");
      } catch (Exception e) {
        System.out.println("Failed to connect to " + data.restName + " (" + data.panelUUID + ")");
        e.printStackTrace();
      }
    }
    return sftpData;
  }
}
