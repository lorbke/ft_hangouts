# ft_hangouts — Architecture

## Layers

- **ui** — Activities + Adapters. Only layer the user sees.
- **data** — SQLite access (`DbHelper` + Repositories). Only layer that touches the database.
- **sms** — Sending/receiving text messages. Only layer that touches `SmsManager`.

UI never touches SQLite or SmsManager directly — always through a Repository.

## Screens

| Screen | Purpose |
|---|---|
| Contact list (home) | Summary list of all contacts, add button, background-time toast |
| Contact form | Create and edit a contact (one screen, two modes) |
| Contact detail | View a contact; edit / delete / message / call |
| Conversation | SMS thread with a contact, send box |
| Header color picker | Dialog from the overflow menu |

## Data

Two tables, our own (not the system contacts table):

- **contact** — name, phone, email, address, photo
- **message** — belongs to a contact, text, timestamp, direction (in/out)

## Cross-cutting

- Header color & background-timestamp → `SharedPreferences`
- Two languages → resource qualifiers (`values` / `values-de`)
- Rotation → handled via saved state, no orientation lock
- Incoming SMS → `BroadcastReceiver`, finds or creates the matching contact
- Calling a contact → hands off to the system dialer
