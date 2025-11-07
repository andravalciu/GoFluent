import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-add-exercise',
  templateUrl: './add-exercise.component.html',
  styleUrls: ['./add-exercise.component.css']
})
export class AddExerciseComponent implements OnInit {
  exercise = {
    question: '',
    answer: '',
    lessonId: null as number | null
  };
  lessonId: number | null = null;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private axiosService: AxiosService
  ) {}

  ngOnInit(): void {
    const lessonIdParam = this.route.snapshot.queryParamMap.get('lessonId');
    if (lessonIdParam) {
      this.lessonId = Number(lessonIdParam);
      this.exercise.lessonId = this.lessonId;
    } else {
      alert('ID-ul lecției nu este specificat în URL.');
      this.router.navigate(['/admin-home']);
    }
  }

  onSubmit(): void {
    if (!this.exercise.question || !this.exercise.answer) {
      alert('Toate câmpurile sunt obligatorii.');
      return;
    }

    this.axiosService.request('POST', '/exercises', this.exercise)
      .then(() => {
        alert('Exercițiul a fost adăugat cu succes!');
        this.router.navigate(['/view-lesson', this.lessonId]);
      })
      .catch(err => {
        console.error('Eroare la adăugare exercițiu:', err);
      });
  }

  goBack(): void {
    this.router.navigate(['/view-lesson', this.lessonId]);
  }
}


