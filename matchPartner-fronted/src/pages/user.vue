<template>
    <van-cell title="昵称" is-link to="/user/edit" :value="user.username" @click="edit('昵称', 'username', user.username)" />
    <van-cell title="账号" :value="user.userAccount" />
    <van-cell title="头像" is-link to="/user/edit" @click="edit('头像', 'avatarUrl', user.avatarUrl)">
        <van-image width="48px" fit="contain" src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg" />
    </van-cell>
    <van-cell title="性别" :value="user.gender" />
    <van-cell title="电话" is-link to="/user/edit" :value="user.phone" @click="edit('电话', 'phone', user.phone)" />
    <van-cell title="邮箱" is-link to="/user/edit" :value="user.email" @click="edit('邮箱', 'email', user.email)" />
    <van-cell title="用户编号" :value="user.userCode" />
    <van-cell title="注册时间" :value="user.createTime.toLocaleDateString()" />
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
const router = useRouter();
const user = {
    id: 1,
    username: "duu",
    userAccount: '123',
    avatarUrl: 'http://gitlab.code-nav.cn/-/ide/project/root/user-center-frontend/tree/master/-/mock/user.ts/',
    gender: 1,
    phone: '23123',
    email: 'string@example.com',
    userStatus: 1,
    userRole: 1,
    userCode: 123,
    tags: ['java', 'python'],
    createTime: new Date(),
};

const edit = (keyName: string, key: string, value: string) => {
    router.push({
        path: '/user/edit',
        query: {
            keyName,
            key,
            value
        }
    });
}
</script>

<style scoped></style>