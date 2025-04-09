<script setup>
import {computed, inject, onMounted, ref, watchEffect} from "vue";
import {useRoute, useRouter} from 'vue-router'
import SessionListComponent from "@/components/SessionListComponent.vue";
import {useUsers} from "@/composables/useUsers.js";

const route = useRoute()
const {userFullName , sessions, usersErr, usersLoading, fetchSessions} = useUsers();
const sessionService = inject("sessionService");
const userId = ref(sessionService.getId())
const useOwnId = ref(true)
onMounted(() => {
  const id = route.params.id
  if (!id) {
    useOwnId.value = true;
    fetchSessions(userId.value);
  } else {
    useOwnId.value = false;
    fetchSessions(id)
  }
  console.log(useOwnId.value)
})

const router = useRouter()
const goBack = () => {
  router.back() // or router.go(-1)
}
</script>

<template>
<div class="body-container">
  <h2 v-if="useOwnId">My Sessions</h2>
  <h2 v-else>{{userFullName}}'s Sessions</h2>
  <a v-if="!useOwnId" href="#" @click="goBack">Go back</a>
  <SessionListComponent
    v-if="sessions.length > 0"
    :sessions="sessions"
  ></SessionListComponent>
  <h3 v-else-if="usersLoading">Loading...</h3>
  <h3 v-else-if="usersErr">{{usersErr.message}}</h3>
  <h3 v-else>You don't have any sessions yet</h3>

</div>
</template>

<style scoped>
a{
  width: 100%;
  color: blue;
  font-size: 14px;
  padding: 0 8px;
}

tr{
  transition: background-color 0.4s;
}
tr:hover{
  background-color: #F6F7F8;
  cursor: pointer;
}
</style>
