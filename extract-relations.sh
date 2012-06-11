GATEHOME=/home/jdb/bin/gate-7.0-build4195-ALL/
REL_EXT_CP=/home/jdb/workspace/RelationAnnotationExtraction/bin
CLASSPATH=$REL_EXT_CP:$GATEHOME/bin/gate.jar:$GATEHOME/lib/*
MAINCLASS=uk.co.jbothma.relations.RelCandidateExtractor
CORPDIR=$1
POPULATEDIR=$2
EXTENTION=$3
STARTMEM=200m
MAXMEM=200m
java -Xms$STARTMEM -Xmx$MAXMEM -XX:+HeapDumpOnOutOfMemoryError -cp $CLASSPATH $MAINCLASS
