<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="userPhone"
          name="userPhone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请填写手机号' }]"
      />
      <van-field
    v-model="sms"
    center
    clearable
    label="短信验证码"
    placeholder="请输入短信验证码"
  >
    <template #button>
      <van-button size="small" type="primary">发送验证码</van-button>
    </template>
  </van-field>
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        登录
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";

const router = useRouter();
const route = useRoute();

const userPhone = ref('');
const sms = ref('');
const onSubmit = async () => {
  const res = await myAxios.post('/user/login/phone', {
    userPhone: userPhone.value,
    sms: sms.value,
  })
  console.log(res, '用户登录');
  if (res.code === 0 && res.data) {
    Toast.success('登录成功');
    router.replace('/');
  } else {
    Toast.fail('登录失败');
  }
};

</script>

<style scoped>

</style>
