<template>
    <div class="comment">
        <h3>By {{ comment.userId }}</h3>
        <p>{{ comment.text }}</p>
    </div>
</template>

<script>
export default {
    name: "Comment",
    props: ["comment"]
}
</script>

<style scoped>
.comment {
    background-color: #f2f2f2;
    padding: 10px;
    border-radius: 5px;
    margin-bottom: 10px;
}

.comment h3 {
    color: #333;
    font-size: 16px;
    margin-bottom: 5px;
}

.comment p {
    color: #666;
    font-size: 14px;
    margin-bottom: 5px;
}

</style>