import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-edit-mcq',
  templateUrl: './edit-mcq.component.html',
  styleUrls: ['./edit-mcq.component.css']
})
export class EditMcqComponent implements OnInit {
  mcqId!: number;
  mcq: any = {
    question: '',
    options: ['', '', '', ''],
    correctAnswer: '',
    levelId: null
  };
  levels: any[] = [];
  isAnswerValid: boolean = true;

  // Language management
  selectedLanguageId: string = '';
  availableLanguages: any[] = [];
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private axiosService: AxiosService
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      // Obține ID-ul MCQ din URL
      const id = this.route.snapshot.paramMap.get('id');
      if (!id) {
        console.error('❌ No MCQ ID provided');
        alert('ID întrebare invalid');
        this.router.navigate(['/manage-mcq']);
        return;
      }
      this.mcqId = Number(id);

      this.isLoading = true;

      // 1. Încarcă limbile disponibile
      await this.loadAvailableLanguages();

      // 2. Selectează limba
      await this.ensureLanguageSelected();

      // 3. Încarcă nivelele și MCQ-ul în paralel
      await Promise.all([
        this.loadLevels(),
        this.loadMcq()
      ]);

    } catch (error) {
      console.error('❌ Error in ngOnInit:', error);
      alert('Eroare la încărcarea datelor');
    } finally {
      this.isLoading = false;
    }
  }

  async loadAvailableLanguages(): Promise<void> {
    try {
      console.log('🔄 Loading available languages...');
      const res = await this.axiosService.request('GET', '/languages', {});
      this.availableLanguages = res.data || [];
      console.log('✅ Available languages loaded:', this.availableLanguages.length);
    } catch (err) {
      console.error('❌ Error loading languages:', err);
      this.availableLanguages = [];
    }
  }

  private async ensureLanguageSelected(): Promise<void> {
    try {
      // Încearcă să obții limba din query params sau localStorage
      this.selectedLanguageId =
        this.route.snapshot.queryParams['languageId'] ||
        localStorage.getItem('adminSelectedLanguage') ||
        '';

      // Dacă nu ai limbă selectată, dar ai limbi disponibile, selectează prima
      if (!this.selectedLanguageId && this.availableLanguages.length > 0) {
        this.selectedLanguageId = String(this.availableLanguages[0].id);
        localStorage.setItem('adminSelectedLanguage', this.selectedLanguageId);
        console.log('✅ Auto-selected language for edit MCQ:', this.selectedLanguageId);
      }

      // Verifică că limba selectată există în lista de limbi disponibile
      if (this.selectedLanguageId && this.availableLanguages.length > 0) {
        const exists = this.availableLanguages.some(lang => String(lang.id) === this.selectedLanguageId);
        if (!exists) {
          this.selectedLanguageId = String(this.availableLanguages[0].id);
          localStorage.setItem('adminSelectedLanguage', this.selectedLanguageId);
        }
      }

      console.log('🌐 Selected language for edit MCQ:', this.selectedLanguageId);
    } catch (err) {
      console.error('❌ Error selecting language:', err);
    }
  }

  async loadLevels(): Promise<void> {
    try {
      if (!this.selectedLanguageId) {
        console.error('❌ Cannot load levels: No language selected');
        return;
      }

      console.log('🔄 Loading levels for language:', this.selectedLanguageId);

      const res = await this.axiosService.request('GET', `/levels?languageId=${this.selectedLanguageId}`, {});
      this.levels = res.data || [];

      console.log('✅ Levels loaded for edit MCQ:', this.levels.length);

    } catch (error: any) {
      console.error('❌ Error loading levels:', error);

      if (error.response?.status === 500 || error.response?.data?.message?.includes('No active language')) {
        alert(`Error: No active language found for ID ${this.selectedLanguageId}.`);
        this.router.navigate(['/manage-mcq']);
      } else {
        alert('Failed to load levels. Please try again.');
      }
    }
  }

  private async loadMcq(): Promise<void> {
    try {
      console.log('🔄 Loading MCQ:', this.mcqId);

      const res = await this.axiosService.request('GET', `/mcq/${this.mcqId}`, {});
      console.log('MCQ primit:', res.data);

      const data = res.data;
      this.mcq = {
        id: data.id,
        question: data.question || '',
        options: Array.isArray(data.options) && data.options.length > 0
          ? data.options
          : ['', '', '', ''],
        correctAnswer: data.correctAnswer || '',
        levelId: data.levelId || null
      };

      console.log('✅ MCQ loaded for edit:', this.mcq.question);

    } catch (error: any) {
      console.error('❌ Error loading MCQ:', error);

      if (error.response?.status === 404) {
        alert('Întrebarea nu a fost găsită.');
      } else if (error.response?.status === 403) {
        alert('Nu aveți permisiunea să editați această întrebare.');
      } else {
        alert('Eroare la încărcarea întrebării.');
      }

      // Redirect back on error
      this.router.navigate(['/manage-mcq']);
    }
  }

  async onSubmit(): Promise<void> {
    try {
      if (!this.mcq.question.trim()) {
        alert('Întrebarea este obligatorie.');
        return;
      }

      if (!this.mcq.levelId) {
        alert('Te rugăm să selectezi un nivel.');
        return;
      }

      const trimmedAnswer = this.mcq.correctAnswer.trim();
      const trimmedOptions = this.mcq.options.map((opt: string) => opt.trim());

      // Validare opțiuni goale
      if (trimmedOptions.includes('')) {
        alert('Toate opțiunile trebuie completate.');
        return;
      }

      // Validare numărul de opțiuni
      if (!Array.isArray(this.mcq.options) || this.mcq.options.length !== 4) {
        alert('Trebuie să existe exact 4 opțiuni.');
        return;
      }

      // Validare răspuns corect
      this.isAnswerValid = trimmedOptions.includes(trimmedAnswer);
      if (!this.isAnswerValid) {
        alert('Răspunsul corect trebuie să corespundă uneia dintre opțiuni.');
        return;
      }

      this.mcq.options = trimmedOptions;
      this.mcq.correctAnswer = trimmedAnswer;

      this.isLoading = true;
      console.log('📝 Updating MCQ:', this.mcqId, this.mcq);

      await this.axiosService.request('PUT', `/mcq/${this.mcq.id}`, this.mcq);

      console.log('✅ MCQ updated successfully');
      alert('Modificarea a fost salvată cu succes!');

      this.router.navigate(['/manage-mcq']);

    } catch (error: any) {
      console.error('❌ Error updating MCQ:', error);

      if (error.response?.status === 400) {
        alert('Date invalide. Te rugăm să verifici informațiile introduse.');
      } else if (error.response?.status === 403) {
        alert('Nu aveți permisiunea să actualizați această întrebare.');
      } else if (error.response?.status === 404) {
        alert('Întrebarea nu a fost găsită.');
      } else {
        alert('Eroare la actualizarea întrebării.');
      }
    } finally {
      this.isLoading = false;
    }
  }

  // Method to manually change language (if you want to add language selection)
  async onLanguageChange(languageId: string): Promise<void> {
    this.selectedLanguageId = languageId;
    localStorage.setItem('adminSelectedLanguage', languageId);

    console.log('🌐 Language changed to:', languageId);

    // Reload levels for new language
    await this.loadLevels();
  }

  trackByIndex(index: number): number {
    return index;
  }

  getLevelName(levelId: any): string {
    if (!this.levels || !levelId) return 'Necunoscut';
    const level = this.levels.find(l => l.id == levelId);
    return level ? level.name : 'Necunoscut';
  }

  goBack(): void {
    this.router.navigate(['/manage-mcq']);
  }
}

