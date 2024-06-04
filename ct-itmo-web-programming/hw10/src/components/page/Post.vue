<template>
    <article>
        <div class="title">
            <a href="#" @click.prevent="viewPost()">{{ post.title }}</a>
        </div>
        <div class="information">By bob</div>
        <div class="body">{{ post.text }}</div>
        <ul class="attachment">
            <li>Announcement of <a href="#">Codeforces Round #510 (Div. 1)</a></li>
            <li>Announcement of <a href="#">Codeforces Round #510 (Div. 2)</a></li>
        </ul>
        <div class="footer">
            <div class="left">
                <img src="../../assets/img/voteup.png" title="Vote Up" alt="Vote Up"/>
                <span class="positive-score">+173</span>
                <img src="../../assets/img/votedown.png" title="Vote Down" alt="Vote Down"/>
            </div>
            <div class="right">
                <img src="../../assets/img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                2 days ago
                <img src="../../assets/img/comments_16x16.png" title="Comments" alt="Comments"/>
                <a href="#">{{ comments.length }}</a>
            </div>
        </div>

        <div class="comments" v-if="showComments">
            <div class="comments-form" v-if="userId">
                <PostCommentForm :postId="post.id" :userId="userId"/>
            </div>
            <Comment v-for="comment in comments" :comment="comment" :key="comment.id"/>
        </div>
    </article>
</template>

<script>
import Comment from "@/components/singles/Comment.vue";
import PostCommentForm from "@/components/singles/PostCommentForm.vue";

export default {
    name: "Post",
    components: {PostCommentForm, Comment},
    props: ["post", "comments", "showComments", "userId"],
    methods: {
        viewPost: function () {
            this.$root.$emit("viewPost", this.post, this.comments);
        }
    }
}


</script>

<style scoped>
</style>