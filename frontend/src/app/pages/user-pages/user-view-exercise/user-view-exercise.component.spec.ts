import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserViewExerciseComponent } from './user-view-exercise.component';

describe('UserViewExerciseComponent', () => {
  let component: UserViewExerciseComponent;
  let fixture: ComponentFixture<UserViewExerciseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserViewExerciseComponent]
    });
    fixture = TestBed.createComponent(UserViewExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
