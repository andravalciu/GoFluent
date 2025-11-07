import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageLevelsComponent } from './manage-levels.component';

describe('ManageLevelsComponent', () => {
  let component: ManageLevelsComponent;
  let fixture: ComponentFixture<ManageLevelsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ManageLevelsComponent]
    });
    fixture = TestBed.createComponent(ManageLevelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
