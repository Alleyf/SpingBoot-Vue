/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */
import {defaultFailure, delAccessToken, get, internalPost, post, storeAccessToken} from "@/utils/request"
import {ElMessage} from "element-plus";
import {reactive} from "vue";


export function login(loginForm, success, failure = defaultFailure) {
    // alert(JSON.stringify(loginForm))
    internalPost("api/auth/login", loginForm, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(data.token, loginForm.remember, data.expire)
        ElMessage.success(`登陆成功，欢迎 ${data.username} 来到我的世界，娇贵的小公主！`)
        success(data)
    }, failure)
}

export function logout(success, failure = defaultFailure) {
    get("api/auth/logout", () => {
        delAccessToken()
        ElMessage.success(`登出成功，欢迎下次再来！`)
        success()
    }, failure)
}

export function register(registerForm, success, failure = defaultFailure) {
    post("api/auth/register", registerForm, () => {
        ElMessage.success(`注册成功，欢迎 ${registerForm.username} 加入我的世界，娇贵的小公主！`)
        success()
    }, failure)
}

export function getVerifyCode(email, type, success, failure = defaultFailure) {
    get("api/auth/getVerifyCode?email=" + email + "&type=" + type, () => {
        ElMessage.success(`验证码已发送至 ${email} 邮箱，请注意查收`)
        success()
    }, failure)
}

export function confirmEmail(confirmForm, success, failure = defaultFailure) {

    post("api/auth/reset-confirm", confirmForm, () => {
        ElMessage.success(`用户身份验证通过，请进入下一步重置密码`)
        success()
    }, failure)
}

export function resetPassword(resetForm, success, failure = defaultFailure) {

    post("api/auth/reset-password", resetForm, () => {
        ElMessage.success(`重置密码成功，请妥善保管您的密码，切勿泄露给他人`)
        success()
    }, failure)
}

export default [getVerifyCode, register, login, logout, confirmEmail, resetPassword]