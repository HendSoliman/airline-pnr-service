db = db.getSiblingDB("pnrdb");

/* BOOKINGS */
db.getCollection('bookings').insertOne({
  bookingReference: "GHTW42",
  cabinClass: "ECONOMY",
  passengers: [
    {
      passengerNumber: 1,
      firstName: "James",
      middleName: null,
      lastName: "McGill",
      customerId: null,
      seat: "32D"
    },
    {
      passengerNumber: 2,
      firstName: "Charles",
      middleName: null,
      lastName: "McGill",
      customerId: "1216",
      seat: "31D"
    }
  ],
  flights: [
    {
      flightNumber: "EK231",
      departureAirport: "DXB",
      departureTimeStamp: ISODate("2025-11-11T02:25:00Z"),
      arrivalAirport: "IAD",
      arrivalTimeStamp: ISODate("2025-11-11T08:25:00Z") // example arrival time
    }
  ],
  createdAt: ISODate("2024-01-20T10:00:00Z"),
  updatedAt: ISODate("2024-01-20T10:00:00Z")
});

/* BAGGAGE ALLOWANCES */
db.getCollection('baggage_allowances').insertOne({
  bookingReference: "GHTW42",
  passengerNumber: 1,
  allowanceUnit: "kg",
  checkedAllowanceValue: 30,
  carryOnAllowanceValue: 7
});

/* E-TICKETS */
db.getCollection('tickets').insertOne({
  bookingReference: "GHTW42",
  passengerNumber: 2,
  ticketUrl: "emirates.com?ticket=someTicketRef"
});

print("✅ Mongo initial data inserted successfully");
