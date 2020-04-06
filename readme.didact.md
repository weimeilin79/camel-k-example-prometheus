# Camel K Monitoring Example
 
![Camel K CI](https://github.com/openshift-integration/camel-k-example-basic/workflows/Camel%20K%20CI/badge.svg)
This example shows how to configures Camel K and export the Prometheus JMX exporter. Setup Prometheus to scrap the numbers and ultimatly display the result in Grafana.

## Before you begin

Make sure you check-out this repository from git and open it with [VSCode](https://code.visualstudio.com/).

Instructions are based on [VSCode Didact](https://github.com/redhat-developer/vscode-didact), so make sure it's installed
from the VSCode extensions marketplace.

From the VSCode UI, click on the `readme.didact.md` file and select "Didact: Start Didact tutorial from File". A new Didact tab will be opened in VS Code.

[Make sure you've checked all the requirements](./requirements.didact.md) before jumping into the tutorial section.

## Checking requirements

<a href='didact://?commandId=vscode.didact.validateAllRequirements' title='Validate all requirements!'><button>Validate all Requirements at Once!</button></a>

**VS Code Extension Pack for Apache Camel**

The VS Code Extension Pack for Apache Camel by Red Hat provides a collection of useful tools for Apache Camel K developers,
such as code completion and integrated lifecycle management.

You can install it from the VS Code Extensions marketplace.

[Check if the VS Code Extension Pack for Apache Camel by Red Hat is installed](didact://?commandId=vscode.didact.extensionRequirementCheck&text=extension-requirement-status$$redhat.apache-camel-extension-pack&completion=Camel%20extension%20pack%20is%20available%20on%20this%20system. "Checks the VS Code workspace to make sure the extension pack is installed"){.didact}

*Status: unknown*{#extension-requirement-status}

**OpenShift CLI ("oc")**

The OpenShift CLI tool ("oc") will be used to interact with the OpenShift cluster.

[Check if the OpenShift CLI ("oc") is installed](didact://?commandId=vscode.didact.cliCommandSuccessful&text=oc-requirements-status$$oc%20help "Tests to see if `oc help` returns a 0 return code"){.didact}

*Status: unknown*{#oc-requirements-status}


**Connection to an OpenShift cluster**

You need to connect to an OpenShift cluster in order to run the examples.

[Check if you're connected to an OpenShift cluster](didact://?commandId=vscode.didact.requirementCheck&text=cluster-requirements-status$$oc%20get%20project$$NAME&completion=OpenShift%20is%20connected. "Tests to see if `kamel version` returns a result"){.didact}

*Status: unknown*{#cluster-requirements-status}

**Apache Camel K CLI ("kamel")**

Apart from the support provided by the VS Code extension, you also need the Apache Camel K CLI ("kamel") in order to 
access all Camel K features.

[Check if the Apache Camel K CLI ("kamel") is installed](didact://?commandId=vscode.didact.requirementCheck&text=kamel-requirements-status$$kamel%20version$$Camel%20K%20Client&completion=Apache%20Camel%20K%20CLI%20is%20available%20on%20this%20system. "Tests to see if `kamel version` returns a result"){.didact}

*Status: unknown*{#kamel-requirements-status}


## 1. Preparing a new OpenShift project

Go to your working project `userX` where you'll run the integrations.

To create the project, open a terminal tab and type the following command:


```
oc project userX
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20project%20userX&completion=New%20project%20creation. "Opens a new terminal and sends the command above"){.didact})


Camel K should have created an IntegrationPlatform custom resource in your project. To verify it:

```
oc get integrationplatform
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20integrationplatform&completion=New%20project%20creation. "Opens a new terminal and sends the command above"){.didact})

If everything is ok, you should see an IntegrationPlatform named `camel-k` with phase `Ready`.

## 2. Setup and start Prometheus

Prometheus is an open-source systems monitoring and alerting toolkit, we will use this to scrap all the running integration metrics. 
Make sure Prometheus operator is installed in your namespace. And now we are ready to start a Prometheus server.

```
oc apply -f prometheus.yaml
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20apply%20-f%20prometheus.yaml "Opens a new terminal and sends the command above"){.didact})

## 3. Running a Camel integration

This repository contains two Camel K integrations. One periodically prints 
out messages, the other one reads a large file from Azure and process the content in loop

The integration in that prints simple message is in `Basic.java` ([open](didact://?commandId=vscode.openFolder&projectFilePath=Basic.java&completion=Opened%20the%20Basic.java%20file "Opens the Basic.java file"){.didact}).

The Prometheus trait configures the Prometheus JMX exporter and exposes the integration with a Service and a ServiceMonitor resources so that the Prometheus endpoint can be scraped.

```
kamel run Basic.java --trait prometheus.enabled=true
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Basic.java%20--trait%20prometheus.enabled=true&completion=Camel%20K%20basic%20integration%20run%20in%20dev%20mode. "Opens a new terminal and sends the command above"){.didact})

The integration in that process the large file from Azure is in `Personal.java` ([open](didact://?commandId=vscode.openFolder&projectFilePath=Personal.java&completion=Opened%20the%Personal.java%20file "Opens the Basic.java file"){.didact}).

```
kamel run Personal.java --trait prometheus.enabled=true
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%Personal.java%20--trait%20prometheus.enabled=true&completion=Camel%20K%20Personal%20integration%20run%20in%20dev%20mode. "Opens a new terminal and sends the command above"){.didact})

If everything is ok, after the build phase finishes, the new two Camel integrations will be running.

```
oc get integrations
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20integrations&completion=Getting%20running%20integrations. "Opens a new terminal and sends the command above"){.didact})

Both `basic` and `personal` should be present in the list and it should be in status `Running`. You can use `kamel get` command to list all running integrations.


## 4. Setup and start Grafana

Grafana is the visualization and analytics software. It allows user to query, visualize and explore  metrics. 

Spin up the Grafana.

```
oc create -f grafana/grafana.yaml 
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%create%20-f%20grafana/grafana.yaml "Opens a new terminal and sends the command above"){.didact})

wait until it done, you will be able to access Grafana via following url 

```
echo http://$(oc get route grafana-route -o jsonpath='{.spec.host}') 
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$echo%20http://$(oc%20get%20route%20grafana-route%20-o%20jsonpath='{.spec.host}') "Opens a new terminal and sends the command above"){.didact})

Login with admin/admin as ID/Password

Go to the following URL in the browser while you are still logged in as `admin`

```
echo http://$(oc get route grafana-route -o jsonpath='{.spec.host}')/datasources
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$echo%20http://$(oc%20get%20route%20grafana-route%20-o%20jsonpath='{.spec.host}/datasources') "Opens a new terminal and sends the command above"){.didact})


Click into the datasrouce called `Prometheus`, in the detail page, add the following prometheus address in the HTTP section, inside the `URL` text field.

```
http://prometheus-operated:9090
```
Click on `Save and Test`. It should return `Data source is working`

Click on the +(Create) on the left menu and select `Import`

On the top right corner, click on `Upload .json File` and choose the `SampleCamelDashboard.json` under `grafana` in this project folder. 

Click `Import` and you will be able to view all the Camel K metrics.

You can continue to hack on the examples.
