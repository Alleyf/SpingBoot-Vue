import {createRouter, createWebHistory} from "vue-router";


// 创建一个路由实例

const router = createRouter({
    // 创建一个web历史记录
    history: createWebHistory(import.meta.env.BASE_URL),
    // 路由配置
    routes: [
        {
            // 根路由
            path: '/',
            name: 'welcome',
            // 组件
            component: () => import('@/views/WelcomeView.vue'),
            // 子路由
            children: [
                {
                    path: '',
                    name: 'login',
                    component: () => import('@/views/welcome/Login.vue'),
                }
            ]
        }
    ]
})

// 导出路由实例
export default router