import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardingConfirmationComponent } from './boarding-confirmation.component';

describe('BoardingConfirmationComponent', () => {
  let component: BoardingConfirmationComponent;
  let fixture: ComponentFixture<BoardingConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardingConfirmationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardingConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
