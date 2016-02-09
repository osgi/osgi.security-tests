#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$DIR/../../.."
cd $ROOT
BUNDLES="$ROOT/tests/bundles"
SYSATT187="$ROOT/tests/bundles/sysatt187"

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
    echo "      OSGi security tests framework"
    echo ""
    echo "USAGE"
    echo "      $0 [bundleNumber]"
    echo ""
    echo "DESCRIPTION"
    echo "      This script permits to execute unit tests "
    echo "      developed by Sogeti High Tech."
    echo ""
    echo "ERROR"
    echo "      $1"

}


if [ ! -d felix ]; then
    echo "ERROR: Felix framework is not installed."
    echo "       Thanks to execute installFelix.sh script."
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
if [ $1 -eq 000 ]; then
    :
elif [ $1 -eq 150 ] || [ $1 -eq 171 ] || [ $1 -eq 175 ] || [ $1 -eq 205 ] || [ $1 -eq 210 ]; then
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt$1.bundle2.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
elif [ $1 -eq 187 ]; then
    if [ ! -d $SYSATT187 ]; then
	echo "Bundle SYS-ATT-$1 directory not found"
	exit 1
    fi
elif [ $1 -eq 230 ]; then
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt230.maliciousBundle.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
elif [ $1 -eq 236 ]; then
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt236.maliciousBundle.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
elif [ $1 -eq 242 ]; then
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt242.maliciousBundle.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
    
elif [ $1 -eq 241 ]; then
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt241.maliciousBundle.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
elif [ $1 -eq 206 ]; then
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt206.bundle.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
else
    if [ ! -f $BUNDLES/com.sogetiht.otb.sysatt${1}.jar ]; then
	echo "Bundle SYS-ATT-$1 not found"
	exit 1
    fi
fi

if [ $1 -eq 135 ] || [ $1 -eq 203 ] || [ $1 -eq 302 ]; then
    OPTIONS="file:$BUNDLES/jna-4.0.0.jar file:$BUNDLES/org.osgi.core-1.4.0.jar"
elif [ $1 -eq 100 ] || [ $1 -eq 165 ] || [ $1 -eq 207 ] || [ $1 -eq 208 ]; then
    OPTIONS="file:$BUNDLES/bndlib-2.1.0.jar file:$BUNDLES/ops4j-base-io-1.4.0.jar file:$BUNDLES/ops4j-base-lang-1.4.0.jar file:$BUNDLES/ops4j-base-monitors-1.4.0.jar file:$BUNDLES/ops4j-base-store-1.4.0.jar file:$BUNDLES/org.osgi.core-1.4.0.jar file:$BUNDLES/org.osgi.core-4.2.0.jar file:$BUNDLES/tinybundles-2.0.0.jar"
    FRAGMENTS="file:$BUNDLES/slf4j-api-1.6.1.jar file:$BUNDLES/slf4j-simple-1.6.1.jar"
elif [ $1 -eq 174 ]; then
    OPTIONS="file:$BUNDLES/org.osgi.compendium-1.4.0.jar"
elif [ $1 -eq 206 ]; then
    OPTIONS="file:$BUNDLES/org.osgi.compendium-1.4.0.jar file:$BUNDLES/servlet-2.3.jar"
elif [ $1 -eq 230 ] || [ $1 -eq 241 ] || [ $1 -eq 242 ]; then
    OPTIONS="file:$BUNDLES/org.apache.felix.log-1.0.1.jar"
elif [ $1 -eq 235 ]; then
    OPTIONS="file:$BUNDLES/com.sogetiht.otb.sysatt235.helloServlet.jar file:$BUNDLES/com.sogetiht.otb.sysatt235.helloService.jar file:$BUNDLES/com.sogetiht.otb.sysatt235.normalizedHello.jar file:$BUNDLES/com.sogetiht.otb.sysatt235.nonNormalizedHello.jar file:$BUNDLES/com.sogetiht.otb.sysatt235.jar file:$BUNDLES/org.apache.felix.http.jetty-2.2.0.jar file:$BUNDLES/org.apache.felix.log-1.0.1.jar "
elif [ $1 -eq 236 ]; then
    OPTIONS="file:$BUNDLES/com.sogetiht.otb.sysatt236.helloServlet.jar file:$BUNDLES/com.sogetiht.otb.sysatt236.helloService.jar file:$BUNDLES/com.sogetiht.otb.sysatt236.normalizedHello.jar file:$BUNDLES/com.sogetiht.otb.sysatt236.nonNormalizedHello.jar file:$BUNDLES/com.sogetiht.otb.sysatt236.maliciousBundle.jar file:$BUNDLES/org.apache.felix.http.jetty-2.2.0.jar file:$BUNDLES/org.apache.felix.log-1.0.1.jar "
fi

if [ ! -d tmp ]; then
    mkdir tmp
    cd tmp
    ln -s $DIR/../com.sogetiht.otb.properties.cfg com.sogetiht.otb.properties.cfg
else
    cd tmp
fi

if [ ! $1 -eq 000 ] && [ ! $1 -eq 187 ] && [ ! $1 -eq 235 ] ; then
    for bundle in $(find $BUNDLES -name "com.sogetiht.otb.sysatt${1}.*")
    do
        if [ ${#OPTIONS} = 0 ];then
            OPTIONS="file:$bundle"
        else
            OPTIONS="$OPTIONS file:$bundle"
        fi
    done
fi

if [ $1 -eq 000 ]; then
    java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -jar $ROOT/felix/bin/felix.jar
elif [ $1 -eq 187 ]; then
    java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action=install,start -Dfelix.auto.deploy.dir="$SYSATT187" -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -Dfelix.auto.start.4="$OPTIONS" -jar $ROOT/felix/bin/felix.jar
elif [ $1 -eq 100 ] || [ $1 -eq 165 ] || [ $1 -eq 207 ] || [ $1 -eq 208 ]; then
    java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action=install,start -Dfelix.auto.install.1="$FRAGMENTS" -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -Dfelix.auto.start.4="$OPTIONS" -jar $ROOT/felix/bin/felix.jar
elif [ $1 -eq 225 ]; then
    java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -Dfelix.auto.start.4="$OPTIONS file:$BUNDLES/com.sogetiht.otb.sysatt165.helloService.jar" -jar $ROOT/felix/bin/felix.jar
elif [ $1 -eq 303 ]; then
    java -Dorg.osgi.framework.system.packages.extra=sun.misc -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -Dfelix.auto.start.4="$OPTIONS" -jar $ROOT/felix/bin/felix.jar
elif [ $1 -eq 304 ]; then
    java -Dorg.osgi.framework.system.packages.extra=sun.dc.pr,sun.dc.path -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -Dfelix.auto.start.4="$OPTIONS" -jar $ROOT/felix/bin/felix.jar
else
    java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1=file:$BUNDLES/com.sogetiht.otb.util.jar -Dfelix.auto.start.4="$OPTIONS" -jar $ROOT/felix/bin/felix.jar
fi