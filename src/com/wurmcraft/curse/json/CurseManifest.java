package com.wurmcraft.curse.json;

import java.util.*;

public class CurseManifest {

  public Minecraft minecraft;
  public String manifestType;
  public long manifestVersion;
  public String name;
  public String version;
  public String author;
  public List<PackMod> files;
  public String overrides;

  public CurseManifest() {}

  public CurseManifest(
      Minecraft minecraft,
      String manifestType,
      long manifestVersion,
      String name,
      String version,
      String author,
      List<PackMod> files,
      String overrides) {
    this.minecraft = minecraft;
    this.manifestType = manifestType;
    this.manifestVersion = manifestVersion;
    this.name = name;
    this.version = version;
    this.author = author;
    this.files = files;
    this.overrides = overrides;
  }

  public class Minecraft {

    public String version;
    public List<ModLoader> modLoaders;

    public Minecraft() {}

    public Minecraft(String version, List<ModLoader> modLoaders) {
      this.version = version;
      this.modLoaders = modLoaders;
    }

    public class ModLoader {

      public String id;
      public boolean primary;

      public ModLoader(String id, boolean primary) {
        this.id = id;
        this.primary = primary;
      }
    }
  }

  public class PackMod {

    public long projectID;
    public long fileID;
    public boolean required;

    public PackMod(long projectID, long fileID, boolean required) {
      this.projectID = projectID;
      this.fileID = fileID;
      this.required = required;
    }
  }
}
