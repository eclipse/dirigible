## Eclipse Dirigible Kyma Buildpack

1. Set the Eclipse Dirigible version:
    > Replace the `#{DirigibleVersion}#` placeholder (e.g. `latest`, `0.7.1`, `1.0.0`) in `buildpack/*.toml` files.

1. Build `Eclipse Dirigible Kyma Stack`:

    ```
    docker build -t dirigiblelabs/buildpacks-stack-base-dirigible-kyma . --target base
    docker push dirigiblelabs/buildpacks-stack-base-dirigible-kyma

    docker build -t dirigiblelabs/buildpacks-stack-run-dirigible-kyma . --target run
    docker push dirigiblelabs/buildpacks-stack-run-dirigible-kyma

    docker build -t dirigiblelabs/buildpacks-stack-build-dirigible-kyma . --target build
    docker push dirigiblelabs/buildpacks-stack-build-dirigible-kyma
    ```

1. Build `Eclipse Dirigible Kyma Buildpack`:

    ```
    cd buildpack/

    pack buildpack package dirigiblelabs/buildpacks-dirigible-kyma --config ./package.toml
    docker push dirigiblelabs/buildpacks-dirigible-kyma

    pack builder create dirigiblelabs/buildpacks-builder-dirigible-kyma --config ./builder.toml
    docker push dirigiblelabs/buildpacks-builder-dirigible-kyma
    ```

1. Usage with `pack`:

    ```
    pack build --builder dirigiblelabs/buildpacks-builder-dirigible-kyma <my-org>/<my-repository>
    ```

## Kpack Installation

1. [Install Pack](https://buildpacks.io/docs/tools/pack/#install)
1. [Install Kpack](https://github.com/pivotal/kpack/blob/main/docs/install.md)
1. [Install logging tool](https://github.com/pivotal/kpack/blob/main/docs/logs.md)
1. Create Docker Registry Secret:
    ```
    kubectl create secret docker-registry docker-registry-secret \
        --docker-username=<your-username> \
        --docker-password=<your-password> \
        --docker-server=https://index.docker.io/v1/ \
        --namespace default
    ```


1. Create Service Account
    ```
    kubectl apply -f service-account.yaml
    ```


1. Create `ClusterStore`, `ClusterStack` and `Builder`:

    ```
    kubectl apply -f kpack.yaml
    ```
    
    > _**Note:** Before creating the Kpack resources, replace the **`<tag>`** placeholder with a valid Eclipse Dirigible version (e.g. 5.0.0, 6.0.0, ...). All available Eclipse Dirigible versions could be found [here](https://github.com/eclipse/dirigible/releases) and the respective Docker images [here](https://hub.docker.com/r/dirigiblelabs/buildpacks-dirigible/tags?page=1&ordering=last_updated)._

## Image Building

1. Create Image:

    ```yaml
    apiVersion: kpack.io/v1alpha1
    kind: Image
    metadata:
      name: dirigible-application
      namespace: default
    spec:
      tag: dirigiblelabs/dirigible-application:<tag>
      serviceAccount: docker-registry-service-account
      imageTaggingStrategy: <tag>
      builder:
        name: dirigible-builder
        kind: Builder
      source:
        blob:
          url: <url-to-zip-content>
    ```

    > _**Note:** Replace the **`<tag>`** placeholder with your Docker image tag and the **`<url-to-zip-content>`** with URL to a zipped application content._

1. Monitor Logs:

    ```
    logs -image dirigible-application -namespace default
    ```
