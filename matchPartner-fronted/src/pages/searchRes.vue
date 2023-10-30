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
import { useRoute } from 'vue-router';
import myaxios from '../axios/myaxios';
import qs from 'qs';
import { onMounted } from 'vue';
const route = useRoute();
const userList = ref({});
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

onMounted(async() => {
  const res = await myaxios.get('/user/tag',{
    params: {
      tags: route.query.tags
    },
  paramsSerializer: params => {
    return qs.stringify(params, {indices: false})
    }
  })
  if (res.data) {
    res.data.forEach(user => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = res.data;
  }
});

</script>

<style scoped></style>