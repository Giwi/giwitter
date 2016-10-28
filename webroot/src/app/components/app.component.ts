import {Component, OnInit} from "@angular/core";
import {EventsService} from "../services/event.service";
import {User} from "../model/user";
import {Router} from "@angular/router";
import {UserService} from "../services/user.service";
import {AuthenticationService} from "../services/authentication.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserProfileComponent} from "./user-profile/user-profile.component";
import {environment} from "../../environments/environment";
/**
 *
 */
@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})

/**
 *
 */
export class AppComponent implements OnInit {
    logged = false;
    user: User;
    isCollapsed = true;
    avatarUrl = environment.apiUrl + '/api/1/user/avatar/';

    /**
     *
     * @param eventsService
     * @param userService
     * @param router
     * @param authenticationService
     * @param modalService
     */
    constructor(private eventsService: EventsService, private userService: UserService, private router: Router,
                private authenticationService: AuthenticationService, private modalService: NgbModal) {
    }

    /**
     *
     */
    ngOnInit() {
        this.eventsService.on('user-logged', user => {
            this.user = user;
            this.logged = true;
        });
        if (this.authenticationService.token) {
            this.userService.getCurrentUser().subscribe(result => {
                this.eventsService.broadcast('user-logged', result);
                this.router.navigate([this.router.url]);
                this.authenticationService.user = result;
            }, error => this.userService.handleError(error));
        }
    }

    openProfile() {
        const modalRef = this.modalService.open(UserProfileComponent);
        modalRef.result.then(r => {
            modalRef.close('');
        });
    }

    /**
     *
     */
    logout() {
        this.isCollapsed = true;
        this.userService.logout();
        this.logged = false;
    }
}
