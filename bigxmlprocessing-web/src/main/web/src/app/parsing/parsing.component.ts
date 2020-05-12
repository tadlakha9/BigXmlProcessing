import { Component, OnInit } from '@angular/core';
import {FormGroup, FormBuilder} from "@angular/forms";
import { AppService } from '../app.service';

@Component({
  selector: 'app-parsing',
  templateUrl: './parsing.component.html',
  styleUrls: ['./parsing.component.css']
})
export class ParsingComponent implements OnInit {

  
  profileForm:FormGroup;
  xmlFilePath:File;
  xsdFilePath:File;
  xsltFilePath:File;
  errorFilePath:File;
  
  constructor(private fb:FormBuilder, private appService:AppService){}
  
  ngOnInit(){
  	this.profileForm = this.fb.group({
  		profile:['']
  	});
  }
  
  onSelectedXMLFile(event){
    console.log(event.target.files);
    this.xmlFilePath = event.target.files[0];
  }
  onSelectedXSDFile(event){
  	console.log(event.target.files);
  	this.xsdFilePath = event.target.files[0];
  }
  
  onSelectedERRORFile(event){
  	console.log(event.target.files);
  }
  
  
  parseXML() {
    console.log("within on submit method" );
    let formData = new FormData();
    formData.append('file', this.xmlFilePath, this.xmlFilePath.name);
    formData.append('xsdFile', this.xsdFilePath, this.xsdFilePath.name);
    this.appService.parseXML(formData)
    .subscribe((response) => {
      console.log('response received is ', response);
  },
  (error) => console.log("receivedError:"+error)
  );
    
  }

}
