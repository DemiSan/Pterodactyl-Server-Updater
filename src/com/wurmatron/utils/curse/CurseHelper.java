package com.wurmatron.utils.curse;

import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.wurmatron.json.ModpackUpdate;
import com.wurmatron.utils.curse.json.ProjectData;
import com.wurmatron.utils.curse.json.ProjectData.ModFile;
import com.wurmatron.utils.URLUtils;
import java.util.HashMap;
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

  public static long getModpackModVersion(String data) {
    long projectID = getProjectIDFromUserInput(data);
    ProjectData projectData = loadProjectData(projectID);
    ModFile newestFile = projectData.latestFiles.get(0);
    for (ModFile file : projectData.latestFiles) {
      if (newestFile.fileDate.dayOfYear < file.fileDate.dayOfYear && newestFile.fileDate.year < file.fileDate.year) {
        newestFile = file;
      }
    }
    projectFileCache.put(newestFile.id, newestFile);
    return newestFile.id;
  }

  public static String getNewestVersion(String curseID) {
    ProjectData data = CurseHelper.loadProjectData(Long.parseLong(curseID));
    if (data.latestFiles.size() > 0) {
      // Check for Beta
      for (ModFile file : data.latestFiles) {
        if (file.releaseType.equalsIgnoreCase("BETA")) {
          return collectFileNameToVersion(file.fileName, file.gameVersion[0]);
        }
      }
      // Check for Release
      for (ModFile file : data.latestFiles) {
        if (file.releaseType.equalsIgnoreCase("RELEASE")) {
          return collectFileNameToVersion(file.fileName, file.gameVersion[0]);
        }
      }
    }
    return "";
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

  public static String getServerDownloadLink(ModpackUpdate update, String currentVersion) {
    String twitchCurrentVersion = getNewestVersion(update.updateURL);
    if (!twitchCurrentVersion.equalsIgnoreCase(currentVersion)) {
      ProjectData data = CurseHelper.loadProjectData(Long.parseLong(update.updateURL));
      return getServerDownloadLink(data);
    }
    return "";
  }

  private static String getServerDownloadLink(ProjectData data) {
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
    return "https://minecraft.curseforge.com/projects/%PROJECT%/files/"
        .replaceAll("%PROJECT%", data.slug) + download
        + "/download";
  }
}