import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

import { AppComponent } from './app.component';
import { AppService } from './app.service';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './header/header.component';
import { ParsingComponent } from './parsing/parsing.component';
import { AppRoutingModule } from './app.routing.module';
import { PrettyPrintComponent } from './pretty-print/pretty-print.component';
import { SearchComponent } from './search/search.component';
import { SortComponent } from './sort/sort.component';
import { SplitComponent } from './split/split.component';
import { ConvertComponent } from './convert/convert.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { ToastrModule} from 'ngx-toastr';
import { LocationStrategy, HashLocationStrategy } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxSpinnerModule } from 'ngx-spinner';




@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ParsingComponent,
    PrettyPrintComponent,
    SearchComponent,
    SortComponent,
    SplitComponent,
    ConvertComponent,
    FeedbackComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    NgxSpinnerModule,
    NgbModule
    
  ],
  providers: [AppService,
  { provide: LocationStrategy, useClass: HashLocationStrategy}],
  bootstrap: [AppComponent]
})
export class AppModule { }
