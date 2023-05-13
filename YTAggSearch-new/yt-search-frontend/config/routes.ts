export default [
  // {
  //   path: '/user',
  //   layout: false,
  //   routes: [
  //     { name: '登录', path: '/user/login', component: './user/Login' },
  //     { component: './404' },
  //   ],
  // },
  { path: '/search', name: '搜索', icon: 'smile', component: './search' },
  // {
  //   path: '/admin',
  //   name: '管理页',
  //   icon: 'crown',
  //   access: 'canAdmin',
  //   routes: [
  //     { path: '/admin/sub-page', name: '二级管理页', icon: 'smile', component: './Index' },
  //     { component: './404' },
  //   ],
  // },
  // { name: '查询表格', icon: 'table', path: '/list', component: './TableList' },
  { path: '/', redirect: '/welcome' },
  { component: './404' },
];
