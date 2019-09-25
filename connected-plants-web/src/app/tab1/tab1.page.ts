import { Component } from '@angular/core';


import { HttpClient, HttpHeaders } from '@angular/common/http';

import { PlantServiceService } from './../plant-service.service'
import { Config } from './../config'
import { ConnectedPlants } from './../connected-plants'

import { Validators, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { AngularDelegate } from '@ionic/angular';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' })
};


@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']
})
export class Tab1Page {

  config: Config;
  threshold: number;
  measuredValue: number;
  url: string
  id: string
  newThreshold: number;
  angForm: FormGroup;
  todo = {
    'title': ''
  }

  constructor(private http: HttpClient, private plantService: PlantServiceService, private fb: FormBuilder
  ) {
    this.showConfig();
    this.createForm();
  }

  showConfig() {
    this.plantService.getConfig()
      .subscribe((data: Config) => {
        this.config = { ...data }
        this.getFromServer(data.serverUrl)
      });
  }

  getFromServer(url: string) {
    this.url = url;
    this.http.get<ConnectedPlants[]>(url).subscribe((data: ConnectedPlants[]) => {
      const lastElement = data.length - 1;
      this.threshold = data[lastElement].threshold
      this.measuredValue = data[lastElement].measuredMoistureValue
      this.id = data[lastElement].id
    })
  }

  createForm() {
    let formInput = new FormControl('Dayana', Validators.required)
    this.angForm = this.fb.group({
      name: ['', Validators.required]
    });
  }
  submitted = false;

  onSubmit() {
    this.submitted = true;
    this.storeToDb()
  }
  storeToDb() {
    let result = {
      'id': this.id,
      'threshold': this.newThreshold,
      'measuredValue': this.measuredValue
    }
    var numbers = new RegExp(/^[0-9]+$/);
    var newThresholdAsString = this.newThreshold.toString;
    if (isNaN(this.newThreshold)) {
      alert("new value is not a number")
    } else { }
    this.threshold = this.newThreshold;
    return this.http.put<ConnectedPlants>(this.url + '/' + this.id, result, httpOptions).subscribe(() => {
      // window.location.reload();
    })
  }

  title = 'connected-plants-web';
}
