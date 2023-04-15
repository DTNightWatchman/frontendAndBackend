<template>
  <a-list :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 3, xxl: 3 }" :data-source="props.userList">
    <template #renderItem="{ item }">
      <a-card hoverable style="width: 240px">
        <template #cover>
          <img alt="example" :src=item.userAvatar />
        </template>
        <a-card-meta :title=item.userName>
          <template #description>{{ item.userProfile }}</template>
        </a-card-meta>
      </a-card>
    </template>
  </a-list>




</template>

<script setup lang="ts">
import { withDefaults, defineProps } from "vue";

interface Props {
  userList: any[];
}

const props = withDefaults(defineProps<Props>(), {
  userList: () => [],
})
</script>

<style scoped>

</style>