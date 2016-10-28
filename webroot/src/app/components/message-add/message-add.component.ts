import {Component, OnInit} from "@angular/core";
import {Message} from "../../model/message";
import {MessageService} from "../../services/message.service";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {AuthenticationService} from "../../services/authentication.service";

/**
 *
 */
@Component({
    selector: 'app-message-add',
    templateUrl: './message-add.component.html',
    styleUrls: ['./message-add.component.css']
})
export class MessageAddComponent implements OnInit {
    message: Message;

    /**
     *
     * @param activeModal
     * @param messageService
     * @param authenticationService
     */
    constructor(public activeModal: NgbActiveModal, private messageService: MessageService, private authenticationService: AuthenticationService) {
    }

    /**
     *
     */
    ngOnInit() {
        this.message = new Message();
        this.message.date = new Date().getTime();
        this.message.author = this.authenticationService.user;
    }

    /**
     *
     */
    save() {

        this.messageService.create(this.message).subscribe(res => {
            this.activeModal.close();
        }, error => this.messageService.handleError(error));
    }
}
