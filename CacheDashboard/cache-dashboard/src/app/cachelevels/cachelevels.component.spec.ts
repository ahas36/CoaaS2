import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CacheLevelsComponent } from './cachelevels.component';

describe('CacheLevelsComponent', () => {
  let component: CacheLevelsComponent;
  let fixture: ComponentFixture<CacheLevelsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CacheLevelsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CacheLevelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
