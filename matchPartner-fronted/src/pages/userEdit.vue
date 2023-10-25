<template>
    <van-form @submit="onSubmit">
        <van-field v-model="username" :name="route.query.key" :label="route.query.keyName"
            :placeholder="route.query.keyName" :rules="[{ required: true, message: `请输入${route.query.keyName}` }]" />
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();
const username = ref(route.query.value);
const onSubmit = (val) => {
    console.log(val);
};
</script>

<style scoped></style>