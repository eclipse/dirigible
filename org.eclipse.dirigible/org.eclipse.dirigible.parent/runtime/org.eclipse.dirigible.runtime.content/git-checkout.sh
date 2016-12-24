#!/bin/bash

# This script is the part of integration GIT to ANT. Once launched it 
# should return the name of the current branch or the current commit (if 
# GIT is the detached HEAD mode). Further the printed name is appended to 
# the name of the resulting directory. To initialize this feature you need 
# to run ANT with the option "-Dusing.git=". 

exec 2>/dev/null

if cd target/template_web_new_or_edit; then git pull; else git clone https://github.com/dirigiblelabs/template_web_new_or_edit.git target/template_web_new_or_edit; fi