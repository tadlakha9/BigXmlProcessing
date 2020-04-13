import { Component, OnInit } from '@angular/core';
import {FormGroup, FormBuilder} from "@angular/forms";
import { AppService } from '../app.service';

@Component({
  selector: 'app-parsing',
  templateUrl: './parsing.component.html',
  styleUrls: ['./parsing.component.css']
})
export class ParsingComponent implements OnInit {

  title = 'BigXMLProcessing';
  
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
  }
  onSelectedXSLTFile(event){
  	console.log(event.target.files);
  }
  onSelectedERRORFile(event){
  	console.log(event.target.files);
  }
  
  
  transformXML() {
    console.log("within on submit method" );
    let formData = new FormData();
    formData.append('file', this.xmlFilePath, this.xmlFilePath.name);
    this.appService.transformXML(formData)
    .subscribe((response) => {
      console.log('response received is ', response);
  },
  (error) => console.log("receivedError:"+error)
  );
    
  }

}
