## Docker build:

1. **dirigible-base-platform-openshift**
    ```
    docker build -t dirigible-base-platform-openshift -f Dockerfile-base .
    docker tag dirigible-base-platform-openshift dirigiblelabs/dirigible-base-platform-openshift
    docker push dirigiblelabs/dirigible-base-platform-openshift
    ```

1. **dirigible-openshift**
    ```
    docker build -t dirigible-openshift .
    docker tag dirigible-openshift dirigiblelabs/dirigible-openshift
    docker push dirigiblelabs/dirigible-openshift
    ```