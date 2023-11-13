import {createApp} from 'vue'
import App from './App.vue'
import router from "@/router";
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from "axios";

axios.defaults.baseURL = "http://localhost:8080/"
// 创建一个应用实例
const app = createApp(App)
// 使用路由
app.use(router)
// 遍历ElementPlusIconsVue对象，将其中的属性作为组件，并将其挂载到应用中
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
// 挂载应用
app.mount('#app')