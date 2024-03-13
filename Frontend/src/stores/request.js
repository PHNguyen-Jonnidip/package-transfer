import { reactive } from 'vue'
import axios from "axios";
import router from "@/routes/routes";

// console.log(this.process.env)

const axiosInstance = axios.create({
  baseURL: process.env.VUE_APP_BACKEND_BASE_URL,
  timeout: 60000,
});

axiosInstance.interceptors.request.use(function (config) {
  config.timeData = {startTime: new Date()};
  if (localStorage.getItem('access_token')) {
    config.headers['Authorization'] = 'Bearer ' + localStorage.getItem('access_token');
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

axiosInstance.interceptors.response.use(function (response) {
  response.config.timeData.endTime = new Date();

  const responseTime = response.config.timeData.endTime - response.config.timeData.startTime;
  response.config.response_time = responseTime;

  const responseDebugMessage = `${response.config.method.toUpperCase()} ${response.config.url} responded with status ${response.status} in ${responseTime}ms`;

  return response;
}, function (error) {
  const response = error.response;
  if (response === undefined) {
    return;
  }
  response.config.timeData.endTime = new Date();

  const responseTime = response.config.timeData.endTime - response.config.timeData.startTime;
  response.config.response_time = responseTime;

  const responseDebugMessage = `${response.config.method.toUpperCase()} ${response.config.url} responded with status ${response.status} in ${responseTime}ms`;


  if ([401, 403].indexOf(response.status) >= 0) {
    router.push('/');
  }

  return Promise.reject(error);
});

export const request = reactive({
  axiosInstance
})
