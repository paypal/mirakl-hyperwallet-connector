# Hyperwallet Mirakl Connector

The Hyperwallet Mirakl Connector (HMC) is a service that integrates the Hyperwallet and Mirakl
platforms.

## Installation

After downloading this repository, you must acquire a Mirakl login account to access the Mirakl
  Artifactory https://artifactory.mirakl.net/artifactory/mirakl-ext-repo/. See the "Access to
  Artifactory" topic
  on https://hyperwallet-dev.mirakl.net/help/Customers/topics/Connectors/SDK/java/access_java_sdk.html
    - These account credentials will be used for setting the Mirakl SDK User and Mirakl SDK Password
      environment variables

### System prerequisites for execution & deployment

* Java JDK 15

If executing/deploying locally, without using the provided Docker templates:

* RabbitMQ

## Configuration

This connector is configured primarily through the use of environment variables.

The following table provides every environment variable, and describes whether the environment variable is mandatory or optional (either by having a default value or only being used when specific features are enabled), and a description and example.

| ENVIRONMENT VARIABLE                                                | MANDATORY                                                    | DESCRIPTION                                                                                                                                                                          | EXAMPLE VALUE                              |
|-------------------------------------------------------------------- |--------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------|
| `PAYPAL_MIRAKL_SDK_USER`                                            |   YES                                                        | Mirakl username for accessing the Mirakl Java SDK.                                                                                                                                   | `yourCompanyName`                          |
| `PAYPAL_MIRAKL_SDK_PASSWORD`                                        |   YES                                                        | Mirakl password, for accessing the Mirakl Java SDK.                                                                                                                                  | `secret`                                   |
| `PAYPAL_MIRAKL_OPERATOR_API_KEY`                                    |   YES                                                        | The Mirakl operator API key generated for your operator account.                                                                                                                     | `c262b297-c8a7-45a5-a22f-a0d9fe25132a`     |
| `PAYPAL_MIRAKL_OPERATOR_TIME_ZONE`                                  |    NO (default value: `UTC`)                                 | The Mirakl time zone of your Mirakl instance. Possible values are documented [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZoneId.html).             | `GMT`, `Europe/London`                     |
| `PAYPAL_MIRAKL_ENVIRONMENT`                                         |   YES                                                        | The URL for your Mirakl environment's API (provided by Mirakl).                                                                                                                      | `https://yourCompany.mirakl.net/api`       |
| `PAYPAL_HYPERWALLET_API_SERVER`                                     |   YES                                                        | The URL for your Hyperwallet environment's API (provided by Hyperwallet).                                                                                                            | `https://uat-api.paylution.com`            |
| `PAYPAL_HYPERWALLET_API_USERNAME`                                   |   YES                                                        | Hyperwallet environment username (provided by Hyperwallet).                                                                                                                          | `restapiuser@000001`                       |
| `PAYPAL_HYPERWALLET_API_PASSWORD`                                   |   YES                                                        | Hyperwallet environment password (provided by Hyperwallet).                                                                                                                          | `yourSecret`                               |
| `PAYPAL_HYPERWALLET_PROGRAM_TOKENS`                                 |   YES                                                        | A set of comma separated values based on the program hierarchy provided by Hyperwallet (See the Program Configuration section below).                                                | `DEFAULT`                                  |
| `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_USERS_DEFAULT`                    |   YES                                                        | The program token for contacting the Hyperwallet API on the `/users` path for `DEFAULT` program.                                                                                     | `prg-6541532-as1a23s242-12as124-as2454`    |
| `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_PAYMENTS_DEFAULT`                 |   YES (default value: `DEFAULT`)                             | The program token for contacting the Hyperwallet API on the `/payments` path for `DEFAULT` program.                                                                                  | `prg-54545a532-asda2refs43-as2fd35-das233` |
| `PAYPAL_HYPERWALLET_OPERATOR_BANK_ACCOUNT_TOKEN_DEFAULT`            |   NO                                                         | The transfer bank account token where commissions will be paid out too. Only needed if the operator commissions feature is being used (see the Operator Commissions section below).  | `trm-2646asas54-21asdas5642-xasa45sxx`     |
| `PAYPAL_HYPERWALLET_OPERATOR_COMMISSIONS_ENABLED`                   |   NO (default value: `true`)                                 | By default, the operator commissions feature is enabled.                                                                                                                             | Possible values:`true` or `false`          |
| `PAYPAL_HYPERWALLET_OPERATOR_CREDIT_NOTES_ENABLED`                  |   NO (default value: `true`)                                 | By default, the manual credit notes processing is enabled.                                                                                                                           | Possible values:`true` or `false`          |
| `PAYPAL_BRAINTREE_MERCHANT_ID`                                      |   NO                                                         | BrainTree merchant id provided by BrainTree.                                                                                                                                         | `myBrainTreeMerchantId`                    |
| `PAYPAL_BRAINTREE_PUBLIC_KEY`                                       |   NO                                                         | BrainTree public key provided by BrainTree.                                                                                                                                          | `myBrainTreePublicKey`                     |
| `PAYPAL_BRAINTREE_PRIVATE_KEY`                                      |   NO                                                         | BrainTree private key provided by BrainTree.                                                                                                                                         | `myBrainTreePrivateKey`                    |
| `PAYPAL_BRAINTREE_REPORT_ENVIRONMENT`                               |   NO                                                         | By default the value is  pointing to the `sandbox` environment. In case you want to point to production environment, set the value to `production`.                                  | `sandbox`                                  |
| `PAYPAL_SERVER_EMAIL_HOST`                                          |   NO                                                         | The URL where your POP3/SMTP server is hosted. If you're using the Docker Compose script provided in this repo, use `smtp`.                                                          | `smtp.example.com`                         |
| `PAYPAL_SERVER_EMAIL_PORT`                                          |   NO                                                         | The port used by your POP3/SMTP server. If you're using the Docker Compose script provided in this repo, use `1025`.                                                                 | `1025`                                     |
| `PAYPAL_MAIL_SMTP_AUTH`                                             |   NO                                                         | Whether or not authentication is needed for accessing the POP3/SMTP mail server.                                                                                                     | Possible values: `true` or `false`         |
| `PAYPAL_MAIL_USER_NAME`                                             |   NO                                                         | The username credential for using the POP3/SMTP server. It can be left empty if `PAYPAL_MAIL_SMTP_AUTH` is set to `false`.                                                           | `smtp-username`                            |
| `PAYPAL_MAIL_USER_PASSWORD`                                         |   NO                                                         | The password credential for using the POP3/SMTP server. It can be left empty if `PAYPAL_MAIL_SMTP_AUTH` is set to `false`.                                                           | `smtp-pass`                                |
| `PAYPAL_MAIL_SMTP_STARTTLS_ENABLE`                                  |   NO                                                         | Whether or not TLS is needed for establishing connection with the POP3/SMTP server.                                                                                                  | Possible values:`true` or `false`          |
| `PAYPAL_HYPERWALLET_MAIL_RECIPIENT`                                 |   NO (default value: `recipient1@test.com`                   | The email recipient for the errors thrown by the connector.                                                                                                                          | `recipient@email.com`                      |
| `PAYPAL_HYPERWALLET_MAIL_FROM`                                      |   NO (default value: `from@email.com`)                       | The from email that appears on the emails sent by the connector.                                                                                                                     | `from@email.com`                           |
| `PAYPAL_RABBITMQ_HOST`                                              |   YES (default value: `queue`)                               | The RabbitMQ host address.                                                                                                                                                           | `localhost`                                |
| `PAYPAL_RABBITMQ_PORT`                                              |   YES (default value: `5672`)                                | The RabbitMQ host port.                                                                                                                                                              | `5672`                                     |
| `PAYPAL_RABBITMQ_USERNAME`                                          |   YES                                                        | The username for connecting with the RabbitMQ host.                                                                                                                                  | `username`                                 |
| `PAYPAL_RABBITMQ_ERLANG_COOKIE`                                     |   YES                                                        | Alphanumeric shared secret up to 255 characters to be used to authenticate to the rabbitMQ target node, [link](https://www.rabbitmq.com/cli.html#erlang-cookie)                      | `f5deqo89sarnukk3ttmrl6oa91ug0ey`          |
| `PAYPAL_RABBITMQ_PASSWORD`                                          |   YES                                                        | The password for connecting with the RabbitMQ host.                                                                                                                                  | `password`                                 |
| `PAYPAL_SPRING_PROFILE_ACTIVE`                                      |   YES                                                        | The Spring Profile to execute/deploy the service on. Possible values: `dev`, `qa`, `prod`, `encrypted`, `financial-report`                                                           | `dev,financial-report`                     |
| `PAYPAL_HYPERWALLET_EXTRACT_SELLERS_CRON_EXPRESSION`                |   NO (default value: `0 0 0 1/1 * ? *` )                     | The cron expression to trigger periodically the Sellers Extract Job.                                                                                                                 | `0 0 0 1/1 * ? *`                          |
| `PAYPAL_HYPERWALLET_EXTRACT_PROFESSIONAL_SELLERS_CRON_EXPRESSION`   |   NO (default value: `0 0 0 1/1 * ? *`)                      | The cron expression to trigger periodically the Professional Sellers Extract Job.                                                                                                    | `0 0 0 1/1 * ? *`                          |
| `PAYPAL_HYPERWALLET_BANK_ACCOUNT_EXTRACT_CRON_EXPRESSION`           |   NO (default value: `0 30 0 1/1 * ? *`)                     | The cron expression to trigger periodically the Bank account Extract Job.                                                                                                            | `0 30 0 1/1 * ? *`                         |
| `PAYPAL_HYPERWALLET_EXTRACT_INVOICES_CRON_EXPRESSION`               |   NO (default value: `1 0 0 1/1 * ? *`)                      | The cron expression to trigger periodically the Invoices Extract Job,.                                                                                                               | `1 0 0 1/1 * ? *`                          |
| `PAYPAL_HYPERWALLET_EXTRACT_DOCUMENTS_CRON_EXPRESSION`              |   NO (default value: `1 30 0 1/1 * ? *`)                     | The cron expression to trigger periodically the Documents Extract Job.                                                                                                               | `1 30 0 1/1 * ? *`                         |
| `PAYPAL_HYPERWALLET_KEY_SET_LOCATION`                               |   NO (default value: `https://uat-api.paylution.com/jwkset`) | The key set uri. For pointing to production, replace the value by `https://api.paylution.com/jwkset`                                                                                 | `https://uat-api.paylution.com/jwkset`     |
| `PAYPAL_HYPERWALLET_ENCRYPTION_ALGORITHM`                           |   NO                                                         | The algorithm used for Layer7 encryption ([Hyperwallet encryption](https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption))                                         | `RSA-OAEP-256`                             |
| `PAYPAL_HYPERWALLET_SIGN_ALGORITHM`                                 |   NO                                                         | The sign algorithm for Layer7 encryption ([Hyperwallet encryption](https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption))                                         | `RS256`                                    |
| `PAYPAL_HYPERWALLET_ENCRYPTION_METHOD`                              |   NO                                                         | The encryption method used for Layer7 encryption ([Hyperwallet encryption](https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption))                                 | `A256CBC-HS512`                            |
| `PAYPAL_HYPERWALLET_PRIVATE_JWK_JSON_LOCATION`                      |   NO                                                         | The private/public JWK set location                                                                                                                                                  | `/your/path/to/private/keys/jwk_set.key`   |
| `PAYPAL_HYPERWALLET_PUBLIC_JWK_JSON_LOCATION`                       |   NO                                                         | The public JWK set location.                                                                                                                                                         |`https://example.com/hw/shared`             |
| `PAYPAL_MOCK_SERVER_URL`                                            |   YES                                                        | The URL to your webhook/mock server. Only used when running with the `qa` Spring profile.                                                                                            | `https://mockserver.example.com`           |

A sample .env file is provided in this repository, primarily for use in the Docker container deployment scenario (documented below). The .env file can also be used to source environment variables for use in local deployment, if you prefer to store these in a file. If you do this, remember that for local deployment you will still need to properly export/source the variables from the file and into the executing shell.

### Property Files

This is a multi-module Gradle project composed by Spring Boot modules ([Official gradle Documentation](https://docs.gradle.org/current/userguide/multi_project_builds.html))

Every module contains a configuration file located under the `MODULE_NAME/src/main/resources` folder path.

All configuration should be done by setting environment variables, as described in the table above. 

The module configuration files listed below contain properties that retrieve their values directly from environment variables. The modules and their configuration files are described here, for troubleshooting purposes or to support advanced configuration & deployment:

- **sellers**: Synchronises seller information between Mirakl and Hyperwallet.  
- **kyc**: Pushes identification documents from Mirakl to Hyperwallet for KYC purposes.
- **invoices**: Distributes funds to sellers based on Mirakl's invoices and manual credit notes, and processes commissions for the operator. 
- **notifications**: Receives incoming webhook notifications from Hyperwallet and enqueues them in RabbitMQ for further processing by other modules.
- **reports**: Generates the CSV report from invoices and manual credit notes from Mirakl and Braintree transactions.
- **infrastructure**: Contains common functionality used by the rest of the modules.
- **web**: Centralises the startup of the web application and exposes all the endpoints for manually running the cron jobs.  

| CONFIGURATION FILE                                      | MODULE          | DESCRIPTION                                                                                                                                                                                                   |
| ------------------------------------------------------- | --------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  `infrastructure_db.properties`                         | `infrastructure`| Database configuration for saving job execution timestamps                                                                                                                                                   |
|  `infrastructure.properties`                            | `infrastructure`| Configuration related with RabbitMQ and Email recipients                                                                                                                                                      |
|  `invoices.properties`                                  | `invoices`      | Hyperwallet/Mirakl API configuration, Hyperwallet bank account/Hyperwallet program token, Manual credit notes/commission toggling, Payment notification types accepted, Extract invoices job scheduling       |
|  `kyc.properties`                                       | `kyc`           | Hyperwallet KYC endpoint/credentials, Mirakl API endpoint/credentials,RabbitMQ queue name for incoming notifications, Hyperwallet program token setup, Extract documents job scheduling                       |
|  `notifications.properties`                             | `notifications` | RabbitMQ configuration for incoming notifications                                                                                                                                                             |
|  `reports.properties`                                   | `reports`       | Mirakl API configuration, Financial report folder location, Financial report CSV Columns, Financial report file name prefix, Server report Uri path, BrainTree credentials                                    |
|  `sellers_db.properties`                                | `sellers`       | Database configuration for saving sellers that should be retrieved for exporting|                                                                                                                                                               |
|  `sellers.properties`                                   | `sellers`       | Hyperwallet/Mirakl API configuration, Extract sellers/professional sellers/bank account job scheduling                                                                                                        |
|  `application.properties`                               | `web`           | Spring profiles, Email server configuration, Layer7 encryption configuration (JOSE/JWT)                                                                                                                       |

## Execution & Deployment

The connector can either be deployed locally, or with a container.  

To ease testing & initial deployment, we strongly recommend to start with the containerized version using Docker Compose and the provided templates.

### Local

For local execution you will need to build the connector and start it up with the following commands:

* `./gradlew build`
* `./gradlew web:bootRun`

We strongly recommend for testing and development purposes to use the containerized version with Docker Compose, explained in the following sections.

#### Running whole stack with Docker Compose

To make it easier to run the application, as it depends on multiple services, a Docker Compose configuration exists within the project.

#### Building the Docker image with Docker Compose

The Docker image will create the file `docker-compose.yml`, which is based on the `docker-compose.yml.template` file:

`./gradlew buildDockerCompose`

#### Executing Docker container with Docker Compose

This Gradle task will run the Docker image based on the generated `docker-compose.yml` file:

`./gradlew dockerComposeUp`  

The Docker deploy uses the .env file to apply all the mandatory environment variables defined previously [in the environment variables table](#Configuration).  
 
Make sure your .env file contains all the mandatory environment variables, otherwise the connector will not be able to start.

To check for configuration issues run:   
`docker-compose --env-file .env config` and see if the variables are all correctly set.

[For more about how Docker Compose uses .env files, consult the official documentation.](https://docs.docker.com/compose/env-file/#:~:text=DOCKER_TLS_VERIFY-,Notes,-Values%20present%20in)

Optionally, you can pass arguments to Docker Compose with the property `dockerComposeArgs`:

`./gradlew dockerCompose -PdockerComposeArgs='up -d'`

This will start the services defined in `docker-compose.yml`.

#### Production build

In order to generate a Docker Compose ready to be used in production, the build command needs the property `prod` set to `true`:

`./gradlew buildDockerCompose -Pprod=true`

## Operator Commissions

By default, the operator commissions feature is enabled. This is set in the property `invoices.operator.commissions.enabled` in the `invoices.properties` file. 
This feature can be disabled by setting the value of this property to `false`.

## Program Configuration

### Single Program (Default)

The default setup provides a single-level hierarchy where 1 Issuing Merchant corresponds to 1 Issuing Store.

This is defined in Mirakl using the `hw-program` shop custom field (see the Mirakl Configuration
section in the Solution Guide), which for a single hierarchy program should contain a single value
list with only one value `DEFAULT`.

The described environment variables dependant on the field `hw-program` mentioned on the setup step are 
designed for this hierarchy type.

### Multiple Programs

The Hyperwallet, Mirakl, and HMC configurations can be extended to accommodate a multiple program
hierarchy structure, where 1 Issuing Merchant can have multiple Issuing Stores.

Based on Hyperwallet's configuration, it will be necessary to modify the Hyperwallet Program
configuration.

By default, HMC supports just one. Just in case it is needed multiple values, we need to do some
easy modifications.

For example:

* We have two different Hyperwallet programs: UK and Europe.
* We defined in Mirakl a custom attribute which label is `hw-program` as `SingleValueList` with these
  values: `EUROPE` and `UK`

In that case, we need to setup the following variables as described:

* Environment:
    * Define variable `PAYPAL_HYPERWALLET_PROGRAM_TOKENS` with value `UK,EUROPE`

* File: `invoices.properties`:
    * Remove `invoices.hyperwallet.api.hyperwalletprogram.token.DEFAULT` property
    * Remove `invoices.operator.commissions.bankAccount.token.DEFAULT` property
    * Define token for UK: `invoices.hyperwallet.api.hyperwalletprogram.token.UK=<YOUR_UK_TOKEN>`
    * Define token for
      EUROPE: `invoices.hyperwallet.api.hyperwalletprogram.token.EUROPE=<YOUR_EUROPE_TOKEN>`
    * Define the operator bank account token for
      UK: `invoices.operator.commissions.bankAccount.token.UK = <YOUR_UK_BANK_ACCOUNT_TOKEN>`
    * Define the operator bank account token for
      EUROPE: `invoices.operator.commissions.bankAccount.token.EUROPE = <YOUR_EUROPE_BANK_ACCOUNT_TOKEN>`

* File: `kyc.properties`:
    * Remove `kyc.hyperwallet.api.hyperwalletprogram.token.DEFAULT`
    * Define token for UK: `kyc.hyperwallet.api.hyperwalletprogram.token.UK=<YOUR_UK_TOKEN>`
    * Define token for
      EUROPE: `kyc.hyperwallet.api.hyperwalletprogram.token.EUROPE=<YOUR_EUROPE_TOKEN>`

* File: `seller.properties`:
    * Remove `sellers.hyperwallet.api.hyperwalletprogram.token.DEFAULT`
    * Define token for UK: `sellers.hyperwallet.api.hyperwalletprogram.token.UK=<YOUR_UK_TOKEN>`
    * Define token for
      EUROPE: `sellers.hyperwallet.api.hyperwalletprogram.token.EUROPE=<YOUR_EUROPE_TOKEN>`

If you're using Docker, remember to update the Docker Compose template file to reflect the existence
of these 2 new environments. Add them into the Docker Compose template file you're
using (`docker-compose.prod.yml.template` or `docker-compose.yml.template`), for example with UK and
Europe:

- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_PAYMENTS_UK`
- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_USERS_UK`
- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_PAYMENTS_EUROPE`
- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_USERS_EUROPE`

Besides, if you had the necessity of adapting the property files to accommodate this kind of hierarchy you can make use of
environment variables substitution feature built in with Spring Boot ([link](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/howto.html#howto-externalize-configuration))

This way the tokens would be store in a safe manner.  

## Financial Reporting (Braintree) Configuration

The Hyperwallet Mirakl Connector has the ability to generate a financial report, compiling
information from the Mirakl and Braintree platforms.

For enabling this functionality you will need to add the `financial-report` value to
the `PAYPAL_SPRING_PROFILE_ACTIVE`, for example: `PAYPAL_SPRING_PROFILE_ACTIVE=dev,financial-report`.

## Setting up jobs

The Hyperwallet Mirakl Connector runs jobs to perform various integrations between the Hyperwallet
and Mirakl platforms.

* Individual sellers extract job: Extracts the individual seller information from Mirakl and creates
  it on Hyperwallet.
* Professional sellers extract job: Extracts the professional seller information from Mirakl and
  creates it on Hyperwallet.
* Bank Accounts job sellers extract job: Extracts the bank detail information from sellers and
  creates a bank on account Hyperwallet associated to the corresponding user in Hyperwallet
* Documents extract job: Extracts the documents from Mirakl and pushes them into Hyperwallet for KYC
  purposes.

Those jobs are currently setup across the properties file as this table follows:

|                       Property                                        |  Cron expression  |                  Properties file                     |
| --------------------------------------------------------------------- | ----------------- | ---------------------------------------------------- |
| `sellers.extractsellers.scheduling.cronexpression`                    | 0 0 0 1/1 * ? *   | `sellers/src/main/resources/sellers.properties`      |
| `sellers.extractprofessionalsellers.scheduling.cronexpression`        | 0 0 0 1/1 * ? *   | `sellers/src/main/resources/sellers.properties`      |
| `sellers.bankaccountextract.scheduling.cronexpression`                | 0 30 0 1/1 * ? *  | `sellers/src/main/resources/sellers.properties`      |
| `invoices.extractinvoices.scheduling.cronexpression`                  | 1 0 0 1/1 * ? *   | `invoices/src/main/resources/invoices.properties`    |
| `kyc.documentsextract.scheduling.cronexpression`                      | 1 30 0 1/1 * ? *  | `kyc/src/main/resources/kyc.properties`              |

The existing jobs can be executed manually through their endpoints. All endpoints support 2 optional
parameters:

* `delta`: When provided for an extract job, the job will only process entities that were
  updated/created after this date
* `name` : When provided, the job will be given this name

|                       Param                                           |            Format              |
| --------------------------------------------------------------------- | -----------------------------  |
| `name`                                                                | String                         |
| `delta`                                                               | yyyy-MM-dd'T'HH:mm:ss.SSSXXX   | 

Endpoints:

|  HTTP Method   |                   PATH              |            Job type            | 
| -------------- | ----------------------------------- | -----------------------------  |
| `POST`         | `/job/sellers-extract`              | Individual Sellers extract     |
| `POST`         | `/job/professional-sellers-extract` | Professional Sellers extract   |
| `POST`         | `/job/bank-accounts-extract`        | Bank accounts extract          |
| `POST`         | `/job/invoices-extract`             | Invoices extract               |
| `POST`         | `/job/documents-extract`            | Documents extract              |

See example of valid execution request:

```curl --location --request POST 'http://localhost:8080/job/bank-accounts-extract?delta=2020-11-22T11:52:00.000-00:00&name=bankAccountExtractJob'```

## Webhook Notifications

The Hyperwallet platform is capable of sending event notifications via webhook. This connector comes
with a built-in listener to process supported webhook notification types, and works with both basic
authentication and payload encryption.

The endpoint for the webhook listener is on the path: `/webhooks/notifications`. This path is used
by default, and no properties or configuration are used for enabling or setting up the webhook
listener.

During the on-boarding process, Hyperwallet will enable webhook notifications by registering the
webhook listener endpoint URL (for example, https://hmc.example.com/webhooks/notifications).

## Payload Encryption

This connector supports payload encryption for connecting with Hyperwallet's
API (https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption). This payload
encryption feature is based on JOSE (https://jose.readthedocs.io/en/latest/) and
JWT (https://jwt.io/).

If you need further information, consult the Hyperwallet v4 API reference
documentation (https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption).

### Setting up JWK key sets

To communicate with the connector Hyperwallet needs to retrieve a jwk key set and this set of keys
should be published in an endpoint with a valid TLS certificate, it is needed that you generate one
key for signing and another one for encrypting the messages.

You can generate the keys via this website: https://mkjwk.org/

Supported sign algorithms (JWS):

- RS256, RS384, RS512
- PS256, PS384, PS512
- ES256, ES384, ES512

Supported JWE encryption algorithms are:

- RSA-OAEP-256
- ECDH-ES, ECDH-ES+A128KW, ECDH-ES+A192KW, ECDH-ES+A256KW

Once you have generated both keys you need create 2 files, one with only the public keys and another
one containing both public and private keys, like the following examples

```json lines
{
{
  "p": "9mH5gBqS-HuYT7K8XTwtvDgJjKJSQ7r3sfAdke0R4xrA1heQQBOCol0TSbnpcxvDNSF89NWSN2regHr3GdjVYrG1SX5jIqwnpKQX79mRURJb0dOuD5QOfUW8J7dhOdBnvE49S-JNTeR4jty2YS1Lj3x-eQyKJWuTkVJiblPmG1s",
  "kty": "RSA",
  "q": "mHWzxs3nS6z7eUtwxJhhzkhf_bsgEWGhtqHkXRRFutCAGOxsUiNOIn8yQGZfSbX7Jc5nGRT8h6r8Gar6Tiyn_uLtWIsBwzypVtFGKAcHboxa4_8TbRPB66Fh_H65LLMiCLxkOADIDFW1-wv5muEYU1dqSdUkv6Gqp--g82DBqjM",
  "d": "cMNoZadBA3M6h-VGD14b07flMuYSey7KO9lOk3yomyxbHt5i7jBJ1W0V1FHYnVIjR7ufubHcsCYjqeVtgCmJtu4a5nCLP_v3iIEm3uV5f627Rknyxe7hNPd6v0BBnCHMjRkM38OhSaB1IYOrl5ElA2a4dLKRRKlRz1g5OaxMw_36jvv555p03eRwMUg5W-lEP7iTl4aaEeh60TV2KAKCM1lD4-2UMb-G7H2DHWA72xMB1oZLiDbPIQCh8uvI7KTl3FdpZ5mYMuRtCpfkhIbxIVW5wpfFDasPOxklhnUr7f6OH9MKJFJ7UpkV79Uv7W8iTEtTiVQ6gCUeRrRNRM2sRQ",
  "e": "AQAB",
  "use": "sig",
  "qi": "j5bvMljGOUbBppAsoPhb9QTKUPsCQcAOoT3gYAGTD-mCUHNHO3BR3NZtOnoHVAShFWQRuB8jIiMIOZs52tzwCFnOBLn7Org2LN3GC5ntASDT-Vvizv_iYKJQcvLGuqNIpZMxil5t4wSM8ZSqw8F8xipsIgNtjhkO-xhyPEQBj4U",
  "dp": "XiAN3kfIsA8foArrdT8BRe-ujkCQ5vktmZfe5BnKhJV66A92d1Q8yuR98uOIcQZBLDIP98UDqBI20KSpdAFne93iISKcoulb98UMs__NSUiXNXEGBUONyYPznsSq6xhYGRNWzX-2ArTu7b0aG8PfIwDvnDUTLbqMVK9BlV6OARM",
  "alg": "RS256",
  "dq": "Bvxlt3dZ4NrVTxKI4UlGOgiQ9XRsnL9HhLHYX_d81nmVVQ8IS584hlYjvFW0ihEfp_TUPo76n1DTam2uOITNUd2eGI-ODh8qd0LxnwXrbkJaK9ZVUos0OJLVhZdc7tJqfdH8GaDXidEnnJBI6LLlxXPc9_MfUvSaeEV_r4dAeIc",
  "n": "krtz8O66BGcsKSui7N-5f9amdWvcZ-Fofgh6_WDgGAQ4ZVcaw61klX4boKngBjxGqWhF2H5fdPJFKHXWuY0gPkEfBg-iJvDv2qJQYZKwdBbjVnDbF63v97-1yIUtHNepGOOPoan-GvqMxpUl3mfjHJHRPpx4vs4AgGJJJwoPr_RLXzhVkfQMuen_HTbuHh0GMumYb1wWcTTy4SEakWuX_dga8WQDhg--kBTNgSAOJa6KuVy6R6CMaG87FzoGXa_wLrRMwDiEr7FfFAZSiTv1Yhbb2E3PAXd-gtBV7iIEPz8xrh-BNc-Jflckwkgel7HZ6NLMFb7_GS-Y5EHPRJzhIQ"
},
{
"p": "4UTkd95iNPs1eBqBm1zNlRAB0MiWZxpq7RGJq9n1bCURJB3e5BH_Ye-bdEoC_wL2ovgIvTZwL1EubyhCTl6FFeK_Kgdx3KhqydM_vi8Gry1H3z1GAKtIo_718wh3BWedqjsKLxKvcR0q4_syLJvdWshQ_2LkYItGu9Gks3ZqY8M",
"kty": "RSA",
"q": "trseuEzp8oMcxkpC0SHOuA54xh00647iOA6ijJ5piUmgJai1H1WFVxjqbhwu5p7m77lrXQIZfjYXCEUrLPb685v7D3C7bYeK2yfF8OKrPVdqR3lhRZx4BgtP6xSem1LeqjaxDDOR5DQ6dnnpGnY-q3B5dN8jBU67487_70_Vvbs",
"d": "UnW76DYdu9JtyCMXgmsNm7haei8rYrQFpb991pU-Jko5zs4ZAgCzyTDRg28Evl16zaacSeqS-MvEJS4f7K_xKUZYF7GZqr7h0CkXqawgckzsddPTuPMYLgd4iR0DTmVmKxTf90AGLQBH739flGJBNHwdgemyLeEO0tdJu73KqW1WO8HKMQFVgFd4jtMrGPnM5I0272GotGtLeew2FXmXUbGJrqe93xH8l8EuiCbdr2KUMsTnWrt7f0l8Hb2k6RAnPRl3pGb0id8awnbhnllK7faRB-J0ByED87QeQp_DAR4Vo463lKjFcl3t0h6Z7I9yFXs8ZdsRv31-JSPR4HN1lQ",
"e": "AQAB",
"use": "enc",
"qi": "N9Z0zqxWlx638Nnwp1bY2j6fFO6UTgA3A6lw2mstNxRxq1CBPdvrXgMtlZQDTHCxC6fuigajUu4aMLYMz_eUWgSOI9LgGgfc-CqO-OInRt-ctYdb5_AqyWv5Fy-6sorYJGehadkJ3WgdAaRu9VO3GJm4zLf2x_e_UIXIS1Z8ITY",
"dp": "sNoAL6cUHJoXeSn-FHXAJEWD12CSy3Du_x0koxkjVvqmwV7-DLmgcEfHGH2-amvuKzVkzv89BbjLJpNJsvL7spnoEFv15REHfVlukqWirsZyxWz6Vy4hgjZ46or7ve-B1RIyxY_60mfes0sTMfhPyKS5CjaeKrlNF8jXb9kIXzc",
"alg": "RSA-OAEP-256",
"dq": "URkO80C_e7AQ7zg6G1LjyfAyTcrcl8bcQ4DLR5luwU150_ziFbwA57zZHnFHp3bSi4ZBThAGfGtJIZSBKv2aNs_9RscDiALl74nhYZ1X3muTcZE_SIO_CP-wQmbuVYUb6XNIdF_W2e8MG1TTzbi466GJZgM6KbrdzKcsE2vdMs0",
"n": "oMuiyFuh1oyq-cSw-EXk1BKKkpwGBDcejNERhv09mF2o0taKCUKUIn6RzoI8qDsd66xhSdaV4fbSMN9uM5DteiKLwdPgnt1PDLRWw3dOXAI2-FT06G58VVaaIIGF1Xy7mGbC65BBDprzycrH0p3aCt81bvs5jvkYwxpWHEkz19Giba6rYVoNMnKy84nTWR5t3_eG_YC84Y-A63268ITlwErdeoKmiVBkMW6lpgNi5Vi6r2PdKP90KbgZEdbE3ci8cXyho33ke9Zjmbo5CaiMqvmjBNSYVcqDfQIo5y3Y23XagivtHI_42Tmp41H7uXsU89v-xCtFXJkaNIjfOZzWcQ"
}
}
```

### Public keys

```json lines
{
{
  "kty": "RSA",
  "e": "AQAB",
  "use": "sig",
  "alg": "RS256",
  "n": "krtz8O66BGcsKSui7N-5f9amdWvcZ-Fofgh6_WDgGAQ4ZVcaw61klX4boKngBjxGqWhF2H5fdPJFKHXWuY0gPkEfBg-iJvDv2qJQYZKwdBbjVnDbF63v97-1yIUtHNepGOOPoan-GvqMxpUl3mfjHJHRPpx4vs4AgGJJJwoPr_RLXzhVkfQMuen_HTbuHh0GMumYb1wWcTTy4SEakWuX_dga8WQDhg--kBTNgSAOJa6KuVy6R6CMaG87FzoGXa_wLrRMwDiEr7FfFAZSiTv1Yhbb2E3PAXd-gtBV7iIEPz8xrh-BNc-Jflckwkgel7HZ6NLMFb7_GS-Y5EHPRJzhIQ"
},
{
"kty": "RSA",
"e": "AQAB",
"use": "enc",
"alg": "RSA-OAEP-256",
}
}
```

Modify accordingly all the environment variables related with the encryption feature:

| NAME                                                  | ACCEPTED VALUES                                                                                                                                                                                                |
| ----------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `PAYPAL_HYPERWALLET_KEY_SET_LOCATION`                 | Hyperwallet's key set uri. Default value is `https://uat-api.paylution.com/jwkset`. For pointing to production, replace the value by `https://api.paylution.com/jwkset`  with `https://uat-api.paylution.com/jwkset`     |
| `PAYPAL_HYPERWALLET_ENCRYPTION_ALGORITHM`             | `RSA-OAEP-256`, `ECDH-ES`, `ECDH-ES+A128KW`, `ECDH-ES+A192KW`, `ECDH-ES+A256KW`|
| `PAYPAL_HYPERWALLET_SIGN_ALGORITHM`                   | `RS256`, `RS384`, `RS512`, `PS256`, `PS384`, `PS512`, `ES256`, `ES384`, `ES512` |
| `PAYPAL_HYPERWALLET_ENCRYPTION_METHOD`                | `A128CBC-HS256`, `A192CBC-HS384`, `A256CBC-HS512`, `A128GCM`, `A192GCM`, `A256GCM`|
| `PAYPAL_HYPERWALLET_PRIVATE_JWK_JSON_LOCATION`        | The private and public JWK set location  `/your/path/to/private/keys/jwk_set.key`                                                                                                                                  |
| `PAYPAL_HYPERWALLET_PUBLIC_JWK_JSON_LOCATION`         | Connector's  public JWK set location  `/your/path/to/public/keys/jwk_set.key`                                                                                                                                                   |

Following this previous example the environment variables values for UAT would be:

```properties
`PAYPAL_HYPERWALLET_KEY_SET_LOCATION          = https://uat-api.paylution.com/jwkset
`PAYPAL_HYPERWALLET_ENCRYPTION_ALGORITHM`     = RSA-OAEP-256
`PAYPAL_HYPERWALLET_SIGN_ALGORITHM            = RS256
`PAYPAL_HYPERWALLET_ENCRYPTION_METHOD`        = A256CBC-HS512
`PAYPAL_HYPERWALLET_PRIVATE_JWK_JSON_LOCATION = /your/path/to/private/keys/jwk_set.key
`PAYPAL_HYPERWALLET_PUBLIC_JWK_JSON_LOCATION  = /your/path/to/public/keys/jwk_set.key
```

By default, and under the encrypted profile, the connector allows you to share your public keys
throughout this endpoint: ```/jwkset```

Take into account that this file can also be published in a different server than the connector (
like an S3 bucket) and you'll simply need to modify the `hyperwallet.api.hmcPublicKeyLocation` with
the proper URL where this file is published.

**_IMPORTANT: Publish publicly only the PUBLIC keys JSON file_**

For enabling the encryption payload feature you will also need to enable in `application.properties`
file the profile `encrypted`, e.g. for a development machine:

`spring.profiles.active=dev,encrypted`

Notice also that Hyperwallet enables the possibility of having the webhook notifications encrypted,
if you have asked this feature to be enabled, the connector will take care of decrypting the
notifications whenever the profile `encrypted` is set.

## Other endpoints

### Health check

The connector exposes via `spring-boot-actuator` library a health check endpoint under
route `/actuator/health` that will return an object like this whenever the server is up and running:

```json
{
  "status": "UP"
}
```

### Build and app information

For knowing the version of the connector you're running you can also query the URL `/actuator/info`
to return a version object:

```json
{
  "app": {
    "name": "Hyperwallet Mirakl Connector",
    "description": "Drop in connector for interconnecting Mirakl and Hyperwallet systems"
  },
  "build": {
    "artifact": "web",
    "name": "web",
    "time": "2021-06-25T12:55:52.428Z",
    "version": "release-3.0-5-ga3d1009.dirty",
    "group": "com.paypal"
  }
}
```

