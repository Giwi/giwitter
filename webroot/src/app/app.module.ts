import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule} from "@angular/router";
import {ToasterModule} from "angular2-toaster/angular2-toaster";
import {AppComponent} from "./components/app.component";
import {LoginBoxComponent} from "./components/login-box/login-box.component";
import {ApiService} from "./services/api.service";
import {MainPageComponent} from "./components/main-page/main-page.component";
import {SignupComponent} from "./components/signup/signup.component";
import {AuthenticationService} from "./services/authentication.service";
import {AuthGuard} from "./guards/auth.guard";
import {EventsService} from "./services/event.service";
import {MessageListComponent} from "./components/message-list/message-list.component";
import {UserService} from "./services/user.service";
import {MessageService} from "./services/message.service";
import {MessageAddComponent} from "./components/message-add/message-add.component";
import {UserProfileComponent} from "./components/user-profile/user-profile.component";
import {FileUploadModule} from "ng2-file-upload";

@NgModule({
    declarations: [
        AppComponent,
        LoginBoxComponent,
        MainPageComponent,
        SignupComponent,
        MessageListComponent,
        MessageAddComponent,
        UserProfileComponent
    ],
    imports: [
        NgbModule.forRoot(),
        RouterModule.forRoot([
            {
                path: '', component: MainPageComponent, pathMatch: 'full'
            },
            {
                path: 'login', component: LoginBoxComponent
            },
            {
                path: 'signup', component: SignupComponent
            },
            {
                path: 'message', component: MessageListComponent, canActivate: [AuthGuard]
            }
        ], {useHash: true}),
        BrowserModule,
        FormsModule,
        HttpModule,
        ToasterModule,
        FileUploadModule
    ],
    providers: [
        ApiService,
        UserService,
        MessageService,
        AuthenticationService,
        AuthGuard,
        EventsService
    ],
    entryComponents: [MessageAddComponent, UserProfileComponent],
    bootstrap: [AppComponent]
})
export class AppModule {
}
