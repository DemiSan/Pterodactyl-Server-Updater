package io.wurmatron.curse;

import io.wurmatron.curse.json.ProjectData;
import io.wurmatron.curse.json.ProjectData.GameFile;
import io.wurmatron.curse.json.ProjectData.ModFile;
import io.wurmatron.utils.URLUtils;
import java.util.ArrayList;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurseHelper {

  public static final String CURSE_API_LINK = "https://curse.nikky.moe/api/";

  public static HashMap<Long, ProjectData> projectCache = new HashMap<>();
  public static HashMap<Long, ModFile> projectFileCache = new HashMap<>();

  public static String getDownloadLink(long projectID, long fileID) {
    return loadModFile(projectID, fileID).downloadURL;
  }

  public static ProjectData loadProjectData(long projectID) {
    if (projectCache.containsKey(projectID)) {
      return projectCache.get(projectID);
    } else {
      ProjectData projectData =
          URLUtils.get(CURSE_API_LINK + "addon/" + projectID, ProjectData.class);
      projectCache.put(projectID, projectData);
      return projectData;
    }
  }

  public static long getProjectIDFromUserInput(String input) {
    try {
      if (input.contains(":")) {
        return Long.parseLong(input.split(":")[0]);
      } else {
        return Long.parseLong(input);
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return -1L;
    }
  }

  public static ModFile loadModFile(long projectID, long fileID) {
    if (projectFileCache.containsKey(fileID)) {
      return projectFileCache.get(fileID);
    } else {
      ModFile fileData =
          URLUtils.get(CURSE_API_LINK + "addon/" + projectID + "/file/" + fileID, ModFile.class);
      projectFileCache.put(fileID, fileData);
      return fileData;
    }
  }

  public static String getNewestVersion(String curseID) {
    List<String> releases = new ArrayList<>();
    ProjectData data = CurseHelper.loadProjectData(Long.parseLong(curseID));
    if (data.gameVersionLatestFiles.size() > 0) {
      // Check for Beta
      for (GameFile file : data.gameVersionLatestFiles) {
        if (file.fileType.equalsIgnoreCase("BETA")) {
          releases.add(collectFileNameToVersion(file.projectFileName, file.gameVersion));
        }
      }
      // Check for Release
      for (GameFile file : data.gameVersionLatestFiles) {
        if (file.fileType.equalsIgnoreCase("RELEASE")) {
          releases.add(collectFileNameToVersion(file.projectFileName, file.gameVersion));
        }
      }
    }
    return findNewestVersion(releases);
  }

  private static String collectFileNameToVersion(String fileName, String mcVersion) {
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

  private static String findNewestVersion(List<String> versions) {
    if (versions.size() == 1) {
      return versions.get(0);
    }
    String latest = versions.get(0);
    for (int index = 1; index < versions.size(); index++) {
      if (compareTo(versions.get(index), latest)) {
        latest = versions.get(index);
      }
    }
    return latest;
  }

  private static boolean compareTo(String higher, String prev) {
    String[] newL = higher.split("\\.");
    String[] pre = prev.split("\\.");
    for (int index = 0; index < Math.min(newL.length, pre.length); index++) {
      if(newL[index].compareTo(pre[index]) > 0) {
        return true;
      }
    }
    return false;
  }
}