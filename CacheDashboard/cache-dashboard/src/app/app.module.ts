import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { CacheLevelsComponent } from './cachelevels/cachelevels.component';
import { OverallComponent } from './overall/overall.component';
import { CSMSComponent } from './csms/csms.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { ChartsModule, ThemeService } from 'ng2-charts';
import { ApiServiceService } from './services/api-service.service';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    CacheLevelsComponent,
    OverallComponent,
    CSMSComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatExpansionModule,
    MatCardModule,
    MatTabsModule,
    ChartsModule 
  ],
  providers: [ThemeService, ApiServiceService],
  bootstrap: [AppComponent]
})

export class AppModule { }
