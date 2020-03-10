## Docker build:

1. **dirigible-base-platform-sap-cf**
    ```
    docker build -t dirigible-base-platform-sap-cf -f Dockerfile-base .
    docker tag dirigible-base-platform-sap-cf dirigiblelabs/dirigible-base-platform-sap-cf
    docker push dirigiblelabs/dirigible-base-platform-sap-cf
    ```

1. **dirigible-sap-cf**
    ```
    docker build -t dirigible-sap-cf .
    docker tag dirigible-sap-cf dirigiblelabs/dirigible-sap-cf
    docker push dirigiblelabs/dirigible-sap-cf