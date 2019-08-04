## Docker build:

1. **dirigible-base-platform-trial**
    ```
    docker build -t dirigible-base-platform-trial -f Dockerfile-base .
    docker tag dirigible-base-platform-trial dirigiblelabs/dirigible-base-platform-trial
    docker push dirigiblelabs/dirigible-base-platform-trial
    ```

1. **dirigible-trial**
    ```
    docker build -t dirigible-trial .
    docker tag dirigible-trial dirigiblelabs/dirigible-trial
    docker push dirigiblelabs/dirigible-trial
    ```