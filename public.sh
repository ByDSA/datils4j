#git rm --cached -r ./
mv .gitignore temp.gitignore
echo "*.gitignore
/bin/
/.settings
/.project
/.classpath
*.sh" > .gitignore
git add ./
git commit -m "commit44"
git push master
git push -u public/master master
mv temp.gitignore .gitignore