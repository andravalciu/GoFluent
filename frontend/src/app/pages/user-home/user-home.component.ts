import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AxiosService } from '../../axios.service';

@Component({
  selector: 'app-user-home',
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit {
  lessons: any[] = [];
  userName: string = '';
  myLanguages: any[] = [];
  availableLanguages: any[] = [];
  activeLanguageId: number | null = null;
  showAddLanguage: boolean = false;
  selectedLanguageName: string = '';

  // ADAUGĂ ACESTEA: 👇
  showLanguageSelection: boolean = false;
  showLevelSelection: boolean = false;
  isLoading: boolean = true;
  selectedLanguageId: number | null = null;
  availableLevels: any[] = [];

  constructor(private axiosService: AxiosService, private router: Router) {}

  logout(): void {
    localStorage.removeItem('token'); // sau ce cheie folosești
    this.router.navigate(['/']); // sau '/login' dacă ai pagină dedicată
  }



  async ngOnInit(): Promise<void> {
    console.log('🚀 Inițializez componenta...');
    await this.getUserName();
    await this.checkUserLanguages();
  }

  private async checkUserLanguages(): Promise<void> {
    try {
      console.log('🔍 Verific limbile utilizatorului...');
      await this.loadMyLanguages();  // ← Aceasta setează activeLanguageId

      if (this.myLanguages.length === 0) {
        console.log('👤 Utilizator nou - arăt selectarea limbii');
        this.showLanguageSelection = true;
        await this.loadAvailableLanguages();
      } else {
        console.log('👤 Utilizator existent - arăt dashboard-ul');
        this.showLanguageSelection = false;

        // ✅ Așteaptă ca activeLanguageId să fie setat înainte de a încărca lecțiile
        if (this.activeLanguageId) {
          await this.loadLessons();
        } else {
          console.log('⏳ Waiting for active language to be set...');
        }
      }
    } catch (err) {
      console.error('❌ Eroare la verificarea limbilor:', err);
    } finally {
      this.isLoading = false;
    }
  }
  async selectLevel(levelId: number): Promise<void> {
    try {
      console.log('🚀 Încep să învăț limba', this.selectedLanguageId, 'la nivelul', levelId);
      this.isLoading = true;

      const res = await this.axiosService.request('POST', `/languages/start-learning/${this.selectedLanguageId}/${levelId}`, {});
      console.log('✅ Succes:', res.data);

      // Ascunde selecțiile și trece la dashboard
      this.showLanguageSelection = false;
      this.showLevelSelection = false;

      // Reîncarcă datele
      await this.loadMyLanguages();
      await this.loadLessons();

    } catch (err) {
      console.error('❌ Eroare:', err);
      alert('Eroare la înregistrarea selecției. Încearcă din nou.');
    } finally {
      this.isLoading = false;
    }
  }

// Metodă pentru a reveni la selectarea limbii
  goBackToLanguageSelection(): void {
    this.showLevelSelection = false;
    this.showLanguageSelection = true;
    this.selectedLanguageId = null;
    this.availableLevels = [];
  }


  private async getUserName(): Promise<void> {
    console.log('🔍 Încercare obținere nume utilizator...');
    try {
      const res = await this.axiosService.request('GET', '/user/me', {});
      console.log('✅ User din backend:', res.data);
      this.userName = res.data.firstName || res.data.login || 'Utilizator';
      console.log('👤 userName final:', this.userName);
    } catch (err) {
      console.error('❌ Eroare la obținerea user-ului:', err);
      this.userName = 'Utilizator';
    }
  }

  async startLearningLanguage(languageId: number): Promise<void> {
    try {
      console.log('🔍 Încărcare niveluri pentru limba:', languageId);

      // Găsește și stochează numele limbii ÎNAINTE de a face request-ul
      const selectedLanguage = this.availableLanguages.find(lang => lang.id === languageId);
      if (!selectedLanguage) {
        alert('Limba selectată nu a fost găsită.');
        return;
      }

      this.selectedLanguageName = selectedLanguage.name; // ADAUGĂ ASTA

      // Încarcă nivelurile pentru limba selectată
      const levelsResponse = await this.axiosService.request('GET', `/languages/${languageId}/levels`, {});
      const levels = levelsResponse.data;

      if (levels.length === 0) {
        alert('Nu sunt niveluri disponibile pentru această limbă încă.');
        return;
      }

      console.log('📊 Niveluri găsite:', levels);

      // Arată utilizatorului să selecteze nivelul
      this.showLanguageSelection = false;
      this.showLevelSelection = true;
      this.selectedLanguageId = languageId;
      this.availableLevels = levels;

    } catch (err) {
      console.error('❌ Eroare la încărcarea nivelurilor:', err);
      alert('Eroare la încărcarea nivelurilor. Încearcă din nou.');
    }
  }

  viewLesson(id: number): void {
    console.log('Navighez la lecția cu id:', id); // adaugă pentru debug
    this.router.navigate(['/user-view-lesson', id]);
  }

  goToProgress(): void {
    this.router.navigate(['/progress']);
  }

  goToTest(): void {
    console.log('Button clicked!');
    // Trebuie să ai levelId disponibil în componentă
    const levelId = 1; // sau this.currentLevelId sau cum îl obții
    this.router.navigate(['/test-level', levelId]);
  }
  async loadMyLanguages(): Promise<void> {
    try {
      const res = await this.axiosService.request('GET', '/languages/my-languages', {});
      this.myLanguages = res.data;
      console.log('📋 Limbile mele:', this.myLanguages);

      const activeLang = this.myLanguages.find(lang => lang.isActive);
      if (activeLang) {
        // ✅ Schimbă și aici:
        this.activeLanguageId = activeLang.languageId; // Nu activeLang.id!
        console.log('🎯 Limba activă:', activeLang.languageName);
      }
    } catch (err) {
      console.error('Eroare la încărcarea limbilor mele:', err);
    }
  }

  async loadAvailableLanguages(): Promise<void> {
    try {
      const res = await this.axiosService.request('GET', '/languages', {});
      this.availableLanguages = res.data;
    } catch (err) {
      console.error('Eroare la încărcarea limbilor:', err);
      this.availableLanguages = [];
    }
  }
  getCurrentLanguage() {
    return this.myLanguages.find(lang => lang.languageId === this.activeLanguageId);
  }


  async loadLessons(): Promise<void> {
    try {
      if (!this.activeLanguageId) {
        console.log('❌ No active language selected yet');
        return;
      }

      console.log('🔄 Loading lessons for language:', this.activeLanguageId);

      const res = await this.axiosService.request('GET', `/lessons?languageId=${this.activeLanguageId}`, {});
      this.lessons = res.data;

      console.log('✅ Lessons loaded:', this.lessons.length);
    } catch (err) {
      console.error('❌ Error loading lessons:', err);
      this.lessons = [];
    }
  }

  // ✅ Fix (reîncarcă și myLanguages):
  async switchLanguage(): Promise<void> {
    if (this.activeLanguageId) {
      try {
        console.log('🔄 Switching to language:', this.activeLanguageId);

        await this.axiosService.request('PUT', `/languages/switch-language/${this.activeLanguageId}`, {});

        console.log('✅ Language switched successfully');

        // ✅ Reîncarcă ambele pentru sincronizare completă
        await this.loadMyLanguages();   // Actualizează isActive flags
        await this.loadLessons();       // Încarcă lecțiile pentru limba nouă

      } catch (err) {
        console.error('❌ Eroare la schimbarea limbii:', err);
      }
    }
  }
  // În user-home.component.ts
  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKey(event: KeyboardEvent) {
    if (this.showAddLanguage) {
      this.showAddLanguage = false;
    }
    if (this.showLevelSelection) {
      this.goBackToLanguageSelection();
    }
  }
  async openAddLanguageModal(): Promise<void> {
    // Încarcă limbile dacă nu sunt deja încărcate
    if (this.availableLanguages.length === 0) {
      await this.loadAvailableLanguages();
    }
    this.showAddLanguage = true;
  }

}

