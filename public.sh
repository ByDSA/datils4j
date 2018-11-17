#git rm --cached -r ./
mv .gitignore temp.gitignore
echo "*.gitignore
/bin/
/.settings
/.project
/.classpath
*.sh" > .gitignore
git add ./
git commit -m "commit33"
git push master
git push -u public master
mv temp.gitignore .gitignore