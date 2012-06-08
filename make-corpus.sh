GATEHOME=/home/jdb/bin/gate-7.0-build4195-ALL/
ONTCP=/home/jdb/workspace/OntologyLearningTool/bin/
CLASSPATH=$ONTCP:$GATEHOME/bin/gate.jar:$GATEHOME/lib/*
MAINCLASS=uk.co.jbothma.olt.CorpusMaker
CORPDIR=/tmp/bigCorp/
POPULATEDIR=/home/jdb/bin/korp_pipeline/corpora/jrc_2006_500/export/
EXTENTION=".xml"
STARTMEM=500m
MAXMEM=3G
java -Xms$STARTMEM -Xmx$MAXMEM -cp $CLASSPATH $MAINCLASS $CORPDIR $POPULATEDIR $EXTENTION
