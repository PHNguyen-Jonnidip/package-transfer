import { reactive } from 'vue'
import router from "@/routes/routes";

export const authentication = reactive({
  loggedIn: false,

  loginUser(token) {
    this.loggedIn = true;
    localStorage.setItem('access_token', token);

    const decodedToken = JSON.parse(Buffer.from(token.split('.')[1], 'base64').toString());
    localStorage.setItem('exp', decodedToken.exp * 1000);

    const redirect = sessionStorage.getItem('redirect');
    sessionStorage.removeItem('redirect');
    if (redirect) {
      router.push(redirect);
    } else {
      router.push('/');
    }
  },

  logoutUser() {
    this.loggedIn= false;
    localStorage.removeItem('access_token');
    localStorage.removeItem('exp');
    router.push('/login');
  },

  isLoggedIn() {
    let now = new Date();
    now = now.setMinutes(now.getMinutes() + 5);
    return !!(localStorage.getItem('access_token') &&
      localStorage.getItem('exp') &&
      new Date(parseInt(localStorage.getItem('exp'))) > now);
  }
})
