<template>
  <div>

    <a-list item-layout="horizontal" :data-source="props.apiList">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta>
            <template #title>
              <a target="_blank" :href=item.url>{{ item.title }}</a>
            </template>
            <template #avatar>
              <a-avatar src="https://i.imgtg.com/2023/03/07/YKfSG.png" />
            </template>
            <template #description>
              <div v-html="item.desc"></div>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </template>
    </a-list>
  </div>

</template>

<script setup lang="ts">

import { withDefaults, defineProps, watch } from "vue";

interface Props {
  apiList: any[];
  loading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  apiList: () => [],
})


import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import { defineComponent, ref } from 'vue';

interface DataItem {
  href: string;
  title: string;
  avatar: string;
  description: string;
  content: string;
}

const listData: DataItem[] = [];
for (let i = 0; i < 3; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
      'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
      'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

const onChange = (checked: boolean) => {

};


</script>

<style scoped>
strong {
  color: red;
}

</style>