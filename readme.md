# LeaderOS API Integration

Unofficial Kotlin Wrapper of the LeaderOS API https://developer.leaderos.net/

## Table of Contents

- [Getting Started](#getting-started)
- [How to use](#how-to-use)
- [Status](#status)
    - [Auth](#auth)
    - [User Endpoints](#user-endpoints)
    - [Store Endpoints](#store-endpoints)
    - [Bazaar Endpoints](#bazaar-endpoints)
    - [Support Endpoints](#support-endpoints)
    - [Credits Endpoints](#credits-endpoints)
    - [Vote Endpoints](#vote-endpoints)
    - [Discord Endpoints](#discord-endpoints)

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
val user: User? = leaderOS.user.profile("iZyy_")

// To add 10$ to the users account
val result = leaderOS.credits.add("iZyy_", 10.0)

// ...
```

## Status

The following tables outline the development status of each API endpoint, categorized accordingly.

This Wrapper has been tested only on `leaderOS 5.5.9`

### Auth

| Endpoint    | Description       | Status |
|-------------|-------------------|--------|
| `/login`    | User login        | 📗     |
| `/register` | User registration | 📕     |
| `/sessions` | Manage sessions   | 📕     |

### User Endpoints

| Endpoint    | Description   | Status |
|-------------|---------------|--------|
| `/profiles` | User profiles | 📗     |
| `/roles`    | User roles    | 📗     |
| `/tickets`  | User tickets  | 📗     |
| `/orders`   | User orders   | 📗     |

### Store Endpoints

| Endpoint      | Description        | Status |
|---------------|--------------------|--------|
| `/listing`    | Store listing      | 📗     |
| `/products`   | Store products     | 📗     |
| `/categories` | Product categories | 📗     |
| `/buy`        | Purchase products  | 📕     |
| `/donations`  | Manage donations   | 📗     |

### Bazaar Endpoints

| Endpoint           | Description     | Status |
|--------------------|-----------------|--------|
| `/player-storages` | Player storages | 📕     |
| `/servers`         | Game servers    | 📕     |

### Support Endpoints

| Endpoint         | Description        | Status |
|------------------|--------------------|--------|
| `/tickets`       | Support tickets    | 📕     |
| `/messages`      | Support messages   | 📕     |
| `/categories`    | Support categories | 📕     |
| `/quick-answers` | Quick answers      | 📕     |

### Credits Endpoints

| Endpoint          | Description        | Status |
|-------------------|--------------------|--------|
| `/show-credits`   | Display credits    | 📗     |
| `/add-credits`    | Add credits        | 📗     |
| `/remove-credits` | Remove credits     | 📗     |
| `/send-credits`   | Transfer credits   | 📗     |
| `/set-credits`    | Set credit balance | 📗     |

### Vote Endpoints

| Endpoint      | Description  | Status |
|---------------|--------------|--------|
| `/vote-links` | Voting links | 📗     |

### Discord Endpoints

| Endpoint    | Description      | Status |
|-------------|------------------|--------|
| `/users`    | Discord users    | 📗     |
| `/settings` | Discord settings | 📕     |
| `/roles`    | Discord roles    | 📕     |

**Note:** The development status of each endpoint is based on the information available in
the [LeaderOS API documentation](https://developer.leaderos.net/). Please refer to the official documentation for the
most up-to-date information.
