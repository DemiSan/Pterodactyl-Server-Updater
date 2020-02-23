package io.wurmatron.sftp;

import io.wurmatron.utils.ServerLinker;
import io.wurmatron.utils.storage.ServerData;
import java.util.List;

public class SFTPController {

  public List<ServerData> serverData;

  public SFTPController(List<ServerData> serverData) {
    if (serverData != null && !serverData.isEmpty() && serverData.get(0).sftp != null) {
      this.serverData = serverData;
    } else {
      ServerLinker.addSFTPData(ServerLinker.linkRestIDToSFTP());
    }
  }
}
