<script setup>
import {inject, nextTick, ref} from 'vue';
import router from '@/router/index.js';

const sessionService = inject('sessionService');
const registerFirstName = ref('');
const registerLastName = ref('');
const registerEmail = ref('');
const registerPassword = ref('');
const registerConfirmPassword = ref('');
const emailError = ref(null);
const showPassword = ref(false);

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

const submit = async () => {
  emailError.value = null;
  try {
    await sessionService.asyncRegister(registerFirstName.value + " " + registerLastName.value, registerEmail.value, registerPassword.value);
  } catch (error) {
    if (error.message.includes('Invalid email or password')) {
      emailError.value = 'E-mailadres of wachtwoord is onjuist.';
    } else {
      emailError.value = 'Er is een probleem opgetreden. Probeer het later opnieuw.';
      console.log(error);
    }
  }

};
</script>

<template>
  <div class="body-container">
    <h2>Create Your Account</h2>
    <h3>Please fill in your credentials</h3>
    <div class="inputs">
      <h5>First Name:</h5>
      <div class="inputField">
        <input
          type="text"
          v-model="registerFirstName"
          placeholder="John"
        />
      </div>
      <h5>Last Name:</h5>
      <div class="inputField">
        <input
          type="text"
          v-model="registerLastName"
          placeholder="Doe"
        />
      </div>
      <h5>E-mail:</h5>
      <div class="inputField">
        <input
          type="text"
          v-model="registerEmail"
          placeholder="John.Doe@hva.nl"
        />
      </div>
      <h5>Password:</h5>
      <div class="inputField">
        <input
          class="password"
          :type="showPassword ? 'text' : 'password'"
          v-model="registerPassword"
          placeholder="MyPassword#123"
        />
        <div class="password-toggle" @click="togglePasswordVisibility">
          <img v-if="showPassword" src="../assets/img/eye-slash-solid.svg" alt="">
          <img v-else src="../assets/img/eye-solid.svg" alt="">
        </div>
      </div>
      <h5>Confirm Password:</h5>
      <div class="inputField">
        <input
          class="password"
          :type="showPassword ? 'text' : 'password'"
          v-model="registerConfirmPassword"
          placeholder="MyPassword#123"
        />
      </div>
      <button class="submitRegister" @click="submit">Register</button>
      <RouterLink to="/login">Already have an account? Login</RouterLink>
    </div>
  </div>
</template>

<style scoped>

img {
  opacity: 50%;
}

.password-toggle {
  display: flex;
  width: 24px;
  justify-content: center;
  align-items: center;
  margin-right: 4px;
}

a{
  width: 100%;
  color: blue;
  font-size: 12px;
  padding: 0 8px;
}
</style>
