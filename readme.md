# LeaderOS API Integration

Unofficial Kotlin Wrapper of the LeaderOS API https://developer.leaderos.net/

## Table of Contents

- [Getting Started](#getting-started)
- [How to use](#how-to-use)
- [LeaderOS versions](#leaderos-versions)
- [Error handling](#error-handling)
- [Status](#status)
    - [Auth](#auth)
    - [User Endpoints](#user-endpoints)
    - [Store Endpoints](#store-endpoints)
    - [Bazaar Endpoints](#bazaar-endpoints)
    - [Support Endpoints](#support-endpoints)
    - [Credits Endpoints](#credits-endpoints)
    - [Vote Endpoints](#vote-endpoints)
    - [Discord Endpoints](#discord-endpoints)
    - [Integrations](#integrations)
- [Tests](#tests)

## Getting Started

To begin using the LeaderOS API, refer to the [Introduction](https://developer.leaderos.net/) section of the official
documentation.

⚠️ Authentication is required to access most endpoints. Detailed information can be found in
the [Authentication](https://developer.leaderos.net/getting-started/authentication) section.

## How to use

1. Initialize the LeaderOS object with your website URL and API token.

```kotlin
val leaderOS: LeaderOS = LeaderOS("https://myWebsite.com", "Token")
```

2. LeaderOS object hold several services that you can use to perform your api calls :

```kotlin
// To get a user by its username
val user: User = leaderOS.user.profile("iZyy_")

// To add 10€ to the users account, returns the new balance
val balance: CreditBalance = leaderOS.credits.add("iZyy_", 10.0)

// To buy products with the user's credits
val purchase: Purchase = leaderOS.store.buy(userId = 3, BuyItem(productId = 1, quantity = 2), BuyItem(productId = 4))

// To answer a support ticket
leaderOS.support.reply(ticketId = 6, message = "Hello!", accountId = 1, isAdmin = true)

// ...
```

Models are typed: ids are `Int`, amounts `BigDecimal`, flags `Boolean` and dates `LocalDateTime`.
LeaderOS "never expires" dates (`1000-01-01 00:00:00`) become `null`, e.g. `role.isPermanent`.

## LeaderOS versions

Some endpoints changed with LeaderOS 6.0 (credits, user profiles, store listing and buy). Set the version of your
website once, the wrapper calls the right endpoints:

```kotlin
val leaderOS = LeaderOS("https://myWebsite.com", "Token")                                   // LeaderOSVersion.LATEST (6.3.3)
val legacy = LeaderOS("https://myWebsite.com", "Token", version = LeaderOSVersion.V5_5_9)

leaderOS.credits.add("iZyy_", 10.0) // POST /api/credits/add
legacy.credits.add("iZyy_", 10.0)   // POST /api/credits/iZyy_/add
```

Endpoints that only exist on 6.x throw an `UnsupportedVersionException` before any request is sent.

## Error handling

Errors sent by LeaderOS are thrown as `LeaderOSException`, with the HTTP status and the LeaderOS error code:

```kotlin
try {
    leaderOS.credits.remove("iZyy_", 50.0)
} catch (e: LeaderOSException) {
    when (e.error) {
        LeaderOSError.NOT_ENOUGH_CREDITS -> println("Not enough credits")
        LeaderOSError.USER_NOT_FOUND -> println("Unknown user")
        else -> throw e
    }
}
```

Network failures are thrown as `IOException`.

## Status

The following tables outline the development status of each API endpoint, categorized accordingly.

This Wrapper targets `LeaderOS 6.3.3`. It was previously tested on `LeaderOS 5.5.9`.

### Auth

| Endpoint              | Description                         | Wrapper                                  | Status |
|-----------------------|-------------------------------------|------------------------------------------|--------|
| `/login`              | User login                          | `auth.login`                             | 📗     |
| `/register`           | User registration                   | `auth.register`                          | 📗     |
| `/sessions`           | Manage sessions                     | `auth.isSessionValid`, `auth.createSession` | 📗  |
| `/tfa/verify`         | Two-factor authentication           | `auth.verifyTfa`                         | 📗     |
| `/game-sessions` (¹)  | In-game authentication state (6.x)  | `auth.gameSession`                       | 📗     |

### User Endpoints

| Endpoint    | Description   | Wrapper                                                              | Status |
|-------------|---------------|----------------------------------------------------------------------|--------|
| `/profiles` | User profiles | `user.profile`                                                       | 📗     |
| `/roles`    | User roles    | `user.roles`, `user.addRole`, `user.updateRole`, `user.removeRole`   | 📗     |
| `/tickets`  | User tickets  | `user.tickets`                                                       | 📗     |
| `/orders`   | User orders   | `user.orders`                                                        | 📗     |

### Store Endpoints

| Endpoint              | Description               | Wrapper                                | Status |
|-----------------------|---------------------------|----------------------------------------|--------|
| `/listing`            | Store listing             | `store.listing`                        | 📗     |
| `/products`           | Store products            | `store.products`, `store.product`      | 📗     |
| `/categories`         | Product categories        | `store.categories`, `store.category`   | 📗     |
| `/buy`                | Purchase products         | `store.buy`                            | 📗     |
| `/donations`          | Manage donations          | `store.donations`                      | 📗     |
| `/community-goal` (¹) | Community goal (6.x)      | `store.communityGoal`                  | 📗     |

### Bazaar Endpoints

| Endpoint           | Description     | Wrapper                                                 | Status |
|--------------------|-----------------|---------------------------------------------------------|--------|
| `/player-storages` | Player storages | `bazaar.items`, `bazaar.addItem`, `bazaar.removeItem`   | 📗     |
| `/servers`         | Game servers    | `bazaar.servers`                                        | 📗     |

### Support Endpoints

| Endpoint         | Description        | Wrapper                                                                       | Status |
|------------------|--------------------|-------------------------------------------------------------------------------|--------|
| `/tickets`       | Support tickets    | `support.tickets`, `support.ticket`, `support.createTicket`, `support.closeTicket` | 📗 |
| `/messages`      | Support messages   | `support.messages`, `support.message`, `support.reply`                        | 📗     |
| `/categories`    | Support categories | `support.categories`, `support.category`                                      | 📗     |
| `/quick-answers` | Quick answers      | `support.quickAnswers`, `support.quickAnswer`                                 | 📗     |

### Credits Endpoints

| Endpoint          | Description        | Wrapper           | Status |
|-------------------|--------------------|-------------------|--------|
| `/show-credits`   | Display credits    | `credits.show`    | 📗     |
| `/add-credits`    | Add credits        | `credits.add`     | 📗     |
| `/remove-credits` | Remove credits     | `credits.remove`  | 📗     |
| `/send-credits`   | Transfer credits   | `credits.send`    | 📗     |
| `/set-credits`    | Set credit balance | `credits.set`     | 📗     |

### Vote Endpoints

| Endpoint      | Description  | Wrapper      | Status |
|---------------|--------------|--------------|--------|
| `/vote-links` | Voting links | `vote.links` | 📗     |

### Discord Endpoints

| Endpoint    | Description      | Wrapper            | Status |
|-------------|------------------|--------------------|--------|
| `/users`    | Discord users    | `discord.user`     | 📗     |
| `/settings` | Discord settings | `discord.settings` | 📗     |
| `/roles`    | Discord roles    | `discord.roles`    | 📗     |

### Integrations

| Endpoint                            | Description                    | Wrapper                     | Status |
|-------------------------------------|--------------------------------|-----------------------------|--------|
| `/leaderos-id/verify-ingame` (¹)    | Link a Minecraft account (6.x) | `integrations.verifyInGame` | 📗     |
| `/leaderos-ai/ai-command/ingame` (¹) | Ask LeaderOS AI (6.x)          | `integrations.aiCommand`    | 📗     |

(¹) Not in the public documentation, implemented from the
[official LeaderOS plugins](https://github.com/leaderos-net).

**Note:** The development status of each endpoint is based on the information available in
the [LeaderOS API documentation](https://developer.leaderos.net/). Please refer to the official documentation for the
most up-to-date information.

## Tests

- `src/test/kotlin/fr/izy/leaderos` runs offline against a mock server and checks the requests sent for each LeaderOS
  version: `./gradlew test --tests "fr.izy.leaderos.*"`
- The other tests call a real website configured in `.env` (`BASE_URL`, `API_KEY`) and **modify its data**
  (credits, purchases, registrations).
