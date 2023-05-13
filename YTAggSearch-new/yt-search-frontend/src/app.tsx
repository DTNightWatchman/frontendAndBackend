import { PageLoading,  } from '@ant-design/pro-components';


// const isDev = process.env.NODE_ENV === 'development';
// const loginPath = '/user/login';

/** 获取用户信息比较慢的时候会展示一个 loading */
export const initialStateConfig = {
  loading: <PageLoading />,
};

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */

