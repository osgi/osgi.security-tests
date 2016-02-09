#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$DIR/../../.."
cd $ROOT
BUNDLES="$ROOT/tests/bundles"

usage() {

	echo "---------------------------------------------------------------------------------"
	echo " __   __   __  ___    __   ___  __       __  ___ ___       ___  ___  __ ___  __  "
	echo "/  \ |__  | __  |    |__  |__  /   |  | |__|  |   |  \ /    |  |__  |__  |  |__  "
	echo "\__/  __| |__| _|_    __| |___ \__ |__| |  \ _|_  |   |     |  |___  __| |   __| "
	echo ""
	echo "---------------------------------------------------------------------------------"
	echo "                                                                 SOGETI HIGH TECH"
	echo "                                                           www.sogeti-hightech.fr"
	echo ""
	echo ""
	echo "NAME"
	echo "      Framework de tests orientés sécurité OSGi"
	echo ""
	echo "USAGE"
	echo "      $0 [bundleNumber]"
	echo ""
	echo "DESCRIPTION"
	echo "      Ce script permet de lancer les tests unitaires "
	echo "      développés par Sogeti High Tech."
	echo ""
	echo "ERROR"
	echo "      $1"

}


if [ ! -d knopflerfish ]; then
  echo "ERROR: Knopflerfish framework is not installed."
   echo "      Thanks to execute installKnopflerfish.sh script."
  exit 1
fi
if [ $# -ne 1 ]; then
  usage "[bundleNumber] is missing"
  exit 1
fi
if [ ${#1} -ne 3 ]; then
  usage "$1 is not a correct [bundleNumber]"
  exit 1
fi
if [ $1 -eq 150 ] || [ $1 -eq 171 ] || [ $1 -eq 175 ] || [ $1 -eq 205 ] || [ $1 -eq 210 ]; then
  if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt$1.bundle2.jar ]; then
    echo "Bundle SYS-ATT-$1 not found"
    exit 1
  fi
elif [ $1 -eq 230 ]; then
  if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt230.maliciousBundle.jar ]; then
    echo "Bundle SYS-ATT-$1 not found"
    exit 1
  fi
elif [ $1 -eq 206 ]; then
  if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt206.bundle.jar ]; then
    echo "Bundle SYS-ATT-$1 not found"
    exit 1
  fi
else
  if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt${1}* ]; then
    echo "Bundle SYS-ATT-$1 not found"
    exit 1
  fi
fi


if [ $1 -eq 135 ] || [ $1 -eq 203 ]; then
  ISTART="$ISTART -istart file:$BUNDLES/jna-4.0.0.jar -istart file:$BUNDLES/org.osgi.core-1.4.0.jar"
elif [ $1 -eq 206 ]; then
  ISTART="$ISTART -istart file:$BUNDLES/org.osgi.compendium-1.4.0.jar -istart file:$BUNDLES/servlet-2.3.jar"
elif [ $1 -eq 174 ]; then
  ISTART="$ISTART -istart file:$BUNDLES/org.osgi.compendium-1.4.0.jar"
elif [ $1 -eq 230 ]; then
  ISTART="$ISTART -istart file:$BUNDLES/org.apache.felix.log-1.0.1.jar"
fi

if [ ! -d tmp ]; then
  mkdir tmp
  cd tmp
  ln -s $DIR/../com.sogetiht.otb.properties.cfg com.sogetiht.otb.properties.cfg
else
  cd tmp
fi
if [ -d fwdir ]; then
  rm -rf fwdir
fi

for bundle in $(find $BUNDLES -name "com.sogetiht.otb.sysatt${1}.*")
do
  ISTART="$ISTART -istart $bundle"
done

java -jar $ROOT/knopflerfish/knopflerfish.jar -istart file:$BUNDLES/com.sogetiht.otb.util.jar${ISTART} -Forg.knopflerfish.framework.debug.errors=true
