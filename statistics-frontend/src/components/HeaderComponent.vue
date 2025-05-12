<script setup>
import {ref, inject, computed} from 'vue';
import router from "@/router/index.js";

const sessionService = inject("sessionService")
const isAuthenticated = computed(() => sessionService.isAuthenticated());
const isStudent = computed(() => sessionService.isStudent());
const isTeacher = computed(() => sessionService.isTeacher());
const isAdmin = computed(() => sessionService.isAdmin());

// Logout function
const logout = async () => {
  await sessionService.logout();
  await router.push("/login");
};
</script>

<template>
  <div class="header-container">
    <div class="top-bar">
      <div class="centered">
        <h1>Forensics VR Project</h1>
      </div>
    </div>
    <div class="nav-bar">
      <div class="centered">
        <div class="left">
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/myprofile" v-if="isAuthenticated">Profile</RouterLink>
          <RouterLink to="/sessions" v-if="!isAdmin && isAuthenticated">My Sessions</RouterLink>
          <RouterLink to="/classes" v-if="isTeacher">Groups</RouterLink>
          <RouterLink to="/replay">Replay</RouterLink>
        </div>
        <div class="right">
          <RouterLink to="/login" v-if="!isAuthenticated">Login</RouterLink>
          <RouterLink to="/admin" v-if="isAdmin">Admin</RouterLink>
          <a href="#" @click="logout" v-if="isAuthenticated">Logout</a>
        </div>


      </div>
    </div>
  </div>

</template>

<style scoped>
.header-container{
  width: 100vw;
  display: flex;
  flex-direction: column;
  padding: 0;
  margin: 0;
  border-bottom: 4px solid #F1F2F3;
}
.top-bar {
  width: 100%;
  height: 8vh;
  background-color: white;
  border-bottom: 1px solid #E8E9EC;
  display: flex;
  justify-content: center;
}

.nav-bar {
  width: 100%;
  height: 5vh;
  background-color: white;
  border-bottom: 1px solid #E8E9EC;
  display: flex;
  justify-content: center;
}
.centered {
  width: 55vw;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.left, .right {
  display: flex;
  flex-direction: row;
}
a{
  font-size: 20px;
  line-height: 5vh;
  align-items: center;
  justify-content: center;
  display: flex;
  padding: 8px;
}
h1{
line-height: 8vh;
}

</style>
