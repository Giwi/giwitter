import {Component, OnInit} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {MessageService} from "../../services/message.service";
import {Message} from "../../model/message";
import {MessageAddComponent} from "../message-add/message-add.component";
import * as EventBus from "vertx3-eventbus-client/vertx-eventbus";
import {environment} from "../../../environments/environment";
import {AuthenticationService} from "../../services/authentication.service";
/**
 *
 */
@Component({
    selector: 'app-baab-list',
    templateUrl: './message-list.component.html',
    styleUrls: ['./message-list.component.css']
})
export class MessageListComponent implements OnInit {
    messages: Message[] = [];
    messageToDelete: string;
    modal;
    uid: string = '';
    avatarUrl = environment.apiUrl + '/api/1/user/avatar/';

    /**
     *
     * @param messageService
     * @param modalService
     * @param authenticationService
     */
    constructor(private messageService: MessageService, private modalService: NgbModal, private authenticationService: AuthenticationService) {
    }

    /**
     *
     */
    ngOnInit() {
        this.refresh();
        var comp = this;
        this.uid = this.authenticationService.user._id;
        var eb = new EventBus(environment.apiUrl + '/api/1/eventbus');
        //noinspection TypeScriptUnresolvedVariable
        eb.onopen = function () {
            // set a handler to receive a message
            //noinspection TypeScriptUnresolvedFunction
            eb.registerHandler('new-message', function (error, message) {
                if (message.body.author._id !== comp.authenticationService.user._id) {
                    comp.refresh();
                }
            });
            //noinspection TypeScriptUnresolvedFunction
            eb.registerHandler('del-message', function () {
                    comp.refresh();
            });
        }
    }

    /**
     *
     * @param content
     * @param id
     */
    open(content, id) {
        this.messageToDelete = id;
        this.modal = this.modalService.open(content);
    }

    /**
     *
     */
    refresh() {
        this.messageService.getList().subscribe(res => {
            this.messages = res;
        }, error => this.messageService.handleError(error));
    }

    openAdd() {
        const modalRef = this.modalService.open(MessageAddComponent);
        modalRef.result.then(r => {
            modalRef.close('');
            this.refresh();
        });
    }

    /**
     *
     */
    deleteMessage() {
        this.messageService.delete(this.messageToDelete).subscribe(res => {
            this.modal.close('');
            this.refresh();
        }, error => this.messageService.handleError(error));
    }
}
