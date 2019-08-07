## Docker build:

1. **dirigible-base-platform**
    ```
    cd ../server-all
    docker build -t dirigible-base-platform -f Dockerfile-base .
    docker tag dirigible-base-platform dirigiblelabs/dirigible-base-platform
    docker push dirigiblelabs/dirigible-base-platform
    cd ../server-keycloak-all
    ```

1. **dirigible-base-platform-keycloak**
    ```
    docker build -t dirigible-base-platform-keycloak -f Dockerfile-base .
    docker tag dirigible-base-platform-keycloak dirigiblelabs/dirigible-base-platform-keycloak
    docker push dirigiblelabs/dirigible-base-platform-keycloak
    ```

1. **dirigible-keycloak**
    ```
    docker build -t dirigible-keycloak .
    docker tag dirigible-keycloak dirigiblelabs/dirigible-keycloak
    docker push dirigiblelabs/dirigible-keycloak
    ```