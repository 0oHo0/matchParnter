<template>
   <van-card
   v-for="user in userList"
  num="2"
  price="2.00"
  :desc="user.description"
  :title="user.username"
  :thumb="user.avatarUrl"
>
<template #tags>
    <van-tag v-for="tag in user.tags" plain type="danger" style="margin-right: 8x; margin-bottom: 8px;">{{ tag }}</van-tag>
  </template>
  <template #footer>
    <van-button size="mini">联系我</van-button>
  </template>
</van-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
const userResult = {
    id: 1,
    username: "duu",
    userAccount: '123',
    avatarUrl: 'http://gitlab.code-nav.cn/-/ide/project/root/user-center-frontend/tree/master/-/mock/user.ts/',
    gender: 1,
    phone: '23123',
    email: 'string@example.com',
    description : 'hhhh',
    userStatus: 1,
    userRole: 1,
    userCode: 123,
    tags: ['java', 'python'],
    createTime: new Date(),
};

const userList = ref({ userResult });
</script>

<style scoped></style>