Segment Micronaut Lib
=====================

[![Build Status](https://travis-ci.org/agorapulse/micronaut-segment.png)](https://travis-ci.org/agorapulse/grails-segment)
[![Download](https://api.bintray.com/packages/agorapulse/libs/micronaut-segment/images/download.svg)](https://bintray.com/agorapulse/libs/segment/_latestVersion)

# Introduction

The **Segment Lib** allows you to integrate [Segment](http://segment.com) in your [Micronaut](https://micronaut.io) or [Grails](https://grails.org) applications.

Segment lets you send your analytics data to any service you want, without you having to integrate with each one individually.

It provides the following bean service:
* **SegmentService** - A server side service client to call [Segment APIs](https://segment.com/docs/libraries/http/), which is a wrapper around the official Segment [Analytics for Java](https://segment.com/docs/libraries/java/) library.

# Installation

Declare the lib dependency in the _build.gradle_ file, as shown here:

```groovy
dependencies {
    ...
    compile "com.agorapulse:micronaut-segment:0.1.0"
    ...
}
```


# Config

Create a [Segment](http://segment.com) account, in order to get your own _apiKey_ (for client-side API calls).

Add your Segment.io site _apiKey_  to your _grails-app/conf/application.yml_:

```yml
segment:
    apiKey: {API_KEY} # Write key
```

# Usage

## SegmentService

You can inject _SegmentService_ into your beans in order to call [Segment APIs](https://segment.com/docs/libraries/).

```groovy
@Singleton
class MyService {

    private final SegmentService segmentService
    
    MyService(SegmentService segmentService) {
        this.segmentService = segmentService
    }
    
    void analyzeUsers() {
    
        // Identify and set traits
        segmentService.identify('bob@bob.com', [gender: 'male'])
        
        // Identify and set traits with past date (JodaTime DateTime representing when the identify took place)
        segmentService.identify(
            'bob@bob.com',
            [gender: 'male'],
            new DateTime(2012, 3, 26, 12, 0, 0, 0)
        )
        
        // Identify and set traits with past date and context
        segmentService.identify(
            'bob@bob.com', [gender: 'male'],
            new DateTime(2012, 3, 26, 12, 0, 0, 0),
            [
                integrations: [
                    'All': false,
                    'Mixpanel': true,
                    'KISSmetrics': true
                ],
                ip: '192.168.0.10'
            ]
        )
        
        // Track an event
        segmentService.track('bob@bob.com', 'Signed up')
        
        // Track an event and set properties
        segmentService.track(
            'bob@bob.com',
            'Signed up',
            [plan: 'Pro', amount: 99.95]
        )
        
        // Track a past event and set properties with past date
        segmentService.track(
            'bob@bob.com', 'Signed up',
            [plan: 'Pro', amount: 99.95],
            new DateTime(2012, 3, 26, 12, 0, 0, 0)
        )
        
        // Track a past event and set properties with past date and context
        segmentService.track(
            'bob@bob.com',
            'Signed up',
            [plan: 'Pro', amount: 99.95],
            new DateTime(2012, 3, 26, 12, 0, 0, 0),
            [
                integrations: [
                    'All': false,
                    'Mixpanel': true,
                    'KISSmetrics': true
                ],
                ip: '192.168.0.10'
            ]
        )
        
        // Group
        segmentService.group('bob@bob.com', 'companyId', [
            name: 'The company name',
            website: 'http://www.company.com'
        ])
        
        // Record page view
        segmentService.page('bob@bob.com', 'Pricing')
        
        // Record page view with extra info
        segmentService.page('bob@bob.com', 'Pricing', 'Business', [
            title: 'Segment.io Pricing',
            path: '/pricing'
        ])
        
        // Record screen view
        segmentService.screen('bob@bob.com', 'Register', 'Business', [
            type: 'facebook'
        ])
        
        // Alias identity
        segmentService.alias('bob@bob.com', 'bob')
    }
}
```

# Bugs

To report any bug, please use the project [Issues](http://github.com/agorapulse/micronaut-segment/issues) section on GitHub.
