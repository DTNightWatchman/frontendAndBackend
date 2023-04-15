<template>
  <a-list item-layout="horizontal" :data-source="props.postList">
    <template #renderItem="{ item }">
      <a-list-item>
        <a-list-item-meta
          :description= item.content
        >
          <template #title>
            <a href="https://www.antdv.com/">{{ item.title }}</a>
          </template>
          <template #avatar>
            <a-avatar src="https://i.imgtg.com/2023/03/07/YKfSG.png" />
          </template>
        </a-list-item-meta>
      </a-list-item>
    </template>
  </a-list>
  {{JSON.stringify(props.postList)}}
</template>

<script setup lang="ts">

import { withDefaults, defineProps } from "vue";

interface Props {
  postList: any[];
}

const props = withDefaults(defineProps<Props>(), {
  postList: () => [],
})


</script>

<style scoped>

</style>