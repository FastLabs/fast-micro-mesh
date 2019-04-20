# user-shell
A clojurescript application backed by a vertx backend. It uses the javascript event bus bridge

## Overview

AWS is the main target but when decoupled with REST api could target any backend


## Development

To get an interactive development environment run:

    lein fig:build
    go to: http://localhost:9500 
 

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

	lein clean

To create a production build run:

	lein clean
	lein fig:min


## from some reason hazelcast warns. Is ok with these jvm params

-Djava.net.preferIPv4Stack=true -> verify if i still need this? I think only required when cluster

--add-modules
java.se
--add-exports
java.base/jdk.internal.ref=ALL-UNNAMED
--add-opens
java.base/java.lang=ALL-UNNAMED
--add-opens
java.base/java.nio=ALL-UNNAMED
--add-opens
java.base/sun.nio.ch=ALL-UNNAMED
--add-opens
java.management/sun.management=ALL-UNNAMED
--add-opens
jdk.management/com.sun.management.internal=ALL-UNNAMED

## License

Copyright © 2019 FastLabs
