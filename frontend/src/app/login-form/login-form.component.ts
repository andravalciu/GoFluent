import { Component } from '@angular/core';
import { AxiosService } from "../axios.service";
import { Router } from "@angular/router";
import { jwtDecode } from "jwt-decode";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent {

  active: string = "login";
  firstName: string = "";
  lastName: string = "";
  login: string = "";
  password: string = "";


  constructor(private axiosService: AxiosService, private router: Router) {}

  onLoginTab(): void {
    this.active = "login";
  }

  onRegisterTab(): void {
    this.active = "register";
  }

  onSubmitLogin(): void {
    this.axiosService.request('POST', '/login', {
      login: this.login,
      password: this.password
    })
      .then(res => {
        const token = res.data?.token || res.token;
        localStorage.setItem('auth_token', token);

        const decoded: any = jwtDecode(token);
        const role = decoded.role;

        if (role === 'ADMIN') {
          this.router.navigate(['/admin-home']);
        } else {
          this.router.navigate(['/user-home']);
        }
      })
      .catch(err => {
        console.error('Login error:', err);
        alert('Autentificare eșuată');
      });
  }

  onSubmitRegister(): void {
    this.axiosService.setAuthToken(null);
    this.axiosService.request("POST", "/register", {
      firstName: this.firstName,
      lastName: this.lastName,
      login: this.login,
      password: this.password,
      role: "USER"
    })
      .then(response => {
        this.axiosService.setAuthToken(response.data.token);
        this.router.navigate(['/user-home']);
      });
  }

}



