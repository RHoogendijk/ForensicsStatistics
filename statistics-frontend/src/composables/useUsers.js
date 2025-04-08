import {ref} from "vue";
import {CONFIG} from "@/config.js";

export function useUsers() {
  const users = ref([]);
  const usersErr = ref(null)
  const usersLoading = ref(null)

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

  return { users, usersErr, usersLoading, fetchUsers, changeRole };
}
