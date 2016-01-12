#!/bin/bash

# Version 1.1

# Ce script lance et vérifie les tests développés dans le cadre d'OpenTheBox.

# Pour ce faire, 5 fichiers peuvent être créés :
#   > sysattXXX        : contient les "println" du bundle
#   > sysattXXX.stdout : contient la sortie stdout et stderr
#   > sysattXXX.cpu    : contient le pourcentage de CPU utilisé par le
#                        processus
#   > sysattXXX.mem    : contient le pourcentage de mémoire utilisé par le
#                        processus
#   > sysattXXX.thread : contient le nombre de threads utilisés par le
#                        processus

# Pour valider le test, le script cherche dans ces fichiers la présence d'une
# chaine de caractères précise.

# Afin d'évaluer le cpu, la mémoire et/ou les threads utilisés,
# les variables CPU, MEMORY et THREADS doivent être mises à true.


# Remettre la console
reset

VERBOSE=false
# mode verbose activé ?
if [ "$1" == "-v" ]; then
    VERBOSE=true
fi

# Permet de se placer dans le répertoire "RD_OTB/trunk/test".
# Cela permet d'exécuter le script depuis n'importe quel répertoire.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$DIR/../../.."

cd $ROOT
BUNDLES="$ROOT/tests/bundles"

# Variables à modifier selon la cible sur laquelle les tests seront exécutés
# afin d'évaluer les résultats
PC_HIGH_MEM=false
PC_LOW_MEM=true
RASPBERRY=false

# Variable pour vérfier la consommation de CPU et/ou mémoire, nombre de threads créés,
# lors de la validation de tests.
VERIFY_RESOURCES=false

TIMEOUT=10

TMP_DIR="tmp"
OUTPUT="$TMP_DIR/output"
NB_CORE=`grep "^core id" /proc/cpuinfo | sort -u | wc -l`

# Vérifier si le nombre de core est 0
if [ $NB_CORE -eq 0 ]; then
    NB_CORE=1
fi

# Variables pour les statistiques
NB_VULNERABLE=0
NB_INVULNERABLE=0
NB_TODO=0
NB_IGNORED=0

# Initialisation du dossier tmp
if [ ! -d $TMP_DIR ]; then
    mkdir $TMP_DIR
    cd $TMP_DIR
    ln -s $DIR/../com.sogetiht.otb.properties.cfg com.sogetiht.otb.properties.cfg
    cd $ROOT
else
    rm -r $TMP_DIR
    mkdir $TMP_DIR
    cd $TMP_DIR
    ln -s $DIR/../com.sogetiht.otb.properties.cfg com.sogetiht.otb.properties.cfg
    cd $ROOT
fi
rm -rf $OUTPUT
mkdir -p $OUTPUT
i=0

# Fonction permettant de vérifier si un string ($1) est bien présente
# dans les println exécutés par le framework
contains() {
    if ! grep -Fq "$1" $OUTPUT/sysatt${list[i]}; then
        echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
        addInvulnerable
        continue
    fi
}

# Fonction permettant de vérifier l'utilisation du CPU, Mémoire et du
# nombre de thread
threshold() {
    case $2 in
        1)
            FILE="$OUTPUT/sysatt${list[i]}.cpu"
            ;;
        2)
            FILE="$OUTPUT/sysatt${list[i]}.mem"
            ;;
        3)
            FILE="$OUTPUT/sysatt${list[i]}.thread"
            ;;
    esac

    if [ `cat $FILE` -le $1 ]; then
        echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
        addInvulnerable
        continue
    fi
}

# Fonction pour incrémenter le nombre de "Passed bundles"
addVulnerable() {
    NB_VULNERABLE=$(($NB_VULNERABLE + 1))
}

# Fonction pour incrémenter le nombre de "Failed bundles"
addInvulnerable() {
    NB_INVULNERABLE=$(($NB_INVULNERABLE + 1))
}

# Fonction pour incrémenter le nombre de "Todo bundles"
addTodo() {
    NB_TODO=$(($NB_TODO + 1))
}

# Fonction pour incrémenter le nombre de "Ignored bundles"
addIgnored() {
    NB_IGNORED=$(($NB_IGNORED + 1))
}


