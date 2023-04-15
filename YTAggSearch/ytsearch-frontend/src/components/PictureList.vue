<template>
  <a-list :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 4, xxl: 4 }" :data-source="props.pictureList">
    <template #renderItem="{ item }">
      <a-card hoverable style="width: 240px">
        <template #cover>
          <img style="height:100px;" alt="example" :src=item.url />
        </template>
        <a-card-meta :title=item.userName>
          <template #description>{{ item.title }}</template>
        </a-card-meta>
      </a-card>
    </template>
  </a-list>
</template>

<script setup lang="ts">
import { withDefaults, defineProps } from "vue";

interface Props {
  pictureList: any[];
}

const props = withDefaults(defineProps<Props>(), {
  pictureList: () => [],
})
</script>

<style scoped>

</style>