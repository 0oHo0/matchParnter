<template>
    <van-form @submit="onSubmit">
        <van-field v-model="editUser.keyvalue" :name="editUser.key" :label="editUser.keyName"
            :placeholder="editUser.keyName" :rules="[{ required: true, message: `请输入${editUser.keyName}` }]" />
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import myaxios from '../axios/myaxios';
import {getCurrentUser} from "../services/user.ts";
import router from '../route/route';
import {showFailToast, showSuccessToast} from "vant";
const route = useRoute();

const editUser = ref({
    keyvalue: route.query.keyvalue,
    key: route.query.key,
    keyName : route.query.keyName
})

const onSubmit = async() => {
    const currentUser = await getCurrentUser();
    const res = await myaxios.post('/user/update', {
        'id': currentUser.id,
        [editUser.value.key as string]: editUser.value.keyvalue
    });
    if (res.code == 0 && res.data != null) {
        showSuccessToast('修改成功');
        router.back();
    } else {
        showFailToast('修改失败');
    }
};
</script>

<style scoped></style>