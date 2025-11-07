import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-edit-exercise',
  templateUrl: './edit-exercise.component.html',
  styleUrls: ['./edit-exercise.component.css']
})
export class EditExerciseComponent implements OnInit {
  exercise: any = {
    question: '',
    answer: '',
    lessonId: null
  };

  constructor(
    private route: ActivatedRoute,
    private axiosService: AxiosService,
    private router: Router
  ) {}

  lessonId: number | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.axiosService.request('GET', `/exercises/${id}`, {})
        .then(res => {
          this.exercise = res.data;
          // 👇 Asigură-te că lessonId e aici
          console.log('Exercițiu încărcat:', this.exercise);
        });
    }
  }


  save(): void {
    this.axiosService.request('PUT', `/exercises/${this.exercise.id}`, this.exercise)
      .then(() => {
        alert('Exercițiul a fost actualizat!');
        this.router.navigate([`/view-lesson/${this.exercise.lessonId}`]);
      })
      .catch(err => {
        console.error('Eroare la salvare:', err);
        alert('Eroare la actualizare exercițiu.');
      });
  }
  editExercise(exerciseId: number): void {
    this.router.navigate(['/view-lesson', this.exercise.lessonId]);

  }
  onSubmit(): void {
    this.axiosService.request('PUT', `/exercises/${this.exercise.id}`, this.exercise)
      .then(() => {
        // 👇 Asigură-te că lessonId există
        if (this.exercise.lessonId) {
          this.router.navigate(['/view-lesson', this.exercise.lessonId]);
        } else {
          alert('Eroare: lessonId lipsă!');
        }
      });
  }




}
