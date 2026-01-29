# API Testing Examples

## Prerequisites
the application is running on `http://localhost:8080`

## PNR Query API Testing Examples

### Get Booking information by pnr 
```bash
curl -X GET http://localhost:8080/booking/GHTW42
```

### Booking Response
```json
{
    "pnr": "GHTW42",
    "cabinClass": "ECONOMY",
    "passengers": [
        {
            "passengerNumber": 1,
            "fullName": "James Morgan McGill",
            "seat": "32D",
            "baggage": {
                "allowanceUnit": "kg",
                "checkedAllowanceValue": 30,
                "carryOnAllowanceValue": 7
            }
        },
        {
            "passengerNumber": 2,
            "customerId": "1216",
            "fullName": "Charles McGill",
            "seat": "31D",
            "ticketUrl": "emirates.com?ticket=someTicketRef"
        }
    ],
    "flights": [
        {
            "flightNumber": "EK231",
            "departureAirport": "DXB",
            "departureTimeStamp": "2025-11-11T02:25:00Z",
            "arrivalAirport": "IAD",
            "arrivalTimeStamp": "2025-11-11T08:25:00Z"
        }
    ]
}
```