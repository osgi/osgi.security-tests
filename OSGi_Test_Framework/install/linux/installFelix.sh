#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR/../..

if [ ! -d felix ]; then

wget https://repo1.maven.org/maven2/org/apache/felix/org.apache.felix.main.distribution/4.2.1/org.apache.felix.main.distribution-4.2.1.tar.gz
tar -xzf org.apache.felix.main.distribution-4.2.1.tar.gz
mv felix-framework-4.2.1 felix
rm org.apache.felix.main.distribution-4.2.1.tar.gz

else
  echo "ERROR: Felix est déjà installé. Pour relancer"
  echo "       l'installation, supprimez le dossier"
  echo "       felix et relancez ce script."

fi
