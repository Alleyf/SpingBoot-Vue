<!--
  - Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
  -->

<script setup>
import {logout} from "@/api/auth";
import router from "@/router";

function userLogout() {
  logout(() => {
    console.log("退出登录成功")
    router.push('/')
  })
}
</script>

<template>
  <div>
    <el-button @click="userLogout">退出登录</el-button>
  </div>
</template>

<style scoped>
</style>
