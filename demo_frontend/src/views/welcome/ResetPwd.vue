<!--
  - Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
  -->

<script setup>


import {computed, reactive, ref} from "vue";
import {confirmEmail, getVerifyCode, resetPassword} from "@/api/auth";
import {ElMessage} from "element-plus";
import router from "@/router";

const active = ref(0)

const resetForm = reactive({
  email: "",
  code: "",
  password: "",
  confirmPassword: ""
})


const resetFormRef = ref()
const coldTime = ref(0)

const confirmPasswordValidator = (rule, value, callback) => {
  if (value === "") {
    return callback(new Error('请再次输入密码'))
  } else if (value !== resetForm.password) {
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

function getCode() {
  if (isEmailValid) {
    coldTime.value = 60
    getVerifyCode(resetForm.email, "reset", () => {
      const coldTimer = setInterval(() => {
        coldTime.value > 0 ? coldTime.value-- : clearInterval(coldTimer)
      }, 1000)
      console.log("验证码发送成功")
    }, (res) => {
      coldTime.value = 0
      ElMessage.warning(res.message)
    })
  } else {
    ElMessage.error('请输入合法正确的邮箱')
    return false
  }
}

function doUserReset() {
  if (resetFormRef.value !== undefined) {
    resetFormRef.value.validate(valid => {
      if (valid) {
        resetPassword(resetForm, () => {
          router.push("/login")
          active.value++
        })
      } else {
        ElMessage.error('请输入合法正确的密码')
      }
    })
  }
}

function confirmReset() {
  // alert(JSON.stringify(resetForm))
  if (resetFormRef.value !== undefined) {
    resetFormRef.value.validate(valid => {
      if (valid) {
        confirmEmail({
          email: resetForm.email,
          code: resetForm.code
        }, () => {
          active.value++
        })
      }
    })
  }
}

const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(resetForm.email))

</script>

<template>
  <div style="text-align:center;margin: 0 20px">
    <div style="margin-top: 100px">
      <el-steps :active="active" align-center finish-status="success">
        <el-step description="输入用户注册时所用的电子邮件进行验证" title="验证电子邮件"></el-step>
        <el-step description="用户设置新密码取代遗忘的旧密码" title="重新设置密码"></el-step>
      </el-steps>
    </div>
    <div style="margin-top: 50px">
      <div style="margin: 10px 0">
        <el-text size="large" style="font-weight: bold;font-size: 30px" type="primary">
          {{
            active === 0 ? "邮箱校验" : "重置密码"
          }}
        </el-text>
      </div>
      <div>
        <el-text size="small" style="font-weight: lighter;font-size: 15px" type="info">
          {{
            active === 0 ? "请输入您注册时所用的电子邮件进行验证" : "请输入您的新密码，务必牢记，防止丢失"
          }}
        </el-text>
      </div>
      <!--//TODO 第一步：验证邮箱-->
      <div v-if="active===0" style="margin: 50px 0">
        <el-form ref="resetFormRef"
                 :model="resetForm"
                 :rules="rules">
          <el-form-item prop="email">
            <el-input v-model="resetForm.email" clearable maxlength="30" minlength="6" placeholder="请输入邮箱"
                      prefix-icon="Message"
                      type="email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="16">
                <el-input v-model="resetForm.code" clearable maxlength="6" minlength="6" placeholder="请输入验证码"
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
        <div style="margin-top: 20px">
          <el-button style="width: 270px;margin: 20px 0" type="success" @click="confirmReset">下一步</el-button>
        </div>
      </div>
      <!--//TODO 第二步：重置密码-->
      <div v-else style="margin: 50px 0">
        <el-form ref="resetFormRef"
                 :model="resetForm"
                 :rules="rules">
          <el-form-item prop="password">
            <el-input v-model="resetForm.password" clearable maxlength="15" minlength="6" placeholder="请输入密码"
                      prefix-icon="Lock" show-password
                      type="password"/>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="resetForm.confirmPassword" clearable maxlength="15" minlength="6"
                      placeholder="请重复输入密码"
                      prefix-icon="Lock" show-password
                      type="password"/>
          </el-form-item>
        </el-form>
        <div style="margin-top: 20px">
          <el-button style="width: 270px;margin: 20px 0" type="success" @click="active--">上一步</el-button>
        </div>
        <div>
          <el-button plain style="width: 270px;margin: 20px 0" type="primary" @click="doUserReset">提交
          </el-button>
        </div>
      </div>
    </div>
    <el-divider content-position="center">
      <el-link disabled type="info">已知道密码？
      </el-link>
      <el-link type="danger" @click="router.push('/login')">立即登录
      </el-link>
    </el-divider>
  </div>
</template>

<style scoped>

</style>