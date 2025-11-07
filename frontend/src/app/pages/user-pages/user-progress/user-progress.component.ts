import { Component, OnInit } from '@angular/core';
import { AxiosService } from '../../../axios.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-user-progress',
  templateUrl: './user-progress.component.html',
  styleUrls: ['./user-progress.component.css']
})
export class UserProgressComponent implements OnInit {
  progressData: any[] = [];

  constructor(private router: Router,private axiosService: AxiosService) {}

  ngOnInit() {
    this.axiosService.request('GET', '/progress', {}).then(response => {
      console.log('Progress data:', response.data);
      this.progressData = response.data;
    }).catch(error => {
      console.error('Eroare la încărcarea progresului:', error);
    });
  }

  goBackToHome(): void {
    this.router.navigate(['/user-home']);
  }
}
