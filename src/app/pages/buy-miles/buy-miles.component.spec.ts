import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuyMilesComponent } from './buy-miles.component';

describe('BuyMilesComponent', () => {
  let component: BuyMilesComponent;
  let fixture: ComponentFixture<BuyMilesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BuyMilesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BuyMilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
