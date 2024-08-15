<template>
  <div>
    <div v-if="users.length === 0">
      Loading...
    </div>

    <table v-if="users.length > 0" class="table-auto">
      <thead>
        <tr>
          <th class="px-4 py-2">Name</th>
          <th class="px-4 py-2">Email</th>
          <th class="px-4 py-2">Phone</th>
          <th class="px-4 py-2">Website</th>
        </tr>
      </thead>

      <tbody>
        <tr v-bind:key="user.name" v-for="user in users">
          <td class="border px-4 py-2">{{ user.name }}</td>
          <td class="border px-4 py-2">{{ user.email }}</td>
          <td class="border px-4 py-2">{{ user.phone }}</td>
          <td class="border px-4 py-2">{{ user.website }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import axios from "axios";
export default {
  name: "UsersGrid",
  data: function() {
    return {
      users: []
    };
  },
  methods: {
    async fetchUsers() {
      const { data } = await axios.get(
        "https://jsonplaceholder.typicode.com/users"
      );
      this.users = data;
    }
  },
  async mounted() {
    await this.fetchUsers();
  }
};
</script>
