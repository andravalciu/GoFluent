import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-user-test-level',
  templateUrl: './user-test-level.component.html',
  styleUrls: ['./user-test-level.component.css']
})
export class UserTestLevelComponent implements OnInit {
  questions: any[] = [];
  userAnswers: string[] = [];
  submitted = false;
  result: any = null; // Rezultatul de la backend
  loading = false;
  levelId!: string;

  constructor(private axiosService: AxiosService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.levelId = this.route.snapshot.paramMap.get('levelId')!;
    this.loadQuestions();
  }

  loadQuestions(): void {
    this.loading = true;
    this.axiosService.request('GET', `/mcq/test/${this.levelId}`, {})
      .then(res => {
        this.questions = res.data;
        this.userAnswers = new Array(this.questions.length).fill('');
      })
      .catch(err => {
        console.error('Eroare la încărcarea testului:', err);
      })
      .finally(() => {
        this.loading = false;
      });
  }

  submitTest(): void {
    if (this.userAnswers.some(answer => !answer || answer.trim() === '')) {
      alert('Te rog răspunde la toate întrebările înainte de a trimite testul!');
      return;
    }

    this.loading = true;

    const submission = {
      levelId: parseInt(this.levelId),
      userAnswers: this.userAnswers
    };

    this.axiosService.request('POST', '/mcq/test/submit', submission)
      .then(res => {
        this.result = res.data;
        this.submitted = true;
      })
      .catch(err => {
        console.error('Eroare la trimiterea testului:', err);
        alert('Eroare la trimiterea testului. Încearcă din nou.');
      })
      .finally(() => {
        this.loading = false;
      });
  }

  retakeTest(): void {
    this.submitted = false;
    this.result = null;
    this.userAnswers = new Array(this.questions.length).fill('');
  }
  // ADAUGĂ ACEASTĂ METODĂ:
  restart(): void {
    this.router.navigate(['/user-home']);
  }

}

