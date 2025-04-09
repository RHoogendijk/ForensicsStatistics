import {jwtDecode} from "jwt-decode";
import {computed, reactive} from "vue";
import {CONFIG} from "@/config.js";

export class SessionService {

  constructor(resourcesUrl, browserStorageItemName) {
    this.RESOURCES_URL = resourcesUrl;
    this.BROWSER_STORAGE_ITEM_NAME = browserStorageItemName;
    this.state = reactive({
      token: this.getTokenFromBrowserStorage()
    })
  }

  async asyncRegister(fullName, email, password) {
    const registerData = {fullName, email, password};
    let response = await fetch(`${CONFIG.BACKEND_URL}/auth/register`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(registerData),
      credentials: 'include',
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText);
    }

    const result = await response.json();
    console.log('Register successful:', result);

    try {
      await this.asyncLogIn(email, password);
      console.log("Login successful after registration");
    } catch (error) {
      console.error("Error during auto-login after registration:", error);
      throw error;
    }
    return result;
  }

  async asyncLogIn(email, password) {
    const loginData = {email, password};
    let response = await fetch(`${CONFIG.BACKEND_URL}/auth/login`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(loginData),
      credentials: 'include',
    })
    if (response.ok) {
      this.saveTokenIntoBrowserStorage(
        response.headers.get('Authorization'),
      );
      window.location.reload();
      return true;
    }
    throw new Error("Invalid email or password")
  }
  async logout(){
    this.saveTokenIntoBrowserStorage(null)
  }

  saveTokenIntoBrowserStorage(token) {
    if (token == null) {
      const sessionStorageToken = window.sessionStorage.getItem(this.BROWSER_STORAGE_ITEM_NAME);
      window.sessionStorage.removeItem(this.BROWSER_STORAGE_ITEM_NAME);

      const localStorageToken = window.localStorage.getItem(this.BROWSER_STORAGE_ITEM_NAME);
      if (localStorageToken === sessionStorageToken) {
        window.localStorage.removeItem(this.BROWSER_STORAGE_ITEM_NAME);
      }
    } else {
      window.sessionStorage.setItem(this.BROWSER_STORAGE_ITEM_NAME, token);
      window.localStorage.setItem(this.BROWSER_STORAGE_ITEM_NAME, token);
    }
    this.state.token = token
  }

  getTokenFromBrowserStorage() {
    let token = window.sessionStorage.getItem(this.BROWSER_STORAGE_ITEM_NAME);

    if (token == null) {
      token = window.localStorage.getItem(this.RESOURCES_URL);
    }
    return token;
  }

  isAuthenticated() {
    return !!this.state.token;
  }

  isAdmin(){
    try{
      let decodedToken = jwtDecode(this.state.token);
      return decodedToken.role === "ADMIN";
    } catch(err){
      console.error("error decoding token")
      this.logout();
      return false;
    }
  }
  isStudent(){
    try{
      let decodedToken = jwtDecode(this.state.token);
      return decodedToken.role === "STUDENT";
    } catch(err){
      console.error("error decoding token")
      this.logout();
      return false;
    }
  }
  isTeacher(){
    try{
      let decodedToken = jwtDecode(this.state.token);
      return decodedToken.role === "TEACHER";
    } catch(err){
      console.error("error decoding token")
      this.logout();
      return false;
    }
  }
  getUsername(){
    try {
      let decodedToken = jwtDecode(this.state.token);
      return decodedToken.sub;
    } catch (err) {
      console.error("error decoding token")
      this.logout();
      return "placeholder username"
    }
  }
  getId() {

    try {
      let decodedToken = jwtDecode(this.state.token);

      console.log( "token = " + decodedToken);
      return decodedToken.jti;
    } catch (err) {
      console.error("error decoding token")
      this.logout();
      return null
    }
  }
}
