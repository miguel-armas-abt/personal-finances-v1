Reemplazar los siguientes valores donde corresponda:

| variable          | valor                                            |
|-------------------|--------------------------------------------------|
| `<APP_JAR>`       | `personal-management-v1-1.0-SNAPSHOT-runner.jar` |
| `<PACKAGE_TYPE>`  | `fast-jar`, `uber-jar` o `mutable-jar`           |
| `<APP_NATIVE>`    | `personal-management-v1-1.0-SNAPSHOT-runner.exe` |
| `<APP_IMAGE>`     | `miguelarmasabt/personal-management:v1.0.1`      |
| `<APP_CONTAINER>` | `personal-management-v1`                         |
| `<APP_PORTS>`     | `8080:8080`                                      |

## ▶️ Local

1. Descargar e instalar [commons-quarkus-parent](https://github.com/miguel-armas-abt/commons-quarkus-parent/README.md)
2. Configurar las [variables de entorno](./variables.env) en el IDE.
3. Ejecutar aplicación
> #### `🟢 Opción 1` Ejecutar perfil dev
> 
> ```shell
> mvn clean compile quarkus:dev
> ```

> #### `🟢 Opción 2` Ejecutar con JVM
> ```shell
> mvn clean package -Dquarkus.package.type=<PACKAGE_TYPE>
> java -jar target/<APP_JAR>
> ```

> #### `🟢 Opción 3` Ejecutar imagen nativa
> Para pruebas locales, desactivar la propiedad `quarkus.native.container-build` en el `pom.xml`
> ```sh
> mvn clean package -Pnative
> ./target/<APP_NATIVE>
> ```

---

## ▶️ Docker

1. Crear imagen

> #### `🟢 Opción 1` JVM
> ```shell
> docker build -t <APP_IMAGE> -f ./docker/Dockerfile.jvm .
> ```

> #### `🟢 Opción 2` Imagen nativa
> ```shell
> docker build -t <APP_IMAGE> -f ./docker/Dockerfile.native .
> ```

2. Crear red
```shell
docker network create --driver bridge common-network
```

3. Ejecutar contenedor
```shell
docker run --rm -p <APP_PORTS> --env-file ./variables.env --name <APP_CONTAINER> --network common-network <APP_IMAGE>
```

---

## ▶️ Kubernetes

1. Encender Minikube
```shell
docker context use default
minikube start
```

2. Crear imagen dentro del clúster
```shell
eval $(minikube docker-env --shell bash)
docker build -t <APP_IMAGE> -f ./docker/Dockerfile.native .
```

3. Crear namespace y aplicar manifiestos
```shell
kubectl create namespace demo
kubectl apply -f ./k8s.yaml -n demo
```

4. Eliminar orquestación
```shell
kubectl delete -f ./k8s.yaml -n demo
```

5. Port-forward
```shell
kubectl port-forward <POD_ID> <APP_PORTS> -n demo
```