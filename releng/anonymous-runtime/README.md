## Docker build:

1. **dirigible-base-platform-runtime-anonymous**
    ```
    docker build -t dirigible-base-platform-runtime-anonymous -f Dockerfile-base .
    docker tag dirigible-base-platform-runtime-anonymous dirigiblelabs/dirigible-base-platform-runtime-anonymous
    docker push dirigiblelabs/dirigible-base-platform-runtime-anonymous
    ```

1. **dirigible-runtime-anonymous**
    ```
    docker build -t dirigible-runtime-anonymous .
    docker tag dirigible-runtime-anonymous dirigiblelabs/dirigible-runtime-anonymous
    docker push dirigiblelabs/dirigible-runtime-anonymous
    ```