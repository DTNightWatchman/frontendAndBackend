<template>
  <a-layout class="layout" >

    <a-layout-header style="background-color:white">

      <div class="logo" >
        <a-row type="flex">
          <a-col flex="50px">
            <a-avatar
              :size="{ xs: 34, sm: 42, md: 42, lg: 42, xl: 42 , xxl: 42 }"
              src="https://i.imgtg.com/2023/03/09/Yl5qq.png"
            />
          </a-col>
          <a-col flex="auto">
            <div>
              <a-typography-title :level="4" style="line-height: 3.0;">聚合搜索平台</a-typography-title>
            </div>
          </a-col>
        </a-row>
      </div>


    </a-layout-header>
    <a-layout-content style="padding: 0 50px">
      <MyDivider />
      <div class="index-page" :style="{ background: '#fff', padding: '24px', minHeight: '570px' }">
        <a-input-search
          v-model:value="searchParams.text"
          placeholder="input search text"
          enter-button="Search"
          size="large"
          @search="onSearch"
        />
        <a-card :bordered="false">
          <a-tabs v-model:activeKey="activeKey" @change="changeTab">
            <a-tab-pane key="postList" tab="帖子列表">
              <PostList :post-list="postList" />
            </a-tab-pane>
            <a-tab-pane key="pictureList" tab="图片列表" force-render>
              <PictureList :picture-list="pictureList" />
            </a-tab-pane>
            <a-tab-pane key="userList" tab="用户列表">
              <UserList :user-list="userList" />
            </a-tab-pane>
            <a-tab-pane key="apiList" tab="api搜索结果">
              <ApiList :api-list="apiList" />
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </div>

    </a-layout-content>
    <a-layout-footer style="text-align: center">
      Ant Design ©2018 Created by YT摆渡人
    </a-layout-footer>
  </a-layout>

</template>

<script setup lang="ts">
import { ref, watchEffect, provide, watch } from "vue";
import PictureList from "@/components/PictureList.vue";
import UserList from "@/components/UserList.vue";
import PostList from "@/components/PostList.vue";
import ApiList from "@/components/ApiList.vue";
import MyDivider from "@/components/MyDivider.vue";
import { useRoute, useRouter } from "vue-router";
import myAxios from "@/plugins/myAxios";
import { message } from "ant-design-vue";


const router = useRouter();
const route = useRoute();

const postList = ref([]);

const pictureList = ref([]);

const userList = ref([]);

const apiList = ref([]);

const loading = ref<boolean>(true);

const loadAllData = (params: any) => {

  const query = {
    ...params,
    searchText : params.text,
  }

  myAxios.post('/search/all', query)
    .then((res: any) => {
      console.log(res);
      userList.value = res.userList;
      pictureList.value = res.pictureList;
      postList.value = res.postList;
    });
}


const activeKey = ref(route.params.category);


/**
* 加载单类模式
* @param params
 */
const loadData = (params: any) => {
  const type = activeKey.value;
  if (!type) {
    message.error("类别为空");
    return;
  }
  const query = {
    ...params,
    type : type,
    searchText : params.text,
  }

  myAxios.post('/search/all', query)
    .then((res: any) => {
      if (type === 'postList') {
        postList.value = res.dataList;
      } else if (type === 'userList') {
        userList.value = res.dataList;
      } else if (type === 'pictureList') {
        pictureList.value = res.dataList;
      }
      console.log(res);
    });
}



const initSearchParams = {
  type: activeKey.value,
  text: route.query.text ?? '',
  pageSize : 10,
  pageNum: 1
};

const searchParams = ref(initSearchParams);





myAxios.get('http://localhost:8081/api/doc/search?query=' + route.query.text + '&pageNum=1&pageSize=5')
  .then((res: any) => {
    console.log(res);
    apiList.value = res;
  });
const changeType = (type: String) => {
  loadData(searchParams.value);
}

// watchEffect(() => {
//   // searchParams.value = {
//   //   ...initSearchParams,
//   // } as any;
//   //loadData(searchParams.value);
//   activeKey.value;
// });

const onSearch = (value: String) => {
  router.push({
    query:{
      text: value,
    }
  })
  loadData(searchParams.value);
}

const changeTab = (key: string) => {
  router.push({
    path: `/${key}`,
    query: searchParams.value,
  })
  loadData(searchParams.value)
}

</script>


<style>
.site-layout-content {
  min-height: 280px;
  padding: 24px;
  background: #fff;
}
#components-layout-demo-top .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}
.ant-row-rtl #components-layout-demo-top .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

[data-theme='dark'] .site-layout-content {
  background: #141414;
}
</style>
