import {createApp} from 'vue'
import App from './App.vue'
import router from "@/router";

// 创建一个应用实例
const app = createApp(App)
// 使用路由
app.use(router)
// 挂载应用
app.mount('#app')