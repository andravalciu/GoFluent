import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AxiosService } from '../../../axios.service';


@Component({
  selector: 'app-create-lesson',
  templateUrl: './create-lesson.component.html',
  styleUrls: ['./create-lesson.component.css']
})
export class CreateLessonComponent implements OnInit {
  lesson = {
    title: '',
    description: '',
    content: '',
    levelId: null
  };

  lessonId: number | null = null;

  exercise = {
    question: '',
    answer: ''
  };

  levels: any[] = [];

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
    console.log('🚀 CreateLessonComponent ngOnInit started');

    try {
      // ÎNTOTDEAUNA încarcă limbile disponibile mai întâi
      await this.loadAvailableLanguages();

      // Apoi încearcă să selectezi o limbă
      await this.ensureLanguageSelected();

      console.log('🔍 After ensureLanguageSelected, selectedLanguageId:', this.selectedLanguageId);

      if (this.selectedLanguageId) {
        console.log('✅ Language found, loading levels...');
        await this.loadLevels();
      } else {
        console.error('❌ No language available to work with');
        console.log('📊 Available languages:', this.availableLanguages);

        if (this.availableLanguages.length === 0) {
          alert('No languages found. Please create a language first, or contact your administrator.');
        } else {
          alert('Please select a language first.');
        }
      }
    } catch (error) {
      console.error('❌ Error in ngOnInit:', error);
    }
  }

  private async ensureLanguageSelected(): Promise<void> {
    try {
      console.log('🔍 Starting ensureLanguageSelected...');

      // Check query params
      const queryLanguageId = this.route.snapshot.queryParams['languageId'];
      console.log('🔍 Query param languageId:', queryLanguageId);

      // Check localStorage
      const storedLanguageId = localStorage.getItem('adminSelectedLanguage');
      console.log('🔍 Stored languageId:', storedLanguageId);

      // Set selectedLanguageId from available sources
      this.selectedLanguageId = queryLanguageId || storedLanguageId || '';
      console.log('🔍 Initial selectedLanguageId:', this.selectedLanguageId);

      // Dacă nu avem limbă selectată, dar avem limbi disponibile, selectează prima
      if (!this.selectedLanguageId && this.availableLanguages.length > 0) {
        console.log('🔄 No language selected, auto-selecting first available...');
        this.selectedLanguageId = String(this.availableLanguages[0].id);
        localStorage.setItem('adminSelectedLanguage', this.selectedLanguageId);
        console.log('✅ Auto-selected language:', this.selectedLanguageId, 'Name:', this.availableLanguages[0].name);
      }

      // Verifică dacă limba selectată chiar există în lista de limbi disponibile
      if (this.selectedLanguageId && this.availableLanguages.length > 0) {
        const selectedLang = this.availableLanguages.find(lang => String(lang.id) === this.selectedLanguageId);
        if (!selectedLang) {
          console.log('⚠️ Selected language not found in available languages, selecting first available...');
          this.selectedLanguageId = String(this.availableLanguages[0].id);
          localStorage.setItem('adminSelectedLanguage', this.selectedLanguageId);
        }
      }

      console.log('🌐 Final selected language ID:', this.selectedLanguageId);

    } catch (err) {
      console.error('❌ Error ensuring language selected:', err);
    }
  }

  async loadAvailableLanguages(): Promise<void> {
    try {
      console.log('🔄 Loading available languages...');
      this.isLoading = true;

      const res = await this.axiosService.request('GET', '/languages', {});
      this.availableLanguages = res.data || [];

      console.log('✅ Available languages loaded:', this.availableLanguages);

    } catch (err) {
      console.error('❌ Error loading languages:', err);
      this.availableLanguages = [];
    } finally {
      this.isLoading = false;
    }
  }

  private async loadLevels(): Promise<void> {
    try {
      console.log('🔍 loadLevels called with selectedLanguageId:', this.selectedLanguageId);

      if (!this.selectedLanguageId) {
        console.error('❌ Cannot load levels: No language selected');
        console.log('🔍 Debug info:');
        console.log('  - selectedLanguageId:', this.selectedLanguageId);
        console.log('  - localStorage value:', localStorage.getItem('adminSelectedLanguage'));
        console.log('  - availableLanguages:', this.availableLanguages);
        return;
      }

      this.isLoading = true;
      console.log('🔄 Loading levels for language:', this.selectedLanguageId);

      const response = await this.axiosService.request('GET', `/levels?languageId=${this.selectedLanguageId}`, {});
      this.levels = response.data || [];

      console.log('✅ Levels loaded successfully:', this.levels.length);
      console.log('📊 Levels details:', this.levels.map(level => ({ id: level.id, name: level.name, description: level.description })));

      // Preselect first level if available and no level is selected
      if (this.levels.length > 0 && !this.lesson.levelId) {
        this.lesson.levelId = this.levels[0].id;
        console.log('✅ Auto-selected first level:', this.levels[0].name);
      } else if (this.levels.length === 0) {
        console.log('⚠️ No levels found for this language');
      }

    } catch (error: any) {
      console.error('❌ Error loading levels:', error);
      console.log('🔍 Error details:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });

      if (error.response?.status === 500 || error.response?.data?.message?.includes('No active language')) {
        alert(`Error: No active language found for ID ${this.selectedLanguageId}. Please select a different language or create/activate this language.`);

        // Clear the invalid language selection
        localStorage.removeItem('adminSelectedLanguage');
        this.selectedLanguageId = '';

        // Try to load available languages again
        await this.loadAvailableLanguages();
      } else if (error.response?.status === 400 && error.response?.data?.code === 'NO_ACTIVE_LANGUAGE') {
        alert('No active language found. Please create and activate a language first.');
      } else {
        alert('Failed to load levels. Please try again.');
      }
    } finally {
      this.isLoading = false;
    }
  }

  // Debug method to manually check what's in localStorage
  checkLocalStorage(): void {
    console.log('🔍 LocalStorage debug:');
    console.log('  - adminSelectedLanguage:', localStorage.getItem('adminSelectedLanguage'));
    console.log('  - All localStorage keys:', Object.keys(localStorage));
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key !== null) {  // Verificăm că key nu este null
        console.log(`  - ${key}:`, localStorage.getItem(key));
      }
    }
  }

  // Debug method to manually set language
  async debugSetLanguage(languageId: string): Promise<void> {
    console.log('🔧 Debug: Manually setting language to:', languageId);
    this.selectedLanguageId = languageId;
    localStorage.setItem('adminSelectedLanguage', languageId);
    await this.loadLevels();
  }

  async onSubmit(): Promise<void> {
    try {
      if (!this.selectedLanguageId) {
        alert('Please select a language first');
        return;
      }

      if (!this.lesson.title || !this.lesson.description || !this.lesson.levelId) {
        alert('Please fill in all required fields');
        return;
      }

      this.isLoading = true;
      console.log('📝 Creating lesson:', this.lesson);

      const response = await this.axiosService.request('POST', '/lessons', this.lesson);

      console.log('✅ Lesson created successfully:', response.data);
      alert('Lecția a fost creată cu succes!');

      this.lessonId = response.data.id;

      // Reset form but keep selected level
      const currentLevelId = this.lesson.levelId;
      this.lesson = {
        title: '',
        description: '',
        content: '',
        levelId: currentLevelId
      };

    } catch (error: any) {
      console.error('❌ Error creating lesson:', error);

      if (error.response?.status === 400) {
        alert('Invalid data provided. Please check your input.');
      } else if (error.response?.status === 403) {
        alert('Access denied. You don\'t have permission to create lessons.');
      } else if (error.response?.status === 401) {
        alert('Please login again.');
        this.router.navigate(['/login']);
      } else {
        alert('A apărut o eroare la creare.');
      }
    } finally {
      this.isLoading = false;
    }
  }

  async addExercise(): Promise<void> {
    try {
      if (!this.lessonId) {
        alert('Lecția nu a fost salvată încă!');
        return;
      }

      if (!this.exercise.question || !this.exercise.answer) {
        alert('Please fill in both question and answer');
        return;
      }

      this.isLoading = true;

      const payload = {
        ...this.exercise,
        lessonId: this.lessonId
      };

      console.log('📝 Adding exercise:', payload);

      await this.axiosService.request('POST', '/exercises', payload);

      console.log('✅ Exercise added successfully');
      alert('Exercițiul a fost adăugat!');

      // Reset exercise form
      this.exercise = { question: '', answer: '' };

    } catch (error: any) {
      console.error('❌ Error adding exercise:', error);

      if (error.response?.status === 400) {
        alert('Invalid exercise data. Please check your input.');
      } else if (error.response?.status === 403) {
        alert('Access denied. You don\'t have permission to add exercises.');
      } else {
        alert('A apărut o eroare la adăugarea exercițiului.');
      }
    } finally {
      this.isLoading = false;
    }
  }

  // Method to manually change language
  async onLanguageChange(languageId: string): Promise<void> {
    console.log('🌐 Language change requested to:', languageId);

    this.selectedLanguageId = languageId;
    localStorage.setItem('adminSelectedLanguage', languageId);

    console.log('🌐 Language changed to:', languageId);

    // Reset lesson data
    this.lesson = {
      title: '',
      description: '',
      content: '',
      levelId: null
    };
    this.lessonId = null;

    // Reload levels for new language
    await this.loadLevels();
  }

  // Method to go back to manage content
  goBack(): void {
    this.router.navigate(['/manage-content']);
  }
}