# Fonction permetttant de lancer le test sur le framework OSGi
runTest() {

    # Exécution du bundle
    $DIR/runBundleWithFelix.sh $1 &> $OUTPUT/sysatt$1.stdout &
    PID=$!
    CPT_TIME=0
    ABORTED=0

    # Initialise le timeout
    case $1 in
        100|110|135|146|155|170|190)
            if $PC_HIGH_MEM || $PC_LOW_MEM; then
                MAX_TIME=$TIMEOUT
            elif $RASPBERRY; then
                MAX_TIME=15
            fi
            ;;
        025|120)
            if $PC_HIGH_MEM || $PC_LOW_MEM; then
                MAX_TIME=60
            elif $RASPBERRY; then
                MAX_TIME=15
            fi
            ;;
        171)
            if $PC_HIGH_MEM || $PC_LOW_MEM; then
                MAX_TIME=60
            elif $RASPBERRY; then
                MAX_TIME=40
            fi
            ;;
        173|175)
            if $PC_HIGH_MEM || $PC_LOW_MEM; then
                MAX_TIME=100
            elif $RASPBERRY; then
                MAX_TIME=15
            fi
            ;;
        187)
            MAX_TIME=90
            ;;
        201|261)
            if $PC_HIGH_MEM || $PC_LOW_MEM; then
                MAX_TIME=$TIMEOUT
            elif $RASPBERRY; then
                MAX_TIME=30
            fi
            ;;
        220|221)
            if $PC_HIGH_MEM || $PC_LOW_MEM; then
                MAX_TIME=60
            elif $RASPBERRY; then
                MAX_TIME=10
            fi
            ;;
        230)
            if $PC_HIGH_MEM; then
                MAX_TIME=600
            elif $PC_LOW_MEM; then
                MAX_TIME=60
            elif $RASPBERRY; then
                MAX_TIME=15
            fi
            ;;
        *)
            MAX_TIME=$TIMEOUT
            ;;
    esac

    # Mise en place d'un timeout
    while ps | grep -q $PID; do

    # Si le processus est toujours exécuté
    # et que le temps d'exécution est supérieur
    # au timeout, le processus est tué
	if [ $CPT_TIME -eq $MAX_TIME ]; then
        # Récupération du PID du framework OSGi
            JAVA_PID=$(pgrep -P $PID java)

            if $VERIFY_RESOURCES; then

	        # Enregistre le nombre de threads utilisés dans un fichier
        	echo $(ps hH -fp $JAVA_PID | wc -l) > $OUTPUT/sysatt$1.thread
       		# Enregistre le pourcentage de CPU utilisé dans un fichier
        	if [ $NB_CORE -eq 1 ]; then
            	    echo $(($(ps uh -p $JAVA_PID | awk {'print $3*100'})/100)) > $OUTPUT/sysatt$1.cpu
        	else
            	    echo $(($(ps uh -p $JAVA_PID | awk {'print $3*100'})/($NB_CORE*100))) > $OUTPUT/sysatt$1.cpu
        	fi
        	# Enregistre le pourcentage de mémoire utilisé dans un fichier
        	if [ $NB_CORE -eq 1 ]; then
            	    echo $(($(ps uh -p $JAVA_PID | awk {'print $4*100'})/100)) > $OUTPUT/sysatt$1.mem
        	else
            	    echo $(($(ps uh -p $JAVA_PID | awk {'print $4*100'})/($NB_CORE*100))) > $OUTPUT/sysatt$1.mem
        	fi

            fi

        # Tue le processus et ses fils
            pkill -9 -P $PID
            ABORTED=1
	fi
	sleep 1
	CPT_TIME=$(($CPT_TIME + 1))
    done
}

runUtil() {
    if [ "$VERBOSE" == "true" ]; then
	echo -n -e "TEST SERVER    ............. RUNNING \n"
    fi
    # Exécution du bundle util = 000
    $DIR/runBundleWithFelix.sh 000 &> $OUTPUT/util.stdout &
    UTIL_PID=$!
    sleep 8
}

doPing() {
    if [ "$VERBOSE" == "true" ]; then
	echo -n -e "TEST CONNEXION ............. RUNNING \n"
    fi
    ping $1 &> $OUTPUT/ping.stdout &
    PING_PID=$!
    sleep 1
}


#
# Main
#

echo "------------------------------------------------------"
echo " __   __   ___         ___       ___     __   __      "
echo "/  \ |__) |__  |\ |     |  |__| |__     |__) /  \ \_/ "
echo "\__/ |    |___ | \|     |  |  | |___    |__) \__/ / \ "
echo ""
echo "------------------------------------------------------"
echo "                                      SOGETI HIGH TECH"
echo "                                www.sogeti-hightech.fr"
echo ""
echo ""

if ! grep -Fxq "test=true" $DIR/../com.sogetiht.otb.properties.cfg; then
    echo    "ERROR"
    echo    "  Test mode is not activated in the configuration"
    echo    "  file 'com.sogetiht.otb.properties.cfg'."
    echo -e "  Please set 'test=true'\n"
    exit 1
fi

# vérifier si la variable timeout a été affectée (affectation basée sur la définition du target).
if ${PC_HIGH_MEM} || ${PC_LOW_MEM} || ${RASPBERRY}; then
    :
else
    echo    "ERROR"
    echo    "  Target for execution of tests not specified."
    echo -e "  Please set it into the runAutomatedTests.sh.\n"
    exit 1
fi

