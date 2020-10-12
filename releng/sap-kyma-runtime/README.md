## Docker build:

1. **dirigible-base-platform-sap-kyma-runtime**
    ```
    docker build -t dirigible-base-platform-sap-kyma-runtime -f Dockerfile-base .
    docker tag dirigible-base-platform-sap-kyma-runtime dirigiblelabs/dirigible-base-platform-sap-kyma-runtime
    docker push dirigiblelabs/dirigible-base-platform-sap-kyma-runtime
    ```

1. **dirigible-sap-kyma-runtime**
    ```
    docker build -t dirigible-sap-kyma-runtime .
    docker tag dirigible-sap-kyma-runtime dirigiblelabs/dirigible-sap-kyma-runtime
    docker push dirigiblelabs/dirigible-sap-kyma-runtime
    ```