First version for micro-service platform

### Main goals

 * Easy to get starting 
 * Event based
 * no other infrastructure

#### Stack
 * platform - vertx.io
 * reactive - rxjava2

### A micro-service ecosystem based on vertx stack
### Why?
 * mature
 * leverages the async and reactive nature of a event based system.
 * high throughput, uses Netty for IO 
 * lightweight and modular
 * well documented and supported
 * pub-sub distributed event bus - with row socket and web socket bridge
 * clustered mode and elements of high availability - no other components are required to be installed for cluster management. Still offering a plugable mechanism 
 * easy to test
 * Simple threading model yet offering a variety ways to scale, starting from a single thread verticles (configuring in multiple instances) and worker verticles for long running computational/blocking tasks 

### Current version supports
 * micro-service building blocks: application template, service template
 * Simple static reference data endpoint for REST support
 * Basic service discovery
 * Service fail-over 
 * first version for the health Web handler
 
### Project structure
 * all projects are modules as part  of {prefix}-micro-pom root project
 * {prefix}-micro-common -  common project to enclose all all shareable features required to run within our micro-service container 
 * {prefix}-micro-service - micro-service project template. Defines the structure of the typical micro-service: Application Launcher, service discovery, health endpoint, Rest Handlers 
 * {prefix}-micro-service-refdata - an example of a service provider. The service will run into a micro-service container will register with service discovery, expose health statistics
 * {prefix}-micro-cluster-manager - placeholder for the cluster management services. At the moment some aspects are implemented as part of {prefix}-micro-service project

### Run locally
  * mvn clean install 
  * scripts/{profile-script}
 
 
### Next steps
 * next level on service module structure and discoverability from classpath?   
 * config management
 * logging
 