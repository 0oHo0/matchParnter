<template>
    <van-cell title="昵称" is-link to="/user/edit" :value="user?.username" @click="edit('昵称', 'username', user.username)" />
    <van-cell title="账号" :value="user?.userAccount" />
    <van-cell title="头像" is-link to="/user/edit" @click="edit('头像', 'avatarUrl', user?.avatarUrl)">
        <van-image width="48px" fit="contain" src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg" />
    </van-cell>
    <van-cell title="性别" :value="user?.gender" />
    <van-cell title="电话" is-link to="/user/edit" :value="user?.phone" @click="edit('电话', 'phone', user.phone)" />
    <van-cell title="邮箱" is-link to="/user/edit" :value="user?.email" @click="edit('邮箱', 'email', user.email)" />
    <van-cell title="用户编号" :value="user?.userCode" />
    <van-cell title="注册时间" :value="user?.createTime" />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { getCurrentUser } from '../services/user';

const router = useRouter();

// const userOrigin = {
//   id: 1,
//   username: '鱼皮',
//   userAccount: 'dogYupi',
//   avatarUrl: 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png',
//   gender: '男',
//   phone: '123112312',
//   email: '12345@qq.com',
//   userCode: '1234',
//   createTime: new Date(),
// }
const user = ref();

onMounted(async () => {
    user.value = await getCurrentUser();
})

const edit = (keyName: string, key: string, keyvalue: string) => {
    router.push({
        path: '/user/edit',
        query: {
            keyName,
            key,
            keyvalue
        }
    });
}
</script>

<style scoped></style>