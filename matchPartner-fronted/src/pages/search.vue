<template>
    <form action="/">
        <van-search v-model="searchText" show-action placeholder="请输入搜索关键词" @search="onSearch" @cancel="onCancel" />
    </form>
    <van-divider content-position="left">文字</van-divider>
    <van-row gutter="16" style="padding: 0 16px;">
        <van-col v-for="tag in activeIds" style="padding: 5px;">
            <van-tag :show="show" closeable size="medium" type="primary" @close="closeTag(tag)">
                {{ tag }}
            </van-tag></van-col>
    </van-row>
    <van-divider content-position="left">文字</van-divider>
    <van-tree-select v-model:active-id="activeIds" v-model:main-active-index="activeIndex" height="90vw" :items="tagList" />
    <van-button icon="https://fastly.jsdelivr.net/npm/@vant/assets/user-active.png" type="primary" @click="search">
        按钮
    </van-button>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import router from '../route/route';
import myaxios from '../axios/myaxios';

const searchText = ref('');
const onCancel = () => {
    searchText.value = '';
    tagList.value = originalTagList;
};
const show = ref();
const activeIds = ref([]);
const activeIndex = ref(0);
const originalTagList = [
    {
        text: '年纪',
        children: [
            { text: '大一', id: '大一' },
            { text: '大二', id: '大二' },
            { text: '大三', id: '大三' },
            { text: '大四', id: '大四' },
            { text: '研一', id: '研一' },
            { text: '研二', id: '研二' },
            { text: '研三', id: '研三' },
        ],
    },
    {
        text: '江苏',
        children: [
            { text: '南京', id: 4 },
            { text: '无锡', id: 5 },
            { text: '徐州', id: 6 },
        ],
    },
];


const tagList = ref(originalTagList);
const onSearch = () => {
    tagList.value = originalTagList.map(parentTag => {
        const tempchildren = [...parentTag.children];
        const tempParentTag = { ...parentTag };
        tempParentTag.children = tempchildren.filter(item => item.text.includes(searchText.value));
        return tempParentTag;
    });
};
const closeTag = (tag: string) => {
    activeIds.value = activeIds.value.filter(activeId => {
        return activeId != tag;
    });
}
watchEffect(() => {
    onSearch();
});
const search = async() => {
    router.push({
        path: "/searchRes",
        query: {
            tags : activeIds.value
        }
    })
}
</script>

<style scoped></style>