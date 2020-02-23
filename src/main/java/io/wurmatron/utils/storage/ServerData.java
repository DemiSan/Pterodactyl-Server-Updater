package io.wurmatron.utils.storage;

public class ServerData {

  // ID
  public String panelUUID;
  public String restName;

  public SFTP sftp;

  public ServerData(String panelUUID, String restName) {
    this.panelUUID = panelUUID;
    this.restName = restName;
  }

  public void addSFTP(SFTP sftp) {
    this.sftp = sftp;
  }
}
