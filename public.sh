git rm --cached -r ./
git add --all
git commit -m $1
git push -u private master

mv .gitignore temp.gitignore
echo "*.gitignore
/bin/
/.settings
/.project
/.classpath
*.sh" > .gitignore
git add --all
git commit -m $1
git push -u public master
mv temp.gitignore .gitignore