<template>
  <div>
    <div v-if="users.length === 0">
      Loading...
    </div>

    <div>
      <div class="grid grid-cols-3 gap-4" v-if="users.length > 0">
        <div class="col-md-3" v-bind:key="user.Name" v-for="user in users">
          <user-card :user="user"></user-card>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import axios from "axios";
import UserCard from "./UserCard";

export default {
  data: function() {
    return {
      users: []
    };
  },
  components: { UserCard },
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
