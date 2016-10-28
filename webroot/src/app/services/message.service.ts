import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import "rxjs/add/operator/map";
import {AuthenticationService} from "./authentication.service";
import {ToasterService} from "angular2-toaster";
import {ApiService} from "./api.service";
import {Message} from "../model/message";
import {Router} from "@angular/router";

/**
 *
 */
@Injectable()
export class MessageService extends ApiService {

    /**
     *
     * @param router
     * @param http
     * @param authenticationService
     * @param toasterService
     */
    constructor(router: Router, private http: Http, authenticationService: AuthenticationService, toasterService: ToasterService) {
        super(toasterService, authenticationService, router);
    }

    /**
     *
     * @returns {Observable<Message[]>}
     */
    getList(): Observable<Message[]> {
        return this.http.get(environment.apiUrl + '/api/1/private/message', this.addHeaderToken())
            .map((response: Response) => {
                return this.extractData(response) as Message[];
            });
    }

    /**
     *
     * @returns {Observable<Message[]>}
     */
    getPaginated(start: number, nb: number): Observable<Message[]> {
        return this.http.get(environment.apiUrl + '/api/1/private/message/' + start + '/' + nb, this.addHeaderToken())
            .map((response: Response) => {
                return this.extractData(response) as Message[];
            });
    }

    /**
     *
     * @param id
     * @returns {Observable<Message>}
     */
    getDetail(id: string): Observable<Message> {
        return this.http.get(environment.apiUrl + '/api/1/private/message/' + id, this.addHeaderToken())
            .map((response: Response) => {
                return this.extractData(response) as Message;
            });
    }

    /**
     *
     * @param message
     * @returns {Observable<Message>}
     */
    create(message: Message): Observable<Message> {
        return this.http.post(environment.apiUrl + '/api/1/private/message/', JSON.stringify(message), this.addHeaderToken())
            .map((response: Response) => {
                return this.extractData(response) as Message;
            });
    }

    /**
     *
     * @param id
     * @returns {Observable<any>}
     */
    delete(id: string): Observable<any> {
        return this.http.delete(environment.apiUrl + '/api/1/private/message/' + id, this.addHeaderToken())
            .map((response: Response) => {
            });
    }
}
