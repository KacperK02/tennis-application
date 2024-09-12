<template>
    <div>
      <h1>Zarejestruj się</h1>
      <form @submit.prevent="register">
        <div>
          <label>Nazwa użytkownika:</label>
          <input v-model="username" type="text" required />
        </div>
        <div>
          <label>Email:</label>
          <input v-model="email" type="email" required />
        </div>
        <div>
          <label>Hasło:</label>
          <input v-model="password" type="password" required />
        </div>
        <div>
          <button type="submit">Zarejestruj się</button>
        </div>
      </form>
      <div v-if="errorMessage" style="color:red">
        {{ errorMessage }}
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        username: '',
        email: '',
        password: '',
        errorMessage: '',
      };
    },
    methods: {
      async register() {
        try {
          const response = await fetch('http://localhost:8080/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              username: this.username,
              email: this.email,
              password: this.password
            })
          });
  
          const result = await response.json();
  
          if (result.status === 'success') {
            // Przekierowanie na stronę główną z komunikatem o sukcesie
            this.$router.push({ path: '/', query: { registration_success: 'Konto zostało pomyślnie utworzone.' } });
          } else {
            this.errorMessage = result.error || 'Błąd rejestracji.';
          }
        } catch (error) {
          this.errorMessage = 'Wystąpił błąd. Spróbuj ponownie.';
        }
      }
    },
    name: "RegisterView"
  };
  </script>
  