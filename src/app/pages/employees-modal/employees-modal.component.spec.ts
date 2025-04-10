import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeesModalComponent } from './employees-modal.component';

describe('EmployeesModalComponent', () => {
  let component: EmployeesModalComponent;
  let fixture: ComponentFixture<EmployeesModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmployeesModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
