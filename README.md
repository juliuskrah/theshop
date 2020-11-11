# TheShop

TheShop is a very basic Multi-Tenant e-shop demonstrating how to build a polyglot system.
This sample application demonstrates how to build a multi-tenant with the tenancy handled
at the Identity Server (Openid-Connect) and Database layer.

## Setting Up

You'd need to create the following DNS entries in your `/etc/hosts` file:

- [x] Windows: `C:\Windows\System32\drivers\etc\hosts`
- [x] Linux:   `/etc/hosts`

> You must have `Administrator` or `sudo` privileges to edit this file

- 127.0.0.1       theshop.com
- 127.0.0.1       dulcet.theshop.com
- 127.0.0.1       aparel.theshop.com
- 127.0.0.1       resk.theshop.com

## Building and Running

If you're using the Visual Studio Code `devcontainer` extension, just run `Remote-Containers: Reopen in Container`.

Otherwise start up the containers with ``docker-compose up -d`. This will startup `postgres`, `nginx` and
`keycloak`.

Build all projects with

```bash
> gradlew assemble
```

You need to run the `tasks` subproject to run the database migrations

```bash
> gradlew :tasks:bootRun
```

After that run the storefront

```bash
> gradlew :shops:bootRun
```

If the application fails to run

1. Confirm the database migrations run. The database should be running on port `8080` with `julius:julius123` 
   as `username:password` combination.
2. Confirm the keycloak realm `tenant` is created and a confidential client `shop_service` is created. Copy 
   the client secret and update the property `spring.security.oauth2.client.registration.shop.client-secret=:id`.

## Database Multi-tenancy

At the database layer, multi-tenancy can be handled in one of three ways:

1. **Tenant per database** - Each tenant has his own database
2. **Tenant per schema** - Each tenant has his own schema, sharing the same database
3. **Tenant shares database, schema and tables** - In this approach, each tenant does not have a unique
   database or schema, but joins the same pool of data, with each tenant's data identified by a
   discriminator column in the table

## Architecture and Approah

-- TODO image here

I decided to go with the first approach to data handling. Each tenant will have his own database.

### Services

I took it one step further, by creating a microservice based application, with each service owing its
data. Each service also has a database created per tenant.

### TODO
The following services will be created

1. Shops Service - Also serves as the entrypoint and catalog area. This list all the shops (tenants)
2. Payment Service
3. Orders Service
4. Basket Servoce
5. Location Service
6. Marketing Service
7. Tenants Service
