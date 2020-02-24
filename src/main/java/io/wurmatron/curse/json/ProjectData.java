
package io.wurmatron.curse.json;

import java.util.*;

public class ProjectData {

  public long id;
  public String name;
  public List<Author> authors;
  public List<Attachment> attachments;
  public String webSiteURL;
  public long gameId;
  public String summary;
  public long defaultFileId;
  public long commentCount;
  public double downloadCount;
  public long rating;
  public long installCount;
  public List<ModFile> latestFiles;
  public List<Category> categories;
  public String primaryAuthorName;
  public String externalUrl;
  public String status;
  public String stage;
  public String donationUrl;
  public String primaryCategoryName;
  public String primaryCategoryAvatorUrl;
  public long likes;
  public CategorySection categorySection;
  public String packageType;
  public String avatarUrl;
  public String slug;
  public String clientUrl;
  public List<GameFile> gameVersionLatestFiles;
  public double popularityScore;
  public long gamePopularityRank;
  public String fullDescription;
  public String gameName;
  public String portalName;
  public String sectionName;
  public FileDate dateModified;
  public FileDate dateCreated;
  public FileDate dateReleased;
  public String categoryList;
  public boolean available;

  public ProjectData(
      long id,
      String name,
      List<Author> authors,
      List<Attachment> attachments,
      String webSiteURL,
      long gameId,
      String summary,
      long defaultFileId,
      long commentCount,
      double downloadCount,
      long rating,
      long installCount,
      List<ModFile> latestFiles,
      List<Category> categories,
      String primaryAuthorName,
      String externalUrl,
      String status,
      String stage,
      String donationUrl,
      String primaryCategoryName,
      String primaryCategoryAvatorUrl,
      long likes,
      CategorySection categorySection,
      String packageType,
      String avatarUrl,
      String slug,
      String clientUrl,
      List<GameFile> gameVersionLatestFiles,
      double popularityScore,
      long gamePopularityRank,
      String fullDescription,
      String gameName,
      String portalName,
      String sectionName,
      FileDate dateModified,
      FileDate dateCreated,
      FileDate dateReleased,
      String categoryList,
      boolean available) {
    this.id = id;
    this.name = name;
    this.authors = authors;
    this.attachments = attachments;
    this.webSiteURL = webSiteURL;
    this.gameId = gameId;
    this.summary = summary;
    this.defaultFileId = defaultFileId;
    this.commentCount = commentCount;
    this.downloadCount = downloadCount;
    this.rating = rating;
    this.installCount = installCount;
    this.latestFiles = latestFiles;
    this.categories = categories;
    this.primaryAuthorName = primaryAuthorName;
    this.externalUrl = externalUrl;
    this.status = status;
    this.stage = stage;
    this.donationUrl = donationUrl;
    this.primaryCategoryName = primaryCategoryName;
    this.primaryCategoryAvatorUrl = primaryCategoryAvatorUrl;
    this.likes = likes;
    this.categorySection = categorySection;
    this.packageType = packageType;
    this.avatarUrl = avatarUrl;
    this.slug = slug;
    this.clientUrl = clientUrl;
    this.gameVersionLatestFiles = gameVersionLatestFiles;
    this.popularityScore = popularityScore;
    this.gamePopularityRank = gamePopularityRank;
    this.fullDescription = fullDescription;
    this.gameName = gameName;
    this.portalName = portalName;
    this.sectionName = sectionName;
    this.dateModified = dateModified;
    this.dateCreated = dateCreated;
    this.dateReleased = dateReleased;
    this.categoryList = categoryList;
    this.available = available;
  }

  public class Author {

    public String name;
    public String url;
  }

  public class Attachment {

    public long id;
    public long projectID;
    public String description;
    public String thumbnailUrl;
    public String title;
    public String url;
  }

  public class ModFile {

    public long id;
    public String fileName;
    public String fileNameOnDisk;
    public FileDate fileDate;
    public String releaseType;
    public String fileStatus;
    public String downloadURL;
    public long alternativeFileId;
    public List<Dependencicy> dependencies;
    public List<Module> modules;
    public long packageFingerprlong;
    public String[] gameVersion;
    public String installMetadata;
    public long fileLength;
    public boolean alternate;
    public boolean available;

    public ModFile(long id, String fileName, String fileNameOnDisk, FileDate fileDate, String releaseType, String fileStatus,
        String downloadURL, long alternativeFileId, List<Dependencicy> dependencies,
        List<Module> modules, long packageFingerprlong, String[] gameVersion, String installMetadata, long fileLength, boolean alternate,
        boolean available) {
      this.id = id;
      this.fileName = fileName;
      this.fileNameOnDisk = fileNameOnDisk;
      this.fileDate = fileDate;
      this.releaseType = releaseType;
      this.fileStatus = fileStatus;
      this.downloadURL = downloadURL;
      this.alternativeFileId = alternativeFileId;
      this.dependencies = dependencies;
      this.modules = modules;
      this.packageFingerprlong = packageFingerprlong;
      this.gameVersion = gameVersion;
      this.installMetadata = installMetadata;
      this.fileLength = fileLength;
      this.alternate = alternate;
      this.available = available;
    }
  }

  public class FileDate {

    public int year;
    public String month;
    public int dayOfMonth;
    public String dayOfWeek;
    public int dayOfYear;
    public int nano;
    public int second;
    public int monthValue;
    public int hour;
    public int minute;
    public Chronolgy chronology;

    public FileDate(int year, String month, int dayOfMonth, String dayOfWeek, int dayOfYear, int nano, int second, int monthValue, int hour,
        int minute,
        Chronolgy chronology) {
      this.year = year;
      this.month = month;
      this.dayOfMonth = dayOfMonth;
      this.dayOfWeek = dayOfWeek;
      this.dayOfYear = dayOfYear;
      this.nano = nano;
      this.second = second;
      this.monthValue = monthValue;
      this.hour = hour;
      this.minute = minute;
      this.chronology = chronology;
    }
  }

  public class Chronolgy {

    public String calendarType;
    public String id;

    public Chronolgy(String calendarType, String id) {
      this.calendarType = calendarType;
      this.id = id;
    }
  }

  public class Dependencicy {

    public long addOnId;
    public String type;
  }

  public class Module {

    public long fingerprlong;
    public String foldername;
  }

  public class Category {

    public long id;
    public String name;
    public String url;
    public String avatarUrl;
  }

  public class CategorySection {

    public long id;
    public long gameID;
    public String name;
    public String packageType;
    public String path;
    public String initialInclusionPattern;
    public String extraIncludePattern;
  }

  public class GameFile {

    public String gameVersion;
    public long projectFileID;
    public String projectFileName;
    public String fileType;
  }
}