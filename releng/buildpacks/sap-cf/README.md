## Eclipse Dirigible Cloud Foundry Buildpack

1. Set the Eclipse Dirigible version:
    > Replace the `#{DirigibleVersion}#` placeholder (e.g. `latest`, `0.7.1`, `1.0.0`) in `buildpack/*.toml` files.

1. Build `Eclipse Dirigible Cloud Foundry Stack`:

    ```
    docker build -t dirigiblelabs/buildpacks-stack-base-dirigible-cf . --target base
    docker push dirigiblelabs/buildpacks-stack-base-dirigible-cf

    docker build -t dirigiblelabs/buildpacks-stack-run-dirigible-cf . --target run
    docker push dirigiblelabs/buildpacks-stack-run-dirigible-cf

    docker build -t dirigiblelabs/buildpacks-stack-build-dirigible-cf . --target build
    docker push dirigiblelabs/buildpacks-stack-build-dirigible-cf
    ```

1. Build `Eclipse Dirigible Cloud Foundry Buildpack`:

    ```
    cd buildpack/

    pack buildpack package dirigiblelabs/buildpacks-dirigible-cf --config ./package.toml
    docker push dirigiblelabs/buildpacks-dirigible-cf

    pack builder create dirigiblelabs/buildpacks-builder-dirigible-cf --config ./builder.toml
    docker push dirigiblelabs/buildpacks-builder-dirigible-cf
    ```

1. Usage with `pack`:

    ```
    pack build --builder dirigiblelabs/buildpacks-builder-dirigible-cf <my-org>/<my-repository>
    ```
