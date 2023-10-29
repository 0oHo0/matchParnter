import axios from 'axios'

const myaxios = axios.create({
    baseURL: 'http://localhost:8100/api',
    timeout: 1000,
});
myaxios.defaults.withCredentials = true; // 配置为true
// 添加请求拦截器
myaxios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
myaxios.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    return response.data;
  }, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});
  
export default myaxios;