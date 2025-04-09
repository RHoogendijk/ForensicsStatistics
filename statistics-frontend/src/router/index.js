import {createRouter, createWebHistory} from 'vue-router'
import HomeView from "@/views/HomeView.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import MyProfileView from "@/views/MyProfileView.vue";
import AdminView from "@/views/AdminView.vue";
import ClassesView from "@/views/ClassesView.vue";
import MySessionsView from "@/views/MySessionsView.vue";
import SessionDetailView from "@/views/SessionDetailView.vue";
import ClassListComponent from "@/components/ClassListComponent.vue";
import StudentListComponent from "@/components/StudentListComponent.vue";

const  router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "Home",
      component: HomeView
    },
    {
      path: "/login",
      name: "Login",
      component: LoginView
    },
    {
      path: "/register",
      name: "Register",
      component: RegisterView
    },
    {
      path: "/myprofile",
      name: "My Profile",
      component: MyProfileView
    },
    {
      path: "/admin",
      name: "Admin Panel",
      component: AdminView
    },
    {
      path: "/classes",
      name: "classes",
      component: ClassesView,
      children: [
        {path: "", component: ClassListComponent},
        {path: ":id", name: "ClassDetail", component: StudentListComponent},
      ]
    },
    {
      path: "/sessions",
      name: "My Sessions",
      component: MySessionsView
    },
    {
      path: "/sessions/student/:id",
      name: "Student Sessions",
      component: MySessionsView
    },
    {
      path: "/sessions/:id",
      name: "SessionDetail",
      component: SessionDetailView,
      props: true,
    }
  ]
})

export default router