# récupérer l'adresse IP spécifiée dans le fichier de configuration
SERVER_IP_LINE=$(awk '/server.ip=/{ print; exit }' $DIR/../com.sogetiht.otb.properties.cfg)
SERVER_IP=${SERVER_IP_LINE:10:15}

# faire un ping vers l'adresse spécifiée dans le fichier de configuration
doPing $SERVER_IP

# si ping
if grep -Fq "64 bytes from $SERVER_IP" $OUTPUT/ping.stdout; then
    if [ "$VERBOSE" == "true" ]; then
	echo -n -e " TEST CONNEXION ............. OK \n"
    fi
	# verifier que le serveur est lancé en utilisant le bundle util
    runUtil

    if grep -Fq "java.lang.Exception: An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running." $OUTPUT/util.stdout; then
        echo    "ERROR"
        echo    "  An error has occurred while establishing a connection"
        echo    "  to the server. The server is not running on the"
        echo    "  specified IP address: '$SERVER_IP'. Please verify"
        echo    "  that this IP address is correct and start the server."
        echo    "  If the IP address is not correct, change it by using"
        echo -e "  the command: setServerIP 'server_IP_address'.\n"
        #pkill -9 -P $PING_PID
        pkill -9 -P $UTIL_PID
        exit 1
    else
	if [ "$VERBOSE" == "true" ]; then
	    echo -n -e " TEST SERVER    ............. OK \n"
	fi
        echo -e " The server is running on $SERVER_IP\n"
    fi
# si no ping
else
    echo    "ERROR"
    echo    "  An error has occurred while establishing a connection"
    echo    "  to the server. The server host '$SERVER_IP' is "
    echo    "  unreachable. Please, check your network connection,"
    echo    "  or set the correct server IP address by using the "
    echo -e "  command: setServerIP 'server_IP_address'.\n"
    #pkill -9 -P $PING_PID
    exit 1
fi

#pkill -9 -P $PING_PID
pkill -9 -P $UTIL_PID

# Temps de démmarage des tests
START_TIME=`date +%s`

