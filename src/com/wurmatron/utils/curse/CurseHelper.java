package com.wurmatron.utils.curse;

import com.wurmatron.utils.curse.json.ProjectData;
import com.wurmatron.utils.curse.json.ProjectData.ModFile;
import com.wurmatron.utils.URLUtils;
import java.util.HashMap;

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
      if (newestFile.fileDate < file.fileDate) {
        newestFile = file;
      }
    }
    projectFileCache.put(newestFile.id, newestFile);
    return newestFile.id;
  }


}