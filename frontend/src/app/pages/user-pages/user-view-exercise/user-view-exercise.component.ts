import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-user-view-exercise',
  templateUrl: './user-view-exercise.component.html',
  styleUrls: ['./user-view-exercise.component.css']
})
export class UserViewExerciseComponent implements OnInit {
  exercises: any[] = [];
  exerciseIndex: number = 0;
  exercise: any = null;
  userAnswer: string = '';
  submitted: boolean = false;
  isCorrect: boolean | null = null;
  score: number = 0;

  constructor(private router: Router,private route: ActivatedRoute, private axiosService: AxiosService) {}

  ngOnInit(): void {
    // ✅ Preia corect exerciseId din URL și lessonId din queryParams
    const exerciseId = this.route.snapshot.paramMap.get('id'); // exerciseId din URL
    const lessonId = this.route.snapshot.queryParams['lessonId']; // lessonId din queryParams

    console.log('🔍 Exercise ID from URL:', exerciseId);
    console.log('🔍 Lesson ID from queryParams:', lessonId);

    if (lessonId) {
      this.axiosService.request('GET', `/exercises/by-lesson/${lessonId}`, {})
        .then(res => {
          this.exercises = res.data;
          console.log('📋 All exercises:', this.exercises.map(ex => ({id: ex.id, question: ex.question})));

          // ✅ Găsește exercițiul specific după exerciseId, nu primul!
          if (exerciseId) {
            this.exerciseIndex = this.exercises.findIndex(ex => ex.id == exerciseId);
            if (this.exerciseIndex === -1) {
              console.error('❌ Exercise not found:', exerciseId);
              this.exerciseIndex = 0; // fallback la primul
            }
          }

          this.exercise = this.exercises[this.exerciseIndex];
          console.log('🎯 Selected exercise:', this.exercise.question);
        })
        .catch(err => console.error('Eroare la încărcarea exercițiilor:', err));
    }
  }

  submitAnswer() {
    this.submitted = true;
    this.isCorrect =
      this.userAnswer.trim().toLowerCase() ===
      this.exercise.answer.trim().toLowerCase();

    // ✅ Preia lessonId din queryParams, nu din paramMap
    const lessonId = this.route.snapshot.queryParams['lessonId'];

    if (!lessonId) {
      console.error('Nu am găsit lessonId în queryParams');
      return;
    }

    this.axiosService
      .request(
        "POST",
        `/progress/lesson/${lessonId}/exercise/${this.exercise.id}/complete`,
        {}
      )
      .then(() => console.log("📈 Progres actualizat"))
      .catch(err => console.error("❌ Eroare progres:", err));
  }




  goToPrevious(): void {
    if (this.exerciseIndex > 0) {
      this.exerciseIndex--;
      this.loadExercise();
    }
  }

  goToNext(): void {
    if (this.exerciseIndex < this.exercises.length - 1) {
      this.exerciseIndex++;
      this.loadExercise();
    }
  }

  private loadExercise(): void {
    this.exercise = this.exercises[this.exerciseIndex];
    this.userAnswer = '';
    this.submitted = false;
    this.isCorrect = null;
  }

  goBackToLesson(): void {
    // Navighează înapoi la lecția specifică
    this.router.navigate(['/user-view-lessons',1]);
  }
}

