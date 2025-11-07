import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminHomeComponent } from './pages/admin-home/admin-home.component';
import { UserHomeComponent } from './pages/user-home/user-home.component';

import { ContentComponent } from './content/content.component';
import { CreateLessonComponent } from './pages/admin-pages/create-lesson/create-lesson.component';
import { EditLessonComponent } from './pages/admin-pages/edit-lesson/edit-lesson.component';
import { ManageLevelsComponent } from './pages/admin-pages/manage-levels/manage-levels.component';
import {ManageContentComponent} from "./pages/admin-pages/manage-content/manage-content.component";
import {ViewLessonComponent} from "./pages/admin-pages/view-lesson/view-lesson.component";
import {ViewExerciseComponent} from "./pages/view-exercise/view-exercise.component";
import { EditExerciseComponent } from './pages/admin-pages/edit-exercise/edit-exercise.component';
import {AddMcqComponent} from "./pages/admin-pages/add-mcq/add-mcq.component";
import {ManageMcqComponent} from "./pages/admin-pages/manage-mcq/manage-mcq.component";
import {EditMcqComponent} from "./pages/admin-pages/edit-mcq/edit-mcq.component";
import {UserViewLessonComponent} from "./pages/user-pages/user-view-lesson/user-view-lesson.component";
import {UserViewExerciseComponent} from "./pages/user-pages/user-view-exercise/user-view-exercise.component";
import {UserTestLevelComponent} from "./pages/user-pages/user-test-level/user-test-level.component";
import {AddExerciseComponent} from "./pages/admin-pages/add-exercise/add-exercise.component";
import {UserProgressComponent} from "./pages/user-pages/user-progress/user-progress.component";
import {AdminGuard} from "./guards/admin.guard";
import {AuthGuard} from "./guards/auth.guard";

const routes: Routes = [
  { path: '', component: ContentComponent }, // login/register
  { path: 'user-home', component: UserHomeComponent, canActivate: [AuthGuard] },
  { path: 'admin-home', component: AdminHomeComponent, canActivate: [AuthGuard, AdminGuard] },

  {
    path: 'manage-levels',
    component: ManageLevelsComponent
  },
  { path: 'manage-content', component: ManageContentComponent },
  { path: 'add-lesson', component: CreateLessonComponent },
  { path: 'view-lesson/:id', component: ViewLessonComponent },
  { path: 'edit-lesson/:id', component: EditLessonComponent },
  { path: 'lesson/:id/exercises', component: ViewExerciseComponent },
  { path: 'edit-exercise/:id', component: EditExerciseComponent },
  { path: 'add-mcq', component: AddMcqComponent },
  { path: 'manage-mcq', component: ManageMcqComponent },
  { path: 'edit-mcq/:id', component: EditMcqComponent },
  { path: 'user-view-lesson/:id', component: UserViewLessonComponent },
  { path: 'exercise/:id', component: UserViewExerciseComponent },
  { path: 'test-level/:levelId', component: UserTestLevelComponent},
  { path: 'add-exercise', component: AddExerciseComponent },
  { path: 'progress', component: UserProgressComponent },







  {
    path: '**',
    redirectTo: 'user-home'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
