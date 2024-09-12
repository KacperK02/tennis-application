<template>
  <div>
    <h1>Welcome to the Home Page</h1>

    <div v-if="loginError" style="color:red">
      {{ loginError }}
    </div>

    <div v-if="registrationSuccess" style="color:green">
      {{ registrationSuccess }}
    </div>

    <!-- Sprawdzenie, czy użytkownik jest zalogowany -->
    <div v-if="!isLoggedIn">
      <form @submit.prevent="login">
        <div>
          <label>Email:</label>
          <input v-model="email" type="email" required />
        </div>
        <div>
          <label>Hasło:</label>
          <input v-model="password" type="password" required />
        </div>
        <div>
          <button type="submit">Zaloguj się</button>
          <button @click.prevent="goToRegister">Zarejestruj się</button> <!-- Dodany przycisk rejestracji -->
        </div>
      </form>
    </div>

    <!-- Przyciski, jeśli użytkownik jest zalogowany -->
    <div v-else>
      <p>Jesteś zalogowany.</p>
      <button @click="logout">Wyloguj się</button>
    </div>

    <div v-if="errorMessage">
      <p style="color:red">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
      loginError: '',
      isLoggedIn: false,
    };
  },
  async created() {
    const urlParams = new URLSearchParams(window.location.search);
    this.loginError = urlParams.get('login_error') || '';
    this.registrationSuccess = this.$route.query.registration_success || '';
    await this.checkLoginStatus();
  },
  methods: {
    async checkLoginStatus() {
      try {
        const response = await fetch('http://localhost:8080/checkSession', {
          method: 'GET',
          credentials: 'include',
        });
        const result = await response.json();
        this.isLoggedIn = result.loggedIn;
      } catch (error) {
        console.error('Błąd podczas sprawdzania sesji:', error);
      }
    },
    async login() {
      try {
        const response = await fetch('http://localhost:8080/login', {
          method: 'POST',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email: this.email, password: this.password })
        });
        const result = await response.json();
        if (result.status === 'success' && result.redirectUrl) {
          window.location.href = result.redirectUrl;
        } else {
          this.errorMessage = result.error;
        }
      } catch (error) {
        this.errorMessage = 'An error occurred. Please try again.';
      }
    },
    async logout() {
      try {
        const response = await fetch('http://localhost:8080/logout', {
          method: 'POST',
          credentials: 'include',
        });
        if (response.ok) {
          this.isLoggedIn = false;
          window.location.reload();
        } else {
          this.errorMessage = 'Błąd podczas wylogowywania.';
        }
      } catch (error) {
        this.errorMessage = 'Błąd podczas wylogowywania.';
      }
    },
    goToRegister() {
      this.$router.push('/register'); // Przekierowanie na stronę rejestracji
    }
  },
  name: "IndexView"
};
</script>
