rem $env:DOCKER_BUILDKIT=0
rem docker rm -f $(docker ps -aq)
FOR /f %%i IN ('docker ps -aq') DO docker rm -f %%i
FOR /f %%i IN ('docker images -aq') DO docker rmi -f %%i
cd api-gateway
call .\mvnw clean package -DskipTests

cd ../cliente-service
call .\mvnw clean package -DskipTests

cd ../compra-service
call  .\mvnw clean package -DskipTests

cd ../envio-service
call .\mvnw clean package -DskipTests

cd ../inventario-service
call .\mvnw clean package -DskipTests

cd ../producto-service
call .\mvnw clean package -DskipTests

cd ../resena-service
call .\mvnw clean package -DskipTests

cd ../venta-service
call .\mvnw clean package -DskipTests

