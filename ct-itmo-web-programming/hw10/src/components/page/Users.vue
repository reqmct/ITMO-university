<template>
    <div class="users datatable" v-if="users">
        <div class="caption">Users</div>
        <thead>
        <tr>
            <th>Id</th>
            <th>Login</th>
            <th>Name</th>
        </tr>
        </thead>
        <tbody>
        <UserLine v-for="user in users" :user="user" :key="user.id"/>
        </tbody>
    </div>
    <div v-else>
        No users
    </div>
</template>

<script>
import UserLine from "@/components/singles/UserLine.vue";

export default {
    name: "Users",
    components: {UserLine},
    props: ["users"]
}
</script>

<style scoped>

</style>