# unomi-dummy-score-prediction

This repository contains a prototype integration of [Apache Unomi](https://unomi.apache.org/) with a dummy score prediction.
This project was created to educate myself on how to implement a Apache Unomi Plugin which can possibly integrate with other (AI) backends in a custom action plugin, inspired by https://github.com/Jahia/unomi-predictionio-plugin.

## How to build

```
mvn clean verify
```

The Apache Karaf KAR bundle file is created in `karaf-kar/target/` folder.

## How to deploy

Before deploying the bundle, you can tail logs in the Apache Karaf console:

```
karaf@root()> log:tail
```

Copy the Apache Karaf KAR bundle file in `karaf-kar/target/` folder, `unomi-dummy-score-prediction-kar-x.y.z*.kar`, to $UNOMI_HOME/deploy/ folder.

You may see deployment logs like the following:

```
08:43:34.505 INFO [fileinstall-C:\Users\dev1\testspace\unomi\unomi-1.6.0/deploy] Found a .kar file to deploy.
08:43:34.520 WARN [fileinstall-C:\Users\dev1\testspace\unomi\unomi-1.6.0/deploy] Karaf archive C:\Users\dev1\testspace\unomi\unomi-1.6.0\deploy\unomi-dummy-score-prediction-kar-0.1.1-SNAPSHOT.kar' has been updated; redeploying.
08:43:34.532 INFO [fileinstall-C:\Users\dev1\testspace\unomi\unomi-1.6.0/deploy] Removing features: unomi-dummy-score-prediction-kar/[0.1.1.SNAPSHOT,0.1.1.SNAPSHOT]
08:43:36.130 INFO [features-3-thread-1] Changes to perform:
08:43:36.132 INFO [features-3-thread-1]   Region: root
08:43:36.133 INFO [features-3-thread-1]     Bundles to uninstall:
08:43:36.136 INFO [features-3-thread-1]       com.github.woonsan.unomi-dummy-score-prediction-core/0.1.1.SNAPSHOT
08:43:36.145 INFO [features-3-thread-1]     Bundles to install:
08:43:36.147 INFO [features-3-thread-1]       mvn:org.apache.unomi/unomi-groovy-actions-services/1.6.0
08:43:36.151 INFO [features-3-thread-1] Stopping bundles:
08:43:36.153 INFO [features-3-thread-1]   com.github.woonsan.unomi-dummy-score-prediction-core/0.1.1.SNAPSHOT
08:43:36.163 INFO [features-3-thread-1] Destroying container for blueprint bundle com.github.woonsan.unomi-dummy-score-prediction-core/0.1.1.SNAPSHOT
08:43:36.178 INFO [features-3-thread-1] Uninstalling bundles:
08:43:36.180 INFO [features-3-thread-1]   com.github.woonsan.unomi-dummy-score-prediction-core/0.1.1.SNAPSHOT
08:43:36.203 INFO [features-3-thread-1] Installing bundles:
08:43:36.204 INFO [features-3-thread-1]   mvn:org.apache.unomi/unomi-groovy-actions-services/1.6.0
08:43:36.217 INFO [features-3-thread-1] Refreshing bundles:
08:43:36.218 INFO [features-3-thread-1]     com.github.woonsan.unomi-dummy-score-prediction-core/0.1.1.SNAPSHOT (Bundle will be uninstalled)
08:43:36.226 INFO [features-3-thread-1] Done.
```

## How to test

- Query the profile first like the following example (in Visual Studio Code RestClient format):

```
# Environment variables
@baseUrl = http://localhost:8181
@credentials = karaf:karaf

### Read profile

POST {{baseUrl}}/cxs/context.json?sessionId=1234
Accept: */*
Authorization: Basic {{credentials}}
Content-Type: application/json

{
  "source": {
    "itemId":"homepage",
    "itemType":"page",
    "scope":"example"
  },
  "requiredProfileProperties":["*"],
  "requiredSessionProperties":["*"],
  "requireSegments":true,
  "requireScores":true
}
```

- Now, post a page view event like the following:

```
### Submit an event

POST {{baseUrl}}/cxs/eventcollector
Accept: */*
Authorization: Basic {{credentials}}
Content-Type: application/json

{
  "sessionId" : "1234",
  "events": [
    {
      "eventType": "view",
      "scope": "example",
      "source": {
        "itemType": "site", 
        "scope":"example", 
        "itemId": "mysite" 
      },
      "target": {
        "itemType": "page",
        "scope": "example",
        "itemId": "buy_kia_morning",
        "properties": {
          "pageInfo": {
            "destinationURL": "http://localhost:8080/sites/example/buy_kia_morning.html",
            "pageID": "f20836ab-608f-4551-a930-9796ec991340",
            "pagePath": "/sites/example/home",
            "pageName": "Home",
            "referringURL": "https://www.google.com/search?q=kia+morning",
            "isContentTemplate": false
          }
        }
      }
    }
  ]
}
```

- Query the profile again and you will see the `dummyLeadScoringProbability` property set to the `profileProperties` of the `profile` object.

- Also, try to change the `refereringURL` and `destinationURL` properties in the event input and see how it affects the dummy score calculation.
  For a simple demonstration, the dummy score calculation is hard-coded very simply in [DummyPredictiveLeadScoringAction.java](/unomi-dummy-score-prediction-core/src/main/java/com/github/woonsan/unomi/dummyprediction/DummyPredictiveLeadScoringAction.java).

