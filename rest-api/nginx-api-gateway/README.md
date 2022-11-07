# Overview

Experiment to get Nginx to front APIs from multiple containers on the same domain, using the first part of the path to determine which container to proxy.

Individual APIs will only be accessible via Nginx, their ports not being exposed on the host.

I was unable to get the Swagger UI to play nicely in this configuration and for that reason removed all OpenAPI configuration and dependencies.

There is a single API project that can be configured with an `id` property that is contained in the response.

The [docker-compose.yml](./docker/docker-compose.yml) file launches three instances of this API, each configured with a unique `id`, and an instance of Nginx [configured](./docker/nginx/nginx.conf) to
proxy them by service name.

To get this working, `mvm.com` must resolve to your localhost IP Address. The easiest way to achieve this is to add the following entry to your `hosts` file (
Windows: `C:\Windows\System32\drivers\etc\hosts`, *nix: `/etc/hosts`):

```text
127.0.0.1 mvm.com
```

Details of the services exposed:

| Container Name  | Example URL (through Nginx proxy)                  | Id      | Security Enabled |
|-----------------|----------------------------------------------------|---------|------------------|
| bashful-service | http://mvm.com:9080/bashful-service/api/hello/Mark | Bashful | false            |
| grumpy-service  | http://mvm.com:9080/grumpy-service/api/hello/Mark  | Grumpy  | true             |
| dopey-service   | http://mvm.com:9080/dopey-service/api/hello/Mark   | Dopey   | false            |

Other endpoints:

* [http://mvm.com:9080/bashful-service/api/headers](http://mvm.com:9080/bashful-service/api/headers)
* [http://mvm.com:9080/bashful-service/actuator/health](http://mvm.com:9080/bashful-service/actuator/health)
* [http://mvm.com:9080/bashful-service/actuator/info](http://mvm.com:9080/bashful-service/actuator/info)
* [http://mvm.com:9080/bashful-service/actuator/env](http://mvm.com:9080/bashful-service/actuator/env)
* [http://mvm.com:9080/bashful-service/actuator/metrics](http://mvm.com:9080/bashful-service/actuator/metrics)

To try it out:

* Add the required entry to your `hosts` file
* Launch the docker compose
* Run the application in IntelliJ (or whatever)
* Visit (TestTheApis.html)[http://mvm.com:9081/TestTheApis.html], this is a [static web page](./src/main/resources/static/TestTheApis.html) created just for testing this application and deployed as
  part of the API.

NOTE: The static page should be run from a web server and not just by double-clicking it. The reason being we want to check that CORS is working as well. The application run by IntelliJ will be served
on port 9081, it is calling the API via Nginx on port 9082 and for this to work the API must allow requests from `http://mvm.com:9081`. 