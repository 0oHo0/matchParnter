<template>
  <user-card-list :user-list="userList" :loading="loading"/>
  <van-empty v-if="!userList || userList.length < 1" description="数据为空"/>
</template>

<script setup lang="ts">
import {onMounted, ref, watchEffect} from 'vue';
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import UserCardList from "../components/UserCardList.vue";
import {UserType} from "../models/user";
import {useRoute} from "vue-router";

const isMatchMode = ref<boolean>(false);

const userList = ref([]);
const loading = ref(true);
const route = useRoute();

/**
 * 加载数据
 */
onMounted(async () => {
  let userListData;
  userListData = await myAxios.get('/team/cat', {
    params: {
      teamId: route.query.teamId,
    },
  })
      .then(function (response) {
        console.log('/user/recommend succeed', response);
        loading.value=false;
        return response?.data;
      })
      .catch(function (error) {
        console.error('/user/recommend error', error);
        Toast.fail('请求失败');
      });
  console.log(userListData);
  if (userListData) {
    userListData.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
    console.log(userList.value);
  }
})

</script>

<style scoped>

</style>
