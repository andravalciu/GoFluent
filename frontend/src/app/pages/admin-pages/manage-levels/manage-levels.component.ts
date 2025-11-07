import { Component } from '@angular/core';
import {AxiosService} from "../../../axios.service";
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-manage-levels',
  templateUrl: './manage-levels.component.html',
  styleUrls: ['./manage-levels.component.css']
})
export class ManageLevelsComponent {
  levelName: string = "";
  levels: any[] = []; // sau LevelDto[] dacă ai modelul
  editingLevelId: number | null = null;
  editedLevelName: string = "";
  editedLevelDifficulty: number | null = null;
  difficulty: number | null = null;
  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute  // ← ADAUGĂ
  ) {}

  editLevel(level: any): void {
    this.editingLevelId = level.id;
    this.editedLevelName = level.name;
    this.editedLevelDifficulty = level.difficulty;
  }

  cancelEdit() {
    this.editingLevelId = null;
    this.editedLevelName = "";
  }

  saveEditedLevel(): void {
    if (!this.editedLevelName.trim() || this.editedLevelDifficulty === null) {
      alert('Completează toate câmpurile.');
      return;
    }

    // ✅ CITEȘTE languageId și pentru editare
    const languageId = this.route.snapshot.queryParams['languageId'];

    const updatedLevel = {
      name: this.editedLevelName,
      difficulty: this.editedLevelDifficulty,
      languageId: languageId ? parseInt(languageId) : null  // ✅ ADAUGĂ
    };

    this.axiosService.request('PUT', `/levels/${this.editingLevelId}`, updatedLevel).then(() => {
      this.editingLevelId = null;
      this.editedLevelName = '';
      this.editedLevelDifficulty = null;
      this.loadLevels();
    }).catch(err => {
      console.error('Eroare la editarea nivelului:', err);
      alert('Eroare la editarea nivelului');
    });
  }
  ngOnInit(): void {
    this.loadLevels();
  }

  async loadLevels() {
    try {
      // ✅ CITEȘTE languageId din rută
      const languageId = this.route.snapshot.queryParams['languageId'];

      if (!languageId) {
        console.error('Nicio limbă selectată pentru administrare');
        return;
      }

      // ✅ TRIMITE parametrul către backend
      const res = await this.axiosService.request('GET', `/levels?languageId=${languageId}`, {});
      this.levels = res.data;
      console.log('✅ Nivele încărcate pentru limba:', languageId);

    } catch (err) {
      console.error('Eroare la încărcarea nivelelor:', err);
    }
  }
  addLevel() {
    if (!this.levelName.trim() || this.difficulty === null) {
      alert('Completează numele și dificultatea nivelului.');
      return;
    }

    // ✅ CITEȘTE languageId din rută (la fel ca în loadLevels)
    const languageId = this.route.snapshot.queryParams['languageId'];

    if (!languageId) {
      alert('Nicio limbă selectată pentru administrare');
      return;
    }

    const newLevel = {
      name: this.levelName,
      difficulty: this.difficulty,
      description: '', // dacă ai câmpul și vrei să-l trimiți gol
      languageId: parseInt(languageId)  // ✅ ADAUGĂ languageId!
    };

    console.log('📤 Creating level with data:', newLevel); // ← pentru debugging

    this.axiosService.request('POST', '/levels', newLevel).then(() => {
      this.levelName = '';
      this.difficulty = null;
      alert('Nivel adăugat cu succes!'); // ← feedback user
      this.loadLevels();
    }).catch(err => {
      console.error('Eroare la adăugarea nivelului:', err);
      alert('Eroare la adăugarea nivelului');
    });
  }

  deleteLevel(id: number) {
    this.axiosService.request('DELETE', `/levels/${id}`, {}).then(() => {
      this.loadLevels();
    });
  }

}
