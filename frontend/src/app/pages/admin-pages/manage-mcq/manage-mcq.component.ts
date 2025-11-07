import { Component, OnInit } from '@angular/core';
import { AxiosService } from '../../../axios.service';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-manage-mcq',
  templateUrl: './manage-mcq.component.html',
  styleUrls: ['./manage-mcq.component.css']
})
export class ManageMcqComponent implements OnInit {
  mcqs: any[] = [];

  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute  // ← ADAUGĂ
  ) {}
  ngOnInit(): void {
    this.fetchMcqs();
  }

  fetchMcqs(): void {
    console.log("Fetching MCQs...");

    // ✅ CITEȘTE languageId din rută sau localStorage
    const languageId = this.route.snapshot.queryParams['languageId'] ||
      localStorage.getItem('adminSelectedLanguage');

    if (!languageId) {
      console.error('Nicio limbă selectată pentru administrare');
      return;
    }

    // ✅ TRIMITE parametrul către backend
    this.axiosService.request('GET', `/mcq?languageId=${languageId}`, null)
      .then(res => {
        this.mcqs = res.data.sort((a: any, b: any) => a.levelName.localeCompare(b.levelName));
        console.log("✅ MCQs loaded pentru limba:", languageId, this.mcqs);
      })
      .catch(err => {
        console.error("❌ Eroare la încărcarea MCQ:", err);
      });
  }

  // opțional: pentru ștergere
  deleteMcq(id: number): void {
    if (!confirm('Sigur vrei să ștergi această întrebare?')) return;

    this.axiosService.request('DELETE', `/mcq/${id}`, {})
      .then(() => {
        alert('Întrebarea a fost ștearsă.');
        this.fetchMcqs(); // refresh listă
      })
      .catch(err => {
        console.error('Eroare la ștergere MCQ:', err);
      });
  }
}

