#!/bin/bash
# [1] = Update File
# [2] = Backup-Dir
# [3] = Current Server Version
# [4] = New Server Version
echo "Updating from $3 to $4"

# Move Older Verson to Backup-Dir
mv mods $2
mv config $2
mv scripts $2
echo "Backup Complete"

# Setup Server Modpack
unzip -o $1
echo "Unzip Finished"
# TODO Setup Server-Essentials
# TODO Add Optional Server Mods
# TODO Change Version
# TODO Copy Required Configs