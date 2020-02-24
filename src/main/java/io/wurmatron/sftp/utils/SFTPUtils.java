package io.wurmatron.sftp.utils;

import com.jcraft.jsch.ChannelSftp;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SFTPUtils {

  public static String readFile(String file, ChannelSftp sftp) {
    if (!sftp.isConnected()) {
      try {
        sftp.connect();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    List<String> lines = new ArrayList<>();
    try (InputStream is = sftp.get(file);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr)) {
      String line = br.readLine();
      while (line != null) {
        lines.add(line);
        line = br.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return String.join("", lines.toArray(new String[0]));
  }
}
