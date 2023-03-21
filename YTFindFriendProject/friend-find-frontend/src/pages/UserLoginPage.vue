<template>
  <van-form style="margin-top: 45%; text-align:center" @submit="onSubmit">

    <h2>找伙伴系统</h2>
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="请输入账号"
          :rules="[{ required: true, message: '请填写用户名' }]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="userPassword"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios";
import {showSuccessToast, Toast} from "vant/es";

const router = useRouter();

const userAccount = ref('');
const userPassword = ref('');
const route = useRoute();
const onSubmit = async () => {
  const res:any = await myAxios.post('/user/login', {
    userAccount: userAccount.value,
    userPassword: userPassword.value,
  })
  console.log(res, '用户登录');
  if (res.code === 0 && res.data) {
    showSuccessToast('登录成功');
    const redirectUrl = route.query?.redirectUrl as string ?? '/';
    window.location.replace(redirectUrl);
  } else {
    Toast.fail('登录失败');
  }
};

</script>

<style scoped>

</style>