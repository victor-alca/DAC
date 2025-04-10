import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfimationModalComponent } from './confimation-modal.component';

describe('ConfimationModalComponent', () => {
  let component: ConfimationModalComponent;
  let fixture: ComponentFixture<ConfimationModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfimationModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfimationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
