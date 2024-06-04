<template>
    <footer>
        <a href="/">Codeforces</a> &copy; 2022 by Mike Mirzayanov
        {{countUsers}} users, {{countPosts}} posts
    </footer>
</template>

<script>
export default {
    name: "Footer",
    props: ["countUsers", "countPosts"]
}
</script>

<style scoped>

</style>