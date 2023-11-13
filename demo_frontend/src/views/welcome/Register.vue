<!--
  - Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
  -->

<script setup>
import {reactive, ref, computed} from "vue";
import router from "@/router";
import {getVerifyCode, login, register} from "@/api/auth";
import {ElMessage} from "element-plus";

const registerFrom = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  code: '',
})

const coldTime = ref(0)
const usernameValidator = (rule, value, callback) => {
  if (value === "") {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名只能包含中/英文：字母、数字、下划线'))
  } else {
    callback()
  }
}
const confirmPasswordValidator = (rule, value, callback) => {
  if (value === "") {
    return callback(new Error('请再次输入密码'))
  } else if (value !== registerFrom.password) {
    return callback(new Error('两次密码输入不一致'))
  } else {
    return callback()
  }
}
const emailValidator = (rule, value, callback) => {
  if (value === "") {
    return callback(new Error('请输入邮箱'))
  } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)
  ) {
    return callback(new Error('邮箱不合法，请输入合法邮箱'))
  } else {
    return callback()
  }
}

const rules = {
  username: [
    {required: true, validator: usernameValidator, trigger: ['blur', 'change']},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: ['blur', 'change']}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: ['blur', 'change']}
  ],
  confirmPassword: [
    {required: true, validator: confirmPasswordValidator, trigger: ['blur', 'change']},
    {min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, validator: emailValidator, trigger: 'blur'},
    {min: 6, max: 30, message: '长度在 6 到 30 个字符合法的电子邮箱', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 6, max: 6, message: '长度为 6 个字符', trigger: ['blur', 'change']}
  ]
}

const formRef = ref()

function userRegister() {
  formRef.value.validate((valid) => {
    if (valid) {
      register(registerFrom, () => {
        router.push('/login')
        console.log("注册成功")
      })
    } else {
      ElMessage.error('请按照要求的格式输入用户名和密码')
      return false
    }
  })
}

function getCode() {
  if (isEmailValid) {
    coldTime.value = 60
    getVerifyCode(registerFrom.email, "register", () => {
      const coldTimer = setInterval(() => {
        coldTime.value > 0 ? coldTime.value-- : clearInterval(coldTimer)
      }, 1000)
      console.log("验证码发送成功")
    }, (message) => {
      coldTime.value = 0
      ElMessage.warning(message)
    })
  } else {
    ElMessage.error('请输入合法正确的邮箱')
    return false
  }
}

const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(registerFrom.email))

</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div>
        <el-text class="mx-1" style="font-size: 25px;font-weight: bold" type="primary">注册</el-text>
      </div>
      <div>
        <el-text class="mx-1" style="margin-top: 10px;font-size: 15px;font-weight: lighter;color: rgb(128,128,128)"
                 type="info">
          欢迎注册我的《世 · 界》，请在下方填写个人信息以获取地球通行证加入我们，开启你的人生。
        </el-text>
      </div>
    </div>
    <div style="margin: 50px 10px">
      <el-form ref="formRef"
               :model="registerFrom"
               :rules="rules"
      >
        <el-form-item prop="username">
          <el-input v-model="registerFrom.username" clearable maxlength="20" minlength="2" placeholder="请输入用户名"
                    prefix-icon="User"
                    type="text"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerFrom.password" clearable maxlength="15" minlength="6" placeholder="请输入密码"
                    prefix-icon="Lock" show-password
                    type="password"/>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerFrom.confirmPassword" clearable maxlength="15" minlength="6"
                    placeholder="请重复输入密码"
                    prefix-icon="Lock" show-password
                    type="password"/>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerFrom.email" clearable maxlength="30" minlength="6" placeholder="请输入邮箱"
                    prefix-icon="Message"
                    type="email"/>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="16">
              <el-input v-model="registerFrom.code" clearable maxlength="6" minlength="6" placeholder="请输入验证码"
                        prefix-icon="Key"
                        type="text"/>
            </el-col>
            <el-col :span="8">
              <el-button :disabled="!isEmailValid || coldTime" plain style="width: 100%;margin-left: 10px"
                         type="success"
                         @click="getCode">
                {{ coldTime > 0 ? `请等待 ${coldTime} 秒` : "获取验证码" }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-button style="width: 270px;margin: 20px 0" type="warning" @click="userRegister">立即注册</el-button>
    </div>
    <el-divider content-position="center">
      <el-text class="mx-1" type="info">已有地球通行证吗？</el-text>
    </el-divider>
    <div>
      <el-button plain style="width: 270px;margin: 20px 0" type="primary" @click="router.push('/login')">立即登录
      </el-button>
    </div>
  </div>
</template>

<style scoped>

</style>