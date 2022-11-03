
# Overview

Experiment to get NginX to front APIs from two containers on the same domain.

* The APIs are not exposed via ports on the host, only via the NginX exposed port
* By adding an entry in `/etc/hosts` (`127.0.0.1 mvm.com`) both API can be accessed from the `mvm.com` domain
* A path prefix is used to identify the intended API
  * `/service-on` for the first API
  * `/service-two` for the second API

To try it out:

```shell
docker compose up -d

# Make a note of mvm-echo1 & mvm-echo2 Container IDs from the following:
docker ps

# Hit the services, make sure they each return a hostname matching their container ID
curl http://mvm.com:9080/service-two/?echo_env_body=HOSTNAME
curl http://mvm.com:9080/service-one/?echo_env_body=HOSTNAME

# Hit the services, get a little more insight
curl -s http://mvm.com:9080/service-one/?query=demo | jq .
curl -s http://mvm.com:9080/service-two/?query=demo | jq .
```

