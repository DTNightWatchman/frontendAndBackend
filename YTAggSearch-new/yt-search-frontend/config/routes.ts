export default [

  { path: '/welcome', layout: false, name: '欢迎', icon: 'smile', component: './search/SearchMain' },
  // {
  //   path: '/admin',
  //   name: '管理页',
  //   icon: 'crown',
  //   access: 'canAdmin',
  //   routes: [
  //     { path: '/admin/sub-page', name: '二级管理页', icon: 'smile', component: './Welcome' },
  //     { component: './404' },
  //   ],
  // },
  {  path: '/search', name: '搜索页面', icon: 'table', component: './search/SearchMain' },
  // { path: '/', redirect: '/welcome' },
  { component: './404' },

];
