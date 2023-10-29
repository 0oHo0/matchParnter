<template>
<van-form @submit="onSubmit">
  <van-cell-group inset>
    <van-field
      v-model="username"
      name="username"
      label="账号"
      placeholder="账号"
      :rules="[{ required: true, message: '请填写账号' }]"
    />
    <van-field
      v-model="password"
      type="password"
      name="password"
      label="密码"
      placeholder="密码"
      :rules="[{ required: true, message: '请填写密码' }]"
    />
  </van-cell-group>
  <div style="margin: 16px;">
    <van-button round block type="primary" native-type="submit">
      提交
    </van-button>
  </div>
</van-form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import myaxios from '../axios/myaxios'
import { useRouter } from 'vue-router';
import {showFailToast, showSuccessToast} from "vant";
import { getCurrentUserState, setCurrentUserState } from '../states/user';
const router = useRouter();
const username = ref('');
const password = ref('');

const onSubmit = async () => {
  const res = await myaxios.post('/user/login', {
    userAccount: username.value,
    userPassword: password.value,
  })
  console.log(res.data, '用户登录');
  if (res.code === 0 && res.data) {
    showSuccessToast('登录成功');
    setCurrentUserState(res.data);
    console.log(getCurrentUserState());
    // 跳转到之前的页
    router.replace('/');
  } else {
    //Toast.fail('登录失败');
    showFailToast('登录失败');
  }
};
</script>

<style scoped>

</style>