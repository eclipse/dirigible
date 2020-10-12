## Docker build:

1. **dirigible-base-platform-sap-kyma**
    ```
    docker build -t dirigible-base-platform-sap-kyma -f Dockerfile-base .
    docker tag dirigible-base-platform-sap-kyma dirigiblelabs/dirigible-base-platform-sap-kyma
    docker push dirigiblelabs/dirigible-base-platform-sap-kyma
    ```

1. **dirigible-sap-kyma**
    ```
    docker build -t dirigible-sap-kyma .
    docker tag dirigible-sap-kyma dirigiblelabs/dirigible-sap-kyma
    docker push dirigiblelabs/dirigible-sap-kyma
    ```