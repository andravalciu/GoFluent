import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserTestLevelComponent } from './user-test-level.component';

describe('UserTestLevelComponent', () => {
  let component: UserTestLevelComponent;
  let fixture: ComponentFixture<UserTestLevelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserTestLevelComponent]
    });
    fixture = TestBed.createComponent(UserTestLevelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
