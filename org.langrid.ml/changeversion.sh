echo $1
java -cp bin langrid.ListupModules | xargs sed -i "" s/0\.0-SNAPSHOT/$1/g

