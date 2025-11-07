import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../../../axios.service';

@Component({
  selector: 'app-add-mcq',
  templateUrl: './add-mcq.component.html',
  styleUrls: ['./add-mcq.component.css']
})
export class AddMcqComponent implements OnInit {
  mcq = {
    question: '',
    options: ['', '', '', ''], // 4 opțiuni goale
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
    private axiosService: AxiosService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      // Încarcă limbile disponibile
      await this.loadAvailableLanguages();

      // Selectează limba
      await this.ensureLanguageSelected();

      // Încarcă nivelele pentru limba selectată
      if (this.selectedLanguageId) {
        await this.loadLevels();
      } else {
        console.error('❌ No language available to work with');
        alert('Te rugăm să selectezi o limbă pentru a continua.');
      }
    } catch (error) {
      console.error('❌ Error in ngOnInit:', error);
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
        console.log('✅ Auto-selected language for MCQ:', this.selectedLanguageId);
      }

      // Verifică că limba selectată există în lista de limbi disponibile
      if (this.selectedLanguageId && this.availableLanguages.length > 0) {
        const exists = this.availableLanguages.some(lang => String(lang.id) === this.selectedLanguageId);
        if (!exists) {
          this.selectedLanguageId = String(this.availableLanguages[0].id);
          localStorage.setItem('adminSelectedLanguage', this.selectedLanguageId);
        }
      }

      console.log('🌐 Selected language for MCQ:', this.selectedLanguageId);
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

      this.isLoading = true;
      console.log('🔄 Loading levels for language:', this.selectedLanguageId);

      const res = await this.axiosService.request('GET', `/levels?languageId=${this.selectedLanguageId}`, {});
      this.levels = res.data || [];

      console.log('✅ Levels loaded for MCQ:', this.levels.length);

      // Auto-selectează primul nivel dacă există
      if (this.levels.length > 0 && !this.mcq.levelId) {
        this.mcq.levelId = this.levels[0].id;
        console.log('✅ Auto-selected first level for MCQ:', this.levels[0].name);
      }

    } catch (err: any) {
      console.error('❌ Error loading levels:', err);

      if (err.response?.status === 500 || err.response?.data?.message?.includes('No active language')) {
        alert(`Error: No active language found for ID ${this.selectedLanguageId}.`);
        this.router.navigate(['/manage-mcq']);
      } else {
        alert('Failed to load levels. Please try again.');
      }
    } finally {
      this.isLoading = false;
    }
  }

  async onSubmit(): Promise<void> {
    try {
      if (!this.selectedLanguageId) {
        alert('Te rugăm să selectezi o limbă pentru a continua.');
        return;
      }

      if (!this.mcq.levelId) {
        alert('Te rugăm să selectezi un nivel.');
        return;
      }

      const trimmedAnswer = this.mcq.correctAnswer.trim();
      const trimmedOptions = this.mcq.options.map(opt => opt.trim());

      // Validare opțiuni goale
      if (trimmedOptions.includes('')) {
        alert('Toate opțiunile trebuie completate.');
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
      console.log('📝 Creating MCQ:', this.mcq);

      await this.axiosService.request('POST', '/mcq', this.mcq);

      console.log('✅ MCQ created successfully');
      alert('Întrebarea a fost salvată cu succes!');

      // Reset form dar păstrează nivelul selectat
      const currentLevelId = this.mcq.levelId;
      this.mcq = {
        question: '',
        options: ['', '', '', ''],
        correctAnswer: '',
        levelId: currentLevelId
      };
      this.isAnswerValid = true;

      // Optionally navigate to manage page
      // this.router.navigate(['/manage-mcq']);

    } catch (err: any) {
      console.error('❌ Error saving MCQ:', err);

      if (err.response?.status === 400) {
        alert('Date invalide. Te rugăm să verifici informațiile introduse.');
      } else if (err.response?.status === 403) {
        alert('Nu aveți permisiunea să creați întrebări.');
      } else {
        alert('Eroare la salvarea întrebării. Te rugăm să încerci din nou.');
      }
    } finally {
      this.isLoading = false;
    }
  }

  // Method to manually change language
  async onLanguageChange(languageId: string): Promise<void> {
    this.selectedLanguageId = languageId;
    localStorage.setItem('adminSelectedLanguage', languageId);

    console.log('🌐 Language changed to:', languageId);

    // Reset MCQ data
    this.mcq = {
      question: '',
      options: ['', '', '', ''],
      correctAnswer: '',
      levelId: null
    };

    // Reload levels for new language
    await this.loadLevels();
  }

  trackByIndex(index: number): number {
    return index;
  }

  goBack(): void {
    this.router.navigate(['/manage-mcq']);
  }
}

