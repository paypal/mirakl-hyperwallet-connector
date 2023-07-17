# Hyperwallet Mirakl Connector

> :warning: **If your are upgrading from versions 4.x**, you should read the
> [upgrade guide](https://paypal.github.io/mirakl-hyperwallet-connector/component-hmc/upgrade/upgrade.html),
> since there has been important changes in the requisites and configuration variables
> and the connector won't correctly run if you don't follow the instructions in the
> guide.

Hyperwallet-Mirakl Connector (HMC) is a drop-in service that interconnects
Mirakl and Hyperwallet providing payout connectivity between them.

It's designed to be easy to install and operate, providing self-healing features
and a mail based alert system. The connector supports the following workflows:

- Seller onboarding
- KYC verification
- Payout

You can find more information on its features and how to configure and deploy
the connector in the
[online documentation](https://paypal.github.io/mirakl-hyperwallet-connector/).

## Getting Started

### Requirements

For building and running the connector you will need the following software:

* Java JDK 17
* Docker (Optional)

For the build process you will need to configure these two variables, since the
connector uses the Mirakl SDK which is hosted in a private Maven repository
owned by Mirakl:

```
PAYPAL_MIRAKL_SDK_USER
PAYPAL_MIRAKL_SDK_PASSWORD
```

### Building the Connector

The connector is a standard Spring-Boot application that uses Gradle as its
build system. For building the connector you need to execute the following
command:

* `./gradlew clean build`

Additionally you can create an executable JAR with the following command:

* `./gradlew app:bootJar`

that will generate the JAR in the directory  `app/build/libs`

### Running the Connector

Before running the connector you must set the configuration environment
variables as described [here](#quick-configuration). With the environment set
you can run the connector by executing this command:

* `./gradlew bootRun`

### Checking if the connector is working

To ensure that the connector is properly set up and ready to work, you can check
the health endpoint using, for example, cUrl:

* `curl -X GET --location "http://localhost:8080/actuator/health"`

If the connector is OK the endpoint will respond with a `200` OK status. If
something is wrong it will respond with a `500` status and a JSON detailing the
individual checks and their statuses.

### Quick Configuration

The connector is configured with environment variables. These are the minimum
required variables that you will need to set to run the connector:

**[Hyperwallet Credentials and Programs Configuration](https://paypal.github.io/mirakl-hyperwallet-connector/component-hmc/configuration/configvars/configvars.html#configvarsvars-hyperwallet)**

```
PAYPAL_HYPERWALLET_API_SERVER
PAYPAL_HYPERWALLET_API_USERNAME
PAYPAL_HYPERWALLET_API_PASSWORD
PAYPAL_HYPERWALLET_PROGRAMS_NAMES
PAYPAL_HYPERWALLET_PROGRAMS_USERTOKENS
PAYPAL_HYPERWALLET_PROGRAMS_PAYMENTTOKENS
PAYPAL_HYPERWALLET_PROGRAMS_BANKACCOUNTTOKENS
```

**[Mirakl Credentials](https://paypal.github.io/mirakl-hyperwallet-connector/component-hmc/configuration/configvars/configvars.html#configvarsvars-mirakl)**

```
PAYPAL_MIRAKL_ENVIRONMENT
PAYPAL_MIRAKL_FRONT_API_KEY
PAYPAL_MIRAKL_OPERATOR_API_KEY
```

**[Email Settings](https://paypal.github.io/mirakl-hyperwallet-connector/component-hmc/configuration/configvars/configvars.html#configvarsvars-alerts)**
```
PAYPAL_MAIL_SMTP_AUTH
PAYPAL_MAIL_SMTP_STARTTLS_ENABLE
PAYPAL_MAIL_USER_NAME
PAYPAL_MAIL_USER_PASSWORD
PAYPAL_SERVER_EMAIL_HOST
PAYPAL_SERVER_EMAIL_PORT
```

### Running the connector with Docker Compose

Alternative you can run the connector with Docker and Docker Compose. To do that
yo need to execute the following commands:

* `./gradlew build`
* `./gradlew buildDockerCompose`

In addition to create a docker image for the connector it will also create a
`docker-compose.yml` file for running it with Docker Compose. You can do that
with the following command:

* `docker-compose up`

The generated `docker-compose.yml` is based on the
`docker-compose.yml.template`. In addition to the connector it will also start
an email server so you don't need one for development purposes.

If you want to generate a `docker-compose.yml` with only the connector, you can
run the following command:

* `./gradlew buildDockerCompose -Pprod=true`

In this case it will use `docker-compose.prod.yml.template`
