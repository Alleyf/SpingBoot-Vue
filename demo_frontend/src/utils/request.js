/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

import axios from 'axios'
import {ElMessage} from "element-plus";

// const apiBaseUrl = "http://localhost:8080/"

const authToken = "access_token"


export function defaultFailure(data) {
    console.log(`请求地址： ${data.url}，状态码：${data.code}，错误信息： ${data.message}`)
    ElMessage.warning(data.message)
}

export function defaultError(err) {
    console.log(err)
    ElMessage.error("发生了一些错误，请联系管理员")
}

function getAccessToken() {
    //TODO 判断token是否过期，过期则返回空，否则返回token
    const auth_str = localStorage.getItem(authToken) || sessionStorage.getItem(authToken)
    if (auth_str) {
        const auth_obj = JSON.parse(auth_str)
        // alert(new Date() <= new Date(auth_obj.expire))
        if (new Date(auth_obj.expire) <= new Date().getTime()) {
            delAccessToken()
            ElMessage.warning("登录状态已过期，请重新登录")
            return null
        } else {
            return auth_obj.token
        }
    } else
        return null
}

function getAccessHeader() {
    const token = getAccessToken();
    return token ? {'Authorization': `Bearer ${token}`} : null
}

export function storeAccessToken(token, remember, expire) {
    const auth_obj = {'token': token, 'expire': expire}
    const auth_str = JSON.stringify(auth_obj)
    if (remember)
        localStorage.setItem(authToken, auth_str)
    else
        sessionStorage.setItem(authToken, auth_str)
}

export function delAccessToken() {
    localStorage.removeItem(authToken)
    sessionStorage.removeItem(authToken)
}

export function unAuthorized() {
    return !getAccessToken()
}

export function internalPost(url, data, header, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {
        headers: header
    }).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.code, data.message, url)
        }
    }).catch(err => error(err))
}

export function internalGet(url, header, success, failure = defaultFailure, error = defaultError) {
    // alert(JSON.stringify(header))
    axios.get(url, {
        headers: header
    }).then(({data}) => {
        // alert(JSON.stringify(data))
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data)
        }
    }).catch(err => error(err))
}

export function get(url, success, failure = defaultFailure, error = defaultError) {
    internalGet(url, getAccessHeader(), success, failure, error)
}

export function post(url, data, success, failure = defaultFailure, error = defaultError) {
    internalPost(url, data, getAccessHeader(), success, failure, error)
}

