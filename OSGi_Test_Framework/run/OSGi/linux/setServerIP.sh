#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$DIR/.."

echo -e "# Standard output configuration\ndisplay.box=true\ndisplay.server=true\n\n# Server configuration\nserver.ip=$1\nserver.port=2009\nserver.dport=2010\nserver.sport=2011\n\n# Automated testing\ntest=true\n" > $ROOT/com.sogetiht.otb.properties.cfg

echo -e "Server IP address set to $1\n"
