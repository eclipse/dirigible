# Eclipse Dirigible

[Eclipse Dirigible](https://www.dirigible.io/) is a cloud development platform providing development tools (Web IDE) and runtime environment (Java based) for In-System Development and Low-Code/No-Code Development.

## TL;DR;

```bash
$ helm repo add dirigible https://eclipse.github.io/dirigible
$ helm install dirigible/dirigible
```

## Introduction

This chart bootstraps a Dirigible deployment on a [Kubernetes](http://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Prerequisites

- Kubernetes 1.12+
- Helm 2.11+

## Installing

Install the chart using:

```bash
$ helm repo add dirigible https://eclipse.github.io/dirigible
$ helm install dirigible/dirigible --name my-deployment
```

These commands deploy Dirigible on a Kubernetes cluster with the default configuration and with the release name `my-release`. The deployment configuration can be customized by specifying the customization parameters with the `helm install` command using the `--values` or `--set` arguments. Find more information in the [configuration section](#configuration) of this document.

## Upgrading

Upgrade the chart deployment using:

```bash
$ helm upgrade my-deployment dirigible/dirigible
```

The command upgrades the existing `my-deployment` deployment with the most latest release of the chart.

**TIP**: Use `helm repo update` to update information on available charts in the chart repositories.

## Uninstalling

Uninstall the `my-deployment` deployment using:

```bash
$ helm delete my-deployment
```

The command deletes the release named `my-deployment` and frees all the kubernetes resources associated with the release.

**TIP**: Specify the `--purge` argument to the above command to remove the release from the store and make its name free for later use.

## Configuration

The following table lists all the configurable parameters expose by the Dirigible chart and their default values.

|             Name             |          Description          |            Default            |
|------------------------------|-------------------------------|-------------------------------|
| `replicaCount`               | Number of replicas            | `1`                           |
| `image.repository`           | Dirigible image name          | `dirigiblelabs/dirigible-all` |
| `image.tag`                  | Dirigible image tag           | `5.8.3`                     |
| `env.dirigibleThemeDefault`  | Dirigible default theme       | `fiori`                       |
| `service.type`               | Kubernetes Service type       | `ClusterIP`                   |
| `service.port`               | Dirigible service port        | `8080`                        |
| `service.targetPort`         | Dirigible service targetPort  | `8080`                        |
| `ingress.host`               | Ingress host                  | ``                            |

Specify the parameters you which to customize using the `--set` argument to the `helm install` command. For instance,

```bash
$ helm install dirigible/dirigible --name my-deployment --set ingress.host=my-ingress-host.com
```

The above command sets the `ingress.host` to `my-ingress-host.com`.

Alternatively, a YAML file that specifies the values for the above parameters can be provided while installing the chart. For example,

```bash
$ helm install dirigible/dirigible --name my-deployment --values values.yaml
```

**Tip**: You can use the default [values.yaml](values.yaml).
