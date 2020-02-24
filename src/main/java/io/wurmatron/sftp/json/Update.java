package io.wurmatron.sftp.json;

public class Update {

  public String modpackLink = "";
  public String curseID = "";

  public Update(String modpackLink, String curseID) {
    this.modpackLink = modpackLink;
    this.curseID = curseID;
  }
}
