import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserViewLessonComponent } from './user-view-lesson.component';

describe('UserViewLessonComponent', () => {
  let component: UserViewLessonComponent;
  let fixture: ComponentFixture<UserViewLessonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserViewLessonComponent]
    });
    fixture = TestBed.createComponent(UserViewLessonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
