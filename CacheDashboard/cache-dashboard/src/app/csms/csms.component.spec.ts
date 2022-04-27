import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CSMSComponent } from './csms.component';

describe('CSMSComponent', () => {
  let component: CSMSComponent;
  let fixture: ComponentFixture<CSMSComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CSMSComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CSMSComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
