import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import IndexView from "../views/IndexView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: IndexView,
  },
  {
    path: "/:category",
    component: IndexView,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
