import {ActivatedRoute, Router} from "@angular/router";
import {AxiosService} from "../../axios.service";
import {Component, OnInit} from "@angular/core";

@Component({
  selector: 'app-view-exercise',
  templateUrl: './view-exercise.component.html',
  styleUrls: ['./view-exercise.component.css']
})
export class ViewExerciseComponent implements OnInit {
  exercise: any = null;
  role: string = ''; // Rol extras din token

  constructor(
    private route: ActivatedRoute,
    private axiosService: AxiosService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.loadExercise(id);
    this.extractRoleFromToken();
  }

  loadExercise(id: string | null): void {
    this.axiosService.request('GET', `/exercises/${id}`, {})
      .then(res => this.exercise = res.data)
      .catch(err => alert('Eroare la încărcarea exercițiului'));
  }

  extractRoleFromToken(): void {
    const token = localStorage.getItem('auth_token');
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.role = payload.role;
    }
  }

  editExercise(): void {
    this.router.navigate(['/edit-exercise', this.exercise.id]);
  }

  deleteExercise(): void {
    const confirmDelete = confirm('Ești sigur că vrei să ștergi acest exercițiu?');
    if (confirmDelete) {
      this.axiosService.request('DELETE', `/exercises/${this.exercise.id}`, {})
        .then(() => {
          alert('Exercițiul a fost șters.');
          this.router.navigate(['/manage-content']);
        })
        .catch(() => alert('Eroare la ștergere exercițiu.'));
    }
  }

  solveExercise(): void {
    alert('Aici va fi implementată rezolvarea de către user.');
  }
}

