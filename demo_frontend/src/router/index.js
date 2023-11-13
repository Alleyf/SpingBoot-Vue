import {createRouter, createWebHistory} from "vue-router";
import {unAuthorized} from "@/utils/request";


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
            redirect: {name: 'welcome-login'},
            // 子路由
            children: [
                {
                    path: '/login',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/Login.vue'),
                },
                {
                    path: '/register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/Register.vue'),
                },
                {
                    path: '/reset',
                    name: 'welcome-reset',
                    component: () => import('@/views/welcome/ResetPwd.vue'),
                }
            ]
        },
        {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue')
        }
    ]
})

router.beforeEach((to, from, next) => {
    const isUnauthorized = unAuthorized()
    // alert(to.name)
    if (to.name.startsWith("welcome-") && !isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith("/index") && isUnauthorized) {
        next("/")
    } else {
        next()
    }
})
// 导出路由实例
export default router