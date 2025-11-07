import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';





@Component({
  selector: 'app-user-view-lesson',
  templateUrl: './user-view-lesson.component.html',
  styleUrls: ['./user-view-lesson.component.css']
})
export class UserViewLessonComponent implements OnInit {
  lesson: any;
  exercises: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private axiosService: AxiosService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.axiosService.request('GET', `/lessons/${id}`, {})
        .then(res => this.lesson = res.data)
        .catch(err => console.error('Eroare la lecție:', err));

      this.axiosService.request('GET', `/exercises/by-lesson/${id}`, {})
        .then(res => this.exercises = res.data)
        .catch(err => console.error('Eroare la exerciții:', err));
    }
  }

  viewExercise(exerciseId: number): void {
    this.router.navigate(['/exercise', exerciseId], {
      queryParams: { lessonId: this.lesson?.id }
    });
  }

  goBackToHome(): void {
    this.router.navigate(['/user-home']);
  }

}