echo -e " Test Execution:\n"
# Si option -b activée
# 
for bundlePath in $BUNDLES/com.sogetiht.otb.sysatt*.jar
do
    bundle=`basename $bundlePath`
    bundle=${bundle:23:3}

    if [ ${#bundlePath} -ne 1 ] && [ "$bundle" != "$PREVIOUS" ]; then

        # La commande ci-dessous permet d'exclure des tests parmi tous les bundles existants jusqu'au présent
        # liste de tous les bundles
        #[[ $bundle =~ ^010|025|050|055|060|080|085|090|100|110|115|120|135|136|137|138|139|140|145|146|150|155|156|160|161|162|163|164|165|170|171|172|173|174|175|176|177|178|179|185|186|187|190|195|199|200|201|202|203|205|206|207|208|210|218|220|221|225|226|230|235|240|245|250|260|261|265|275|290|300|301|302|303|304$ ]] && continue
	[[ $bundle =~ ^221$ ]] && continue #fait planter l'ordi

        # Exécution des nouveaux bundles (implémentés par Diana)
        #[[ $bundle =~ ^010|025|050|055|060|080|085|090|100|110|115|120|135|136|137|138|139|140|145|146|150|155|156|160|161|162|163|164|170|171|172|173|174|175|177|190|195|199|200|201|202|203|205|206|210|220|221|230|235|240|250|260|261|265|275|290$ ]] && continue

        # Exécution des bundles livraison IS2T v1.0 - janvier 2014
        #[[ $bundle =~ ^010|025|050|055|060|080|085|090|136|137|138|139|156|160|161|162|163|164|165|176|178|179|185|186|187|199|207|208|218|225|226|235|240|245|250|265|275|290|300|301|302|303|304|305$ ]] && continue

	if [ "$VERBOSE" == "true" ]; then
	    echo -e "  SYS-ATT-$bundle"
	    case $bundle in
		010)
		    echo  "Construction et envoi de paquets : PING(TCP), TCP et UDP"
		    ;;
		025)
		    echo  "Ce bundle identifie les ports en service par active discovery, en balayant l'ensemble d'un sous-réseau local."
		    ;;
		060)
		    echo  "Ce bundle ouvre tous les ports de la machine."
		    ;;
		080)
		    echo  "Cette attaque consiste à modifier le fichier .etc.hosts de la plateforme de manière à usurper l'identité d'entités critiques (PFG)."
		    ;;
		100)
		    echo  "Envoi à un serveur distant de l'ensemble des propriétés système, des propriétés de la JVM et le détail des bundles et registres installés."
		    ;;
		110)
		    echo  "Envoi à un serveur distant des fichiers de log système."
		    ;;
		115)
		    echo  "Envoi à un serveur distant des fichiers linux passwd et shadow."
		    ;;
		120)
		    echo  "Parcours de l'arborescence fichier et renvoie à un serveur distant de fichiers."
		    ;;
		135)
		    echo  "Exécution de code natif avec l'utilisation de JNA(Java Native Access)."
		    ;;
		136)
		    echo  "Exécution de code natif avec l'utilisation de JNI(Java Native Interface)."
		    ;;
		138)
		    echo  "Utilisation de la méthode stop(à du bundle 0."
		    ;;
		139)
		    echo  "Utilisation de la méthode java.lang.Runtime.getRuntime().halt(0)."
		    ;;
		140)
		    echo  "Création de liens symboliques pointant sur des repertoires ou fichiers sensibles (racine, binaire et librairies)."
		    ;;
		145)
		    echo  "Manipulation de fichiers de configuration système."
		    ;;
		146)
		    echo  "Manipulation de fichiers de configuration système en redirigeant la sortie system.out.println dans un fichier."
		    ;;
		150)
		    echo  "Envoie sur un serveur distant des données relatives à l'exécution des bundles sur la plateforme en s'abonnat à tous les services."
		    ;;
		155)
		    echo  "Envoi sur un serveur distant de tous les bundles installées sur le framework."
		    ;;
		160)
		    echo  "Header invalide dans le fichier MANIFEST.MF."
		    ;;
		162)
		    echo  "Valeur inappropriée dans le fichier MANIFEST.MF."
		    ;;
		164)
		    echo  "Epuisement des ressources par import excessif de bibliothèques natives."
		    ;;
		165)
		    echo  "Empêcher l'exécution d'un bundle par usurpation de son SymbolicName et Version."
		    ;;
		170)
		    echo  "Envoi depuis un serveur distant de bundles malicieux."
		    ;;
		171)
		    echo  "N bundles souscrivent à des services dépendant eux-mêmes de services sous-jacents, de façon récursive. Si A,B, ... N-1, N sont des bundles, ceux-ci souscrivent respectivement aux services b (du bundle B), c, ... n."
		    ;;
		172)
		    echo  "L'exécution de ce bundle provoque l'épuisement des ressources lors de la phase de décompression du fichier JAR (création d'un bundle aux dimension disproportionnées)."
		    ;;
		173)
		    echo  "Ce bundle monopolise inutilement les ressources systèmes par des calculs à l'horizon inatteignable."
		    ;;
		174)
		    echo  "Déclaration d'un nombre important de services."
		    ;;
		175)
		    echo  "Ces deux bundles monopolisent des ressources par la souscription mutuelle de services entre eux."
		    ;;
		176)
		    echo  "Provocation de l'erreur StackOverflow dans un bundle tiers par souscritpion de services mutuellement dépendant. Le service du bundle tiers appelle le service malicieux quin au lieu de s'exécuter normalement, appelle le service du bundle tiers."
		    ;;
		177)
		    echo  "Le bundle souscrit à un service qu'il fournit lui-même. Un changement d'état produit un nouveau changement d'état, et ainsi de suiten dans un schéma de boucle infinie."
		    ;;
		178)
		    echo  "blocage de l'exécution du framework par une boucle infinie dans le Bundle-Activator (méthode start)."
		    ;;
		179)
		    echo  "Blocage de l'exécution du framework par un \"Thread-Hanging\" dans le Bundle-Activator (méthode start)."
		    ;;
		185)
		    echo  "Epuisement des ressources par implémentation d'une boucle infinie dans un thread."
		    ;;
		186)
		    echo  "Epuisement des ressources par implémentation d'une boucle infinie dans une méthode du bundle (méthode start du Bundle-Activator)."
		    ;;
		187)
		    echo  "Epuisement des ressources par lancement d'un nombre important de bundle sur le framework."
		    ;;
		190)
		    echo  "Ce bundle crée des fichiers encombrants sur le système (=Zombie data)."
		    ;;
		195)
		    echo  "Ce bundle alloue de la mémoire et appelle périodiquement le Garbage Collector pour le libérer."
		    ;;
		199)
		    echo  "Ce bundle déclare et alloue des tableaux avec un grand nombre de cases en attribut de la classe Activator."
		    ;;
		200)
		    echo  "Ce bundle alloue des objets en cascade."
		    ;;
		201)
		    echo  "Ce bundle crée un infinité de fichiers et de dossiers sur la plateforme."
		    ;;
		202)
		    echo  "Création d'une multitude d'objets en mémoire."
		    ;;
		203)
		    echo  "Allocation de toute la mémoire vive."
		    ;;
		205)
		    echo  "Ce bundle crée un \"lock\" sur un fichier donnée et ne fera jamais de \"unlock\". Ce fichier n'est donc pas accessible à un autre bundle."
		    ;;
		206)
		    echo  "Ce bundle implémente des deadlocks dans les services classiques (normalisé par l'OSGi alliance)."
		    ;;
		207)
		    echo  "Création d'une multitude de bundles."
		    ;;
		208) 
		    echo  "Création d'une multitude de bundles par multuiplication."
		    ;;
		210)
		    echo  "Ce bundle exploite une race condition."
		    ;;
		218)
		    echo  "Accès à un package protégé d'un bundle tiers par \"split package\" : ce bundle exporte un package ayant le même nom que le package protégé du bundle tiers."
		    ;;
		220)
		    echo  "Génération et exécution simultanée d'une multitude de threads."
		    ;;
		221)
		    echo  "Ce bundle monopolise des ressources par la déclaration de threads dormants."
		    ;;
		225) 
		    echo  "Modification de l'état des bundles par utilisation des méthodes du bundleContext (start, stop, uninstall)."
		    ;;
		226)
		    echo  "Exécution permanente du bundle par appel de la méthode start dans la méthode stop."
		    ;;
		230) 
		    echo  "Injection de données aléatoires dans les services."
		    ;;
		245)
		    echo  "Injection de données forgées : valeurs non autorisées, dépassant les bornes imposées par l'API."
		    ;;
		250)
		    echo  "Ce bundle récupère la classe Java depuis un serveur distant et la charge en mémoire."
		    ;;
		260) 
		    echo  "Inclusion du framework dans un bundle et copie de l'ensemble des bundles du framework."
		    ;;
		261)
		    echo  "Inclusion du framework dansu n bundle et lancement du bundle dans ce nouveau framework (boucle infinie)."
		    ;;
		275)
		    echo  "Envoie à un serveur distant des fichiers temporaires disponibles sur la plateforme."
		    ;;
		300)
		    echo  "Crash de la JVM en utilisant JNI (compilation de la librairie native, correspondante à l'OS et processeur hôtes, effectuée lors de la compilation du bundle."
		    ;;
		301)
		    echo  "Crash de la JVM en utilisant JNI (librairies natives incluses dans le bundle pour différents OS et processeurs)."
		    ;;
		302)
		    echo  "Crash de la JVM en utilisant JNA."
		    ;;
		303)
		    echo  "Crash de la JVM en utilisant sun.misc.Unsafe;"
		    ;;
		304)
		    echo  "Crash de la JVM par des bogues connus."
		    ;;
	    esac
	    
	    echo -n -e "   RUNNING"
        # Exécution du test
            runTest $bundle
            if [ $bundle -eq 190 ]; then
		runTest $bundle
            fi
	    
            PREVIOUS=$bundle
            list[i]=$bundle
            i=$((i+1))
            if [ $ABORTED -eq 0 ]; then
		echo -n -e "\r   FINISHED\n\n"
            else
		echo -n -e "\r   FINISHED (killed)  \n\n"
		ABORTED=0
            fi
	    
	else
	    
	    echo -n -e "  SYS-ATT-$bundle ................................ RUNNING"
        # Exécution du test
            runTest $bundle
            if [ $bundle -eq 190 ]; then
		runTest $bundle
            fi
	    
            PREVIOUS=$bundle
            list[i]=$bundle
            i=$((i+1))
            if [ $ABORTED -eq 0 ]; then
		echo -n -e "\r  SYS-ATT-$bundle ................................ FINISHED\n"
            else
		echo -n -e "\r  SYS-ATT-$bundle ................................ FINISHED (killed)  \n"
		ABORTED=0
            fi
	fi
    fi
