import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageMcqComponent } from './manage-mcq.component';

describe('ManageMcqComponent', () => {
  let component: ManageMcqComponent;
  let fixture: ComponentFixture<ManageMcqComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ManageMcqComponent]
    });
    fixture = TestBed.createComponent(ManageMcqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
