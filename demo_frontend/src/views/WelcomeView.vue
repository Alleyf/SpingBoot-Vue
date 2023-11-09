<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">

    <div style="flex: 1;background-color: black">
      <el-image fit="cover" src="https://picsum.photos/800" style="width: 100%;height: 100%;"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin: 5px auto">本次将开启你的首次前后端分离项目实战</div>
      <div>激动的心，颤抖的手，享受编程之美，做新一代牛码！</div>
    </div>
    <div class="right-card">

    </div>

  </div>
</template>

<style scoped>
.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px #000;
}

.right-card {
  width: 400px;
  z-index: 1;
  background-color: white;
}
</style>
