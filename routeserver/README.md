# what to show:
# 0) using openroute + qwen3-coder + aider ai + vs code + github
# 0.1) application for openstreet map - routing, poi unsing spring-boot + h2 + rest endpoints, swagger 
# 1) aider install / run
# 2) aider commands
# 3) programming
# 3.0) always show gradle build
# 3.1)aider new controller + test + arc42 + plantuml
# 3.2) ->   /ask a new controller "ShowNewRoutes" with an rest  endpoint "getNewRoutes" for a given poi. create tests, update  00_buildingblocks.puml and update 05_building_
# 3.3) aider /ask /code go ahead -> commit nur lokal -> push nach github
# 3.3) aider /ls 
# 4) do junit test with aider
# 5) show network issus -> cloudflare + ssl urls können nicht aufgelöst werden
# 6) start bootrun with aider
# 7) architecture hexagonal architecuret
# 8) free style




# nelliebly 2
# java spring 3.5
# gradle
# openroute qwen3-coder - 10 Euro
# aider ai
# github
# checkstyle spring

# plantuml
# podman run -d -p 8080:8080 plantuml/plantuml-server:jetty
# alt + d 


# install
# export JAVA_HOME=/usr/lib/jvm/jdk-24.0.2/
# export PATH=$PATH;$JAVA_HOME/bin

# echo $PATH >> ~/.bashrc
# echo $JAVA_HOME >> ~/.bashrc

# gradle
# gradle build
# gradle bootrun
# gradle format
# gradle test

# aider ai
# export OPENROUTER_API_KEY=""
# aider --model openrouter/qwen/qwen3-coder
# /ask
# /chat-mode code
# go ahead
# /ask


# swagger
# http://localhost:8090/
# http://localhost:8090/swagger-ui/index.html
# http://localhost:8090/v3/api-docs

# programming
#  /ask a new controller "LastVisitedRouteController" with an rest  endpoint "getLastRoutes" for a given userid. create tests, update  00_buildingblocks.puml 05_building_
> block_view.adoc  

# /test gradle -p routeserver test 

#  network
# watch -n 1 "sudo ss -ntup | grep 'pid=8346'"
# sudo nethogs
# watch "sudo ss -ntup --process | grep 8346"
