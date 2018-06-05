# Hey birthday
Birthday reminders bot for Discord app

[![Build Status](https://travis-ci.org/FireZenk/HeyBirthday.svg?branch=develop)](https://travis-ci.org/FireZenk/HeyBirthday)
[![codecov](https://codecov.io/gh/FireZenk/HeyBirthday/branch/develop/graph/badge.svg)](https://codecov.io/gh/FireZenk/HeyBirthday)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/FireZenk/HeyBirthday/issues)
[![Beerpay](https://img.shields.io/beerpay/hashdog/scrapfy-chrome-extension.svg)](https://beerpay.io/FireZenk/HeyBirthday)

### Commands

- Configuration
    - eb!reminderChannel `channelName`
    - eb!reminderHour `23:59`
- Usage
    - eb!add `Name` `MM-dd-yyyy`
    - eb!remove `Name`


You will recieve a reminder message on the `reminderChannel` at `reminderHour` like:

Today is `Name`'s birthday (number)! :tada: :tada:
(followed by a related image)

### How to install

1. `git clone https://github.com/FireZenk/HeyBirthday.git`
2. `cd HeyBirthday/`
2. `./gradlew stage`
3. `export DISCORD=YOUR_DISCORD_BOT_TOKEN`
4. `export GIPHY=YOUR_GIHPY_API_KEY`
5. `./build/install/heybirthday/bin/heybirthday $DISCORD $GIPHY`
6. Enjoy!

### How to deploy

1. Create a *free account* on Google Cloud Platform
2. Go to Google App Engine: [link](https://console.cloud.google.com/projectselector/appengine/create?lang=java&st=true&_ga=2.99197598.-1421285151.1528186199)
3. Create a project
4. Open Google Cloud Shell
5. Follow "How to install" above