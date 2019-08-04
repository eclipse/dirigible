## Docker build:

1. **dirigible-base-platform-runtime**
    ```
    cd ../server-runtime
    docker build -t dirigible-base-platform-runtime -f Dockerfile-base .
    docker tag dirigible-base-platform-runtime dirigiblelabs/dirigible-base-platform-runtime
    docker push dirigiblelabs/dirigible-base-platform-runtime
    cd ../server-runtime-keycloak
    ```

1. **dirigible-base-platform-runtime-keycloak**
    ```
    docker build -t dirigible-base-platform-runtime-keycloak -f Dockerfile-base .
    docker tag dirigible-base-platform-runtime-keycloak dirigiblelabs/dirigible-base-platform-runtime-keycloak
    docker push dirigiblelabs/dirigible-base-platform-runtime-keycloak
    ```

1. **dirigible-runtime-keycloak**
    ```
    docker build -t dirigible-runtime-keycloak .
    docker tag dirigible-runtime-keycloak dirigiblelabs/dirigible-runtime-keycloak
    docker push dirigiblelabs/dirigible-runtime-keycloak
    ```