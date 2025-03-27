import { Injectable } from '@angular/core';
import { Client } from '../../shared/models/client/client';

const LS_KEY = "client";

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor() { }

  getAll(): Client[] {
    const clients = localStorage[LS_KEY];

    return clients ? JSON.parse(clients) : [];
  }

  getById(id: number) : Client | undefined{
    const clients = this.getAll();

    return clients.find(c => c.ID === id);
  }

  create(client: Client){
    const clients = this.getAll();

    client.ID = (clients.length > 0 ? Math.max(...clients.map(c => c.ID)) : 0) + 1;

    clients.push(client);

    localStorage[LS_KEY] = JSON.stringify(clients);
  }

  update(client : Client){
    const clients = this.getAll();

    clients.forEach( (cli, ind, objs) => {
      if (client.ID === cli.ID) {
        objs[ind] = client
      }
    })

    localStorage[LS_KEY] = JSON.stringify(client)
  }

  delete(id: number){
    var clients = this.getAll();

    clients = clients.filter(c => c.ID !== id);

    localStorage[LS_KEY] = JSON.stringify(clients);
  }
}
