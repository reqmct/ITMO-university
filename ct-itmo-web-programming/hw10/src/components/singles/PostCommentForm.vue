<template>
    <form class="comment-form" @submit.prevent="onComment">
        <div class="form-group">
            <label for="comment">Comment:</label>
            <textarea id="comment" name="comment" rows="4" v-model="text"></textarea>
        </div>
        <div class="error">{{ error }}</div>
        <button type="submit">Submit</button>
    </form>
</template>

<script>
export default {
    name: "PostCommentForm",
    data: function () {
        return {
            text: "",
            error: ""
        }
    },
    props: ["postId"],
    methods: {
        onComment: function () {
            this.error = ""
            this.$root.$emit("onComment", this.text, this.postId);
        }
    },
    beforeMount() {
        this.$root.$on("onCommentValidationError", error => this.error = error);
    }
}
</script>

<style scoped>
.comment-form {
    width: 300px;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 10px;
}

label {
    display: block;
    font-weight: bold;
}

textarea {
    width: 100%;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

button[type="submit"] {
    padding: 10px 20px;
    background-color: #888888;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button[type="submit"]:hover {
    background-color: #45a049;
}
</style>