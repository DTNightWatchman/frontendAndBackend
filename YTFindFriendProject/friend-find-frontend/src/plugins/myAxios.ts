import axios, {AxiosInstance} from "axios";
const myAxios: AxiosInstance = axios.create({
    baseURL: 'http://127.0.0.1:8080/api'
});
myAxios.defaults.withCredentials = true;

// Add a request interceptor
myAxios.interceptors.request.use(function (config) {
    console.log('我要发请求啦', config)
    // Do something before request is sent
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
myAxios.interceptors.response.use(function (response) {
    console.log('我收到你的响应啦', response)
    // Do something with response data
    if (response?.data?.code === 40100) {
        // 重定向
        const redirectUrl = window.location.href;
        window.location.href = `/#/user/login?redirectUrl=${redirectUrl}`;
    }
    return response.data;
}, function (error) {
    // Do something with response error
    return Promise.reject(error);
});

export default myAxios;