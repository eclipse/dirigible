## Docker build:

1. **dirigible-base-platform-runtime**
    ```
    docker build -t dirigible-base-platform-runtime -f Dockerfile-base .
    docker tag dirigible-base-platform-runtime dirigiblelabs/dirigible-base-platform-runtime
    docker push dirigiblelabs/dirigible-base-platform-runtime
    ```

1. **dirigible-runtime**
    ```
    docker build -t dirigible-runtime .
    docker tag dirigible-runtime dirigiblelabs/dirigible-runtime
    docker push dirigiblelabs/dirigible-runtime
    ```