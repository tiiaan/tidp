#!/bin/zsh

cd /Users/tiiaan/Projects/tidp
comment="$1"
day=$(date "+%-d")
time=$(date "+%H:%M:%S")
git add .
git commit -m "${comment}"
GIT_COMMITTER_DATE="July ${day} ${time} 2022 +0800" git commit --amend --date "July ${day} ${time} 2022 +0800"
git push -u origin master
#tar -zcvf "/Users/tiiaan/Projects/BACKUP/myrpc/${comment}.tar.gz" /Users/tiiaan/Projects/myrpc
id=$(git rev-parse HEAD)
memo="- [[${${id}: 0: 7}]](https://github.com/tiiaan/tidp/commit/${id})"
echo "- ${memo} ${comment}" >> /Users/tiiaan/Projects/tidp/README.md
