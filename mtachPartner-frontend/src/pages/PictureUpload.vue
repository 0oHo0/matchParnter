<template>
  <van-uploader v-model="fileList" :after-read="afterRead" multiple :max-count="1" />
</template>
<script setup lang="ts">
import myAxios from "../plugins/myAxios";
import {ref} from "vue";

const fileList = ref([
]);

const afterRead = async (file) => {
  file.status = 'uploading';
  file.message = '上传中...';
  const forms = new FormData();
  forms.append("file", file.file);
  const res = await myAxios.post('/common/upload',forms, {
    headers: {
      "content-type": "multipart/form-data",
    },
  })
  if(res.code == 0 && res.data){
    file.status = 'succeed';
    file.message = '上传成功';
  }else{
    file.status = 'failed';
    file.message = '上传失败';
  }
};
</script>



<style scoped>

</style>