if [[ $# -eq 0 ]] ; then
    echo 'Falta mensaje de commit'
    exit 0
fi

./private.sh "PUBLIC: $1"
git rm --cached -r ./
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