done

# Nombre de répertoires dans .openthebox créés par le bundle 201
if [ -d ~/.openthebox ]; then
    NB_DIR=`find ~/.openthebox -type d | wc -l`
fi

echo -e "\n Test Report:\n"

cnt=${#list[@]}
for ((i=0;i<cnt;i++)); do

    # Test si les fichiers de sorties existent
    if [ ! -f $OUTPUT/sysatt${list[i]}.stdout ] || [ ! -f $OUTPUT/sysatt${list[i]} ]; then
        [[ ! ${list[i]} =~ ^146|160|161|162|164|170|172|187|199|203|206$ ]] && echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE" && addInvulnerable && continue
    fi

    # Test si le fichier sysattXXX.stdout contient du texte
    if [ -s $OUTPUT/sysatt${list[i]}.stdout ];then
        case "${list[i]}" in
            025)
                if ! grep -Fq "::PROBLEM::" $OUTPUT/sysatt025.stdout; then
                    echo "  SYS-ATT-025 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            135)
                if ! grep -Fq "printf: Hello, World" $OUTPUT/sysatt135.stdout; then
                    echo "  SYS-ATT-135 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            136)
                if ! grep -Fq "# A fatal error has been detected by the Java Runtime Environment:" $OUTPUT/sysatt136.stdout; then
                    echo "  SYS-ATT-136 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            146)
                if ! grep -Fq "Ecriture dans le fichier $HOME/.bashrc" $OUTPUT/sysatt146.stdout; then
                    echo "  SYS-ATT-146 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            165)
                if ! grep -Fq "Hello World!" $OUTPUT/sysatt165.stdout; then
                    echo "  SYS-ATT-165 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            170)
                if ! grep -Fq "Injected Bundle: it works" $OUTPUT/sysatt170.stdout; then
                    echo "  SYS-ATT-170 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            172)
                if ! grep -Fq "java.lang.OutOfMemoryError: Java heap space" $OUTPUT/sysatt172.stdout; then
                    echo "  SYS-ATT-172 ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            110|150|171|173|174|175|176|177|178|179|185|186|187|195|200|201|202|203|205|206|207|208|220|221|261|275)
                # Doit être ajusté suivant l'OS
                if ! egrep -q 'Killed|Processus arrêté' $OUTPUT/sysatt${list[i]}.stdout; then
                    echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            160|161|162|164|199|230|245)
                if ! grep -Fq "" $OUTPUT/sysatt${list[i]}.stdout; then
                    echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            218)
                if ! grep -Fq "[Class TestB - Protected Package] In method test" $OUTPUT/sysatt${list[i]}.stdout; then
                    echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            300|301|302|303|304)
                if ! grep -Fq "A fatal error has been detected by the Java Runtime Environment" $OUTPUT/sysatt${list[i]}.stdout; then
                    echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                    addInvulnerable
                    continue
                fi
                ;;
            *)
                # Un bundle ne doit normalement rien avoir d'écrit dans stdout
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
                ;;
        esac
    fi

    # Test si le fichier sysattXXX contient du texte
    if [ -s $OUTPUT/sysatt${list[i]} ];then
        # Test si le fichier sysattXXX contient la string "::ERROR::". Cela
        # indique que le bundle a levé un exception
        if grep -Fq "::ERROR::" $OUTPUT/sysatt${list[i]}; then
            echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
            addInvulnerable
            continue
        fi
    else
        [[ ! ${list[i]} =~ ^146|160|161|162|164|170|172|185|186|187|199|203|206|208|300|301|302|303|304$ ]] && echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE" && addInvulnerable && continue
    fi


    # Valide le test en cherchant des string spécifiques dans les fichiers
    # sysattXXX, sysattXXX.mem, sysattXXX.cpu et sysattXXX.thread
    case "${list[i]}" in
        025)
            if ! grep -Fq "open" $OUTPUT/sysatt025; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        060)
            contains "Opening port"
            contains "OK"
            ;;
        080)
            more /etc/hosts > $OUTPUT/sysatt080.host
            if ! grep -Fq "PFG6.6.6.6" $OUTPUT/sysatt080.host; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        100)
            contains "Bundle-Name"
            ;;
        110)
            contains "successfully sent!"
            ;;
        115)
            if ! egrep -q 'passwd : successfully sent|shadow : successfully sent' $OUTPUT/sysatt115; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        120)
            contains "shadow : successfully sent"
            ;;
        135)
            contains "Calling C native code from Java"
            ;;
        136)
            contains "Calling C native code"
            ;;
        138|139)
            contains "The framework is shutting down"
            if [ -f $OUTPUT/sysatt${list[i]}.cpu ]; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        140)
            contains "Creation of a symlink to"
            ;;
        145)
            if ! egrep -q 'CAN BE MODIFIED *****|CAN BE CREATED *****' $OUTPUT/sysatt145; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        146)
            if [ -s $OUTPUT/sysatt${list[i]} ]; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        150)
            if ! egrep -q 'Event MODIFIED detected from com.sogetiht.otb.sysatt150.com.sogetiht.otb.sysatt150.bundle2' $OUTPUT/sysatt150; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        155)
            contains "successfully sent!"
            ;;
        160|161|162|164)
            if [ -f $OUTPUT/sysatt${list[i]}.cpu ]; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        165)
            if ! egrep -q 'Usurper bundle created, installed and started successfully' $OUTPUT/sysatt165; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        170)
            ;;
        171)
            contains "servicenumber : 13"
            ;;
        172)
            ;;
        173)
            if $PC_HIGH_MEM; then
                contains "2581364975862300585532124873915403603275335812450126405357504267461260533031799733949538538107820239745453202432994216552807405873080696870236414935353549155675437156303046005077161645887107657764740435734465761841445450858918789730796141822787225293389709709788904854085453068910736329752534151913804676428321412694581211281514855853019464071416761930712599661380303292500146732667735442403077277044399987001776679299458247923015236026378880588825316496922866889441278478860953334417121389774762136121393912597923025402143766474927279082649021145035315660551597453701684384574143645081638094987211130179352149393948833281286909602734414671772414806151910877048731216605619807327537737931204829020546683133369388600145729894216185455100680406176596122565006259071116188171561541366838391982456603565526858223757894946968701589581162874001057520002040080637175704446649714534104450295517771620538745118103009067946340767819487410294355744748869872242936895487392936675203015715820367075787641770981373728431317699610402293244759358664327528693031901557074266356727883275615852747753313050483944391077742575482648832163840558056129857956109274508022998909150859926017951115677404483151370099617182727285355062468829502108101672151677033180089246949889546722412163244905833297161105113007637626982787736863407959463558932443032703927574217750542428006256345993577905553121124440744142284529902284485601623152685498458695218533260438663662401634322331833198811844941900082645686983938477553899894414166907043812353566287916923686816920040985102597605248235078603262382409188630293178758124064680873873126315483419776269526416075447955593691471885412739460952953583677013779481638559505843476846417221680058731598438997499851134919748160025467756178967821884766368576746262619015743686339998748734453445158572047794761487693888398308222004382326388322305470691518523122396600382204883533243211547343836763145097719601430378816504669784801299054444961326138416216829716012017761204592043975200787527505623340630166084562351264048531601881347140619931836142147306843782850609331507030283480121300276586537227208229864131659385843603315803945525034943166357954466327185792276463821006429439151396309885313207258358268131602332124095218242349795217226482709676161592305180055235719579032722554334852435625541373556723691118473724956908144317150954780025710727349294299170699306014811559204449970506700927740220301496355795704340940943785391928804137690829791221679926312568473498946272221381109385838268810294641283627147633149481588821187516021791712864205984780706074871470233870143335162232619290191923546784220110590166721266055116982814036391232727606277766650328204290098229222378328496015865258471520145761259522685923953681056074359016199573802209588387501254311251257405643007638122286743247426841696394262887140867069981633759967028286704531708375864278880858322049655702021573074527594884732682265770933467601063968451812797318784163780168999379435244240831935671612023071236"
                if $VERIFY_RESOURCES; then
                    threshold 15 1
                fi
            elif $PC_LOW_MEM; then
                contains "258080348888515099592332164484462756339873138465439573430307783197416299243027155954177662782377988121786330241704205895696623863512136060073312399642903214981208677314332718179153141978050966912949044667285654509382893505742607320421138132489315975231024810455007561169027261467512016768219520949535523546245854587488300301587183831771306983230117702008541840588049868016262367530942050459679189959463016758263347175105682443459003758959420242420726446706871468610625180427774309822202013596386312335770257465620916795856541100240403653418300558044035800114916334162467899218825148943236293534338393691010564310549992850783040465739751374426786747138626367579170192142097000349188282966355601543364983866957807309071157224340301273138290328107224394242829508399720499754226069171146720700234961415691287653024650299449906791576444251896485737971148164303644519711822671071868913869053620498919344969212998198225423025504218155127792386786458610552302642301073848826614379519325627718818344197310234412241124626537898818059135881858620937671892189773976665326097756916365777800910711964674956323807152975316097362834398899617301975133152609071733317184179899830467924960162542548165522288593680830726827471269664435668435452031231830173305890851437975594409695453191272723171217148965163615764918779939588269561484233990603264734628306313494325446968691199021480680030775923198882305274863248464494464792535591126770493037747746544723130922945086462793250633576961329623263968229991681219729167736126795908539581595379671619786936503310162538854374487828471187674691932819466467181689212704982380869534574016124715756846188037238391662694608404480000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            elif $RASPBERRY; then
                contains "788657867364790503552363213932185062295135977687173263294742533244359449963403342920304284011984623904177212138919638830257642790242637105061926624952829931113462857270763317237396988943922445621451664240254033291864131227428294853277524242407573903240321257405579568660226031904170324062351700858796178922222789623703897374720000000000000000000000000000000000000000000000000"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            fi
            ;;
        174)
            if $PC_HIGH_MEM; then
                contains "Services créés : 80000"
                if $VERIFY_RESOURCES; then
                    threshold 15 1
                fi
            elif $PC_LOW_MEM; then
                contains "Services créés : 35000"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            elif $RASPBERRY; then
                contains "Services créés : 1000"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            fi
            ;;
        175)
            if $PC_HIGH_MEM; then
                contains "Bundle2: Service of com.sogetiht.otb.sysatt175.com.sogetiht.otb.sysatt175.bundle1 changed (New key = 10000)"
                if $VERIFY_RESOURCES; then
                    threshold 15 1
                fi
            elif $PC_LOW_MEM; then
                contains "Bundle2: Service of com.sogetiht.otb.sysatt175.com.sogetiht.otb.sysatt175.bundle1 changed (New key = 2000)"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            elif $RASPBERRY; then
                contains "Bundle2: Service of com.sogetiht.otb.sysatt175.com.sogetiht.otb.sysatt175.bundle1 changed (New key = 100)"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            fi
            ;;
        176)
            contains "StackOverflowError in Hello Dispatcher service"
            ;;
        177)
            if $PC_HIGH_MEM; then
                contains "Event no. 40000: service com.sogetiht.otb.sysatt177 changed"
                if $VERIFY_RESOURCES; then
                    threshold 15 1
                fi
            elif $PC_LOW_MEM; then
                contains "Event no. 14500: service com.sogetiht.otb.sysatt177 changed"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            elif $RASPBERRY; then
                contains "Event no. 1000: service com.sogetiht.otb.sysatt177 changed"
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            fi
            ;;
        178|179)
            contains "Blocking Framework"
            ;;
        185|186|187)
            ;;
        190)
            contains "Zombie Data still there. Did you try to uninstall and reinstall the bundle ?"
            ;;
        195)
            contains "Excessive use of the GC. Check the system monitor"
            if $PC_HIGH_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 20 1
                fi
            elif $PC_LOW_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            elif $RASPBERRY; then
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            fi
            ;;
        199)
            if [ -f $OUTPUT/sysatt${list[i]} ]; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
                continue
            fi
            ;;
        200)
            contains "Allocation en cours..."
            if $PC_HIGH_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 25 1
                fi
            elif $PC_LOW_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            elif $RASPBERRY; then
                if $VERIFY_RESOURCES; then
                    threshold 5 1
                fi
            fi
            ;;
        201)
            if [ $NB_DIR -le 10000 ]; then
                echo "  SYS-ATT-${list[i]} ................................ INVULNERABLE"
                addInvulnerable
            fi
            ;;
        202)
            contains "Please wait allocation space"
            if $PC_HIGH_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 19 2
                fi
            elif $PC_LOW_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 10 2
                fi
            elif $RASPBERRY; then
                if $VERIFY_RESOURCES; then
                    threshold 2 2
                fi
            fi
            ;;
        203)
            ;;
        205)
            contains "The file (tmp) is locked. Next try in 2s."
            ;;
        206)
            contains "Calling log(int, java.lang.String) from LogService"
            ;;
        207)
            contains "usedMemory"
            ;;
        208)
            ;;
        210)
            contains "End writting in the file..."
            ;;
        218)
            contains "Method in protected package invoked successfully"
            ;;
        220)
            contains "Thread no. 150: OK"
            if $PC_HIGH_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 85 1
                    threshold 150 3
                fi
            elif $PC_LOW_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 30 1
                    threshold 150 3
                fi
            elif $RASPBERRY; then
                if $VERIFY_RESOURCES; then
                    threshold 30 1
                    threshold 10 3
                fi
            fi
            ;;
        221)
            if $PC_HIGH_MEM; then
                contains "Thread no. 10500 is sleeping"
            elif $PC_LOW_MEM; then
                contains "Thread no. 6500 is sleeping"
            elif $RASPBERRY; then
                contains "Thread no. 500 is sleeping"
            fi
            ;;
        225)
            if ! grep -Fq "uninstalled successfully" $OUTPUT/sysatt225; then
                echo "  SYS-ATT-225 ................................ INVULNERABLE"
                continue
            fi
            ;;
        230)
            contains "Method ->"
            contains "Types ->"
            contains "Fuzzing in progress..."
            ;;
        245)
            contains "Fuzzing in progress..."
            ;;
        260)
            contains "Embedded framework started correctly"
            ;;
        261)
            contains "Starting new instances of the framework in an infinite loop"
            if $PC_HIGH_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 3 2
                    threshold 175 3
                fi
            elif $PC_LOW_MEM; then
                if $VERIFY_RESOURCES; then
                    threshold 2 2
                    threshold 30 3
                fi
            elif $RASPBERRY; then
                if $VERIFY_RESOURCES; then
                    threshold 3 2
                    threshold 10 3
                fi
            fi
            ;;
        275)
            contains "successfully sent!"
            ;;
        300|301|302|303|304)
            ;;
        *)
            echo "  SYS-ATT-${list[i]} ................................ TODO"
            addTodo
            continue
            ;;
    esac

    echo "  SYS-ATT-${list[i]} ................................ VULNERABLE"
    addVulnerable

done

END_TIME=`date +%s`
ELAPSED_TIME=$(($END_TIME-$START_TIME))

if [ ! -d logs ]; then
    mkdir logs
fi

cd logs
LOG_TEMP=all_`date +%d-%m-%Y_%H:%M`
mkdir $LOG_TEMP
cd $ROOT
mv $OUTPUT/* $LOG_TEMP

echo -e "\n"
echo -e "------------------------------------------------------"
echo -e " Test Statistics:\n"
echo    "  No. BUNDLES:  $cnt"
if [ $cnt -gt 0 ]; then
    echo    "  VULNERABLE:   $(($NB_VULNERABLE*100/$cnt))%"
    echo -e "  INVULNERABLE: $(($NB_INVULNERABLE*100/$cnt))%"
fi
if [ $(($ELAPSED_TIME/60)) -gt 0 ]; then
    echo -e "\n  Total Time:   $(($ELAPSED_TIME/60))min. $(($ELAPSED_TIME%60))s."
else
    echo -e "\n  Total Time:   $(($ELAPSED_TIME%60))s."
fi
echo -e "------------------------------------------------------\n"