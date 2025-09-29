import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from "../../../axios.service";

@Component({
  selector: 'app-edit-lesson',
  templateUrl: './edit-lesson.component.html',
  styleUrls: ['./edit-lesson.component.css']
})
export class EditLessonComponent implements OnInit {
  lessonId!: number;
  lesson: any = {
    title: '',
    description: '',
    content: '',
    levelId: null
  };
  levels: any[] = [];

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
      // Obține ID-ul lecției din URL
      this.lessonId = Number(this.route.snapshot.paramMap.get('id'));

      if (!this.lessonId) {
        console.error('❌ No lesson ID provided');
        alert('ID lecție invalid');
        this.router.navigate(['/manage-content']);
        return;
      }

      this.isLoading = true;

      // 1. Încarcă limbile disponibile
      await this.loadAvailableLanguages();

      // 2. Selectează limba
      await this.ensureLanguageSelected();

      // 3. Încarcă lecția și nivelele în paralel
      await Promise.all([
        this.loadLevels(),
        this.loadLesson()
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
        console.log('✅ Auto-selected language for edit:', this.selectedLanguageId);
      }

      // Verifică că limba selectată există în lista de limbi disponibile
      if (this.selectedLanguageId && this.availableLanguages.length > 0) {
        const exists = this.availableLanguages.some(lang => String(lang.id) === this.selectedLanguageId);
        if (!exists) {
          this.selectedLanguageId = String(this.availableLanguages[0].id);
          localStorage.setItem('adminSelectedLanguage', this.selectedLanguageId);
        }
      }

      console.log('🌐 Selected language for edit:', this.selectedLanguageId);
    } catch (err) {
      console.error('❌ Error selecting language:', err);
    }
  }

  private async loadLevels(): Promise<void> {
    try {
      if (!this.selectedLanguageId) {
        console.error('❌ Cannot load levels: No language selected');
        return;
      }

      console.log('🔄 Loading levels for language:', this.selectedLanguageId);

      const res = await this.axiosService.request('GET', `/levels?languageId=${this.selectedLanguageId}`, {});
      this.levels = res.data || [];

      console.log('✅ Levels loaded for edit:', this.levels.length);

    } catch (error: any) {
      console.error('❌ Error loading levels:', error);

      if (error.response?.status === 500 || error.response?.data?.message?.includes('No active language')) {
        alert(`Error: No active language found for ID ${this.selectedLanguageId}.`);
        // Redirect back to manage content
        this.router.navigate(['/manage-content']);
      } else {
        alert('Failed to load levels. Please try again.');
      }
    }
  }

  private async loadLesson(): Promise<void> {
    try {
      console.log('🔄 Loading lesson:', this.lessonId);

      const res = await this.axiosService.request('GET', `/lessons/${this.lessonId}`, {});
      this.lesson = res.data;

      console.log('✅ Lesson loaded for edit:', this.lesson.title);

    } catch (error: any) {
      console.error('❌ Error loading lesson:', error);

      if (error.response?.status === 404) {
        alert('Lecția nu a fost găsită.');
      } else if (error.response?.status === 403) {
        alert('Nu aveți permisiunea să editați această lecție.');
      } else {
        alert('Eroare la încărcarea lecției.');
      }

      // Redirect back on error
      this.router.navigate(['/manage-content']);
    }
  }

  async onUpdate(): Promise<void> {
    try {
      if (!this.lesson.title || !this.lesson.description || !this.lesson.levelId) {
        alert('Te rugăm să completezi toate câmpurile obligatorii.');
        return;
      }

      this.isLoading = true;
      console.log('📝 Updating lesson:', this.lessonId, this.lesson);

      await this.axiosService.request('PUT', `/lessons/${this.lessonId}`, this.lesson);

      console.log('✅ Lesson updated successfully');
      alert('Lecția a fost actualizată cu succes!');

      this.router.navigate(['/manage-content']);

    } catch (error: any) {
      console.error('❌ Error updating lesson:', error);

      if (error.response?.status === 400) {
        alert('Date invalide. Te rugăm să verifici informațiile introduse.');
      } else if (error.response?.status === 403) {
        alert('Nu aveți permisiunea să actualizați această lecție.');
      } else if (error.response?.status === 404) {
        alert('Lecția nu a fost găsită.');
      } else {
        alert('Eroare la actualizarea lecției.');
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

  goBack(): void {
    this.router.navigate(['/manage-content']);
  }
}



