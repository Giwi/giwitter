import {User} from "./user";
export class Message {
    id: string;
    date: number;
    author: User;
    content: string;
    user_id: string;
}
