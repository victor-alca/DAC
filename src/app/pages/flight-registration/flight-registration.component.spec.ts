import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlightRegistrationComponent } from './flight-registration.component';

describe('FlightRegistrationComponent', () => {
  let component: FlightRegistrationComponent;
  let fixture: ComponentFixture<FlightRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FlightRegistrationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlightRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
