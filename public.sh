mv .gitignore temp.gitignore
echo ".gitignore
/bin/
/.settings
/.project
/.classpath
/*.sh" > .gitignore
git push public
mv temp.gitignore .gitignore