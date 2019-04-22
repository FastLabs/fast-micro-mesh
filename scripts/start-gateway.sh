echo 'Starting client gateway'

export CLASSPATH=/Users/oleg/dev/hub/fast-micro-mesh/fast-micro-service-refdata/target/fast-micro-service-refdata-1.0-SNAPSHOT.jar:/Users/oleg/dev/hub/fast-micro-mesh/fast-micro-service/target/fast-micro-service-1.0-SNAPSHOT-fat.jar
echo $CLASSPATH
java   org.flabs.streamer.Main ref-data-consumer
