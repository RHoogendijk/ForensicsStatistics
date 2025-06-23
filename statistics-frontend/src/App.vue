<script setup>
import {SessionService} from "@/services/session-service.js";
import {CONFIG} from "../config.js";
import {onBeforeMount, onBeforeUnmount, provide, shallowReactive} from "vue";
import {FetchInterceptor} from "@/services/fetch-interceptor.js";
import router from "@/router/index.js";
import FooterComponent from "@/components/FooterComponent.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";

const theSessionService = shallowReactive(
  new SessionService(`${CONFIG.BACKEND_URL}`, CONFIG.JWT_STORAGE_ITEM));

const theFetchInterceptor = new FetchInterceptor(theSessionService);

provide('sessionService', theSessionService);
provide('fetchInterceptor', theFetchInterceptor);

const routerGuard = (to, from) => {
  if (to.name === 'Admin Panel'){
    if (!theSessionService.isAdmin()){
      console.error(`User not authorized to visit ${to}`)
      return {name: 'Home', query: {targetRoute: to.name}};
    }
  } else if (to.name === 'My Profile' || to.name === 'My Sessions' || to.name === 'SessionDetail'){
    console.log(theSessionService.isAuthenticated());
    if (!theSessionService.isAuthenticated()){
      console.log("test")
      return {name: 'Home', query: {targetRoute: to.name}};
    }
  }  else if (to.name === 'Login' || to.name === 'Register'){
    console.log(theSessionService.isAuthenticated());
    if (theSessionService.isAuthenticated()){

      return {name: 'My Profile'};
    }
  }
}

onBeforeMount(() => {
  router.beforeEach(routerGuard)
})
onBeforeUnmount(() =>{
  theFetchInterceptor.unregister();
})
</script>

<template>
  <HeaderComponent />
  <div class="container">

    <RouterView />
  </div>
  <FooterComponent />
</template>

<style scoped>
.container{
  width: 55vw;
  margin: 0 auto;
}
</style>
