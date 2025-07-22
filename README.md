# Expenseses

An Android application for tracking expenses with weekly, monthly and yearly budgets.

## Features
- Add, edit and delete expenses.
- Set budgets for each time period and see percentage used.
- Data persisted using Room database (`Expense` and `Budget` tables).
- Tabbed interface powered by ViewPager2 with fragments for each period.

## Building
This project uses the Gradle wrapper (`gradlew`) and requires the Android SDK.

```bash
./gradlew build
```

You can also open the project in Android Studio for development.

## Tests
Basic unit and instrumentation tests are located in `app/src/test` and `app/src/androidTest`.
Run them with:

```bash
./gradlew test
```

## Running
After building, install the APK on an emulator or device:

```bash
./gradlew installDebug
```

The main screen contains tabs for weekly, monthly and yearly views.
