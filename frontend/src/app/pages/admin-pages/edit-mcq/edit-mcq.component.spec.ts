import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditMcqComponent } from './edit-mcq.component';

describe('EditMcqComponent', () => {
  let component: EditMcqComponent;
  let fixture: ComponentFixture<EditMcqComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditMcqComponent]
    });
    fixture = TestBed.createComponent(EditMcqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
