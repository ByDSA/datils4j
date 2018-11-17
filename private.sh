git config --global user.name "danisales"
git config --global user.email "danisales.es@gmail.com"
git rm --cached -r ./
#echo "/bin/" > .gitignore
git add --all
git commit -m "commit3"
git push master
git push -u master private