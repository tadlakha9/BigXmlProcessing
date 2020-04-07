import { Component, OnInit } from '@angular/core';
import {FormGroup, FormBuilder} from "@angular/forms";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'BigXMLProcessing';
  
  profileForm:FormGroup;
  
  constructor(private fb:FormBuilder){}
  
  ngOnInit(){
  	this.profileForm = this.fb.group({
  		profile:['']
  	});
  }
  
  onSelectedFile(event){
  	console.log(event.target.files);
  }
  
  
   onSubmit() {
    console.log("within on submit method" );
    //this.authService.signIn(this.loginForm.value);
  }
  
}
