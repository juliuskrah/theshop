<template>
  <div>
    <div v-if="$apollo.queries.node.loading">Loading...</div>
    <div v-if="!$apollo.queries.node.loading">{{ node.author.login }}</div>
  </div>
</template>

<script>
import gql from "graphql-tag";

export default {
  apollo: {
    // Simple query that will update the 'node' vue property
    node: {
      query: gql`
        query findLogin($id: ID!) {
          node(id: $id) {
            id
            ... on Issue {
              id
              author {
                login
              }
            }
          }
        }
      `,
      variables: { id: "MDU6SXNzdWUyMzEzOTE1NTE=" }
    }
  }
};
</script>
