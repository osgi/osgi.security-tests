#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"
echo $DIR
cd $DIR/../..

if [ ! -d knopflerfish ]; then

	wget https://github.com/knopflerfish/knopflerfish.org/archive/master.zip
	unzip master.zip	
	mkdir knopflerfish
	cd knopflerfish.org-master
	ant all
	mv osgi/framework.jar ../knopflerfish/knopflerfish.jar
	rm -rf ../master.zip ../knopflerfish.org-master

else
	echo "ERROR: Knopflerfish est déjà installé. Pour relancer"
	echo "       l'installation, supprimez le dossier"
	echo "       knopflerfish et relancez ce script."
fi
