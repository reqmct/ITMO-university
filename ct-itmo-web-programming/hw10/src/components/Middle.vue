<template>
    <div class="middle">
        <Sidebar :posts="viewPosts"/>
        <main>
            <Index v-if="page === 'Index'" :posts="posts" :comments="comments"/>
            <Enter v-if="page === 'Enter'"/>
            <Register v-if="page === 'Register'"/>
            <WritePost v-if="page === 'WritePost'"/>
            <EditPost v-if="page === 'EditPost'"/>
            <Users v-if="page === 'Users'" :users="users"/>
            <Post v-if="page === 'Post'" :post="post" :comments="postComments(post.id)" :showComments="true" :userId="userId"/>
        </main>
    </div>
</template>

<script>
import Sidebar from "./sidebar/Sidebar";
import Index from "./page/Index";
import Enter from "./page/Enter";
import WritePost from "./page/WritePost";
import EditPost from "./page/EditPost";
import Register from "@/components/page/Register.vue";
import Users from "@/components/page/Users.vue";
import Post from "@/components/page/Post.vue";

export default {
    name: "Middle",
    data: function () {
        return {
            page: "Index"
        }
    },
    components: {
        Post,
        Users,
        Register,
        WritePost,
        Enter,
        Index,
        Sidebar,
        EditPost
    },
    props: ["users", "posts", "comments", "userId"],
    methods: {
        postComments: function (postId) {
            return Object.values(this.comments).filter(comment => comment.postId === postId);
        }
    },
    computed: {
        viewPosts: function () {
            return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 2);
        }
    }, beforeCreate() {
        this.$root.$on("onChangePage", (page) => this.page = page);
        this.$root.$on("viewPost",
            (post) => {
                this.page = 'Post'
                this.post = post;
            });
    }
}
</script>

<style scoped>

</style>
