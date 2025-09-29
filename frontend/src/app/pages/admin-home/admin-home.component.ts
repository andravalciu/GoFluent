import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AxiosService } from '../../axios.service';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  showDeleteConfirmation: boolean = false;
  languageToDelete: any = null;
  availableLanguages: any[] = [];
  selectedLanguageId: number | null = null;
  showAddLanguageForm: boolean = false;
  newLanguage = {
    name: '',
    code: '',
    flagEmoji: '',
    active: true
  };

  constructor(private axiosService: AxiosService, private router: Router) {}

  async ngOnInit() {
    await this.loadAvailableLanguages();

    // ✅ RESTABILEȘTE LIMBA SELECTATĂ DIN LOCALSTORAGE
    const savedLanguageId = localStorage.getItem('adminSelectedLanguage');
    if (savedLanguageId) {
      this.selectedLanguageId = parseInt(savedLanguageId);
      console.log('🔄 Limba restabilită din localStorage:', this.selectedLanguageId);
    }
  }

  async loadAvailableLanguages() {
    try {
      const res = await this.axiosService.request('GET', '/languages', {});
      this.availableLanguages = res.data;
    } catch (err) {
      console.error('Eroare la încărcarea limbilor:', err);
    }
  }

  selectLanguageForAdmin(languageId: number) {
    this.selectedLanguageId = languageId;
    localStorage.setItem('adminSelectedLanguage', languageId.toString());
  }

  changeLanguage() {
    this.selectedLanguageId = null;
    localStorage.removeItem('adminSelectedLanguage');
  }

  getSelectedLanguage() {
    return this.availableLanguages.find(lang => lang.id === this.selectedLanguageId);
  }

  async addLanguage() {
    try {
      const res = await this.axiosService.request('POST', '/languages/admin', this.newLanguage);
      console.log('Limbă adăugată cu succes:', res.data);
      await this.loadAvailableLanguages();
      this.cancelAdd();
    } catch (err) {
      console.error('Eroare la adăugarea limbii:', err);
      alert('Eroare la adăugarea limbii');
    }
  }

  cancelAdd() {
    this.showAddLanguageForm = false;
    this.newLanguage = { name: '', code: '', flagEmoji: '', active: true };
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('adminSelectedLanguage');
    this.router.navigate(['/']);
  }
  confirmDeleteLanguage(language: any): void {
    this.languageToDelete = language;
    this.showDeleteConfirmation = true;
  }

  cancelDelete(): void {
    this.showDeleteConfirmation = false;
    this.languageToDelete = null;
  }

  async deleteLanguageConfirmed(): Promise<void> {
    if (!this.languageToDelete) return;

    // Salvează numele limbii înainte să înceapă procesul
    const languageName = this.languageToDelete.name;
    const languageId = this.languageToDelete.id;

    try {
      console.log('🗑️ Șterg limba:', languageName);

      const res = await this.axiosService.request(
        'DELETE',
        `languages/admin/languages/${languageId}/cascade`,
        {}
      );

      console.log('✅ DELETE cu succes - Status:', res.status);
      console.log('✅ DELETE cu succes - Data:', res.data);

      // Dacă limba ștearsă era selectată, resetează selecția
      if (this.selectedLanguageId === languageId) {
        this.changeLanguage();
      }

      // Închide modalul ÎNAINTE să reîncarci
      this.cancelDelete();

      // Reîncarcă lista de limbi
      await this.loadAvailableLanguages();

      alert(`Limba "${languageName}" a fost ștearsă cu succes!`);

    } catch (err: any) {
      console.error('❌ EROARE la ștergere:', err);

      // Dacă a fost de fapt un succes (status 200-299)
      if (err.response && err.response.status >= 200 && err.response.status < 300) {
        console.log('✅ Era de fapt SUCCESS - Status:', err.response.status);

        if (this.selectedLanguageId === languageId) {
          this.changeLanguage();
        }
        this.cancelDelete();
        await this.loadAvailableLanguages();
        alert(`Limba "${languageName}" a fost ștearsă cu succes!`);
        return;
      }

      console.error('❌ Eroare reală:', err.message);
      alert('Eroare la ștergerea limbii. Încearcă din nou.');
    }
  }
}
