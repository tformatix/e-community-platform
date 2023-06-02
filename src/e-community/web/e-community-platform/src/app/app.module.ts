import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { Configuration } from './core/api/v1';
import { CoreModule } from './core/core.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CoreModule,
    HttpClientModule,
  ],
  providers: [
    {
      provide: Configuration,
      useValue: new Configuration({
        basePath: 'https://e-community.azurewebsites.net'
        // basePath: 'https://localhost:5001'
      })
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
