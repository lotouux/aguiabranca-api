# Águia Branca API

> [!NOTE]
> **Notice for Evaluators:** Running this API locally is optional. The frontend application is already configured to consume a live instance of this API,
> so you can skip the local setup and proceed directly to running the client app.

Spring Boot 3.5 / Java 21 API for an innovation-idea pipeline: Operators submit ideas, Managers
triage them and run projects, Leaders steer strategy and read the resulting metrics.

## Running

This project is built and run through Docker only - no local JDK or Maven install is required
or supported.

```bash
docker build -t aguiabranca-api .
docker run --rm -p 8080:8080 -e JWT_SECRET=$(openssl rand -base64 32) aguiabranca-api
```

`JWT_SECRET` is required in every profile. To also enable the H2 console at `/h2-console` for
local inspection, add `-e SPRING_PROFILES_ACTIVE=dev` (its fallback secret exists purely so the
container still boots if you forget `-e JWT_SECRET`; never rely on it outside local development).

Seeded accounts (senha `123` for all): `OP001` (Operador), `GS001` (Gestor), `LD001` (Liderança).

## Known limitation: data does not survive a restart

The database is `jdbc:h2:mem:` with `spring.jpa.hibernate.ddl-auto=create-drop`, and
`DataSeeder` reseeds on every boot. Every restart wipes all data, including anything a Manager
recorded in `economiaAnualRealizada` - this undercuts the Dashboard's premise of tracking realized
savings over time, but is accepted as within scope for this project.

Making data durable would mean `jdbc:h2:file:` (or another persistent store) plus
`ddl-auto=validate` and a migration tool (e.g. Flyway) instead of letting Hibernate generate the
schema - at which point every future schema change needs a migration. Decide on that before
relying on this API for anything beyond a demo.
