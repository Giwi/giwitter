import {Component, OnInit} from "@angular/core";
import {AuthenticationService} from "../../services/authentication.service";
import {User} from "../../model/user";
import {UserService} from "../../services/user.service";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {FileUploader} from "ng2-file-upload";
import {environment} from "../../../environments/environment";
/**
 *
 */
@Component({
    selector: 'user-profile',
    templateUrl: './user-profile.component.html',
    styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
    user: User;
    public uploader: FileUploader;
    public hasBaseDropZoneOver: boolean = false;
    avatarUrl = environment.apiUrl + '/api/1/user/avatar/';

    /**
     *
     * @param activeModal
     * @param authenticationService
     * @param userService
     */
    constructor(public activeModal: NgbActiveModal, private authenticationService: AuthenticationService, private userService: UserService) {
    }

    /**
     *
     */
    ngOnInit() {
        this.user = <User> JSON.parse(JSON.stringify(this.authenticationService.user));
        this.uploader = new FileUploader({url: environment.apiUrl + '/api/1/private/user/avatar/' + this.user._id});
        this.uploader.setOptions({
            autoUpload: true, removeAfterUpload: true, headers: [{
                name: 'X-secure-Token',
                value: this.authenticationService.token
            }]
        })
    }

    /**
     *
     */
    save() {
        this.userService.updateUser(this.user).subscribe(res => {
            this.authenticationService.user = res;
            this.activeModal.close();
        }, err => this.userService.handleError(err));
    }

    fileOverBase(e: any): void {
        this.hasBaseDropZoneOver = e;
    }
}
