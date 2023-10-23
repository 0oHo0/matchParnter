<template>
    <van-cell title="昵称" is-link to="/user/edit" :value="user.username" />
    <van-cell title="账号" :value="user.userAccount" />
    <van-cell title="头像" is-link to="/user/edit">
        <van-image
        width="48px"
  fit="contain"
  src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
/>
    </van-cell>
    <van-cell title="性别" is-link to="/user/edit" :value="user.gender" />
    <van-cell title="电话" is-link to="/user/edit" :value="user.phone" />
    <van-cell title="邮前" is-link to="/user/edit" :value="user.email" />
    <van-cell title="用户编号" :value="user.userCode" />
    <van-cell title="注册时间" :value="user.createTime.toLocaleDateString()" />
</template>

<script setup lang="ts">
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
</script>

<style scoped></style>