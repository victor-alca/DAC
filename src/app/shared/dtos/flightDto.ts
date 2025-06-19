import { Flight } from "../models/flight/flight.model";

export class FlightsDTO {
    constructor(
        public fim: string,
        public inicio: string,
        public voos: Flight[]
    ) {}
}

