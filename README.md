> [!TIP]
> For a multi-tenant implementation checkout [multi-tenant](tree/multi-tenant) branch.

# Features

1. Multi-instance
2. Merchant chooses version of Shoperal to use
3. Settings object

## Skaffold

- `skaffold dev --skip-tests`

## Security (GraphQL)

- https://netflix.github.io/dgs/
- https://dimitr.im/graphql-spring-security
- https://github.com/spring-projects/spring-graphql/tree/main/samples

## AOP (Webhooks)

- https://www.javatpoint.com/spring-boot-aop

## Jamstack Ecommerce (using TailwindCSS)

- https://github.com/jamstack-cms/jamstack-ecommerce
- https://aws.amazon.com/blogs/startups/building-your-app-from-idea-to-mvp-part-2/
- https://dev.to/dabit3/the-complete-guide-to-user-authentication-with-the-amplify-framework-2inh

## Tenant versioning

k8s `Deployment` and `Namespace`. We will also need a Shoperal Controller, that can send update notifications 
to merchants.

## APIs for extensibility

- Enable an API or interface to plug-in different authentication providers [Keycloak](https://www.keycloak.org/docs/latest/server_admin/#_user-storage-federation)
- Enable an API or interface to plug-in different authorization providers (e.g. OPA)

## Submodule Cheat Sheet

- `git submodule update --init --recursive`: Pull submodules
- `git submodule update --remote --merge`: Fetch remote submodule changes and merge with local changes
- `git submodule update --remote --rebase`: Fetch remote submodule changes and rebase with local changes
- `git push --recurse-submodules=check`: Ensures push fails if any updated submodule hasn't been pushed
- `git push --recurse-submodules=on-demand`: Recursively push all submodule before pushing this commit
- `git submodule foreach 'git stash'`: Run 'git stash' in each submodule
- `git submodule foreach 'git checkout -b featureA'`: Run 'git checkout -b featureA' in each submodule
- `git diff; git submodule foreach 'git diff'`: A unified diff 


# Improve Startup time

- https://github.com/dsyer/spring-boot-startup-bench
- https://stackoverflow.com/questions/27230702/speed-up-spring-boot-startup-time
- https://dzone.com/articles/how-to-delay-connection-acquisition-until-its-real
- https://github.com/liquibase/liquibase/issues/1312
