if [[ $# -eq 0 ]] ; then
    echo 'Falta mensaje de commit'
    exit 0
fi

#git config --global user.name "danisales"
#git config --global user.email "danisales.es@gmail.com"
git rm --cached -r ./
#echo "/bin/" > .gitignore
git add --all
git commit -m $1
git push -u private master