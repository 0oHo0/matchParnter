<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入要搜索的标签"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签</div>
  <van-row gutter="16" style="padding: 0 16px">
    <van-col v-for="tag in activeIds">
      <van-tag closeable size="small" type="primary" @close="doClose(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />
  <div style="padding: 12px">
    <van-button block type="primary" @click="doSearchResult">搜索</van-button>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {useRouter} from "vue-router";

const router = useRouter()

const searchText = ref('');

const originTagList = [
  {
    text: 'FPS',
    children: [
      {text: 'CSGO', id: 'CSGO'},
      {text: 'Valorant', id: 'Valorant'},
      {text: 'CF', id: 'CF'},
      {text: 'OW', id: 'OW'},
      {text: 'CSOL', id: 'CSOL'},
      {text: '逆战', id: '逆战'},
    ],
  },
  {
    text: 'MOBA',
    children: [
      {text: 'LOL', id: 'LOL'},
      {text: 'DOTA', id: 'DOTA'},
      {text: '王者荣耀', id: '王者荣耀'},
    ],
  },
  {
    text: '二次元',
    children: [
      {text: '原神', id: '原神'},
      {text: '崩坏', id: '崩坏'},
      {text: '明日方舟', id: '明日方舟'},
      {text: '阴阳师', id: '阴阳师'},
    ],
  },
  {
    text: '生存类',
    children: [
      {text: 'PUBG', id: 'PUBG'},
      {text: 'APEX', id: 'APEX'},
      {text: '永劫无间', id: '永劫无间'},
      {text: '和平精英', id: '和平精英'},
    ],
  },
  {
    text: '策略类',
    children: [
      {text: '云顶之奕', id: '云顶之奕'},
      {text: '金铲铲', id: '金铲铲'},
      {text: '三国杀', id: '三国杀'},
      {text: '炉石传说', id: '炉石传说'},
    ],
  },
]

// 标签列表
let tagList = ref(originTagList);

/**
 * 搜索过滤
 * @param val
 */
const onSearch = (val) => {
  tagList.value = originTagList.map(parentTag => {
    const tempChildren = [...parentTag.children];
    const tempParentTag = {...parentTag};
    tempParentTag.children = tempChildren.filter(item => item.text.includes(searchText.value));
    return tempParentTag;
  });

}
const onCancel = () => {
  searchText.value = '';
  tagList.value = originTagList;
};

// 已选中的标签
const activeIds = ref([]);
const activeIndex = ref(0);

// 移除标签
const doClose = (tag) => {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag;
  })
}

/**
 * 执行搜索
 */
const doSearchResult = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeIds.value
    }
  })
}

</script>

<style scoped>

</style>
