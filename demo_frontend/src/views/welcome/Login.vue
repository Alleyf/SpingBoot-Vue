<!--
  - Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
  -->

<script setup>
import {reactive, ref} from "vue";
import {login} from "@/api/auth";
import {ElMessage} from "element-plus";
import router from "@/router";

const formRef = ref()

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const rule = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur'}
  ]
}

function userLogin() {
  formRef.value.validate((valid) => {
    if (valid) {
      login(loginForm, (data) => {
        // alert(JSON.stringify(data))
        router.push("index")
      })
    } else {
      ElMessage.error('请按照要求的格式输入用户名和密码')
      return false
    }
  })
}
</script>

<template>
  <div style="text-align:center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div>
        <el-text class="mx-1" style="font-size: 25px;font-weight: bold" type="primary">登录</el-text>
      </div>
      <div>
        <el-text class="mx-1" style="margin-top: 10px;font-size: 15px;font-weight: lighter;color: rgb(128,128,128)"
                 type="info">
          进入系统之前，首先输入用户名和密码进行登录
        </el-text>
      </div>
    </div>
    <div style="margin-top: 50px">
      <el-form ref="formRef"
               :model="loginForm"
               :rules="rule"
               style="max-width: 400px"
      >
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" clearable maxlength="20" placeholder="用户名/邮箱" prefix-icon="User"
                    type="text"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" clearable maxlength="15" placeholder="密码" prefix-icon="Key"
                    show-password type="password"></el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left;">
            <el-form-item prop="remember">
              <el-checkbox v-model="loginForm.remember" label="记住我" size="large"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link type="warning" @click="router.push('/reset')">忘记密码？</el-link>
          </el-col>
        </el-row>


      </el-form>
    </div>
    <div>
      <el-button style="width: 270px;margin: 20px 0" type="success" @click="userLogin">立即登录</el-button>
    </div>
    <el-divider>
      <el-text class="mx-1" type="info">没有账号？</el-text>
    </el-divider>
    <div>
      <el-button plain style="width: 270px;margin-top: 20px" type="primary" @click="router.push('/register')">立即注册
      </el-button>
    </div>
  </div>
</template>

<style scoped>

</style>