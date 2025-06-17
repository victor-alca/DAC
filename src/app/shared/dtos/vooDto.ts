import { Flight } from "../models/flight/flight.model";

export class VoosDTO {
    constructor(
        public fim: string,
        public inicio: string,
        public voos: Flight[]
    ) {}
}

