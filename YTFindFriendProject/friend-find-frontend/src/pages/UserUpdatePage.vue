<template>
  <template v-if="user">
    <van-cell title="昵称" is-link to="/user/edit" :value="user.username"  @click="toEdit('username', '昵称', user.username)"/>
    <van-cell title="账号" :value="user.userAccount"/>
    <van-cell title="头像" is-link to="" @click="show=true">
      <img style="height: 48px" :src="user.avatarUrl"/>
    </van-cell>
    <van-cell title="性别" is-link :value="user.gender === 0 ? '男':'女'" @click="toEditGender('gender', '性别', user.gender)"/>
    <van-cell title="电话" is-link to="/user/edit" :value="user.phone" @click="toEdit('phone', '电话', user.phone)"/>
    <van-cell title="邮箱" is-link to="/user/edit" :value="user.email" @click="toEdit('email', '邮箱', user.email)"/>
    <van-cell title="注册时间" :value="dateUtil.formatDate(new Date(user.createTime))"/>
  </template>

  <van-dialog v-model:show="show" title="修改头像" show-cancel-button width="240">
    <van-uploader style="margin-left: 30px" v-model="fileList" preview-size="180" multiple :max-count="1" />
  </van-dialog>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import {getCurrentUser} from "../services/user";
import {DateUtil} from "../utils/DateUtil";

const fileList = ref([]);

const dateUtil = new DateUtil();

const show = ref(false);

const user = ref();

onMounted(async () => {
  user.value = await getCurrentUser();
})

const router = useRouter();



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

const toEditGender = (editKey: string, editName: string, currentValue: string) => {
  router.push({
    path: '/user/edit/gender',
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