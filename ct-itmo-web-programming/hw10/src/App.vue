<template>
    <div id="app">
        <Header :userId="userId" :users="users"/>
        <Middle :users="users" :posts="posts" :comments="comments" :userId="userId"/>
        <Footer :countUsers="countUsers" :countPosts="countPosts"/>
    </div>
</template>

<script>
import Header from "./components/Header";
import Middle from "./components/Middle";
import Footer from "./components/Footer";

export default {
    name: 'App',
    components: {
        Footer,
        Middle,
        Header
    },
    data: function () {
        return this.$root.$data;
    },
    computed: {
        countUsers: function () {
            return Object.values(this.users).length
        },
        countPosts: function () {
            return Object.values(this.users).length
        }
    },
    beforeCreate() {
        this.$root.$on("onEnter", (login, password) => {
            if (password === "") {
                this.$root.$emit("onEnterValidationError", "Password is required");
                return;
            }

            const users = Object.values(this.users).filter(u => u.login === login);
            if (users.length === 0) {
                this.$root.$emit("onEnterValidationError", "No such user");
            } else {
                this.userId = users[0].id;
                this.$root.$emit("onChangePage", "Index");
            }
        });

        this.$root.$on("onRegister", (login, name) => {
            if (login === "" || login.length < 3 || login.length > 16) {
                this.$root.$emit("onRegisterValidationError", "Incorrect size of login");
                return;
            }
            if (!/^[a-z]+$/.test(login)) {
                this.$root.$emit("onRegisterValidationError", "Only small latin characters in login");
                return;
            }
            if (login === "" || login.length < 1 || login.length > 32) {
                this.$root.$emit("onRegisterValidationError", "Incorrect size of name");
                return;
            }

            const users = Object.values(this.users).filter(u => u.login === login);
            if (users.length !== 0) {
                this.$root.$emit("onRegisterValidationError", "Login is already taken");
            } else {
                const id = Math.max(...Object.keys(this.users)) + 1;
                this.$root.$set(this.users, id, {
                    id, login, name, admin: false
                });
                this.userId = id;
                this.$root.$emit("onChangePage", "Index");
            }
        });

        this.$root.$on("onLogout", () => this.userId = null);

        this.$root.$on("onWritePost", (title, text) => {
            if (this.userId) {
                if (!title || title.length < 5) {
                    this.$root.$emit("onWritePostValidationError", "Title is too short");
                } else if (!text || text.length < 10) {
                    this.$root.$emit("onWritePostValidationError", "Text is too short");
                } else {
                    const id = Math.max(...Object.keys(this.posts)) + 1;
                    this.$root.$set(this.posts, id, {
                        id, title, text, userId: this.userId
                    });
                }
            } else {
                this.$root.$emit("onWritePostValidationError", "No access");
            }
        });

        this.$root.$on("onComment", (text, postId) => {
            if (this.userId) {
                if (!text || text.trim().length < 4) {
                    this.$root.$emit("onCommentValidationError", "Text is too short");
                    return;
                }
                if(!postId) {
                    this.$root.$emit("onCommentValidationError", "Can't find post");
                    return;
                }
                const id = Math.max(...Object.keys(this.comments)) + 1;
                this.$root.$set(this.comments, id, {
                    id, userId: this.userId, postId, text
                });
                const post = this.posts[postId];
                this.$root.$emit("viewPost", post);
            } else {
                this.$root.$emit("onCommentValidationError", "No access");
            }

        });

        this.$root.$on("onEditPost", (id, text) => {
            if (this.userId) {
                if (!id) {
                    this.$root.$emit("onEditPostValidationError", "ID is invalid");
                } else if (!text || text.length < 10) {
                    this.$root.$emit("onEditPostValidationError", "Text is too short");
                } else {
                    let posts = Object.values(this.posts).filter(p => p.id === parseInt(id));
                    if (posts.length) {
                        posts.forEach((item) => {
                            item.text = text;
                        });
                    } else {
                        this.$root.$emit("onEditPostValidationError", "No such post");
                    }
                }
            } else {
                this.$root.$emit("onEditPostValidationError", "No access");
            }
        });
    }
}
</script>

<style>
#app {

}
</style>
