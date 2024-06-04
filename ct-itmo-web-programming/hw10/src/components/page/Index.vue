<template>
    <div>
        <Post v-for="post in posts" :post="post" :comments="postComments(post.id)" :key="post.id"/>
    </div>
</template>

<script>


import Post from "@/components/page/Post.vue";

export default {
    name: "Index",
    components: {Post},
    props: ["posts", "comments"],
    methods: {
        postComments: function (postId) {
            return Object.values(this.comments).filter(comment => comment.postId === postId);
        }
    }
}
</script>

<style scoped>

</style>
