#!/bin/bash

days='1'

if [ $# -eq 1 ] # Used for debugging purpose
then
  days=$1
fi

IFS='\n'

yesterday=$(date -d "-$days days" '+%s')
new_commits=$(git log --since "$yesterday")

if [ "$new_commits" ]
then
  echo "New changes!"
  ./millw -i io.kipp.mill.ci.release.ReleaseModule/publishAll
else
  echo "No changes"
fi