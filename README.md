# Hyperwallet Mirakl Connector

The Hyperwallet Mirakl Connector (HMC) is a service that integrates the Hyperwallet and Mirakl platforms.

## Getting Started - Local Development

This is a multi-module Gradle project composed by Spring Boot modules.

### Requirements for local execution/deployment

* Java 15
* RabbitMQ

### Requirements for Docker execution/deployment

* Docker

### Installation Steps

- Download the repo
- Acquire a Mirakl login account to access the Mirakl Artifactory https://artifactory.mirakl.net/artifactory/mirakl-ext-repo/. See the "Access to Artifactory" topic on https://hyperwallet-dev.mirakl.net/help/Customers/topics/Connectors/SDK/java/access_java_sdk.html
    - These account credentials will be used for setting the Mirakl SDK User and Mirakl SDK Password environment variables

- Define the following environment variables with the relevant values:

| ENVIRONMENT VARIABLE                                     | DESCRIPTION                                                                                                                                                                         | EXAMPLE VALUE                              |
|----------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------|
| `PAYPAL_MIRAKL_SDK_USER`                                 | Mirakl username for accessing the Mirakl Java SDK.                                                                                                                                  | `yourCompanyName`                          |
| `PAYPAL_MIRAKL_SDK_PASSWORD`                             | Mirakl password, for accessing the Mirakl Java SDK.                                                                                                                                 | `secret`                                   |
| `PAYPAL_MIRAKL_OPERATOR_API_KEY`                         | The operator API key generated for your operator account.                                                                                                                           | `c262b297-c8a7-45a5-a22f-a0d9fe25132a`     |
| `PAYPAL_MIRAKL_ENVIRONMENT`                              | The URL for your Mirakl environment's API (provided by Mirakl).                                                                                                                     | `https://yourCompany.mirakl.net/api`       |
| `PAYPAL_HYPERWALLET_API_SERVER`                          | The URL for your Hyperwallet environment's API (provided by Hyperwallet).                                                                                                           | `https://uat-api.paylution.com`            |
| `PAYPAL_HYPERWALLET_API_USERNAME`                        | Hyperwallet environment username (provided by Hyperwallet).                                                                                                                         | `restapiuser@000001`                       |
| `PAYPAL_HYPERWALLET_API_PASSWORD`                        | Hyperwallet environment password (provided by Hyperwallet).                                                                                                                         | `yourSecret`                               |
| `PAYPAL_HYPERWALLET_PROGRAM_TOKENS`                      | A set of comma separated values based on the program hierarchy provided by Hyperwallet (See the Program Configuration section below).                                               | `DEFAULT`                                  |
| `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_USERS_DEFAULT`         | The program token for contacting the Hyperwallet API on the `/users` path for `DEFAULT` program.                                                                                    | `prg-6541532-as1a23s242-12as124-as2454`    |
| `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_PAYMENTS_DEFAULT`      | The program token for contacting the Hyperwallet API on the `/payments` path for `DEFAULT` program.                                                                                 | `prg-54545a532-asda2refs43-as2fd35-das233` |
| `PAYPAL_SERVER_EMAIL_HOST`                               | The URL where your POP3/SMTP server is hosted. If using the Docker Compose script provided in this repo, use `smtp`.                                                                | `smtp.example.com`                         |
| `PAYPAL_SERVER_EMAIL_PORT`                               | The port used by your POP3/SMTP server. If using the Docker Compose script provided in this repo, use `1025`.                                                                       | `1025`                                     |
| `PAYPAL_MAIL_SMTP_AUTH`                                  | Whether or not authentication is needed for accessing the POP3/SMTP mail server.                                                                                                    | Possible values: `true` or `false`         |
| `PAYPAL_MAIL_USER_NAME`                                  | The username credential for using the POP3/SMTP server. Can be left empty if `PAYPAL_MAIL_SMTP_AUTH` is set to `false`.                                                             | `smtp-username`                            |
| `PAYPAL_MAIL_USER_PASSWORD`                              | The password credential for using the POP3/SMTP server. Can be left empty if `PAYPAL_MAIL_SMTP_AUTH` is set to `false`.                                                             | `smtp-pass`                                |
| `PAYPAL_MAIL_SMTP_STARTTLS_ENABLE`                       | Whether or not TLS is needed for establishing connection with the POP3/SMTP server.                                                                                                 | Possible values:`true` or `false`          |
| `PAYPAL_HYPERWALLET_OPERATOR_BANK_ACCOUNT_TOKEN_DEFAULT` | The transfer bank account token where commissions will be paid out too. Only needed if the operator commissions feature is being used (see the Operator Commissions section below). | `trm-2646asas54-21asdas5642-xasa45sxx`     |
| `PAYPAL_RABBITMQ_USERNAME`                               | The username for connecting with the RabbitMQ host.                                                                                                                                 | `username`                                 |
| `PAYPAL_RABBITMQ_PASSWORD`                               | The password for connecting with the RabbitMQ host.                                                                                                                                 | `password`                                 |
| `PAYPAL_SPRING_PROFILE_ACTIVE`                           | The Spring Profile to execute/deploy the service on. Possible values: `dev`, `qa`, `prod`, followed by any feature profiles documented below. Default: `dev`.                       | `qa,financial-report`                      |
| `PAYPAL_MOCK_SERVER_URL`                                 | The URL to your webhook/mock server. Only used when running with the `QA` Spring profile.                                                                                           | `https://mockserver.example.com`           |

