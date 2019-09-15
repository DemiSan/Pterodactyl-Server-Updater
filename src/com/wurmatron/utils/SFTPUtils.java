package com.wurmatron.utils;


import com.jcraft.jsch.ChannelSftp;
import com.wurmatron.Updater;
import com.wurmatron.json.ModpackUpdate;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SFTPUtils {

  public static final ModpackUpdate getModpackUpdater(ChannelSftp sftp) {
    try (InputStream is = sftp.get("Update.json")) {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      StringBuilder builder = new StringBuilder();
      while ((line = br.readLine()) != null) {
        builder.append(line);
      }
      return Updater.GSON.fromJson(builder.toString(), ModpackUpdate.class);
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
    return null;
  }
}
