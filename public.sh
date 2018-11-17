#git rm --cached -r ./
mv .gitignore temp.gitignore
echo "*.gitignore
/bin/
/.settings
/.project
/.classpath
*.sh" > .gitignore
git add ./
git commit -m "commit2"
git push master
git push public
mv temp.gitignore .gitignore