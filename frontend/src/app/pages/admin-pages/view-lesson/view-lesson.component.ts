import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-view-lesson',
  templateUrl: './view-lesson.component.html',
  styleUrls: ['./view-lesson.component.css']
})
export class ViewLessonComponent implements OnInit {
  lesson: any = null;
  exercises: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private axiosService: AxiosService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.fetchLesson(id);
    } else {
      console.error('ID-ul lecției nu a fost găsit în URL.');
    }
  }


  fetchLesson(id: string | null): void {
    if (!id) return;

    // 1. Încărcăm lecția
    this.axiosService.request('GET', `/lessons/${id}`, {})
      .then(res => {
        this.lesson = res.data;
      })
      .catch(err => {
        console.error('Eroare la încărcare lecție:', err);
      });

    // 2. Încărcăm exercițiile
    this.axiosService.request('GET', `/exercises/by-lesson/${id}`, {})
      .then(res => {
        this.exercises = res.data;
      })
      .catch(err => {
        console.error('Eroare la încărcare exerciții:', err);
      });
  }

  editLesson(): void {
    this.router.navigate(['/edit-lesson', this.lesson.id]);
  }

  deleteLesson(): void {
    if (!confirm('Sigur vrei să ștergi această lecție?')) return;

    this.axiosService.request('DELETE', `/lessons/${this.lesson.id}`, {})
      .then(() => {
        alert('Lecția a fost ștearsă');
        this.router.navigate(['/manage-content']);
      })
      .catch(err => {
        console.error('Eroare la ștergere:', err);
      });
  }

  addExercise(): void {
    this.router.navigate(['/add-exercise', this.lesson.id]);
  }

  viewExercise(exerciseId: number): void {
    this.router.navigate(['/view-exercise', exerciseId]);
  }

  goToExercises(): void {
    this.router.navigate([`/lesson/${this.lesson.id}/exercises`]);
  }

  editExercise(exerciseId: number): void {
    this.router.navigate(['/edit-exercise', exerciseId]);
  }

  deleteExercise(exerciseId: number): void {
    if (!confirm('Sigur vrei să ștergi acest exercițiu?')) return;

    this.axiosService.request('DELETE', `/exercises/${exerciseId}`, {})
      .then(() => {
        alert('Exercițiul a fost șters');
        this.exercises = this.exercises.filter(e => e.id !== exerciseId); // actualizează lista local
      })
      .catch(err => {
        console.error('Eroare la ștergere exercițiu:', err);
        alert('A apărut o eroare la ștergere.');
      });
  }


  fetchExercises(lessonId: number): void {
    this.axiosService.request('GET', `/exercises/by-lesson/${lessonId}`, {})
      .then(res => {
        this.exercises = res.data;
      })
      .catch(err => {
        console.error('Eroare la încărcarea exercițiilor:', err);
      });
  }
}
