## Docker build:

1. **dirigible-base-platform-anonymous**
    ```
    docker build -t dirigible-base-platform-anonymous -f Dockerfile-base .
    docker tag dirigible-base-platform-anonymous dirigiblelabs/dirigible-base-platform-anonymous
    docker push dirigiblelabs/dirigible-base-platform-anonymous
    ```

1. **dirigible-anonymous**
    ```
    docker build -t dirigible-anonymous .
    docker tag dirigible-anonymous dirigiblelabs/dirigible-anonymous
    docker push dirigiblelabs/dirigible-anonymous
    ```