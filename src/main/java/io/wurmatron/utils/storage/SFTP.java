package io.wurmatron.utils.storage;

public class SFTP {
    public String ip;
    public String port;
    public String username;
    public String password;

    public SFTP(String ip, String port, String username, String password) {
      this.ip = ip;
      this.port = port;
      this.username = username;
      this.password = password;
    }
  }