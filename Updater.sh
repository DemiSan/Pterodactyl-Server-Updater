#!/bin/bash
# [1] = Update File
# [2] = Backup-Dir
# [3] = Current Server Version
# [4] = New Server Version
# [5] = Base Dir
echo "Updating from $3 to $4"

# Move Older Verson to Backup-Dir
mv ./mods $2
mv ./config $2
mv ./scripts $2
echo "Backup Complete"

# Setup Server Modpack
unzip -o $1 -d $5
echo "Unzip Finished"
rm -rf $1

# Optional
# Handle Server-Essentials (Temp-Link)
wget https://www.dropbox.com/s/dg5ywg64fd9wo3d/ServerEssentials.jar?dl=1
mv ServerEssentials.jar?dl=1 $5mods/ServerEssentials.jar
mv $2config/ServerEssentials.cfg $5config/ServerEssentials.cfg
# Handle MatterLink (Temp-Link)
wget https://www.dropbox.com/s/o6j3gadmetny3jl/MatterLink-1.12.2.jar?dl=1
mv MatterLink-1.12.2.jar?dl=0 $5mods/MatterLink.jar
mv $2config/matterlink/ %5config/