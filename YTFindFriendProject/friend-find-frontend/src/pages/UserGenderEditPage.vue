<template>
  <van-form @submit="onSubmit">
    <van-field
        v-model="fieldValue"
        is-link
        readonly
        label="性别"
        placeholder="选择性别"
        @click="showPicker = true"
    />
    <van-popup v-model:show="showPicker" round position="bottom">
      <van-picker
          :columns="columns"
          @cancel="showPicker = false"
          @confirm="onConfirm"
      />
    </van-popup>
<!--      <van-field-->
<!--          v-model="editUser.currentValue"-->
<!--          :name="editUser.editKey"-->
<!--          :label="editUser.editName"-->
<!--          :placeholder="`请输入${editUser.editName}`"-->
<!--      />-->
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import { ref } from "vue";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import {getCurrentUser} from "../services/user";
import {showFailToast, showSuccessToast, showToast} from "vant/es";

const columns = [
  { text: '男', value: 0 },
  { text: '女', value: 1 },
];

const route = useRoute();
const router = useRouter();

const editUser = ref({
  editKey: route.query.editKey,
  currentValue: route.query.currentValue,
  editName: route.query.editName,
})

const fieldValue = ref((editUser.value.currentValue === '0' ? '男': '女'));
const showPicker = ref(false);

const onConfirm = ({ selectedOptions }) => {
  showPicker.value = false;
  fieldValue.value = selectedOptions[0].text;
  editUser.value.currentValue = selectedOptions[0].value
};




const onSubmit = async () => {

  const currentUser = await getCurrentUser();

  if (!currentUser) {
    Toast.fail('用户未登录');
    return;
  }

  console.log(currentUser, '当前用户')

  const res:any = await myAxios.post('/user/update', {
    'id': currentUser.id,
    [editUser.value.editKey as string]: editUser.value.currentValue,
  })
  console.log(res, '更新请求');
  if (res.code === 0 && res.data > 0) {
    showSuccessToast("修改成功");
    router.back();
  } else {
    showFailToast('修改错误');
  }
};

</script>

<style scoped>

</style>
