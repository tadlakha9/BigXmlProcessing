import { Component, OnInit } from '@angular/core';
import { FormGroup, NgForm } from '@angular/forms';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  
  constructor() { }

  ngOnInit() {
  }
  submitform(){
    console.log('inside submit button');
  }

   filesPicked(files) {
    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const path = file.webkitRelativePath.split('/');
    
    }
}

save(form:NgForm){

}

}
