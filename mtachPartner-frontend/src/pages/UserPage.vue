<template>
  <template v-if="user">
    <van-cell title="当前用户" :value="user?.username" />
    <van-cell title="修改信息" is-link to="/user/update" />
    <van-cell title="我创建的队伍" is-link to="/user/team/create" />
    <van-cell title="我加入的队伍" is-link to="/user/team/join" />
    <div style="margin: 16px;">
      <van-button round block type="primary" @click="logout">
        登出
      </van-button>
    </div>
  </template>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import {getCurrentUser} from "../services/user";

// const user = {
//   id: 1,
//   username: '鱼皮',
//   userAccount: 'dogYupi',
//   avatarUrl: 'https://www.bing.com/images/search?view=detailV2&ccid=Zte3ljd4&id=3818C0A36E353844880BB3B0E3AF1C80A1CA0FB1&thid=OIP.Zte3ljd4g6kqrWWyg-8fhAHaEo&mediaurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.66d7b796377883a92aad65b283ef1f84%3frik%3dsQ%252fKoYAcr%252bOwsw%26riu%3dhttp%253a%252f%252fwww.quazero.com%252fuploads%252fallimg%252f140305%252f1-140305131415.jpg%26ehk%3dHxl%252fQ9pbEiuuybrGWTEPJOhvrFK9C3vyCcWicooXfNE%253d%26risl%3d%26pid%3dImgRaw%26r%3d0&exph=1200&expw=1920&q=%e5%9b%be%e7%89%87&simid=607997370600551753&FORM=IRPRST&ck=3DA6646B577B78EABE89C9523E494288&selectedIndex=0',
//   gender: '男',
//   phone: '123112312',
//   email: '12345@qq.com',
//   planetCode: '1234',
//   createTime: new Date(),
// }

const user = ref();
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  user.value = await getCurrentUser();
})
const logout = async () => {
  const res = await myAxios.post('/user/logout');
  if (res.code === 0 && res.data) {
    Toast.success('登出成功');
    // 跳转到之前的页面
    await router.replace('/user/login');
  } else {
    Toast.fail('登出失败');
  }
}
const toEdit = (editKey: string, editName: string, currentValue: string) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue,
    }
  })
}
</script>

<style scoped>

</style>
