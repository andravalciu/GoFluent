import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule} from "@angular/forms";

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { AuthContentComponent } from './auth-content/auth-content.component';
import { WelcomeContentComponent } from './welcome-content/welcome-content.component';
import { LoginFormComponent } from './login-form/login-form.component';
import { ContentComponent } from './content/content.component';
import { ButtonsComponent } from './buttons/buttons.component';
import { AdminHomeComponent } from './pages/admin-home/admin-home.component';
import { UserHomeComponent } from './pages/user-home/user-home.component';
import { AppRoutingModule } from './app-routing.module';
import { CreateLessonComponent } from './pages/admin-pages/create-lesson/create-lesson.component';
import { EditLessonComponent } from './pages/admin-pages/edit-lesson/edit-lesson.component';
import { ManageLevelsComponent } from './pages/admin-pages/manage-levels/manage-levels.component';
import { ManageContentComponent } from './pages/admin-pages/manage-content/manage-content.component';
import { ViewLessonComponent } from './pages/admin-pages/view-lesson/view-lesson.component';
import { ViewExerciseComponent } from './pages/view-exercise/view-exercise.component';
import { EditExerciseComponent } from './pages/admin-pages/edit-exercise/edit-exercise.component';
import { AddMcqComponent } from './pages/admin-pages/add-mcq/add-mcq.component';
import { ManageMcqComponent } from './pages/admin-pages/manage-mcq/manage-mcq.component';
import { EditMcqComponent } from './pages/admin-pages/edit-mcq/edit-mcq.component';
import { UserViewLessonComponent } from './pages/user-pages/user-view-lesson/user-view-lesson.component';
import { UserViewExerciseComponent } from './pages/user-pages/user-view-exercise/user-view-exercise.component';
import { UserTestLevelComponent } from './pages/user-pages/user-test-level/user-test-level.component';
import { AddExerciseComponent } from './pages/admin-pages/add-exercise/add-exercise.component';
import { UserProgressComponent } from './pages/user-pages/user-progress/user-progress.component';



@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    AuthContentComponent,
    WelcomeContentComponent,
    LoginFormComponent,
    ContentComponent,
    ButtonsComponent,
    AdminHomeComponent,
    UserHomeComponent,
    ContentComponent,
    CreateLessonComponent,
    EditLessonComponent,
    ManageLevelsComponent,
    ManageContentComponent,
    ViewLessonComponent,
    ViewExerciseComponent,
    EditExerciseComponent,
    AddMcqComponent,
    ManageMcqComponent,
    EditMcqComponent,
    UserViewLessonComponent,
    UserViewExerciseComponent,
    UserTestLevelComponent,
    AddExerciseComponent,
    UserProgressComponent,


  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
