import {ref} from "vue";
import {CONFIG} from "@/config.js";

export function useUsers() {
  const users = ref([]);
  const sessions = ref([]);
  const userFullName = ref("");
  const uploadCode = ref("")
  const usersErr = ref(null)
  const usersLoading = ref(false)


  const fetchUsers = async () => {
    usersLoading.value = true;
    try {
      const response = await fetch(
        `${CONFIG.BACKEND_URL}/users`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      if (!response.ok) {
        throw new Error(response.status);
      }

      const data = await response.json();
      users.value = data;
    } catch (error) {
      usersErr.value = error;
    } finally {
      usersLoading.value = false;
    }
  }

  const changeRole = async (userId, role) => {
    usersLoading.value = true;
    try{
      const response = await fetch(`${CONFIG.BACKEND_URL}/users/${userId}/role`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(role)
      });
      if (!response.ok) {
        throw new Error(response.status);
      }
      return await response.json();
    } catch (error) {
      usersErr.value = error;
    } finally {
      usersLoading.value = false;
    }
  }

  const fetchSessions = async (userId) => {
    usersLoading.value = true;
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/users/${userId}/sessions`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        },
      });
      if (!response.ok) {
        throw new Error(response.status);
      }
      const data =  await response.json();
      console.log(data)
      userFullName.value = data.username;
      sessions.value = data.playSessions;
    } catch (error) {
      usersErr.value = error;
    } finally {
      usersLoading.value = false;
    }
  }

  const fetchUploadCode = async (userId) => {
    usersLoading.value = true;
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/users/${userId}/uploadcode`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        },
      })
      if (!response.ok){
        throw new Error(response.status)
      }
      const data = await response.text();
      uploadCode.value = data;
    } catch (error) {
      usersErr.value = error;
    } finally {
      usersLoading.value = false;
    }
  }

  return { sessions, userFullName, uploadCode, users, usersErr, usersLoading, fetchUsers, changeRole,  fetchSessions, fetchUploadCode};
}
