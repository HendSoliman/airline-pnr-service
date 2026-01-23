db = db.getSiblingDB("pnrdb");

db.getSiblingDB('pnrdb').createCollection('bookings');
/* BOOKINGS */
db.bookings.insertOne({
  pnr: "GHTW42",
  cabinClass: "ECONOMY",
  passengers: [
    {
      pNum: 1,
      fName: "James",
      lName: "McGill",
      custId: null,
      seat: "32D"
    },
    {
      pNum: 2,
      fName: "Charles",
      lName: "McGill",
      custId: "1216",
      seat: "31D"
    }
  ],
  flights: [
    {
      fNo: "EK231",
      dep: "DXB",
      arr: "IAD",
      time: ISODate("2025-11-11T02:25:00Z")
    }
  ],
  updatedAt: ISODate("2024-01-20T10:00:00Z")
});

/* BAGGAGE ALLOWANCES */
db.baggage_allowances.insertOne({
  pnr: "GHTW42",
  passengerNumber: 1,
  allowanceUnit: "kg",
  checkedAllowanceValue: 30,
  carryOnAllowanceValue: 7
});

/* TICKETS */
db.tickets.insertOne({
  pnr: "GHTW42",
  passengerNumber: 2,
  ticketUrl: "emirates.com?ticket=someTicketRef",
  status: "ISSUED"
});

print("✅ Mongo initial data inserted successfully");
