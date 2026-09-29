# Civic Voice UI Ad Requirements

This starter package covers the required UI changes for both the web app and Android app:

- reserve ad spaces on every page
- show a video ad for 5–10 seconds after a report is successfully submitted
- show a video ad after a search query is submitted
- keep the public reporting flow intact while using ads as monetization placeholders

## Web app

Use the example in `web/AdSlots.tsx` to add a standard ad slot component and a timed video overlay.

## Android app

Use the example in `android/AdWidgets.dart` to add banner placements and a rewarded-video flow using the Google Mobile Ads SDK.

## Important note

For real production, replace the placeholder ad unit IDs with valid Google AdMob or Google Ad Manager IDs and follow Google policy for app/site compliance.
