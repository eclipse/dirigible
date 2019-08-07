## Docker build:

1. **dirigible-base-platform**
    ```
    docker build -t dirigible-base-platform -f Dockerfile-base .
    docker tag dirigible-base-platform dirigiblelabs/dirigible-base-platform
    docker push dirigiblelabs/dirigible-base-platform
    ```

1. **dirigible-all**
    ```
    docker build -t dirigible-all .
    docker tag dirigible-all dirigiblelabs/dirigible-all
    docker push dirigiblelabs/dirigible-all
    ```