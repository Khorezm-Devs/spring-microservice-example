## Spring microservice example

This project is the initial version of the mobile banking project of Agrobank (version of just learning time).

## Technology stack

* [Spring Boot](https://spring.io/projects/spring-boot) (uses Spring Framework 5.1) - open source Java-based framework used to create a micro Service
* [Spring Cloud](https://spring.io/projects/spring-cloud) Greenwich - provides tools to quickly build some of the common patterns in distributed systems (e.g. configuration management, service discovery, circuit breakers, intelligent routing, micro-proxy, control bus, one-time tokens, global locks, leadership election, distributed sessions, cluster state)
* [Eureka Server](https://github.com/Netflix/eureka) (from Netflix Stack) - service registration and discovery. All services will register with eureka.
* [Zuul Gateway](https://github.com/Netflix/zuul) (Intelligent Routing from Netflix Stack) - gateway service that provides dynamic routing, monitoring, resiliency, security, and more. All requests to our application pass this central instance. Here you can process the requests in multiple ways.
* [Ribbon Load Balancer](https://github.com/Netflix/ribbon) (from Netflix Stack) - Inter Process Communication (remote procedure calls) library with built in software load balancers. The primary usage model involves REST calls with various serialization scheme support.
* [Zipkin](https://zipkin.io/) – distributed tracing system with request visualization.