For a local execution you will need to build the connector and start it up with the following commands:

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

This is defined in Mirakl using the `hw-program` shop custom field (see the Mirakl Configuration section in the Solution Guide), which for a single hierarchy program should contain a single value list with only one value `DEFAULT`.

### Multiple Programs

The Hyperwallet, Mirakl, and HMC configurations can be extended to accommodate a multiple program hierarchy structure, where 1 Issuing Merchant can have multiple Issuing Stores.

Based on Hyperwallet's configuration, it will be necessary to modify Hyperwallet Program configuration. 

By default, HMC supports just one. Just in case it is needed multiple values, we need to do some easy modifications. 

Let put this example:

* We have two different Hyperwallet programs: UK and Europe.
* We defined in Mirakl a custom attribute which label is `hw-program` as SingleValueList with these values: `EUROPE` and `UK`

In that case, we need to setup these variables as described:

* Environment:
    * Define variable `PAYPAL_HYPERWALLET_PROGRAM_TOKENS` with value `UK,EUROPE`

* File: `invoices.properties`:
    * Remove `invoices.hyperwallet.api.hyperwalletprogram.token.DEFAULT` property
    * Remove `invoices.operator.commissions.bankAccount.token.DEFAULT` property
    * Define token for UK: `invoices.hyperwallet.api.hyperwalletprogram.token.UK=<YOUR_UK_TOKEN>`
    * Define token for EUROPE: `invoices.hyperwallet.api.hyperwalletprogram.token.EUROPE=<YOUR_EUROPE_TOKEN>`
    * Define the operator bank account token for
      UK: `invoices.operator.commissions.bankAccount.token.UK = <YOUR_UK_BANK_ACCOUNT_TOKEN>`
    * Define the operator bank account token for
      EUROPE: `invoices.operator.commissions.bankAccount.token.EUROPE = <YOUR_EUROPE_BANK_ACCOUNT_TOKEN>`

* File: `kyc.properties`:
    * Remove `kyc.hyperwallet.api.hyperwalletprogram.token.DEFAULT`
    * Define token for UK: `kyc.hyperwallet.api.hyperwalletprogram.token.UK=<YOUR_UK_TOKEN>`
    * Define token for EUROPE: `kyc.hyperwallet.api.hyperwalletprogram.token.EUROPE=<YOUR_EUROPE_TOKEN>`

* File: `seller.properties`:
    * Remove `sellers.hyperwallet.api.hyperwalletprogram.token.DEFAULT`
    * Define token for UK: `sellers.hyperwallet.api.hyperwalletprogram.token.UK=<YOUR_UK_TOKEN>`
    * Define token for EUROPE: `sellers.hyperwallet.api.hyperwalletprogram.token.EUROPE=<YOUR_EUROPE_TOKEN>`

If you're using Docker, remember to update the Docker Compose template file to reflect the existence of these 2 new environments. 
Add them into the Docker Compose template file you're using (`docker-compose.prod.yml.template` or `docker-compose.yml.template`), for example with UK and Europe:

- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_PAYMENTS_UK`
- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_USERS_UK`
- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_PAYMENTS_EUROPE`
- `PAYPAL_HYPERWALLET_PROGRAM_TOKEN_USERS_EUROPE`

## Financial Reporting (Braintree)

The Hyperwallet Mirakl Connector has the ability to generate a financial report, compiling information from the Mirakl and Braintree platforms. 

For enabling this functionality you will need to add the `financial-report` value to the `PAYPAL_SPRING_PROFILE_ACTIVE`, for example: `PAYPAL_SPRING_PROFILE_ACTIVE=dev,financial-report`.

The following environment variables are required when enabling this Spring Profile:

- `PAYPAL_BRAINTREE_MERCHANT_ID=<YOUR_BRAINTREE_MERCHANT_ID>`
- `PAYPAL_BRAINTREE_PUBLIC_KEY=<YOUR_BRAINTREE_PUBLIC_KEY>`
- `PAYPAL_BRAINTREE_PRIVATE_KEY=<YOUR_BRAINTREE_PRIVATE_KEY>`

The default setup uses `SANDBOX` environment, for using this feature on production remember to modify the file
`reports/src/main/resources/reports.properties` and uncomment this line `#reports.braintree.environment=production`

## Setting up jobs

The Hyperwallet Mirakl Connector runs jobs to perform various integrations between the Hyperwallet and Mirakl platforms.

* Individual sellers extract job: Extracts the individual seller information from Mirakl and create it on Hyperwallet.
* Professional sellers extract job: Extracts the professional seller information from Mirakl and create it on Hyperwallet.
* Bank Accounts job sellers extract job: Extracts the bank detail information from sellers nad creates a bank on account hyperwallet associated to the corresponding user in hyperwallet
* Documents extract job: Extracts the documents from Mirakl and pushes them into Hyperwallet for kyc purposes.

