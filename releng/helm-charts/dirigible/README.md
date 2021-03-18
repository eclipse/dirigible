# Eclipse Dirigible

[Eclipse Dirigible](https://www.dirigible.io/) is a cloud development platform providing development tools (Web IDE) and runtime environment (Java based) for In-System Development and Low-Code/No-Code Development.

## TL;DR;

```
helm repo add dirigible https://eclipse.github.io/dirigible
helm repo update
helm install dirigible dirigible/dirigible
```

## Introduction

This chart bootstraps a Eclipse Dirigible deployment on a [Kubernetes](http://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Prerequisites

- Kubernetes 1.19+
- Helm 3+

## Setup

Add the Eclipse Dirigible chart repository:

```
helm repo add dirigible https://eclipse.github.io/dirigible
helm repo update
```

## Deployment

### Basic:

```
helm install dirigible dirigible/dirigible
```

> _**Note:** This will install Eclipse Dirigible **Deployment** and **Service** with **ClusterIP** only._

To access the Dirigible instance execute the command that was printed in the console, similar to this one:

```
export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=dirigible,app.kubernetes.io/instance=dirigible" -o jsonpath="{.items[0].metadata.name}")
echo "Visit http://127.0.0.1:8080 to use your application"
kubectl --namespace default port-forward $POD_NAME 8080:8080    
```

Navigate to: [http://127.0.0.1:8080](http://127.0.0.1:8080)
Login with: `dirigible`/`dirigible`

### Kubernetes

#### Basic:

```
helm install dirigible dirigible/dirigible \
--set ingress.enabled=true \
--set ingress.host=<ingress-host>
```

> _**Note:** This will expose the Dirigible instance through **Ingress** host (**http://...**) ._

#### Basic with PostgreSQL:

```
helm install dirigible dirigible/dirigible \
--set ingress.enabled=true \
--set ingress.host=<ingress-host> \
--set database.enabled=true
```

> _**Note:** This will install also **PostgreSQL** database with **1Gi** storage and update the Dirigible datasource configuration to consume the database._

### Kyma

#### Basic:

```
helm install dirigible dirigible/dirigible \
--set kyma.enabled=true \
--set kyma.apirule.host=<kyma-host>
```

> _**Note:** This will install additionally an **ApiRule** and **XSUAA** **ServiceInstance** and **ServiceBinding**. The appropriate roles should be assigned to the user._

#### Basic with PostgreSQL:

```
helm install dirigible dirigible/dirigible \
--set kyma.enabled=true \
--set kyma.apirule.host=<kyma-host> \
--set database.enabled=true
```

> _**Note:** This will install also **PostgreSQL** database with **1Gi** storage and update the Dirigible datasource configuration to consume the database._

#### Basic with PostgreSQL and Keycloak:

```
helm install dirigible dirigible/dirigible \
--set kyma.enabled=true \
--set kyma.apirule.host=<kyma-host> \
--set database.enabled=true \
--set keycloak.enabled=true \
--set keycloak.install=true
```

> _**Note:** In addition **Keycloak** will be deployed and configured._

These commands deploy Dirigible on a Kubernetes cluster with the default configuration and with the release name `my-release`. The deployment configuration can be customized by specifying the customization parameters with the `helm install` command using the `--values` or `--set` arguments. Find more information in the [configuration section](#configuration) of this document.

## Upgrading

Upgrade the chart deployment using:

```
helm upgrade dirigible dirigible/dirigible
```

The command upgrades the existing `dirigible` deployment with the most latest release of the chart.

**TIP**: Use `helm repo update` to update information on available charts in the chart repositories.

## Uninstalling

Uninstall the `dirigible` deployment using:

```
helm uninstall dirigible
```

The command uninstall the release named `dirigible` and frees all the kubernetes resources associated with the release.

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
