import { TestBed } from '@angular/core/testing';

import { PlantServiceService } from './plant-service.service';

describe('PlantServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PlantServiceService = TestBed.get(PlantServiceService);
    expect(service).toBeTruthy();
  });
});
