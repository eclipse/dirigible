## Eclipse Dirigible Buildpack

1. Set the Eclipse Dirigible version:
    > Replace the `#{DirigibleVersion}#` placeholder (e.g. `latest`, `0.7.1`, `1.0.0`) in `buildpack/*.toml` files.

1. Build `Eclipse Dirigible Stack`:

    ```
    docker build -t dirigiblelabs/buildpacks-stack-base-dirigible . --target base
    docker push dirigiblelabs/buildpacks-stack-base-dirigible

    docker build -t dirigiblelabs/buildpacks-stack-run-dirigible . --target run
    docker push dirigiblelabs/buildpacks-stack-run-dirigible

    docker build -t dirigiblelabs/buildpacks-stack-build-dirigible . --target build
    docker push dirigiblelabs/buildpacks-stack-build-dirigible
    ```

1. Build `Eclipse Dirigible Buildpack`:

    ```
    cd buildpack/

    pack buildpack package dirigiblelabs/buildpacks-dirigible --config ./package.toml
    docker push dirigiblelabs/buildpacks-dirigible

    pack builder create dirigiblelabs/buildpacks-builder-dirigible --config ./builder.toml
    docker push dirigiblelabs/buildpacks-builder-dirigible
    ```

1. Usage with `pack`:

    ```
    pack build --builder dirigiblelabs/buildpacks-builder-dirigible <my-org>/<my-repository>
    ```