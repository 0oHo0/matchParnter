<template>
    <van-nav-bar title="标题" right-text="" left-arrow @click-left="onClickLeft" @click-right="onClickRight">
        <template #right>
            <van-icon name="search" size="18" />
        </template>
    </van-nav-bar>
    <div class="content">
        <router-view />
    </div>
    <van-tabbar route v-model="active" @change="onChange">
        <van-tabbar-item to="/" icon="home-o">主页</van-tabbar-item>
        <van-tabbar-item to="/team" icon="search">队伍</van-tabbar-item>
        <van-tabbar-item to="/user" icon="friends-o">我的</van-tabbar-item>
    </van-tabbar>

</template>

<script setup lang="ts">

import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const onClickLeft = () => history.back();
const onClickRight = () => {
    router.push("/search");
};

const active = ref(0);
const onChange = () => (1);

</script>

<style scoped></style>