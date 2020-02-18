import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'chutney-doc-page',
  templateUrl: './documentation.component.html',
  styleUrls: ['./documentation.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DocumentationComponent implements OnInit {

  isActivated: boolean;
  documentation: string;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    fetch('/assets/doc/user_manual.adoc')
    .then(response => response.text())
    .then((data) => {
      this.documentation = data;
    });

    this.getActivation();
  }

  getActivation(): any {
    this.getActivationAPI().subscribe(
      (res) => {
        this.isActivated = res;
      },
      (error) => { console.log(error); }
    );
  }

  getActivationAPI(): Observable<boolean> {
    return this.http.get<boolean>(environment.backend + '/api/documentation');
  }

  toggle() {
    this.toggleAPI().subscribe(
      (res) => {
        this.isActivated = res;
      },
      (error) => { console.log(error);}
    );
  }

  toggleAPI(): Observable<boolean> {
    return this.http.post<boolean>(environment.backend + '/api/documentation', '');
  }

}
