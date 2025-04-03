import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookingLookupComponent } from './booking-lookup.component';

describe('BookingLookupComponent', () => {
  let component: BookingLookupComponent;
  let fixture: ComponentFixture<BookingLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BookingLookupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookingLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