Those jobs are currently setup across the properties file as this table follows:

|                       Property                                        |  Cron expression  |                  Properties file                     |
| --------------------------------------------------------------------- | ----------------- | ---------------------------------------------------- |
| `sellers.extractsellers.scheduling.cronexpression`                    | 0 0 0 1/1 * ? *   | `sellers/src/main/resources/sellers.properties`      |
| `sellers.extractprofessionalsellers.scheduling.cronexpression`        | 0 0 0 1/1 * ? *   | `sellers/src/main/resources/sellers.properties`      |
| `sellers.bankaccountextract.scheduling.cronexpression`                | 0 30 0 1/1 * ? *  | `sellers/src/main/resources/sellers.properties`      |
| `invoices.extractinvoices.scheduling.cronexpression`                  | 1 0 0 1/1 * ? *   | `invoices/src/main/resources/invoices.properties`    |
| `kyc.documentsextract.scheduling.cronexpression`                      | 1 30 0 1/1 * ? *  | `kyc/src/main/resources/kyc.properties`              |

The existing jobs can be executed manually through their endpoints. All endpoints support 2 optional parameters:

* `delta`: When provided for an extract job, the job will only process entities that were updated/created after this date
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

The Hyperwallet platform is capable of sending event notifications via webhook.
This connector comes with a built-in listener to process supported webhook notification types, and works with both basic authentication and payload encryption.

The endpoint for the webhook listener is on the path: `/webhooks/notifications`. 
This path is used by default, and no properties or configuration are used for enabling or setting up the webhook listener.

During the onboarding process, Hyperwallet will enable webhook notifications by registering the webhook listener endpoint URL (for example, https://hmc.example.com/webhooks/notifications).

## Payload Encryption

This connector supports payload encryption for connecting with Hyperwallet's API (https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption).
This payload encryption feature is based on JOSE (https://jose.readthedocs.io/en/latest/) and JWT (https://jwt.io/).

If you need further information, consult the Hyperwallet v4 API reference documentation (https://docs.hyperwallet.com/content/api/v4/overview/payload-encryption)

### Setting up JWK key sets

To communicate with the connector Hyperwallet needs to retrieve a jwk key set and this set of keys should be published in an endpoint with a valid TLS certificate, it is needed that you generate one key for signing and another one for encrypting the messages.

You can generate the keys via this website: https://mkjwk.org/

Supported sign algorithms (JWS):

- RS256, RS384, RS512
- PS256, PS384, PS512
- ES256, ES384, ES512

Supported JWE encryption algorithms are:

- RSA-OAEP-256
- ECDH-ES, ECDH-ES+A128KW, ECDH-ES+A192KW, ECDH-ES+A256KW

Once you have generated both keys you need create 2 files, one with only the public keys and another one containing both public and private keys, like the following examples

```
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

By default, and under the encrypted profile, the connector allows you to share your public keys throughout this endpoint: ```/jwkset```

### Public keys

``` 
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

Modify the property `hyperwallet.api.hmcKeySetLocation` of `application.properties` to point to the route where the file with private/public keys JSON file is stored.

Modify accordingly the properties `hyperwallet.api.encryptionAlgorithm`, `hyperwallet.api.signAlgorithm`, and `hyperwallet.api.encryptionMethod` with the encryption, sign and algorithm method you want to use.

Following this previous example the values would be:

```properties
hyperwallet.api.encryptionAlgorithm = RSA-OAEP-256
hyperwallet.api.signAlgorithm       = RS256
hyperwallet.api.encryptionMethod    = A256CBC-HS512
```

The public JSON file can be published directly in the connector specifying the file route where this file is stored via `hyperwallet.api.hmcPublicKeyLocation` stored in the `application.properties` file.

Take into account that this file can also be published in a different server than the connector (like an S3 bucket) and you'll simply need to modify the `hyperwallet.api.hmcPublicKeyLocation` with the proper URL where this file is published.

**_IMPORTANT: Publish publicly only the PUBLIC keys JSON file_**

For enabling the encryption payload feature you will also need to enable in `application.properties` file the profile `encrypted`, e.g. for a development machine:

`spring.profiles.active=dev,encrypted`

By default, the connector is pointing to Hyperwallet's UAT JWK key set url, for production you'll need to uncomment the following line:

```properties
# Uncomment for PRODUCTION
#hyperwallet.api.hwKeySetLocation = https://api.paylution.com/jwkset
```

Notice also that Hyperwallet enables the possibility of having the webhook notifications encrypted, if you have asked this feature to be enabled, the connector will take care of decrypting the notifications whenever the profile `encrypted` is set.

## Other endpoints

### Health check

The connector exposes via `spring-boot-actuator` library a health check endpoint under route `/actuator/health` that will return an object like this whenever the server is up and running:

```json
{
  "status": "UP"
}
```

### Build and app information

For knowing the version of the connector you're running you can also query the URL `/actuator/info` to return a version object:

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

