
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AxiosService } from 'src/app/axios.service';

@Component({
  selector: 'app-manage-content',
  templateUrl: './manage-content.component.html',
  styleUrls: ['./manage-content.component.css']
})
export class ManageContentComponent implements OnInit {
  lessons: any[] = [];
  availableLevels: any[] = [];

  // Form properties for adding lessons
  lessonTitle: string = '';
  lessonDescription: string = '';
  lessonContent: string = '';
  selectedLevelId: string = '';

  constructor(
    private axiosService: AxiosService,
    private router: Router,
    private route: ActivatedRoute // Added missing injection
  ) {}

  async ngOnInit(): Promise<void> { // Made async and added return type
    await this.loadLevelsForLanguage();
    await this.loadLessons(); // Made this async too for consistency
  }

  async loadLevelsForLanguage(): Promise<void> {
    try {
      const languageId = this.route.snapshot.queryParams['languageId'] ||
        localStorage.getItem('adminSelectedLanguage');

      if (!languageId) {
        console.warn('No language ID found');
        return;
      }

      const res = await this.axiosService.request('GET', `/levels?languageId=${languageId}`, {});
      this.availableLevels = res.data;
      console.log('✅ Levels loaded for language:', languageId);
    } catch (err) {
      console.error('Error loading levels:', err);
    }
  }

  async addLesson(): Promise<void> {
    try {
      // Validation
      if (!this.lessonTitle || !this.lessonDescription || !this.lessonContent || !this.selectedLevelId) {
        console.error('All fields are required');
        return;
      }

      const newLesson = {
        title: this.lessonTitle,
        description: this.lessonDescription,
        content: this.lessonContent,
        levelId: parseInt(this.selectedLevelId)
      };

      await this.axiosService.request('POST', '/lessons', newLesson);
      console.log('✅ Lesson added successfully');

      // Clear form after successful addition
      this.clearForm();

      // Reload lessons
      await this.loadLessons();
    } catch (err) {
      console.error('Error adding lesson:', err);
    }
  }

  private clearForm(): void {
    this.lessonTitle = '';
    this.lessonDescription = '';
    this.lessonContent = '';
    this.selectedLevelId = '';
  }

  async loadLessons(): Promise<void> {
    try {
      const selectedLanguageId = localStorage.getItem('adminSelectedLanguage');

      if (!selectedLanguageId) {
        console.error('No language selected for administration');
        return;
      }

      const res = await this.axiosService.request('GET', `/lessons?languageId=${selectedLanguageId}`, {});
      this.lessons = res.data;
      console.log('✅ Lessons loaded for language:', selectedLanguageId);

    } catch (err) {
      console.error('Error loading lessons:', err);
    }
  }

  goToAddLesson(): void {
    this.router.navigate(['/add-lesson']);
  }

  viewLesson(id: number): void {
    this.router.navigate(['/view-lesson', id]);
  }

  editLesson(id: number): void {
    this.router.navigate(['/edit-lesson', id]);
  }

  async deleteLesson(id: number): Promise<void> {
    if (confirm('Are you sure you want to delete this lesson?')) {
      try {
        await this.axiosService.request('DELETE', `/lessons/${id}`, {});
        console.log('✅ Lesson deleted successfully');
        await this.loadLessons(); // Reload the list
      } catch (err) {
        console.error('Error deleting lesson:', err);
      }
    }
  }
}
